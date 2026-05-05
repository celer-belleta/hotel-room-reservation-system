package ui;

import dao.RoomDB;
import dao.PaymentDB;
import dao.ReservationDB;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class AdminSummaryPanel extends JPanel {

    public AdminSummaryPanel() {
        setBackground(new Color(245, 246, 247));
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 20, 0));
        statsRow.setOpaque(false);

        RoomDB roomDB = new RoomDB();
        PaymentDB payDB = new PaymentDB();
        ReservationDB resDB = new ReservationDB();

        Map<String, Integer> roomStats = roomDB.getOccupancyStats();
        int avail = roomStats.getOrDefault("Available", 0);
        int occ = roomStats.getOrDefault("Occupied", 0);
        int maint = roomStats.getOrDefault("Maintenance", 0);

        int totalRooms = avail + occ + maint;
        int occRate = (totalRooms > 0) ? (occ * 100 / totalRooms) : 0;

        int newReservationsToday = resDB.getNewReservationsToday();

        statsRow.add(createStatBox("NEW RESERVATIONS", String.valueOf(newReservationsToday), "Confirmed today"));
        statsRow.add(createStatBox("ROOM AVAILABILITY", String.valueOf(avail), "Ready for check-in"));
        statsRow.add(createStatBox("OCCUPANCY RATE", occRate + "%", "Current hotel load"));
        statsRow.add(createStatBox("TOTAL REVENUE", "P " + String.format("%,.0f", payDB.getTotalRevenue(0)), "Today's earnings"));

        add(statsRow, BorderLayout.NORTH);

        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 25, 0));
        chartsRow.setOpaque(false);

        chartsRow.add(new AnalyticsCard("Occupancy Distribution", new SummaryPieChart(avail, occ, maint)));

        double[] revenues = {payDB.getTotalRevenue(0), payDB.getTotalRevenue(7), payDB.getTotalRevenue(30)};
        String[] labels = {"Daily", "Weekly", "Monthly"};
        chartsRow.add(new AnalyticsCard("Financial Performance", new SummaryBarChart(revenues, labels)));

        add(chartsRow, BorderLayout.CENTER);
    }

    private JPanel createStatBox(String title, String value, String subtext) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 28));

        JLabel lblSub = new JLabel(subtext);
        lblSub.setFont(new Font("Arial", Font.ITALIC, 11));
        lblSub.setForeground(new Color(40, 167, 69));

        box.add(lblTitle);
        box.add(Box.createRigidArea(new Dimension(0, 10)));
        box.add(lblValue);
        box.add(Box.createRigidArea(new Dimension(0, 5)));
        box.add(lblSub);

        return box;
    }

    static class AnalyticsCard extends JPanel {
        public AnalyticsCard(String title, JPanel chart) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                    new EmptyBorder(20, 20, 20, 20)
            ));

            JLabel lbl = new JLabel(title);
            lbl.setFont(new Font("Arial", Font.BOLD, 18));
            lbl.setBorder(new EmptyBorder(0, 0, 15, 0));

            add(lbl, BorderLayout.NORTH);
            add(chart, BorderLayout.CENTER);
        }
    }

    static class SummaryPieChart extends JPanel {
        private final int avail, occ, maint;

        public SummaryPieChart(int avail, int occ, int maint) {
            this.avail = avail;
            this.occ = occ;
            this.maint = maint;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int total = avail + occ + maint;
            if (total == 0) {
                g2.drawString("No data available", getWidth() / 2 - 40, getHeight() / 2);
                return;
            }

            int diameter = Math.min(getWidth(), getHeight()) - 100;
            int x = (getWidth() - diameter) / 2 - 50;
            int y = (getHeight() - diameter) / 2;

            int arcAvail = (int) Math.round((double) avail / total * 360);
            int arcOcc = (int) Math.round((double) occ / total * 360);

            double pAvail = (double) avail / total * 100;
            double pOcc = (double) occ / total * 100;
            double pMaint = (double) maint / total * 100;

            int startAngle = 0;

            g2.setColor(new Color(40, 167, 69));
            g2.fillArc(x, y, diameter, diameter, startAngle, arcAvail);
            drawLegendItem(g2, "Available (" + String.format("%.1f", pAvail) + "%)", new Color(40, 167, 69), 1);
            startAngle += arcAvail;

            g2.setColor(new Color(220, 53, 69));
            g2.fillArc(x, y, diameter, diameter, startAngle, arcOcc);
            drawLegendItem(g2, "Occupied (" + String.format("%.1f", pOcc) + "%)", new Color(220, 53, 69), 2);
            startAngle += arcOcc;

            g2.setColor(new Color(255, 193, 7));
            g2.fillArc(x, y, diameter, diameter, startAngle, 360 - startAngle);
            drawLegendItem(g2, "Maintenance (" + String.format("%.1f", pMaint) + "%)", new Color(255, 193, 7), 3);
        }

        private void drawLegendItem(Graphics2D g2, String text, Color color, int position) {
            int legendX = getWidth() - 160;
            int legendY = (getHeight() / 2) - 40 + (position * 25);

            g2.setColor(color);
            g2.fillRect(legendX, legendY, 12, 12); // Color box

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.drawString(text, legendX + 20, legendY + 11); // Text
        }
    }

    static class SummaryBarChart extends JPanel {
        private final double[] values;
        private final String[] labels;

        public SummaryBarChart(double[] values, String[] labels) {
            this.values = values;
            this.labels = labels;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            FontMetrics fm = g2.getFontMetrics();

            int leftPadding = 80;
            int bottomPadding = 40;
            int topPadding = 30;
            int rightPadding = 30;

            int width = getWidth() - leftPadding - rightPadding;
            int height = getHeight() - bottomPadding - topPadding;

            double maxVal = 0;
            for (double v : values) if (v > maxVal) maxVal = v;

            double ceiling = (maxVal < 1000) ? 1000 : Math.ceil(maxVal / 5000) * 5000;

            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= 5; i++) {
                int lineY = (getHeight() - bottomPadding) - (i * height / 5);
                double labelValue = (ceiling / 5) * i;

                g2.setColor(new Color(235, 235, 235));
                g2.drawLine(leftPadding, lineY, getWidth() - rightPadding, lineY);

                g2.setColor(Color.GRAY);
                String labelText = "P " + String.format("%,.0f", labelValue);
                g2.drawString(labelText, leftPadding - fm.stringWidth(labelText) - 10, lineY + 5);
            }

            int barWidth = (width / values.length) - 40;
            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((values[i] / ceiling) * height);
                int x = leftPadding + (i * (width / values.length)) + 20;
                int y = (getHeight() - bottomPadding) - barHeight;

                g2.setColor(new Color(0, 123, 255));
                g2.fillRect(x, y, barWidth, barHeight);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                String valStr = "P " + String.format("%,.0f", values[i]);
                g2.drawString(valStr, x + (barWidth / 2) - (g2.getFontMetrics().stringWidth(valStr) / 2), y - 5);

                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                g2.drawString(labels[i], x + (barWidth / 2) - (fm.stringWidth(labels[i]) / 2), getHeight() - 15);
            }

            g2.setColor(Color.BLACK);
            g2.drawLine(leftPadding, topPadding, leftPadding, getHeight() - bottomPadding); // Y Axis
            g2.drawLine(leftPadding, getHeight() - bottomPadding, getWidth() - rightPadding, getHeight() - bottomPadding); // X Axis
        }
    }}