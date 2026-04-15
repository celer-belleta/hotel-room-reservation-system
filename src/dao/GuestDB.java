package dao;

import db.DBConnection;
import model.Guest;
import java.sql.*;
import java.util.ArrayList;

// GuestDB handles all database operations related to Guests.
// This matches the Table: guests (guest_id, name, contact, id_number).

public class GuestDB {

    // ADD GUEST METHOD
    public void addGuest(String name, String contact, String idNumber) {
        String sql = "INSERT INTO guests (name, contact, id_number) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, contact);
            ps.setString(3, idNumber);

            ps.executeUpdate();
            System.out.println("Guest registered successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // GET ALL GUESTS METHOD
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
}