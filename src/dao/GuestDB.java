package dao;

import db.DBConnection;
import model.Guest;
import java.sql.*;
import java.util.ArrayList;

// GuestDB handles all database operations related to Guests.
// This matches the Table: guests (guest_id, name, contact, id_number).

public class GuestDB {

    // ADD GUEST
    public boolean addGuest(String firstName, String lastName, String contact, String idType, String idNumber, String username, String password) {
        String sql = "INSERT INTO guests (first_name, last_name, contact, id_type, id_number, username, password) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, contact);
            ps.setString(4, idType);
            ps.setString(5, idNumber);
            ps.setString(6, username);
            ps.setString(7, password);

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
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("contact"),
                        rs.getString("id_type"),
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
    public boolean updateGuest(int guestId, String firstName, String lastName, String contact, String idType, String idNumber, String username, String password) {
        String sql = "UPDATE guests SET first_name = ?, last_name = ?, contact = ?, id_type = ?, id_number = ?, username = ?, password = ? WHERE guest_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, contact);
            ps.setString(4, idType);
            ps.setString(5, idNumber);
            ps.setString(6, username);
            ps.setString(7, password);
            ps.setInt(8, guestId);

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
                        rs.getString("first_name"), // Split
                        rs.getString("last_name"),  // Split
                        rs.getString("contact"),
                        rs.getString("id_type"),    // Added
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

    //
    public boolean resetGuestPassword(String username, String contact, String idNumber, String newPassword) {
        String sql = "UPDATE guests SET password = ? WHERE username = ? AND contact = ? AND id_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, username);
            ps.setString(3, contact);
            ps.setString(4, idNumber);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //
    public int getGuestIdByUsername(String username) {
        int id = -1;
        String sql = "SELECT guest_id FROM guests WHERE LOWER(TRIM(username)) = LOWER(TRIM(?))";
        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("guest_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    //
    public String getGuestNameById(int id) {
        String name = "";
        String sql = "SELECT first_name, last_name FROM guests WHERE guest_id = ?";
        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("first_name") + " " + rs.getString("last_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }
}