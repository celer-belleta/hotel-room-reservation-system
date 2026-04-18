package dao;

import db.DBConnection;
import model.Reservation;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;

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
    public boolean createReservation(int guestId, int roomId, Date checkIn, Date checkOut) {
        if (!isRoomAvailable(roomId, checkIn, checkOut)) {
            JOptionPane.showMessageDialog(null, "Room is already booked for these dates!");
            return false;
        }

        String sql = "INSERT INTO reservations (guest_id, room_id, check_in, check_out, status) VALUES (?, ?, ?, ?, 'Confirmed')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            ps.setInt(2, roomId);
            ps.setDate(3, checkIn);
            ps.setDate(4, checkOut);

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // CANCEL RESERVATION (returns true/false)
    public boolean cancelReservation(int resId) {
        String sql = "UPDATE reservations SET status = 'Cancelled' WHERE res_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resId);
            // Returns true if 1 or more rows were updated
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // GET RESERVATIONS
    public ArrayList<Reservation> getAllReservations() {
        ArrayList<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Connection conn = db.DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Reservation(
                        rs.getInt("res_id"),
                        rs.getInt("guest_id"),
                        rs.getInt("room_id"),
                        rs.getDate("check_in"),
                        rs.getDate("check_out"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}