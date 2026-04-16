package ui;

import dao.ReservationDB;
import model.Reservation;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ReservationManagementFrame extends JFrame {
    private JTable resTable;
    private DefaultTableModel tableModel;
    private ReservationDB resDB = new ReservationDB();

    public ReservationManagementFrame() {
        setTitle("Reservation Management");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Table Setup ---
        String[] columns = {"ID", "Guest ID", "Room ID", "Check-In", "Check-Out", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        resTable = new JTable(tableModel);
        add(new JScrollPane(resTable), BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("New Booking");
        JButton btnCancel = new JButton("Cancel Reservation");

        JButton refreshBtn = new JButton("\u21BB");
        refreshBtn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
        refreshBtn.setToolTipText("Refresh Table");

        btnPanel.add(btnAdd);
        btnPanel.add(btnCancel);
        btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // --- Actions ---
        btnAdd.addActionListener(e -> new AddReservationFrame(this).setVisible(true));

        btnCancel.addActionListener(e -> {
            int row = resTable.getSelectedRow();

            if (row != -1) {
                // Get the ID from the selected row
                int id = (int) tableModel.getValueAt(row, 0);

                // Ask for confirmation
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to cancel reservation ID: " + id + "?",
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                // Only proceed if they clicked 'YES'
                if (confirm == JOptionPane.YES_OPTION) {
                    resDB.cancelReservation(id);
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Reservation cancelled successfully.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a reservation to cancel.");
            }
        });

        refreshBtn.addActionListener(e -> refreshTable());

        refreshTable();
    }

    public void refreshTable() {
        // Clear the existing rows so we don't have duplicates
        tableModel.setRowCount(0);

        // Fetch the latest list from the database
        ArrayList<Reservation> list = resDB.getAllReservations();

        // Loop through the list and add each reservation to the table
        for (Reservation res : list) {
            Object[] row = {
                    res.getResId(),
                    res.getGuestId(),
                    res.getRoomId(),
                    res.getCheckIn(),
                    res.getCheckOut(),
                    res.getStatus()
            };
            tableModel.addRow(row);
        }
    }
}