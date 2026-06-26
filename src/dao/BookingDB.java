package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class BookingDB {

    public boolean finalizeBooking(int roomId, int guestId, int packageId,
                                   java.util.Calendar checkIn, java.util.Calendar checkOut,
                                   double total, int adults, int seniors, int pwds, int children,
                                   String cartId) {

        String sql = "INSERT INTO reservations (room_id, guest_id, package_id, check_in, check_out, " +
                "total_amount, status, adults, seniors, pwds, children, cart_id, handled_by_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'Pending', ?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = db.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ps.setInt(2, guestId);
            ps.setInt(3, packageId);
            ps.setDate(4, new java.sql.Date(checkIn.getTimeInMillis()));
            ps.setDate(5, new java.sql.Date(checkOut.getTimeInMillis()));
            ps.setDouble(6, total);
            ps.setInt(7, adults);
            ps.setInt(8, seniors);
            ps.setInt(9, pwds);
            ps.setInt(10, children);
            ps.setString(11, cartId);

            model.User sessionUser = ui.Session.getCurrentUser();
            if (sessionUser != null && sessionUser.getId() != guestId) {
                ps.setInt(12, sessionUser.getId());
            } else {
                ps.setNull(12, java.sql.Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean payReservationDeposit(int resId, double amount, String method) {
        String findCartSql = "SELECT cart_id FROM reservations WHERE res_id = ?";

        String updateAllRes = "UPDATE reservations SET amount_paid = total_amount * 0.20, " +
                "remaining_balance = total_amount * 0.80, " +
                "status = 'Reserved' WHERE cart_id = (SELECT cart_id FROM (SELECT cart_id FROM reservations WHERE res_id = ?) AS tmp)";

        try (Connection conn = db.DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psUpdate = conn.prepareStatement(updateAllRes)) {
                psUpdate.setInt(1, resId);
                psUpdate.executeUpdate();

                String insertPay = "INSERT INTO payments (res_id, amount_paid, total_amount_due, payment_method, payment_type, invoice_number, payment_date) " +
                        "VALUES (?, ?, ?, 'Cart Payment', 'Downpayment', ?, NOW())";
                try (PreparedStatement psPay = conn.prepareStatement(insertPay)) {
                    psPay.setInt(1, resId);
                    psPay.setDouble(2, amount);
                    psPay.setDouble(3, amount);
                    psPay.setString(4, "INV-CART-" + System.currentTimeMillis() % 1000);
                    psPay.executeUpdate();
                }

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