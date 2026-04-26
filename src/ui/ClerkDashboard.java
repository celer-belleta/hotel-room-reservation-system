package ui;

import javax.swing.*;
import java.awt.*;

public class ClerkDashboard extends JFrame {

    public ClerkDashboard() {
        setTitle("Clerk Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // TITLE
        JLabel title = new JLabel("RECEPTION MANAGEMENT", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 50));
        add(title, BorderLayout.NORTH);

        // BUTTON PANEL
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel buttons = new JPanel(new GridLayout(3, 2, 20, 20));

        JButton registerGuestBtn = new JButton("Register New Guest");
        JButton viewRoomsBtn = new JButton("Check Room Availability");
        JButton resBtn = new JButton("Manage Reservations");
        JButton logoutBtn = new JButton("Logout");

        registerGuestBtn.setFont(new Font("Arial", Font.BOLD, 20));
        viewRoomsBtn.setFont(new Font("Arial", Font.BOLD, 20));
        resBtn.setFont(new Font("Arial", Font.BOLD, 20));
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 20));

        buttons.add(registerGuestBtn);
        buttons.add(viewRoomsBtn);
        buttons.add(resBtn);
        buttons.add(logoutBtn);

        panel.add(buttons);
        add(panel, BorderLayout.CENTER);

        // BUTTON ACTIONS
        registerGuestBtn.addActionListener(e -> new AddGuestFrame(this).setVisible(true));

        viewRoomsBtn.addActionListener(e -> new RoomManagementFrame("Clerk").setVisible(true));

        resBtn.addActionListener(e -> new ReservationManagementFrame().setVisible(true));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });
    }
}