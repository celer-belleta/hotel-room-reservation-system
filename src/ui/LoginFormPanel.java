package ui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class LoginFormPanel extends JPanel {

    JTextField txtUsername;
    JPasswordField txtPassword;
    JButton btnLogin;
    JLabel lblError;
    private JFrame parentFrame;

    public LoginFormPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;

        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        // BOX
        JPanel contentWrap = new JPanel(new GridBagLayout());
        contentWrap.setBackground(Color.WHITE);
        contentWrap.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        GridBagConstraints wrapGbc = new GridBagConstraints();
        wrapGbc.insets = new Insets(5, 20, 5, 20);
        wrapGbc.fill = GridBagConstraints.NONE;

        JLabel title = new JLabel("LOGIN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        wrapGbc.gridx = 0;
        wrapGbc.gridy = 0;
        wrapGbc.gridwidth = 2;
        wrapGbc.anchor = GridBagConstraints.CENTER;
        wrapGbc.insets = new Insets(20, 20, 20, 20);
        contentWrap.add(title, wrapGbc);

        wrapGbc.insets = new Insets(5, 20, 5, 20);
        wrapGbc.gridwidth = 1;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 18));
        wrapGbc.gridx = 0;
        wrapGbc.gridy = 1;
        wrapGbc.anchor = GridBagConstraints.LINE_END;
        contentWrap.add(userLabel, wrapGbc);

        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 16));
        txtUsername.setPreferredSize(new Dimension(250, 30));
        wrapGbc.gridx = 1;
        wrapGbc.anchor = GridBagConstraints.LINE_START;
        contentWrap.add(txtUsername, wrapGbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 18));
        wrapGbc.gridx = 0;
        wrapGbc.gridy = 2;
        wrapGbc.anchor = GridBagConstraints.LINE_END;
        contentWrap.add(passLabel, wrapGbc);

        // PASSWORD
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPassword.setPreferredSize(new Dimension(250, 30));
        wrapGbc.gridx = 1;
        wrapGbc.anchor = GridBagConstraints.LINE_START;
        contentWrap.add(txtPassword, wrapGbc);

        // LOGIN BUTTON
        btnLogin = new JButton("LOGIN");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnLogin.setFocusPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(200, 40));
        wrapGbc.gridx = 0;
        wrapGbc.gridy = 3;
        wrapGbc.gridwidth = 2;
        wrapGbc.anchor = GridBagConstraints.CENTER;
        wrapGbc.insets = new Insets(15, 20, 5, 20);
        contentWrap.add(btnLogin, wrapGbc);

        // FORGOT PASSWORD
        JLabel lblForgot = new JLabel("Forgot password?", SwingConstants.CENTER);
        lblForgot.setFont(new Font("Arial", Font.BOLD, 14));
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        wrapGbc.gridy = 4;
        wrapGbc.insets = new Insets(5, 20, 5, 20);
        contentWrap.add(lblForgot, wrapGbc);

        // SIGN UP
        JLabel lblSignUp = new JLabel("Dont have an account? Sign up", SwingConstants.CENTER);
        lblSignUp.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        wrapGbc.gridy = 5;
        contentWrap.add(lblSignUp, wrapGbc);

        // ERROR LABEL
        lblError = new JLabel("", SwingConstants.CENTER);
        lblError.setForeground(Color.RED);
        wrapGbc.gridy = 6;
        wrapGbc.insets = new Insets(5, 20, 20, 20);
        contentWrap.add(lblError, wrapGbc);

        add(contentWrap);

        // BUTTON ACTIONS
        btnLogin.addActionListener(e -> login());

        lblSignUp.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (parentFrame instanceof MainFrame) {
                    ((MainFrame) parentFrame).showCard("SIGNUP_PAGE");
                }
            }
        });

        lblForgot.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (parentFrame instanceof MainFrame) {
                    ((MainFrame) parentFrame).showCard("FORGOT_PASSWORD_PAGE");
                }
            }
        });
    }

    public void login() {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                lblError.setText("Database connection failed");
                return;
            }

            String user = txtUsername.getText();
            String pass = new String(txtPassword.getPassword());

            // Try Staff Login (Admins/Clerks)
            String staffSql = "SELECT role FROM users WHERE username=? AND password=?";
            try (PreparedStatement ps = conn.prepareStatement(staffSql)) {
                ps.setString(1, user);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");
                    parentFrame.dispose();

                    if (role.equalsIgnoreCase("Admin")) {
                        new AdminDashboard().setVisible(true);
                    } else if (role.equalsIgnoreCase("Clerk")) {
                        new ClerkDashboard().setVisible(true);
                    }
                    return; // Exit if staff found
                }
            }

            // Try Guest Login (If no staff found)
            String guestSql = "SELECT name FROM guests WHERE username=? AND password=?";
            try (PreparedStatement ps = conn.prepareStatement(guestSql)) {
                ps.setString(1, user);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    parentFrame.dispose();
                    new GuestDashboard(user).setVisible(true);
                } else {
                    lblError.setText("Invalid username or password");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Error occurred");
        }
    }
}