package ui;

import dao.UserDB;
import javax.swing.*;
import java.awt.*;

public class AddUserFrame extends JFrame {

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField usernameField;
    private JTextField passwordField;
    private JComboBox<String> roleComboBox;

    private UserTablePanel parentPanel;

    public AddUserFrame(UserTablePanel parent) {
        this.parentPanel = parent;

        setTitle("Add User");
        setSize(400, 400);
        setLocationRelativeTo(null);

        setLayout(new GridLayout(6, 2, 10, 10));

        add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        add(firstNameField);

        add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        add(lastNameField);

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JTextField();
        add(passwordField);

        add(new JLabel("Role:"));
        String[] roles = {"Admin", "Clerk"};
        roleComboBox = new JComboBox<>(roles);
        add(roleComboBox);

        JButton addBtn = new JButton("Confirm");
        JButton cancelBtn = new JButton("Cancel");

        // BUTTON ACTIONS
        addBtn.addActionListener(e -> addUser());
        cancelBtn.addActionListener(e -> dispose());

        add(addBtn);
        add(cancelBtn);

        setVisible(true);
    }

    private void addUser() {
        String fName = firstNameField.getText().trim();
        String lName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (fName.isEmpty() || lName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        UserDB db = new UserDB();

        boolean success = db.addUser(fName, lName, username, password, role);

        if (success) {
            JOptionPane.showMessageDialog(this, "User added successfully!");
            if (parentPanel != null) {
                parentPanel.loadUsers();
            }
            dispose();
        }
    }
}