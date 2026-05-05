package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GuestDashboard extends JFrame {
    private JPanel mainContent;
    private CardLayout cardLayout;
    private int currentGuestId;

    public GuestDashboard(String guestIdStr) {
        try {
            this.currentGuestId = Integer.parseInt(guestIdStr.trim());
        } catch (NumberFormatException e) {
            this.currentGuestId = 0;
        }

        setTitle("Guest Dashboard - Aurelia Grand Hotel");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        ViewBookingsPanel bookingsPanel = new ViewBookingsPanel(currentGuestId);

        RoomBookingPanel bookingPanel = new RoomBookingPanel(currentGuestId, bookingsPanel);

        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 35));
        navBar.setBackground(Color.WHITE);
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        addNavMenu(navBar, "ABOUT", "ABOUT_PAGE", null);
        addNavMenu(navBar, "ROOMS", "VIEW_ROOMS", null);
        addNavMenu(navBar, "BOOK NOW!", "BOOK_ROOM", null);
        addNavMenu(navBar, "VIEW BOOKINGS", "MY_BOOKINGS", bookingsPanel);

        addNavMenu(navBar, "LOGOUT", "LOGOUT", null);

        add(navBar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);

        mainContent.add(new AboutPanel(), "ABOUT_PAGE");
        mainContent.add(new RoomPanel(), "VIEW_ROOMS");
        mainContent.add(bookingsPanel, "MY_BOOKINGS");
        mainContent.add(bookingPanel, "BOOK_ROOM");

        add(mainContent, BorderLayout.CENTER);
        cardLayout.show(mainContent, "ABOUT_PAGE");
        setVisible(true);
    }

    private void addNavMenu(JPanel parent, String text, String cardName, ViewBookingsPanel vbp) {
        JLabel menuLabel = new JLabel(text);
        menuLabel.setFont(new Font("Arial", Font.BOLD, 18));
        menuLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cardName.equals("LOGOUT")) {
                    dispose();
                    new MainFrame().setVisible(true);
                } else {
                    cardLayout.show(mainContent, cardName);

                    if (vbp != null) {
                        vbp.refreshData();
                    }
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) { menuLabel.setForeground(new Color(197, 160, 89)); }
            @Override
            public void mouseExited(MouseEvent e) { menuLabel.setForeground(Color.BLACK); }
        });
        parent.add(menuLabel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuestDashboard("1"));
    }
}