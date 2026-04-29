package dao;

import db.DBConnection;
import model.Guest;
import java.sql.*;
import java.util.ArrayList;

// GuestDB handles all database operations related to Guests.
// This matches the Table: guests (guest_id, name, contact, id_number).

public class GuestDB {

    // ADD GUEST
    public boolean addGuest(String name, String contact, String idNumber, String username, String password) {
        String sql = "INSERT INTO guests (name, contact, id_number, username, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, contact);
            ps.setString(3, idNumber);
            ps.setString(4, username);
            ps.setString(5, password);

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
                        rs.getString("id_number"),
                        rs.getString("username"),
                        rs.getString("password")
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
    public boolean updateGuest(int guestId, String name, String contact, String idNumber, String username, String password) {
        String sql = "UPDATE guests SET name = ?, contact = ?, id_number = ?, username = ?, password = ? WHERE guest_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, contact);
            ps.setString(3, idNumber);
            ps.setString(4, username);
            ps.setString(5, password);
            ps.setInt(6, guestId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //
    public boolean updateGuestPassword(int guestId, String newPassword) {
        String sql = "UPDATE guests SET password = ? WHERE guest_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, guestId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //
    public Guest verifyGuestLogin(String username, String password) {
        String sql = "SELECT * FROM guests WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Guest(
                        rs.getInt("guest_id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("id_number"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Login failed
    }
}