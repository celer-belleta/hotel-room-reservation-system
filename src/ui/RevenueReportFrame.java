package ui;

import dao.PaymentDB;
import javax.swing.*;
import java.awt.*;

public class RevenueReportFrame extends JFrame {
    public RevenueReportFrame() {
        setTitle("Revenue Analysis Report");
        setSize(700, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(20, 20));

        PaymentDB payDB = new PaymentDB();
        double daily = payDB.getTotalRevenue(0);
        double weekly = payDB.getTotalRevenue(7);
        double monthly = payDB.getTotalRevenue(30);

        JLabel header = new JLabel("Financial Performance Summary", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 26));
        header.setForeground(new Color(50, 50, 50));
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        double[] revenues = {daily, weekly, monthly};
        String[] labels = {"Daily", "Weekly", "Monthly"};
        add(new BarChartPanel(revenues, labels), BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.setFocusPainted(false);
        btnClose.setFont(new Font("Arial", Font.BOLD, 14));
        btnClose.addActionListener(e -> dispose());

        JPanel southPanel = new JPanel();
        southPanel.setBackground(Color.WHITE);
        southPanel.add(btnClose);
        add(southPanel, BorderLayout.SOUTH);
    }

    class BarChartPanel extends JPanel {
        private double[] values;
        private String[] labels;

        public BarChartPanel(double[] values, String[] labels) {
            this.values = values;
            this.labels = labels;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 60;
            int chartWidth = width - (2 * padding);
            int chartHeight = height - (2 * padding);

            // Calculate max scale
            double maxRevenue = 0;
            for (double v : values) if (v > maxRevenue) maxRevenue = v;

            // Handle empty state
            if (maxRevenue == 0) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.setFont(new Font("Arial", Font.ITALIC, 18));
                g2.drawString("No revenue data recorded yet.", width/2 - 120, height/2);
                maxRevenue = 1000;
            }

            // Draw Background Grid Lines (Web Style)
            g2.setColor(new Color(230, 230, 230));
            for (int i = 0; i <= 5; i++) {
                int lineY = height - padding - (i * chartHeight / 5);
                g2.drawLine(padding, lineY, width - padding, lineY);
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                g2.drawString("₱" + (int)(maxRevenue / 5 * i), 10, lineY + 5);
                g2.setColor(new Color(230, 230, 230));
            }

            int barWidth = (chartWidth / values.length) - 50;

            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((values[i] / maxRevenue) * chartHeight);
                int x = padding + (i * (chartWidth / values.length)) + 25;
                int y = height - padding - barHeight;

                // Draw Bar
                g2.setColor(new Color(0, 123, 255)); // Vibrant Web Blue
                g2.fillRect(x, y, barWidth, barHeight);

                // Draw Value Label on Top (Bold)
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                String priceLabel = "₱" + String.format("%,.0f", values[i]);
                FontMetrics fm = g2.getFontMetrics();
                int labelX = x + (barWidth - fm.stringWidth(priceLabel)) / 2;
                g2.drawString(priceLabel, labelX, y - 10);

                // Draw Category Label at Bottom
                g2.setFont(new Font("Arial", Font.PLAIN, 14));
                int textX = x + (barWidth - fm.stringWidth(labels[i])) / 2;
                g2.drawString(labels[i], textX, height - padding + 25);
            }

            // Base Line
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(padding, height - padding, width - padding, height - padding);
        }
    }
}