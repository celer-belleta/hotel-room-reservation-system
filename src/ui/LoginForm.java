package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;   
    JButton loginButton;
    JLabel errorLabel;

    public LoginForm() {

        setTitle("Hotel Login System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ===== CENTER LAYOUT =====
        setLayout(new GridBagLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(235, 235, 235));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== TITLE =====
        JLabel title = new JLabel("HOTEL LOGIN SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        // ===== USERNAME =====
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(userLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 22));
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // ===== PASSWORD =====
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 22));
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // ===== LOGIN BUTTON =====
        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        // ===== ERROR LABEL =====
        errorLabel = new JLabel("");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        errorLabel.setForeground(Color.RED);
        gbc.gridy = 4;
        panel.add(errorLabel, gbc);

        add(panel);

        // ===== ACTION =====
        loginButton.addActionListener(e -> login());
    }

    public void login() {

        try {
            Connection conn = DBConnection.getConnection();

            if (conn == null) {
                errorLabel.setText("Database connection failed");
                return;
            }

            String sql = "SELECT role FROM users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, usernameField.getText());
            ps.setString(2, new String(passwordField.getPassword()));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String role = rs.getString("role");

                this.dispose();

                if (role.equalsIgnoreCase("Admin")) {
                    new AdminDashboard().setVisible(true);
                } else if (role.equalsIgnoreCase("Clerk")) {
                    new ClerkDashboard().setVisible(true);
                }

            } else {
                errorLabel.setText("Invalid username or password");
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error occurred");
        }
    }
}