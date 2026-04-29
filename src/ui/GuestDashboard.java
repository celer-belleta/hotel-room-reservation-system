package ui;

import javax.swing.*;
import java.awt.*;

public class GuestDashboard extends JFrame {

    private String currentUsername;

    public GuestDashboard(String username) {
        this.currentUsername = username;

        // FRAME SETTINGS
        setTitle("Guest Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // TITLE
        JLabel title = new JLabel("", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 50));
        add(title, BorderLayout.NORTH);

        // CENTER PANEL (BUTTONS)
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(4, 2, 20, 20));

        // GUEST SPECIFIC BUTTONS
        JButton bookRoomBtn = new JButton("Book a Reservation");
        JButton viewMyBookingsBtn = new JButton("My Bookings (View/Cancel)");
        JButton availableRoomsBtn = new JButton("Check Room Availability");
        JButton editProfileBtn = new JButton("Update Contact Info");
        JButton logoutBtn = new JButton("Logout");

        // FONT SETTINGS
        Font btnFont = new Font("Arial", Font.BOLD, 20);
        bookRoomBtn.setFont(btnFont);
        viewMyBookingsBtn.setFont(btnFont);
        availableRoomsBtn.setFont(btnFont);
        editProfileBtn.setFont(btnFont);
        logoutBtn.setFont(btnFont);

        buttons.add(bookRoomBtn);
        buttons.add(viewMyBookingsBtn);
        buttons.add(availableRoomsBtn);
        buttons.add(editProfileBtn);
        buttons.add(logoutBtn);

        panel.add(buttons);
        add(panel, BorderLayout.CENTER);

        // BUTTON ACTIONS

        // This opens the room management but as a Guest (so they can't edit rooms)

        //availableRoomsBtn.addActionListener(e -> new RoomManagementFrame("Guest").setVisible(true));

        // This would be a new frame specifically for the logged-in user
        viewMyBookingsBtn.addActionListener(e -> {
            // new MyBookingsFrame(currentUsername).setVisible(true);
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new MainFrame().setVisible(true);
        });

        setVisible(true);
    }
}