package ui;

import dao.UserDB;
import javax.swing.*;
import java.awt.*;

public class ForgotPasswordFrame extends JFrame {
    public ForgotPasswordFrame() {
        setTitle("RESET PASSWORD");
        setSize(400, 300);
        setLocationRelativeTo(null);

        setLayout(new GridLayout(4, 2, 10, 10));

        JTextField userField = new JTextField();
        JTextField contactField = new JTextField();
        JPasswordField newPassField = new JPasswordField();
        JButton resetBtn = new JButton("CONFIRM");

        add(new JLabel(" Enter Username:"));
        add(userField);

        add(new JLabel(" Enter Registered Contact No:"));
        add(contactField);

        add(new JLabel(" Enter New Password:"));
        add(newPassField);

        add(new JLabel());
        add(resetBtn);

        resetBtn.addActionListener(e -> {
            String user = userField.getText();
            String contact = contactField.getText();
            String newPass = new String(newPassField.getPassword());

            if(user.isEmpty() || contact.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            dao.UserDB db = new dao.UserDB();
            boolean success = db.resetPassword(user, contact, newPass);

            if (success) {
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Verification failed. Check your Username or Contact No.");
            }
        });
    }
}