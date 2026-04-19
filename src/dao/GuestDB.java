package dao;

import db.DBConnection;
import model.Guest;
import java.sql.*;
import java.util.ArrayList;

// GuestDB handles all database operations related to Guests.
// This matches the Table: guests (guest_id, name, contact, id_number).

public class GuestDB {

    // ADD GUEST
    public boolean addGuest(String name, String contact, String idNumber) {
        String sql = "INSERT INTO guests (name, contact, id_number) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, contact);
            ps.setString(3, idNumber);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // GET ALL GUESTS
    public ArrayList<Guest> getAllGuests() {
        ArrayList<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM guests";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Guest(
                        rs.getInt("guest_id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("id_number")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // DELETE GUEST
    public boolean deleteGuest(int guestId) {
        String sql = "DELETE FROM guests WHERE guest_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, guestId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE GUEST
    public boolean updateGuest(int guestId, String name, String contact, String idNumber) {
        String sql = "UPDATE guests SET name = ?, contact = ?, id_number = ? WHERE guest_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, contact);
            ps.setString(3, idNumber);
            ps.setInt(4, guestId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}