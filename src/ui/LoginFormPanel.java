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
        Color matchedNavy = new Color(44, 62, 80);

        setBackground(matchedNavy);
        setLayout(new GridBagLayout());

        // BOX
        JPanel contentWrap = new JPanel(new GridBagLayout());
        contentWrap.setBackground(Color.WHITE);
        contentWrap.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        contentWrap.setPreferredSize(new Dimension(500, 450));

        GridBagConstraints wrapGbc = new GridBagConstraints();
        wrapGbc.insets = new Insets(5, 20, 5, 20);
        wrapGbc.fill = GridBagConstraints.NONE;

        JLabel title = new JLabel("LOGIN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(matchedNavy); // Navy Title
        wrapGbc.gridy = 0; wrapGbc.gridwidth = 2;
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
        btnLogin.setBackground(matchedNavy);
        btnLogin.setForeground(Color.WHITE);

        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
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

            String staffSql = "SELECT id, first_name, last_name, role FROM users WHERE username=? AND password=?";
            try (PreparedStatement ps = conn.prepareStatement(staffSql)) {
                ps.setString(1, user);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    model.User loggedInUser = new model.User();
                    loggedInUser.setId(rs.getInt("id"));
                    loggedInUser.setFirstName(rs.getString("first_name"));
                    loggedInUser.setLastName(rs.getString("last_name"));
                    loggedInUser.setRole(rs.getString("role"));

                    Session.login(loggedInUser);

                    String role = rs.getString("role");
                    parentFrame.dispose();

                    if (role.equalsIgnoreCase("Admin")) {
                        new AdminDashboard().setVisible(true);
                    } else if (role.equalsIgnoreCase("Clerk")) {
                        new ClerkDashboard().setVisible(true);
                    }
                    return;
                }
            }

            String guestSql = "SELECT guest_id FROM guests WHERE username=? AND password=?";
            try (PreparedStatement ps = conn.prepareStatement(guestSql)) {
                ps.setString(1, user);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int id = rs.getInt("guest_id");
                    parentFrame.dispose();
                    new GuestDashboard(String.valueOf(id)).setVisible(true);
                } else {
                    lblError.setText("Invalid username or password");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Error occurred");
        }
    }}