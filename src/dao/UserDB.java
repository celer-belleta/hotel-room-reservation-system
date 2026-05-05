package dao;

import db.DBConnection;
import model.User;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

// UserDB handles all database operations related to Users.
// This includes login, adding users, retrieving users, and deleting users.

public class UserDB {

    // LOGIN
    public String login(String username, String password) {
        String role = null;
        String sql = "SELECT role FROM users WHERE username=? AND password=?";

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
    public boolean addUser(String firstName, String lastName, String username, String password, String role) {
        String sql = "INSERT INTO users (first_name, last_name, username, password, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setString(5, role);

            ps.executeUpdate();
            System.out.println("User added successfully!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(null,
                        "The username is already taken. Please try a different one.",
                        "Registration Error",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "An unexpected database error occurred.",
                        "System Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }

    // GET ALL USERS
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // DELETE USER
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE USER
    public boolean updateUser(int id, String firstName, String lastName, String username, String password, String role) {
        String query;
        boolean isPasswordEmpty = password == null || password.trim().isEmpty();

        if (isPasswordEmpty) {
            query = "UPDATE users SET first_name = ?, last_name = ?, username = ?, role = ? WHERE id = ?";
        } else {
            query = "UPDATE users SET first_name = ?, last_name = ?, username = ?, password = ?, role = ? WHERE id = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, username);

            if (isPasswordEmpty) {
                pstmt.setString(4, role);
                pstmt.setInt(5, id);
            } else {
                pstmt.setString(4, password);
                pstmt.setString(5, role);
                pstmt.setInt(6, id);
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // RESET PASSWORD
    public boolean resetPassword(String username, String firstName, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ? AND first_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, username);
            ps.setString(3, firstName);

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}