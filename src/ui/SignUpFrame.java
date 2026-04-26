package ui;

import dao.UserDB;
import javax.swing.*;
import java.awt.*;

public class SignUpFrame extends JFrame {
    // Fields for User and Guest tables
    private JTextField txtName = new JTextField(20);
    private JTextField txtUser = new JTextField(20);
    private JPasswordField txtPass = new JPasswordField(20);
    private JTextField txtContact = new JTextField(20);
    private JButton btnSubmit = new JButton("Create My Account");

    public SignUpFrame() {
        setTitle("Sign Up");
        setSize(400, 450);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(new JLabel("Full Name:"));
        mainPanel.add(txtName);
        mainPanel.add(new JLabel("Username:"));
        mainPanel.add(txtUser);
        mainPanel.add(new JLabel("Password:"));
        mainPanel.add(txtPass);
        mainPanel.add(new JLabel("Contact Number:"));
        mainPanel.add(txtContact);
        mainPanel.add(new JLabel("")); // Spacer
        mainPanel.add(btnSubmit);

        add(mainPanel);

        // BUTTON ACTION
        btnSubmit.addActionListener(e -> {
            String name = txtName.getText().trim();
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());
            String contact = txtContact.getText().trim();

            if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            UserDB db = new UserDB();
            if (db.registerGuest(user, pass, name, contact)) {
                JOptionPane.showMessageDialog(this, "Success! You can now login to your account.");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error: Username might be taken.");
            }
        });
    }
}