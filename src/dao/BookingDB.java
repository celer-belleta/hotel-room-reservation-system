package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class BookingDB {

    public boolean finalizeBooking(int roomId, int guestId, int packageId, Calendar checkIn, Calendar checkOut, double totalAmount) {
        ReservationDB resChecker = new ReservationDB();
        Date sqlIn = new java.sql.Date(checkIn.getTimeInMillis());
        Date sqlOut = new java.sql.Date(checkOut.getTimeInMillis());

        if (!resChecker.isRoomAvailable(roomId, sqlIn, sqlOut)) {
            return false;
        }

        String reserveSql = "INSERT INTO reservations (room_id, guest_id, check_in, check_out, package_id, total_amount, amount_paid, remaining_balance, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Pending')";

        String updateRoomSql = "UPDATE rooms SET status = 'Booked' WHERE id = ?";

        try (Connection conn = db.DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(reserveSql)) {
                ps.setInt(1, roomId);          // room_id
                ps.setInt(2, guestId);         // guest_id
                ps.setDate(3, sqlIn);          // check_in
                ps.setDate(4, sqlOut);         // check_out
                ps.setInt(5, packageId);       // package_id
                ps.setDouble(6, totalAmount);  // total_amount
                ps.setDouble(7, 0.00);         // amount_paid (initial state is 0.00)
                ps.setDouble(8, totalAmount);  // remaining_balance (initial state is full total)

                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(updateRoomSql)) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean payReservationDeposit(int resId, double amount, String method) {
        String updateRes = "UPDATE reservations SET amount_paid = ?, remaining_balance = total_amount - ?, status = 'Pending' WHERE res_id = ?";

        String insertPay = "INSERT INTO payments (res_id, amount_paid, total_amount_due, discount_amount, payment_method, payment_type, invoice_number, payment_date) " +
                "VALUES (?, ?, ?, 0.0, ?, 'Downpayment', ?, NOW())";

        try (Connection conn = db.DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(updateRes);
                 PreparedStatement ps2 = conn.prepareStatement(insertPay)) {

                ps1.setDouble(1, amount);
                ps1.setDouble(2, amount);
                ps1.setInt(3, resId);
                ps1.executeUpdate();

                ps2.setInt(1, resId);
                ps2.setDouble(2, amount);
                ps2.setDouble(3, amount);
                ps2.setString(4, method);
                ps2.setString(5, "INV-" + resId + "-" + System.currentTimeMillis() % 1000);

                ps2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Object[]> getGuestReservations(int guestId) {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "SELECT r.room_number, r.type, res.check_in, res.check_out, res.total_amount, res.status " +
                "FROM reservations res " +
                "JOIN rooms r ON res.room_id = r.id " +
                "WHERE res.guest_id = ? ORDER BY res.res_id DESC";

        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("room_number"),
                        rs.getString("type"),
                        rs.getDate("check_in"),
                        rs.getDate("check_out"),
                        rs.getDouble("total_amount"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}