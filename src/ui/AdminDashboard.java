package ui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {

        // FRAME SETTINGS
        setTitle("Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // TITLE
        JLabel title = new JLabel("OPERATIONS MANAGEMENT", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 50));

        add(title, BorderLayout.NORTH);

        // CENTER PANEL (BUTTONS)
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(4, 2, 20, 20));

        JButton viewUsersBtn = new JButton("View Users");
        JButton addUserBtn = new JButton("Add User");
        JButton viewRoomsBtn = new JButton("Manage Rooms");
        JButton viewGuestsBtn = new JButton("Manage Guests");
        JButton resBtn = new JButton("Manage Reservations");
        JButton occupancyBtn = new JButton("Occupancy Report");
        JButton revenueBtn = new JButton("Revenue Report");
        JButton logoutBtn = new JButton("Logout");

        viewUsersBtn.setFont(new Font("Arial", Font.BOLD, 20));
        addUserBtn.setFont(new Font("Arial", Font.BOLD, 20));
        viewRoomsBtn.setFont(new Font("Arial", Font.BOLD, 20));
        viewGuestsBtn.setFont(new Font("Arial", Font.BOLD, 20));
        resBtn.setFont(new Font("Arial", Font.BOLD, 20));
        occupancyBtn.setFont(new Font("Arial", Font.BOLD, 20));
        revenueBtn.setFont(new Font("Arial", Font.BOLD, 20));
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 20));

        // 4 rows, 2 columns each
        buttons.add(viewUsersBtn);
        buttons.add(addUserBtn);
        buttons.add(viewRoomsBtn);
        buttons.add(viewGuestsBtn);
        buttons.add(resBtn);
        buttons.add(occupancyBtn);
        buttons.add(revenueBtn);
        buttons.add(logoutBtn);

        panel.add(buttons);
        add(panel, BorderLayout.CENTER);

        // BUTTON ACTIONS
        viewUsersBtn.addActionListener(e -> new UserTableFrame());

        addUserBtn.addActionListener(e -> new AddUserFrame(null).setVisible(true));

        viewRoomsBtn.addActionListener(e -> new RoomManagementFrame("Admin").setVisible(true));

        viewGuestsBtn.addActionListener(e -> new GuestManagementFrame().setVisible(true));

        resBtn.addActionListener(e -> new ReservationManagementFrame().setVisible(true));

        // occupancyBtn.addActionListener(e -> new OccupancyReportFrame().setVisible(true));

        //revenueBtn.addActionListener(e -> new RevenueReportFrame().setVisible(true));

        logoutBtn.addActionListener(e -> {
            dispose();
            new MainFrame().setVisible(true);
        });

        setVisible(true);
    }

    /*

    // NOTIFICATION PANEL
    private JPanel createNotificationPanel() {
        JPanel panel = new JPanel();
        // Layout: Stack items vertically
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Today's Alerts & Notifications"));
        panel.setBackground(new Color(255, 250, 240)); // A light cream color so it stands out

        dao.AlertDB alertDB = new dao.AlertDB();

        // Combine our two alert types into one list
        java.util.List<String> allAlerts = new java.util.ArrayList<>();
        allAlerts.addAll(alertDB.getCheckOutAlerts());
        allAlerts.addAll(alertDB.getMaintenanceAlerts());

        if (allAlerts.isEmpty()) {
            JLabel clear = new JLabel("  ✅ All clear! No urgent alerts for today.");
            clear.setFont(new Font("Arial", Font.ITALIC, 14));
            panel.add(clear);
        } else {
            for (String msg : allAlerts) {
                JLabel label = new JLabel("  • " + msg);
                label.setFont(new Font("Arial", Font.PLAIN, 15));
                label.setForeground(new Color(180, 0, 0)); // Red for urgency
                panel.add(label);
                panel.add(Box.createRigidArea(new Dimension(0, 5))); // Add small gap between alerts
            }
        }
        return panel;
    }


    // ADD THE NOTIFICATION PANEL TO THE RIGHT SIDE
    add(createNotificationPanel(), BorderLayout.EAST);
     */
}