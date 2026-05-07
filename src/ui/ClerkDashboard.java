package ui;

import javax.swing.*;
import java.awt.*;

public class ClerkDashboard extends JFrame {

    private JPanel mainContent;
    private CardLayout cardLayout;

    public ClerkDashboard() {
        setTitle("Clerk Dashboard - Aurelia Grand Hotel");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        ClerkSidebar sidebar = new ClerkSidebar(this);
        add(sidebar, BorderLayout.WEST);

        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBackground(Color.WHITE);

        JLabel lblHotelName = new JLabel("AURELIA GRAND HOTEL", SwingConstants.CENTER);
        lblHotelName.setFont(new Font("Arial", Font.BOLD, 50));
        lblHotelName.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        rightContainer.add(lblHotelName, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(Color.WHITE);

        mainContent.add(new AdminSummaryPanel(), "DASHBOARD");
        mainContent.add(new GuestTablePanel(), "GUEST_LIST");
        mainContent.add(new RoomTablePanel("Clerk"), "ROOM_LIST");
        mainContent.add(new ReservationTablePanel(), "RESERVATIONS");

        rightContainer.add(mainContent, BorderLayout.CENTER);
        add(rightContainer, BorderLayout.CENTER);

        cardLayout.show(mainContent, "DASHBOARD");
        setVisible(true);
    }

    public void showPage(String cardName) {
        if (cardName.equals("LOGOUT")) {
            dispose();
            new MainFrame().setVisible(true);
        } else {
            cardLayout.show(mainContent, cardName);
        }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new ClerkDashboard()); }
}