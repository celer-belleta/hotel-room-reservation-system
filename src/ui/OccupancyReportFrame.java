package ui;

import dao.RoomDB;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class OccupancyReportFrame extends JFrame {
    public OccupancyReportFrame() {
        setTitle("Occupancy Status Report");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        RoomDB roomDB = new RoomDB();
        Map<String, Integer> stats = roomDB.getOccupancyStats();

        // Get counts or default to 0 if none exist yet
        int available = stats.getOrDefault("Available", 0);
        int occupied = stats.getOrDefault("Occupied", 0);
        int maintenance = stats.getOrDefault("Maintenance", 0);

        // Create labels with color coding
        JLabel lblAvailable = new JLabel("Available Rooms: " + available);
        lblAvailable.setForeground(new Color(0, 153, 0)); // Green
        lblAvailable.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel lblOccupied = new JLabel("Occupied Rooms: " + occupied);
        lblOccupied.setForeground(Color.RED);
        lblOccupied.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel lblMaint = new JLabel("Maintenance: " + maintenance);
        lblMaint.setForeground(Color.ORANGE);
        lblMaint.setFont(new Font("Arial", Font.BOLD, 18));

        statsPanel.add(lblAvailable);
        statsPanel.add(lblOccupied);
        statsPanel.add(lblMaint);

        add(new JLabel("Current Hotel Status", SwingConstants.CENTER), BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        add(btnClose, BorderLayout.SOUTH);
    }
}