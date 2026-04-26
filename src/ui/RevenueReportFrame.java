package ui;

import dao.PaymentDB;
import javax.swing.*;
import java.awt.*;

public class RevenueReportFrame extends JFrame {
    public RevenueReportFrame() {
        setTitle("Revenue Analysis Report");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // Create the main panel to hold the revenue cards
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        PaymentDB payDB = new PaymentDB();

        double daily = payDB.getTotalRevenue(0);  // 0 means today
        double weekly = payDB.getTotalRevenue(7); // last 7 days
        double monthly = payDB.getTotalRevenue(30); // last 30 days

        // Add the sections to the screen
        mainPanel.add(createRevenueCard("Daily Earnings", daily, new Color(230, 245, 255)));
        mainPanel.add(createRevenueCard("Weekly Earnings", weekly, new Color(210, 235, 255)));
        mainPanel.add(createRevenueCard("Monthly Earnings", monthly, new Color(180, 220, 255)));

        add(new JLabel("Financial Performance", SwingConstants.CENTER), BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        JButton btnClose = new JButton("Close Report");
        btnClose.addActionListener(e -> dispose());
        add(btnClose, BorderLayout.SOUTH);
    }

    private JPanel createRevenueCard(String title, double amount, Color bg) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel lblTitle = new JLabel("  " + title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblAmount = new JLabel("₱" + String.format("%.2f", amount) + "  ");
        lblAmount.setFont(new Font("Arial", Font.BOLD, 22));
        lblAmount.setForeground(new Color(0, 102, 204));

        card.add(lblTitle, BorderLayout.WEST);
        card.add(lblAmount, BorderLayout.EAST);
        return card;
    }
}