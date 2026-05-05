package ui;

import dao.ReservationDB;
import model.Reservation;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ReservationTablePanel extends JPanel {
    private JTable resTable;
    private DefaultTableModel tableModel;
    private ReservationDB resDB = new ReservationDB();
    private ArrayList<Reservation> reservationList = new ArrayList<>();

    private final Color AURELIA_GOLD = new Color(197, 160, 89);
    private final Color BG_COLOR = new Color(245, 246, 247);
    private final Color SELECTION_COLOR = new Color(245, 230, 200);

    public ReservationTablePanel() {
        setBackground(BG_COLOR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_COLOR);

        JLabel title = new JLabel("Manage Reservations");
        title.setFont(new Font("Serif", Font.BOLD, 30));
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        actions.setBackground(BG_COLOR);
        actions.add(createActionLabel("New Booking", "ADD"));
        actions.add(createActionLabel("Process Payment", "PAY"));
        actions.add(createActionLabel("Check-In", "IN"));
        actions.add(createActionLabel("Check-Out", "OUT"));
        actions.add(createActionLabel("Cancel Booking", "CANCEL"));
        header.add(actions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "Guest ID", "Room ID", "Check-In", "Check-Out", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        resTable = new JTable(tableModel);
        resTable.setRowHeight(45);
        resTable.setSelectionBackground(SELECTION_COLOR);
        resTable.setSelectionForeground(Color.BLACK);
        resTable.setShowGrid(false);
        resTable.setIntercellSpacing(new Dimension(0, 0));

        resTable.getTableHeader().setBackground(Color.WHITE);
        resTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        resTable.getTableHeader().setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (value != null) {
                    String status = value.toString();
                    if (status.equalsIgnoreCase("Checked-In")) {
                        c.setForeground(new Color(40, 167, 69));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else if (status.equalsIgnoreCase("Confirmed")) {
                        c.setForeground(AURELIA_GOLD);
                    } else if (status.equalsIgnoreCase("Pending")) {
                        c.setForeground(new Color(255, 152, 0));
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                }
                if (isSelected) c.setForeground(Color.BLACK);
                return c;
            }
        };

        for (int i = 0; i < resTable.getColumnCount(); i++) {
            resTable.getColumnModel().getColumn(i).setCellRenderer(statusRenderer);
        }

        refreshTable();

        JScrollPane scrollPane = new JScrollPane(resTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createActionLabel(String text, String action) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (action.equals("ADD")) {
                    String inputId = JOptionPane.showInputDialog(null,
                            "Enter Guest ID:",
                            "New Booking Login",
                            JOptionPane.PLAIN_MESSAGE);

                    if (inputId != null && !inputId.trim().isEmpty()) {
                        try {
                            new GuestDashboard(inputId.trim()).setVisible(true);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                        }
                    }

                } else if (action.equals("PAY")) {
                    handlePayment(resTable.getSelectedRow());
                } else if (action.equals("CANCEL")) {
                    handleCancel(resTable.getSelectedRow());
                } else if (action.equals("IN")) {
                    handleCheckIn(resTable.getSelectedRow());
                } else if (action.equals("OUT")) {
                    handleCheckOut(resTable.getSelectedRow());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) { label.setForeground(AURELIA_GOLD); }
            @Override
            public void mouseExited(MouseEvent e) { label.setForeground(Color.BLACK); }
        });
        return label;
    }

    private void handlePayment(int row) {
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a reservation to process payment.");
            return;
        }

        int modelRow = resTable.convertRowIndexToModel(row);
        int resId = (int) tableModel.getValueAt(modelRow, 0);
        String status = (String) tableModel.getValueAt(modelRow, 5);

        Reservation selectedRes = null;
        for (Reservation res : reservationList) {
            if (res.getResId() == resId) {
                selectedRes = res;
                break;
            }
        }

        if (selectedRes != null) {
            double balance = selectedRes.getRemainingBalance();

            if (balance <= 0.01) {
                JOptionPane.showMessageDialog(this,
                        "This reservation is already fully paid.",
                        "Payment Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (status.equalsIgnoreCase("Checked-Out") || status.equalsIgnoreCase("Cancelled")) {
                JOptionPane.showMessageDialog(this,
                        "Cannot process payment for a finalized or cancelled booking.",
                        "Action Denied", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String guestName = new dao.GuestDB().getGuestNameById(selectedRes.getGuestId());

            new PaymentProcessingFrame(
                    resId,
                    guestName != null ? guestName : "Guest #" + selectedRes.getGuestId(),
                    selectedRes.getGuestId(),
                    selectedRes.getTotalAmount(),
                    selectedRes.getAmountPaid(),
                    this
            ).setVisible(true);

        } else {
            JOptionPane.showMessageDialog(this, "Error: Reservation data not found in the list.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void handleCancel(int row) {
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a reservation to cancel.");
            return;
        }
        int resId = (int) tableModel.getValueAt(row, 0);
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel Reservation #" + resId + "?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            if (resDB.deleteReservation(resId)) {
                JOptionPane.showMessageDialog(this, "Reservation removed successfully.");
                refreshTable();
            }
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        reservationList = resDB.getAllReservations();

        for (Reservation res : reservationList) {
            tableModel.addRow(new Object[]{
                    res.getResId(),
                    res.getGuestId(),
                    res.getRoomId(),
                    res.getCheckIn(),
                    res.getCheckOut(),
                    res.getStatus()
            });
        }
    }

    private void handleCheckIn(int row) {
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a reservation to Check-In.");
            return;
        }

        int modelRow = resTable.convertRowIndexToModel(row);
        int resId = (int) tableModel.getValueAt(modelRow, 0);
        int roomId = (int) tableModel.getValueAt(modelRow, 2);
        String currentStatus = (String) tableModel.getValueAt(modelRow, 5);

        if (currentStatus.equalsIgnoreCase("Checked-In")) {
            JOptionPane.showMessageDialog(this, "Guest is already currently Checked-In.");
            return;
        }

        if (currentStatus.equalsIgnoreCase("Checked-Out") || currentStatus.equalsIgnoreCase("Cancelled")) {
            JOptionPane.showMessageDialog(this,
                    "Cannot Check-In: This reservation is already " + currentStatus + ".",
                    "Action Denied",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        double balance = 0.0;
        for (Reservation res : reservationList) {
            if (res.getResId() == resId) {
                balance = res.getRemainingBalance();
                break;
            }
        }

        if (balance > 0.01) {
            JOptionPane.showMessageDialog(this,
                    String.format("Cannot Check-In! Full payment of PHP %,.2f is required.", balance),
                    "Payment Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (resDB.processCheckIn(resId, roomId)) {
            JOptionPane.showMessageDialog(this, "Guest Checked-In Successfully!");
            refreshTable();
        }
    }
    private void handleCheckOut(int row) {
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a reservation to Check-Out.");
            return;
        }

        int modelRow = resTable.convertRowIndexToModel(row);
        int resId = (int) tableModel.getValueAt(modelRow, 0);
        int roomId = (int) tableModel.getValueAt(modelRow, 2);
        String status = (String) tableModel.getValueAt(modelRow, 5);

        if (!status.equalsIgnoreCase("Checked-In")) {
            JOptionPane.showMessageDialog(this, "Only guests with 'Checked-In' status can be Checked-Out.");
            return;
        }

        double balance = 0.0;
        for (Reservation res : reservationList) {
            if (res.getResId() == resId) {
                balance = res.getRemainingBalance();
                break;
            }
        }

        if (balance > 0.01) {
            JOptionPane.showMessageDialog(this,
                    String.format("Cannot Check-Out! Guest still has a balance of PHP %,.2f", balance),
                    "Payment Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Database Execution
        int choice = JOptionPane.showConfirmDialog(this,
                "Confirm Check-Out for Reservation #" + resId + "?",
                "Aurelia Grand Hotel", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            if (resDB.processCheckOut(resId, roomId)) {
                JOptionPane.showMessageDialog(this,
                        "Guest Checked-Out Successfully. Room " + roomId + " is now Available.");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Database error during Check-Out.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}