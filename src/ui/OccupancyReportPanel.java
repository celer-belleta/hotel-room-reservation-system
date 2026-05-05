package ui;

import dao.RoomDB;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class OccupancyReportPanel extends JPanel {

    public OccupancyReportPanel() {
        setBackground(new Color(245, 246, 247));
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        RoomDB roomDB = new RoomDB();
        Map<String, Integer> stats = roomDB.getOccupancyStats();

        int avail = stats.getOrDefault("Available", 0);
        int occ = stats.getOrDefault("Occupied", 0);
        int maint = stats.getOrDefault("Maintenance", 0);

        JLabel title = new JLabel("Occupancy Status Report");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        add(title, BorderLayout.NORTH);

        AdminSummaryPanel.AnalyticsCard chartCard = new AdminSummaryPanel.AnalyticsCard(
                "Room Occupancy Overview",
                new AdminSummaryPanel.SummaryPieChart(avail, occ, maint)
        );

        add(chartCard, BorderLayout.CENTER);
    }
}