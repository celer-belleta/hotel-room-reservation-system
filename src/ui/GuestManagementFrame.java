package ui;

import dao.GuestDB;
import model.Guest;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class GuestManagementFrame extends JFrame {
    private JTable guestTable;
    private DefaultTableModel tableModel;
    private GuestDB guestDB;

    public GuestManagementFrame() {
        guestDB = new GuestDB();

        // =========================
        // FRAME SETTINGS
        // =========================
        setTitle("Guest Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // =========================
        // TABLE SETUP
        // =========================
        String[] columns = {"ID", "Name", "Contact", "ID Number"};
        tableModel = new DefaultTableModel(columns, 0);
        guestTable = new JTable(tableModel);
        loadGuests();

        add(new JScrollPane(guestTable), BorderLayout.CENTER);

        // =========================
        // BOTTOM PANEL (BUTTONS)
        // =========================
        JPanel buttonPanel = new JPanel();

        JButton registerBtn = new JButton("Register New Guest");

        // SYMBOL REFRESH BUTTON
        JButton refreshBtn = new JButton("\u21BB");
        refreshBtn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
        refreshBtn.setToolTipText("Refresh Table");

        buttonPanel.add(registerBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // =========================
        // BUTTON ACTIONS
        // =========================

        refreshBtn.addActionListener(e -> loadGuests());

        registerBtn.addActionListener(e -> new AddGuestFrame(this).setVisible(true));

        setVisible(true);
    }

    // LOAD GUESTS METHOD
    public void loadGuests() {
        tableModel.setRowCount(0);
        ArrayList<Guest> guests = guestDB.getAllGuests();
        for (Guest g : guests) {
            tableModel.addRow(new Object[]{
                    g.getGuestId(),
                    g.getName(),
                    g.getContact(),
                    g.getIdNumber()
            });
        }
    }
}