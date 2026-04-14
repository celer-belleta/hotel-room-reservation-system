package dao;

import db.DBConnection;
import model.Room;

import java.sql.*;
import java.util.ArrayList;

// RoomDB handles all database operations related to Rooms.
// This includes adding rooms, retrieving availability, and updating room status.

public class RoomDB {

    // ADD ROOM METHOD
    public void addRoom(String roomNumber, String type, double price, String amenities) {
        String sql = "INSERT INTO rooms (room_number, type, price, status, amenities) VALUES (?, ?, ?, 'Available', ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roomNumber);
            ps.setString(2, type);
            ps.setDouble(3, price);
            ps.setString(4, amenities);

            ps.executeUpdate();
            System.out.println("Room added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // GET ALL ROOMS METHOD
    public ArrayList<Room> getAllRooms() {
        ArrayList<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getString("type"),
                        rs.getDouble("price"),
                        rs.getString("status"),
                        rs.getString("amenities")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE STATUS METHOD
    public void updateRoomStatus(int id, String status) {
        String sql = "UPDATE rooms SET status=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);

            ps.executeUpdate();
            System.out.println("Room status updated!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // UPDATE PRICE METHOD
    public void updateRoomPrice(String type, double newPrice) {
        String sql = "UPDATE rooms SET price=? WHERE type=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newPrice);
            ps.setString(2, type);

            ps.executeUpdate();
            System.out.println("Room updated!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}