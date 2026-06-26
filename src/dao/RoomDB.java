package dao;

import db.DBConnection;
import model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoomDB {

    public boolean addRoom(String roomNumber, String type, double price, String amenities, String packageType, int maxGuest) {
        String sql = "INSERT INTO rooms (room_number, type, price, status, amenities, package_type, max_guest) VALUES (?, ?, ?, 'Available', ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            ps.setString(2, type);
            ps.setDouble(3, price);
            ps.setString(4, amenities);
            ps.setString(5, packageType);
            ps.setInt(6, maxGuest);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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
                        rs.getString("amenities"),
                        rs.getString("package_type"),
                        rs.getInt("max_guest")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateRoom(int id, String roomNum, String type, double price, String status, String amenities, String packageType, int maxGuest) {
        String query = "UPDATE rooms SET room_number = ?, type = ?, price = ?, status = ?, amenities = ?, package_type = ?, max_guest = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, roomNum);
            pstmt.setString(2, type);
            pstmt.setDouble(3, price);
            pstmt.setString(4, status);
            pstmt.setString(5, amenities);
            pstmt.setString(6, packageType);
            pstmt.setInt(7, maxGuest);
            pstmt.setInt(8, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRoom(int id) {
        String sql = "DELETE FROM rooms WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRoomStatus(int id, String status) {
        String sql = "UPDATE rooms SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Integer> getOccupancyStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Available", 0);
        stats.put("Occupied", 0);
        stats.put("Maintenance", 0);

        String sql = "SELECT " +
                "  CASE " +
                "    WHEN r.status = 'Maintenance' THEN 'Maintenance' " +
                "    WHEN res.room_id IS NOT NULL THEN 'Occupied' " +
                "    ELSE 'Available' " +
                "  END AS category, COUNT(*) AS count " +
                "FROM rooms r " +
                "LEFT JOIN reservations res ON r.id = res.room_id " +
                "  AND CURDATE() >= res.check_in " +
                "  AND CURDATE() < res.check_out " +
                "  AND res.status = 'Checked-In' " +
                "GROUP BY category";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString("category"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}