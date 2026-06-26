package dao;

import db.DBConnection;
import model.Reservation;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDate;

public class ReservationDB {

    public boolean isRoomAvailable(int roomId, Date start, Date end) {
        String sql = "SELECT COUNT(*) FROM reservations " +
                "WHERE room_id = ? AND status IN ('Pending', 'Confirmed', 'Checked-In', 'Occupied') " +
                "AND (check_in < ? AND check_out > ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setDate(2, end);
            ps.setDate(3, start);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int createReservation(int guestId, int roomId, Date checkIn, Date checkOut, int packageId) {
        if (!isRoomAvailable(roomId, checkIn, checkOut)) {
            JOptionPane.showMessageDialog(null, "Room is already booked for these dates!");
            return -1;
        }

        String sql = "INSERT INTO reservations (guest_id, room_id, check_in, check_out, package_id, status, handled_by_id) VALUES (?, ?, ?, ?, ?, 'Pending', ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, guestId);
            ps.setInt(2, roomId);
            ps.setDate(3, checkIn);
            ps.setDate(4, checkOut);
            ps.setInt(5, packageId);

            model.User sessionUser = ui.Session.getCurrentUser();
            if (sessionUser != null && sessionUser.getId() != guestId) {
                ps.setInt(6, sessionUser.getId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public boolean updateStatus(int resId, String newStatus) {
        String sql = "UPDATE reservations SET status = ? WHERE res_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, resId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelReservation(int resId, int roomId) {
        String sql = "UPDATE reservations SET status = 'Cancelled' WHERE res_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, resId);
                ps.executeUpdate();
            }
            new RoomDB().updateRoomStatus(roomId, "Available");
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Reservation> getAllReservations() {
        ArrayList<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, u.first_name, u.last_name " +
                "FROM reservations r " +
                "LEFT JOIN users u ON r.handled_by_id = u.id";

        java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("hh:mm a");

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Reservation res = new Reservation(
                        rs.getInt("res_id"), rs.getInt("guest_id"), rs.getInt("room_id"),
                        rs.getDate("check_in"), rs.getDate("check_out"),
                        rs.getString("status"), rs.getInt("package_id")
                );

                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String clerkName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                res.setManagedBy(clerkName.trim().isEmpty() ? "Self-Service" : clerkName);

                res.setTotalAmount(rs.getDouble("total_amount"));
                res.setAmountPaid(rs.getDouble("amount_paid"));

                Timestamp checkedInAt = rs.getTimestamp("checked_in_at");
                Timestamp checkedOutAt = rs.getTimestamp("checked_out_at");

                res.setDamageFee(rs.getDouble("damage_fee"));
                res.setDamagePaid(rs.getDouble("damage_paid"));

                // Apply 12-hour formatting to Time In
                if (checkedInAt != null) {
                    res.setTimeIn(timeFormat.format(checkedInAt));
                }

                // Apply 12-hour formatting to Time Out
                if (checkedOutAt != null) {
                    res.setTimeOut(timeFormat.format(checkedOutAt));
                } else {
                    res.setTimeOut("");
                }

                list.add(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteReservation(int resId) {
        String sql = "DELETE FROM reservations WHERE res_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getNewReservationsToday() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM reservations WHERE DATE(check_in) = CURDATE()";

        try (java.sql.Connection conn = db.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {

            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public boolean processCheckIn(int resId, int roomId) {
        String sql = "UPDATE reservations SET status = 'Checked-In', checked_in_at = NOW() WHERE res_id = ?";

        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean processCheckOut(int resId, int roomId) {
        String sql = "UPDATE reservations SET status = 'Checked-Out', checked_out_at = NOW() WHERE res_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, resId);
                ps.executeUpdate();

                new RoomDB().updateRoomStatus(roomId, "Available");

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //
    public boolean addDamageFee(int resId, double amount) {
        String sql = "UPDATE reservations SET damage_fee = ? WHERE res_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, resId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //
    public boolean updateCartStatusByResId(int resId, String newStatus) {
        String sql = "UPDATE reservations SET status = ? " +
                "WHERE cart_id = (SELECT cart_id FROM (SELECT cart_id FROM reservations WHERE res_id = ?) AS tmp)";

        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, resId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}