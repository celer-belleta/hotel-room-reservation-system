package dao;

import db.DBConnection;
import model.Reservation;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDate;

// ReservationDB handles all database operations related to Bookings.
// This includes checking room availability, creating new reservations,
// and managing existing records (viewing and cancelling).

public class ReservationDB {

    // AVAILABILITY CHECK (The "Overlap" Logic)
    public boolean isRoomAvailable(int roomId, Date start, Date end) {
        String sql = "SELECT COUNT(*) FROM reservations " +
                "WHERE room_id = ? AND status = 'Confirmed' " +
                "AND (check_in < ? AND check_out > ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setDate(2, end); // Closes the gap
            ps.setDate(3, start); // Checks the start

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // CREATE RESERVATION
    public int createReservation(int guestId, int roomId, Date checkIn, Date checkOut, int packageId) {
        if (!isRoomAvailable(roomId, checkIn, checkOut)) {
            JOptionPane.showMessageDialog(null, "Room is already booked for these dates!");
            return -1;
        }

        String sql = "INSERT INTO reservations (guest_id, room_id, check_in, check_out, package_id, status) VALUES (?, ?, ?, ?, ?, 'Confirmed')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, guestId);
            ps.setInt(2, roomId);
            ps.setDate(3, checkIn);
            ps.setDate(4, checkOut);
            ps.setInt(5, packageId);

            ps.executeUpdate();

            // Get the generated reservation ID
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);

                // Only update status to Occupied if the booking starts today
                LocalDate today = LocalDate.now();
                if (checkIn.toLocalDate().equals(today)) {
                    new RoomDB().updateRoomStatus(roomId, "Occupied");
                }

                return generatedId; // Return the actual ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // CANCEL RESERVATION (returns true/false)
    public boolean cancelReservation(int resId, int roomId) {
        String sql = "UPDATE reservations SET status = 'Cancelled' WHERE res_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, resId);
                ps.executeUpdate();
            }

            // make the room available again
            new RoomDB().updateRoomStatus(roomId, "Available");

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // GET RESERVATIONS
    public ArrayList<Reservation> getAllReservations() {
        ArrayList<Reservation> list = new ArrayList<>();

        // Join with packages to ensure we only get reservations linked to valid packages
        String sql = "SELECT r.* FROM reservations r " +
                "JOIN packages p ON r.package_id = p.package_id";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Reservation(
                        rs.getInt("res_id"),
                        rs.getInt("guest_id"),
                        rs.getInt("room_id"),
                        rs.getDate("check_in"),
                        rs.getDate("check_out"),
                        rs.getString("status"),
                        rs.getInt("package_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE RESERVATION (For editing dates)
    public boolean updateReservation(int resId, Date checkIn, Date checkOut) {
        String sql = "UPDATE reservations SET check_in = ?, check_out = ? WHERE res_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, checkIn);
            ps.setDate(2, checkOut);
            ps.setInt(3, resId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE RESERVATION (Permanent Removal)
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

    // this method is for the Guest to see ONLY their own history
    public ArrayList<model.Reservation> getReservationsByGuestId(int guestId) {
        ArrayList<model.Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE guest_id = ? AND status != 'Cancelled'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, guestId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new model.Reservation(
                        rs.getInt("res_id"),
                        rs.getInt("guest_id"),
                        rs.getInt("room_id"),
                        rs.getDate("check_in"),
                        rs.getDate("check_out"),
                        rs.getString("status"),
                        rs.getInt("package_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}