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

    private ArrayList<Reservation> reservationList = new ArrayList<>();

    public ReservationManagementFrame() {
        setTitle("Reservation Management");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Table Setup
        String[] columns = {"ID", "Guest ID", "Room ID", "Check-In", "Check-Out", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        resTable = new JTable(tableModel);
        add(new JScrollPane(resTable), BorderLayout.CENTER);

        // Button Panel
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

        // BUTTON ACTIONS
        btnAdd.addActionListener(e -> new AddReservationFrame(this).setVisible(true));

        btnCancel.addActionListener(e -> {
            int row = resTable.getSelectedRow();

            if (row != -1) {
                int id = (int) tableModel.getValueAt(row, 0);

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to cancel reservation ID: " + id + "?",
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    if (resDB.cancelReservation(id)) {

                        Reservation res = reservationList.get(row);
                        res.setStatus("Cancelled");

                        tableModel.setValueAt("Cancelled", row, 5);

                        JOptionPane.showMessageDialog(this, "Reservation cancelled successfully.");

                    } else {
                        JOptionPane.showMessageDialog(this, "Error updating database.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a reservation to cancel.");
            }
        });

        refreshBtn.addActionListener(e -> refreshTable());

        refreshTable();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);

        reservationList = resDB.getAllReservations();

        for (Reservation res : reservationList) {
            Object[] rowData = {
                    res.getResId(),
                    res.getGuestId(),
                    res.getRoomId(),
                    res.getCheckIn(),
                    res.getCheckOut(),
                    res.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }
}