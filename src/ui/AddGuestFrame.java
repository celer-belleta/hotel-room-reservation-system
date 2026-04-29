package ui;

import dao.GuestDB;
import javax.swing.*;
import java.awt.*;

public class AddGuestFrame extends JFrame {

    private JTextField txtName, txtContact, txtIdNumber;
    private GuestDB guestDB;
    private GuestTablePanel parent;

    public AddGuestFrame(GuestTablePanel parent) {
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

        // BUTTON ACTIONS
        saveBtn.addActionListener(e -> {
            String name = txtName.getText().trim();
            String contact = txtContact.getText().trim();
            String idNum = txtIdNumber.getText().trim();

            if (name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Contact are required!");
                return;
            }

            // username and password generation
            String cleanName = name.toLowerCase().replace(" ", "");

            String generatedUser = cleanName;

            String generatedPass = cleanName + "123";

            if (guestDB.addGuest(name, contact, idNum, generatedUser, generatedPass)) {
                JOptionPane.showMessageDialog(this,
                        "Guest Registered!\n" +
                                "Username: " + generatedUser + "\n" +
                                "Temp Password: " + generatedPass);

                if (parent != null) {
                    parent.loadGuests();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not register guest.");
            }
        });

        cancelBtn.addActionListener(e -> dispose());
    }
}