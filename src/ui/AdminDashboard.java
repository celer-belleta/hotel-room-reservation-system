package ui;

import dao.AlertDB;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private JPanel mainContent;
    private CardLayout cardLayout;

    public AdminDashboard() {
        setTitle("Admin Dashboard - Aurelia Grand Hotel");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Sidebar (Left)
        AdminSidebar sidebar = new AdminSidebar(this);
        add(sidebar, BorderLayout.WEST);

        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBackground(Color.WHITE);

        JLabel lblHotelName = new JLabel("AURELIA GRAND HOTEL", SwingConstants.CENTER);
        lblHotelName.setFont(new Font("Arial", Font.BOLD, 50));
        lblHotelName.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        rightContainer.add(lblHotelName, BorderLayout.NORTH);

        // Content Area (CardLayout)
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(Color.WHITE);

        // Adding pages
        mainContent.add(new AdminSummaryPanel(), "DASHBOARD");
        mainContent.add(new UserTablePanel(), "USER_LIST");
        mainContent.add(new GuestTablePanel(), "GUEST_LIST");
        mainContent.add(new RoomTablePanel("Admin"), "ROOM_LIST");
        // mainContent.add(new PackageTablePanel(), "AMENITIES");
        mainContent.add(new ReservationTablePanel(), "RESERVATIONS");
        mainContent.add(new RevenueReportPanel(), "REVENUE_REPORT");
        mainContent.add(new OccupancyReportPanel(), "OCCUPANCY_REPORT");

        rightContainer.add(mainContent, BorderLayout.CENTER);
        add(rightContainer, BorderLayout.CENTER);

        // Show Dashboard Summary by default
        cardLayout.show(mainContent, "DASHBOARD");

        setVisible(true);

        AlertDB alertDao = new AlertDB();
        String dailyAlerts = alertDao.getDailySummary();

        if (!dailyAlerts.equals("No urgent alerts for today!")) {
            JOptionPane.showMessageDialog(this, dailyAlerts, "Daily Updates", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showPage(String cardName) {
        if (cardName.equals("LOGOUT")) {
            dispose();
            new MainFrame().setVisible(true);
        } else if (cardName.equals("DASHBOARD")) {
            mainContent.remove(0);

            mainContent.add(new AdminSummaryPanel(), "DASHBOARD", 0);

            mainContent.revalidate();
            mainContent.repaint();
            cardLayout.show(mainContent, "DASHBOARD");
        } else {
            cardLayout.show(mainContent, cardName);
        }
    }
    private JPanel createPlaceholder(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.add(new JLabel(text));
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard());
    }
}