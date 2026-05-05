package ui;

import dao.GuestDB;
import javax.swing.*;
import java.awt.*;

public class AddGuestFrame extends JFrame {

    private JTextField txtFirstName, txtLastName, txtContact, txtIdNumber;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> comboIdType;

    private GuestDB guestDB;
    private GuestTablePanel parent;

    public AddGuestFrame(GuestTablePanel parent) {
        this.parent = parent;
        guestDB = new GuestDB();

        // FRAME SETTINGS
        setTitle("Register New Guest");
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridLayout(8, 2, 15, 15));

        // COMPONENTS
        add(new JLabel(" First Name:"));
        txtFirstName = new JTextField();
        add(txtFirstName);

        add(new JLabel(" Last Name:"));
        txtLastName = new JTextField();
        add(txtLastName);

        add(new JLabel(" Contact Number:"));
        txtContact = new JTextField();
        add(txtContact);

        add(new JLabel(" ID Type:"));
        comboIdType = new JComboBox<>(new String[]{"Passport", "Drivers License", "National ID", "Student ID"});
        comboIdType.setPreferredSize(new Dimension(250, 30));
        comboIdType.setBackground(Color.WHITE);
        add(comboIdType);

        add(new JLabel(" ID Number:"));
        txtIdNumber = new JTextField();
        add(txtIdNumber);

        add(new JLabel(" Username:"));
        txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel(" Password:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        JButton saveBtn = new JButton("REGISTER");
        JButton cancelBtn = new JButton("CANCEL");

        add(saveBtn);
        add(cancelBtn);

        // BUTTON ACTIONS
        saveBtn.addActionListener(e -> {
            String fName = txtFirstName.getText().trim();
            String lName = txtLastName.getText().trim();
            String contact = txtContact.getText().trim();
            String idType = (String) comboIdType.getSelectedItem();
            String idNum = txtIdNumber.getText().trim();
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword());

            if (fName.isEmpty() || lName.isEmpty() || contact.isEmpty() || idNum.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            if (guestDB.addGuest(fName, lName, contact, idType, idNum, user, pass)) {
                JOptionPane.showMessageDialog(this,
                        "Guest Registered Successfully!\n" +
                                "Username: " + user + "\n" +
                                "Password: " + pass);

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