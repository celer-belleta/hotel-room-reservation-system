package ui;

import dao.GuestDB;
import javax.swing.*;
import java.awt.*;

public class AddGuestFrame extends JFrame {

    private JTextField txtName, txtContact, txtIdNumber;
    private GuestDB guestDB;
    private JFrame parent; // Can be GuestManagementFrame later

    public AddGuestFrame(JFrame parent) {
        this.parent = parent;
        guestDB = new GuestDB();

        // FRAME SETTINGS
        setTitle("Register New Guest");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 15, 15));

        // COMPONENTS
        add(new JLabel(" Guest Name:"));
        txtName = new JTextField();
        add(txtName);

        add(new JLabel(" Contact Number:"));
        txtContact = new JTextField();
        add(txtContact);

        add(new JLabel(" ID Number:"));
        txtIdNumber = new JTextField();
        add(txtIdNumber);

        JButton saveBtn = new JButton("REGISTER");
        JButton cancelBtn = new JButton("CANCEL");

        add(saveBtn);
        add(cancelBtn);

        // SAVE GUEST ACTION
        saveBtn.addActionListener(e -> {
            String name = txtName.getText().trim();
            String contact = txtContact.getText().trim();
            String idNum = txtIdNumber.getText().trim();

            if (name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Contact are required!");
                return;
            }

            guestDB.addGuest(name, contact, idNum);
            JOptionPane.showMessageDialog(this, "Guest Registered Successfully!");

            // If we make a GuestManagementFrame later, we will refresh it here
            dispose();
        });

        // CANCEL ACTION
        cancelBtn.addActionListener(e -> dispose());
    }
}