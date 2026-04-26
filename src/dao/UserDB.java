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
    public boolean addUser(String username, String password, String role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

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
    public boolean updateUser(int id, String username, String password, String role) {
        String sql = "UPDATE users SET username=?, password=?, role=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setInt(4, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // REGISTER GUEST
    public boolean registerGuest(String username, String password, String fullName, String contact) {

        String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'Guest')";
        String guestSql = "INSERT INTO guests (name, contact, user_id) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert into users table
            PreparedStatement psUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, username);
            psUser.setString(2, password);
            psUser.executeUpdate();

            // Get the ID of the user we just created
            ResultSet rs = psUser.getGeneratedKeys();
            int newUserId = 0;
            if (rs.next()) {
                newUserId = rs.getInt(1);
            }

            // Insert into guests table using that new ID
            PreparedStatement psGuest = conn.prepareStatement(guestSql);
            psGuest.setString(1, fullName);
            psGuest.setString(2, contact);
            psGuest.setInt(3, newUserId);
            psGuest.executeUpdate();

            conn.commit(); // Save both changes
            return true;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {} // Undo changes on error
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    // RESET PASSWORD
    public boolean resetPassword(String username, String contact, String newPassword) {
        String sql = "UPDATE users " +
                "JOIN guests ON users.id = guests.user_id " +
                "SET users.password = ? " +
                "WHERE users.username = ? AND guests.contact = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, username);
            ps.setString(3, contact);

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}