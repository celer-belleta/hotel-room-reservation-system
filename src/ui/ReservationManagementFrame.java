package ui;

import dao.ReservationDB;
import dao.PackageDB;
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

        // Table setup
        String[] columns = {"ID", "Guest ID", "Room ID", "Check-In", "Check-Out", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        resTable = new JTable(tableModel);
        add(new JScrollPane(resTable), BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("New Booking");
        JButton editBtn = new JButton("Edit Dates");
        JButton btnProcessPayment = new JButton("Process Payment");
        JButton btnCancel = new JButton("Cancel Reservation");
        JButton deleteBtn = new JButton("Delete Reservation");

        JButton refreshBtn = new JButton("\u21BB");
        refreshBtn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));

        btnPanel.add(btnAdd);
        btnPanel.add(editBtn);
        btnPanel.add(btnProcessPayment);
        btnPanel.add(btnCancel);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);


        // BUTTON ACTIONS
        btnAdd.addActionListener(e -> new AddReservationFrame(this).setVisible(true));

        editBtn.addActionListener(e -> {
            int row = resTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a reservation to edit!");
                return;
            }

            int resId = (int) tableModel.getValueAt(row, 0);
            String currentIn = tableModel.getValueAt(row, 3).toString();
            String currentOut = tableModel.getValueAt(row, 4).toString();

            JTextField txtIn = new JTextField(currentIn);
            JTextField txtOut = new JTextField(currentOut);
            Object[] message = {"Check-in:", txtIn, "Check-out:", txtOut};

            if (JOptionPane.showConfirmDialog(this, message, "Edit", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    java.sql.Date sqlIn = java.sql.Date.valueOf(txtIn.getText());
                    java.sql.Date sqlOut = java.sql.Date.valueOf(txtOut.getText());
                    if (resDB.updateReservation(resId, sqlIn, sqlOut)) {
                        refreshTable();
                        JOptionPane.showMessageDialog(this, "Updated!");
                    }
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Format: YYYY-MM-DD"); }
            }
        });

        btnProcessPayment.addActionListener(e -> {
            int row = resTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a reservation!");
                return;
            }

            int resId = (int) tableModel.getValueAt(row, 0);
            int gId = (int) tableModel.getValueAt(row, 1);
            int rId = (int) tableModel.getValueAt(row, 2);
            java.sql.Date start = (java.sql.Date) tableModel.getValueAt(row, 3);
            java.sql.Date end = (java.sql.Date) tableModel.getValueAt(row, 4);

            // Get package info from the reservation
            Reservation selectedRes = reservationList.get(row);
            int pkgId = selectedRes.getPackageId();

            PackageDB pkgDB = new PackageDB();
            String pkgName = pkgDB.getPackageName(pkgId);
            double pkgPrice = pkgDB.getPackagePrice(pkgId);

            new PaymentFrame(resId, "Guest #" + gId, "Room #" + rId, pkgPrice, start, end, pkgName).setVisible(true);
        });

        btnCancel.addActionListener(e -> {
            int row = resTable.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a reservation to cancel!");
                return;
            }

            int id = (int) tableModel.getValueAt(row, 0);

            int roomId = (int) tableModel.getValueAt(row, 2);

            if (resDB.cancelReservation(id, roomId)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Reservation #" + id + " has been cancelled.");
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = resTable.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a reservation to delete!");
                return;
            }

            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Permanently delete Reservation #" + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (resDB.deleteReservation(id)) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Record deleted successfully.");
                }
            }
        });

        refreshBtn.addActionListener(e -> refreshTable());
        refreshTable();
    }

    public void refreshTable() {
        try {
            tableModel.setRowCount(0);
            reservationList = resDB.getAllReservations();

            for (Reservation res : reservationList) {
                tableModel.addRow(new Object[]{
                        res.getResId(), res.getGuestId(), res.getRoomId(),
                        res.getCheckIn(), res.getCheckOut(), res.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: Could not load reservations.\n" + e.getMessage(),
                    "System Error", JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();
        }
    }
}