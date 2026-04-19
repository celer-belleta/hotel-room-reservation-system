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

    private ArrayList<Guest> guestList = new ArrayList<>();

    public GuestManagementFrame() {
        guestDB = new GuestDB();

        // FRAME SETTINGS
        setTitle("Guest Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // TABLE SETUP
        String[] columns = {"ID", "Name", "Contact", "ID Number"};
        tableModel = new DefaultTableModel(columns, 0);
        guestTable = new JTable(tableModel);
        loadGuests();

        add(new JScrollPane(guestTable), BorderLayout.CENTER);

        // BOTTOM PANEL (BUTTONS)
        JPanel buttonPanel = new JPanel();
        JButton registerBtn = new JButton("Register New Guest");
        JButton deleteBtn = new JButton("Delete Guest");

        // SYMBOL REFRESH BUTTON
        JButton refreshBtn = new JButton("\u21BB");
        refreshBtn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
        refreshBtn.setToolTipText("Refresh Table");

        buttonPanel.add(registerBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // BUTTON ACTIONS
        refreshBtn.addActionListener(e -> loadGuests());

        registerBtn.addActionListener(e -> new AddGuestFrame(this).setVisible(true));

        deleteBtn.addActionListener(e -> {
            int row = guestTable.getSelectedRow();
            if (row != -1) {
                int id = (int) tableModel.getValueAt(row, 0);

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this guest?", "Confirm", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (guestDB.deleteGuest(id)) {

                        guestList.remove(row);
                        tableModel.removeRow(row);

                        JOptionPane.showMessageDialog(this, "Guest deleted!");
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Error: Cannot delete guest. They might have an active reservation!",
                                "Delete Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a guest to delete.");
            }
        });

        setVisible(true);
    }

    public void loadGuests() {
        tableModel.setRowCount(0);

        guestList = guestDB.getAllGuests();

        for (Guest g : guestList) {
            tableModel.addRow(new Object[]{
                    g.getGuestId(),
                    g.getName(),
                    g.getContact(),
                    g.getIdNumber()
            });
        }
    }
}