package ui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {

        // =========================
        // FRAME SETTINGS
        // =========================
        setTitle("Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // =========================
        // TITLE
        // =========================
        JLabel title = new JLabel("ADMIN DASHBOARD", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 60));

        add(title, BorderLayout.NORTH);

        // =========================
        // CENTER PANEL (BUTTONS)
        // =========================
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        JPanel buttons = new JPanel();
        // Changed to 5 rows to fit the restored button
        buttons.setLayout(new GridLayout(5, 1, 20, 20));

        JButton viewUsersBtn = new JButton("View Users");
        JButton addUserBtn = new JButton("Add User");
        JButton viewRoomsBtn = new JButton("Manage Rooms");
        JButton logoutBtn = new JButton("Logout");

        viewUsersBtn.setFont(new Font("Arial", Font.BOLD, 20));
        addUserBtn.setFont(new Font("Arial", Font.BOLD, 20));
        viewRoomsBtn.setFont(new Font("Arial", Font.BOLD, 20));
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 20));

        buttons.add(viewUsersBtn);
        buttons.add(addUserBtn);
        buttons.add(viewRoomsBtn);
        buttons.add(logoutBtn);

        panel.add(buttons);
        add(panel, BorderLayout.CENTER);

        // =========================
        // BUTTON ACTIONS
        // =========================

        viewUsersBtn.addActionListener(e -> new UserTableFrame());

        addUserBtn.addActionListener(e -> new AddUserFrame(null).setVisible(true));

        viewRoomsBtn.addActionListener(e -> new RoomManagementFrame().setVisible(true));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        setVisible(true);
    }
}