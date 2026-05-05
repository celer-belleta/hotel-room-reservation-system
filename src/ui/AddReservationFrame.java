package ui;

import dao.GuestDB;
import dao.RoomDB;
import dao.PackageDB;
import dao.ReservationDB;
import model.Guest;
import model.Room;
import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Map;

public class AddReservationFrame extends JFrame {
    private JComboBox<String> comboGuest, comboRoom, comboPackage;
    private JTextField txtCheckIn, txtCheckOut;
    private ReservationTablePanel parentPanel;
    private ReservationDB resDB = new ReservationDB();
    private RoomDB roomDB = new RoomDB();

    public AddReservationFrame(ReservationTablePanel parent) {
        this.parentPanel = parent;

        setTitle("New Booking - Aurelia Grand Hotel");
        setSize(450, 500);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2, 15, 15));

        add(new JLabel(" Select Guest:"));
        comboGuest = new JComboBox<>();
        loadGuests();
        add(comboGuest);

        add(new JLabel(" Select Package:"));
        comboPackage = new JComboBox<>();
        loadPackages();
        add(comboPackage);

        add(new JLabel(" Select Available Room:"));
        comboRoom = new JComboBox<>();
        filterRoomsByPackage((String) comboPackage.getSelectedItem());
        add(comboRoom);

        comboPackage.addActionListener(e -> {
            String selectedPkg = (String) comboPackage.getSelectedItem();
            filterRoomsByPackage(selectedPkg);
        });

        add(new JLabel(" Check-In (YYYY-MM-DD):"));
        txtCheckIn = new JTextField();
        add(txtCheckIn);

        add(new JLabel(" Check-Out (YYYY-MM-DD):"));
        txtCheckOut = new JTextField();
        add(txtCheckOut);

        JButton btnSave = new JButton("CONFIRM BOOKING");
        JButton btnCancel = new JButton("CANCEL");

        btnSave.addActionListener(e -> processBooking());
        btnCancel.addActionListener(e -> dispose());

        add(btnSave);
        add(btnCancel);
    }

    private void loadGuests() {
        ArrayList<Guest> list = new GuestDB().getAllGuests();
        for (Guest g : list) {
            comboGuest.addItem(g.getGuestId() + " - " + g.getFirstName() + " " + g.getLastName());
        }
    }

    private void loadPackages() {
        Map<String, Integer> map = new PackageDB().getPackageMap();
        for (String name : map.keySet()) {
            comboPackage.addItem(name);
        }
    }

    private void filterRoomsByPackage(String packageType) {
        comboRoom.removeAllItems();
        ArrayList<Room> allRooms = roomDB.getAllRooms();

        for (Room r : allRooms) {
            if (r.getStatus().equalsIgnoreCase("Available")) {
                if (packageType.contains("Amenities")) {
                    if (r.getType().equalsIgnoreCase("Deluxe") || r.getType().equalsIgnoreCase("Suite")) {
                        comboRoom.addItem(r.getId() + " - " + r.getType() + " (Room " + r.getRoomNumber() + ")");
                    }
                } else {
                    comboRoom.addItem(r.getId() + " - " + r.getType() + " (Room " + r.getRoomNumber() + ")");
                }
            }
        }

        if (comboRoom.getItemCount() == 0) {
            comboRoom.addItem("No rooms available for this package");
        }
    }

    private void processBooking() {
        if (txtCheckIn.getText().isEmpty() || txtCheckOut.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter stay dates.");
            return;
        }

        if (comboRoom.getSelectedItem().toString().contains("No rooms")) {
            JOptionPane.showMessageDialog(this, "Please select a different package or room.");
            return;
        }

        try {
            int gId = Integer.parseInt(comboGuest.getSelectedItem().toString().split(" - ")[0]);
            int rId = Integer.parseInt(comboRoom.getSelectedItem().toString().split(" - ")[0]);

            String pkgName = (String) comboPackage.getSelectedItem();
            int pkgId = new PackageDB().getPackageMap().get(pkgName);

            Date start = Date.valueOf(txtCheckIn.getText().trim());
            Date end = Date.valueOf(txtCheckOut.getText().trim());

            int resId = resDB.createReservation(gId, rId, start, end, pkgId);

            if (resId != -1) {
                JOptionPane.showMessageDialog(this, "Booking Successful! ID: " + resId);
                if (parentPanel != null) parentPanel.refreshTable(); // Refreshes the main table
                dispose();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date Format. Use YYYY-MM-DD.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}