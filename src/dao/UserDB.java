package dao;

import db.DBConnection;
import model.User;

import java.sql.*;
import java.util.ArrayList;

// UserDB handles all database operations related to Users.
// This includes login, adding users, retrieving users, and deleting users.

public class UserDB {

    // LOGIN METHOD
    public String login(String username, String password) {
        String role = null;
        String sql = "SELECT role FROM users WHERE username=? AND password=?";

        // To close automatically
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    role = rs.getString("role");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return role;
    }

    // ADD USER
    public void addUser(String username, String password, String role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            ps.executeUpdate();
            System.out.println("User added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // GET ALL USERS
    public ArrayList<User> getAllUsers() {
        ArrayList<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // DELETE USER
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("User deleted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // UPDATE USER
    public void updateUser(int id, String username, String password, String role) {
        String sql = "UPDATE users SET username=?, password=?, role=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setInt(4, id);

            ps.executeUpdate();
            System.out.println("User updated!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}