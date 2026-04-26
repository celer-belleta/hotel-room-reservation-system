package ui;

import dao.RoomDB;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class OccupancyReportFrame extends JFrame {

    public OccupancyReportFrame() {
        // Basic Frame Setup
        setTitle("Occupancy Status Report");
        setSize(500, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(20, 20));

        // Fetch Data from your existing RoomDB logic
        RoomDB roomDB = new RoomDB();
        Map<String, Integer> stats = roomDB.getOccupancyStats();

        int available = stats.getOrDefault("Available", 0);
        int occupied = stats.getOrDefault("Occupied", 0);
        int maintenance = stats.getOrDefault("Maintenance", 0);

        // --- TOP: HEADER ---
        JLabel header = new JLabel("Room Occupancy Overview", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setForeground(new Color(50, 50, 50));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        // --- CENTER: DYNAMIC PIE CHART ---
        PieChartPanel chart = new PieChartPanel(available, occupied, maintenance);
        add(chart, BorderLayout.CENTER);

        // --- BOTTOM: LEGEND ---
        JPanel footerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20));

        footerPanel.add(createLegendItem("Available", available, new Color(40, 167, 69)));
        footerPanel.add(createLegendItem("Occupied", occupied, new Color(220, 53, 69)));
        footerPanel.add(createLegendItem("Maintenance", maintenance, new Color(255, 193, 7)));

        add(footerPanel, BorderLayout.SOUTH);
    }

    // Helper method to create legend items with consistent styling
    private JPanel createLegendItem(String label, int count, Color color) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(label + ": " + count, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(color);

        item.add(lbl, BorderLayout.CENTER);
        return item;
    }

    // --- CUSTOM PIE CHART PANEL WITH DYNAMIC CENTERING ---
    class PieChartPanel extends JPanel {
        private int avail, occ, maint;

        public PieChartPanel(int avail, int occ, int maint) {
            this.avail = avail;
            this.occ = occ;
            this.maint = maint;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Enable Anti-Aliasing for smooth, professional curves
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int total = avail + occ + maint;
            if (total == 0) {
                g2.drawString("No room data available.", getWidth()/2 - 50, getHeight()/2);
                return;
            }

            // DYNAMIC CENTERING: Calculate diameter and position based on current window size
            int diameter = Math.min(getWidth(), getHeight()) - 100;
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;

            // ARC MATH: Convert counts to degrees (360 total)
            int startAngle = 0;
            int arcAvail = (int) Math.round((double) avail / total * 360);
            int arcOcc = (int) Math.round((double) occ / total * 360);
            int arcMaint = 360 - arcAvail - arcOcc; // Remainder to ensure a perfect circle

            // DRAW SEGMENTS
            g2.setColor(new Color(40, 167, 69)); // Available: Green
            g2.fillArc(x, y, diameter, diameter, startAngle, arcAvail);

            startAngle += arcAvail;
            g2.setColor(new Color(220, 53, 69)); // Occupied: Red
            g2.fillArc(x, y, diameter, diameter, startAngle, arcOcc);

            startAngle += arcOcc;
            g2.setColor(new Color(255, 193, 7)); // Maintenance: Yellow/Orange
            g2.fillArc(x, y, diameter, diameter, startAngle, arcMaint);
        }
    }
}