package ui;

import dao.PaymentDB;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RevenueReportPanel extends JPanel {
    public RevenueReportPanel() {
        setBackground(new Color(245, 246, 247));
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        PaymentDB payDB = new PaymentDB();
        double daily = payDB.getTotalRevenue(0);
        double weekly = payDB.getTotalRevenue(7);
        double monthly = payDB.getTotalRevenue(30);

        JLabel title = new JLabel("Revenue Analysis Report");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        add(title, BorderLayout.NORTH);

        double[] revenues = {daily, weekly, monthly};
        String[] labels = {"Daily", "Weekly", "Monthly"};

        AdminSummaryPanel.AnalyticsCard chartCard = new AdminSummaryPanel.AnalyticsCard(
                "Financial Performance Summary",
                new AdminSummaryPanel.SummaryBarChart(revenues, labels)
        );

        add(chartCard, BorderLayout.CENTER);
    }
}