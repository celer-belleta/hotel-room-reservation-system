package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ViewBookingsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private int currentGuestId;
    private String displayName = "Guest";
    private final Color AURELIA_GOLD = new Color(197, 160, 89);
    private final Color NAVY_BLUE = new Color(44, 62, 80);

    public ViewBookingsPanel(int guestId) {
        this.currentGuestId = guestId;
        setLayout(new BorderLayout());
        setBackground(NAVY_BLUE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        fetchGuestDisplayName();

        // HEADER SECTION
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(NAVY_BLUE);

        JLabel title = new JLabel(displayName + "'s Reservations", SwingConstants.LEFT);
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(AURELIA_GOLD);
        headerPanel.add(title, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Room #", "Room Type", "Package Type", "Nights", "Total", "Paid", "Balance", "Status", "Time In", "Time Out"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        table.setSelectionBackground(AURELIA_GOLD);
        table.setSelectionForeground(Color.WHITE);

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.GRAY));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        refreshData();

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(AURELIA_GOLD, 1));
        add(scroll, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(NAVY_BLUE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel noteLabel = new JLabel("<html><i style='color: white;'>Note: A 20% downpayment is required to officially confirm your reservation.</i></html>");
        noteLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerPanel.add(noteLabel, BorderLayout.WEST);

        JButton payBtn = new JButton("SETTLE DOWNPAYMENT");
        payBtn.setBackground(AURELIA_GOLD);
        payBtn.setForeground(Color.WHITE);
        payBtn.setFont(new Font("Arial", Font.BOLD, 13));
        payBtn.setFocusPainted(false);
        payBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        payBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String status = (String) model.getValueAt(row, 8);
                double balance = Double.parseDouble(model.getValueAt(row, 7).toString());

                if (balance > 0.01 && !status.equalsIgnoreCase("Cancelled") && !status.equalsIgnoreCase("Checked-Out")) {
                    showPaymentDialog(row);
                } else if (balance <= 0.01) {
                    JOptionPane.showMessageDialog(this,
                            "This reservation is already fully paid.",
                            "Payment Complete", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "This reservation is already " + status + ".");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a reservation to proceed with payment.");
            }
        });
        footerPanel.add(payBtn, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void showPaymentDialog(int row) {
        int resId = (int) model.getValueAt(row, 0);
        double total = Double.parseDouble(model.getValueAt(row, 5).toString());
        double deposit = total * 0.20;

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblHeader = new JLabel("AURELIA SECURE CHECKOUT");
        lblHeader.setFont(new Font("Serif", Font.BOLD, 18));
        lblHeader.setForeground(AURELIA_GOLD);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        form.add(lblHeader, gbc);

        JLabel lblRes = new JLabel("Processing Downpayment for Reservation #" + resId);
        lblRes.setFont(new Font("Arial", Font.ITALIC, 12));
        gbc.gridy = 1;
        form.add(lblRes, gbc);

        JLabel lblAmount = new JLabel("Amount Due: PHP " + String.format("%,.2f", deposit));
        lblAmount.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 2;
        form.add(lblAmount, gbc);

        gbc.gridwidth = 1; gbc.gridy = 3;
        form.add(new JLabel("Payment Method:"), gbc);

        String[] methods = {"Credit / Debit Card", "Cash Payment"};
        JComboBox<String> combo = new JComboBox<>(methods);
        combo.setBackground(Color.WHITE);
        gbc.gridx = 1;
        form.add(combo, gbc);

        int result = JOptionPane.showConfirmDialog(this, form, "Aurelia Grand Hotel - Payment Portal",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String method = combo.getSelectedItem().toString();

            boolean success = new dao.BookingDB().payReservationDeposit(resId, deposit, method + " (Simulated)");

            if (success) {
                showReceipt(resId, deposit, method);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Transaction failed. Check database connection.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showReceipt(int resId, double amount, String method) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        String formattedDateTime = now.format(dtf);

        int row = table.getSelectedRow();
        String roomNum = (row != -1) ? model.getValueAt(row, 1).toString() : "N/A";
        String currentStatus = (row != -1) ? model.getValueAt(row, 8).toString().toUpperCase() : "UNKNOWN";

        String msg = "<html><body style='width: 250px; padding: 10px; font-family: Arial;'>" +
                "<h2 style='color: #C5A059; text-align: center;'>PAYMENT SUCCESSFUL</h2>" +
                "<hr color='#C5A059'>" +
                "<p style='margin: 5px 0;'><b>Transaction Date:</b><br>" + formattedDateTime + "</p>" +
                "<p style='margin: 5px 0;'><b>Reservation ID:</b> " + resId + "</p>" +
                "<p style='margin: 5px 0;'><b>Room Number:</b> " + roomNum + "</p>" +
                "<hr color='#C5A059' style='border: 0; border-top: 1px dashed #C5A059;'>" +
                "<p style='margin: 5px 0;'><b>Amount Paid:</b> PHP " + String.format("%,.2f", amount) + "</p>" +
                "<p style='margin: 5px 0;'><b>Payment Method:</b> " + method + "</p>" +
                "<p style='margin: 5px 0;'><b>Status:</b> " + currentStatus + "</p>" +
                "<hr color='#C5A059'>" +
                "<p style='text-align: center; font-size: 9pt;'><i>Thank you for choosing<br>Aurelia Grand Hotel.</i></p>" +
                "</body></html>";

        JOptionPane.showMessageDialog(this, msg, "Official E-Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    public void refreshData() {
        if (model == null) return;
        model.setRowCount(0);

        String sql = "SELECT r.*, rm.room_number, rm.type, p.package_name " +
                "FROM reservations r " +
                "JOIN rooms rm ON r.room_id = rm.id " +
                "JOIN packages p ON r.package_id = p.package_id " +
                "WHERE r.guest_id = ?";

        java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("hh:mm a");

        try (java.sql.Connection conn = db.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentGuestId);
            java.sql.ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.Reservation resMath = new model.Reservation(
                        rs.getInt("res_id"),
                        currentGuestId,
                        0, null, null,
                        rs.getString("status"),
                        0
                );

                resMath.setTotalAmount(rs.getDouble("total_amount"));
                resMath.setAmountPaid(rs.getDouble("amount_paid"));
                resMath.setDamageFee(rs.getDouble("damage_fee"));
                resMath.setDamagePaid(rs.getDouble("damage_paid"));

                String totalStr = String.format("%.2f", resMath.getTotalAmount());
                String paidStr = String.format("%.2f", resMath.getAmountPaid() + resMath.getDamagePaid());
                String balanceStr = String.format("%.2f", resMath.getRemainingBalance());

                java.sql.Timestamp timeInTs = rs.getTimestamp("created_at");
                java.sql.Timestamp timeOutTs = rs.getTimestamp("checked_out_at");

                String timeInStr = (timeInTs != null) ? timeFormat.format(timeInTs) : "";
                String timeOutStr = (timeOutTs != null) ? timeFormat.format(timeOutTs) : "";

                model.addRow(new Object[]{
                        rs.getInt("res_id"),
                        rs.getString("room_number"),
                        rs.getString("type"),
                        rs.getString("package_name"),
                        resMath.getNights(),
                        totalStr,
                        paidStr,
                        balanceStr,
                        rs.getString("status"),
                        timeInStr,
                        timeOutStr
                });
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchGuestDisplayName() {
        String sql = "SELECT first_name, last_name FROM guests WHERE guest_id = ?";
        try (java.sql.Connection conn = db.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentGuestId);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String first = rs.getString("first_name");
                String last = rs.getString("last_name");
                this.displayName = (first != null) ? (first + " " + (last != null ? last : "")) : "Guest";
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            this.displayName = "Guest";
        }
    }
}