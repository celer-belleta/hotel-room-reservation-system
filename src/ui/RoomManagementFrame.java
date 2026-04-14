package ui;

import dao.RoomDB;
import model.Room;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class RoomManagementFrame extends JFrame {
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private RoomDB roomDB;

    public RoomManagementFrame() {
        roomDB = new RoomDB();

        // =========================
        // FRAME SETTINGS
        // =========================
        setTitle("Room Management - Admin");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // =========================
        // TABLE SETUP
        // =========================
        String[] columns = {"ID", "Room #", "Type", "Price", "Status", "Amenities"};
        tableModel = new DefaultTableModel(columns, 0);
        roomTable = new JTable(tableModel);
        loadRooms();

        add(new JScrollPane(roomTable), BorderLayout.CENTER);

        // =========================
        // BOTTOM PANEL (BUTTONS)
        // =========================
        JPanel buttonPanel = new JPanel();

        JButton addBtn = new JButton("Add New Room");

        // SYMBOL REFRESH BUTTON
        JButton refreshBtn = new JButton("\u21BB");
        refreshBtn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
        refreshBtn.setToolTipText("Refresh Table");

        buttonPanel.add(addBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // =========================
        // BUTTON ACTIONS
        // =========================

        refreshBtn.addActionListener(e -> loadRooms());

        // This will open the form to add rooms
        addBtn.addActionListener(e -> new AddRoomFrame(this).setVisible(true));
    }

    // LOAD ROOMS METHOD
    public void loadRooms() {
        tableModel.setRowCount(0);
        ArrayList<Room> rooms = roomDB.getAllRooms();
        for (Room r : rooms) {
            tableModel.addRow(new Object[]{
                    r.getId(), r.getRoomNumber(), r.getType(),
                    r.getPrice(), r.getStatus(), r.getAmenities()
            });
        }
    }
}