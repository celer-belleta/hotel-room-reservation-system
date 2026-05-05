package ui;

import dao.GuestDB;
import dao.UserDB;
import javax.swing.*;
import java.awt.*;

public class ForgotPasswordPanel extends JPanel {
    private MainFrame parent;

    private JTextField txtUsername;
    private JTextField txtContact;
    private JTextField txtIdNum; // Declared here
    private JPasswordField txtNewPassword;

    public ForgotPasswordPanel(MainFrame parent) {
        this.parent = parent;
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        // BOX
        JPanel contentWrap = new JPanel(new GridBagLayout());
        contentWrap.setBackground(Color.WHITE);
        contentWrap.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        GridBagConstraints wrapGbc = new GridBagConstraints();
        wrapGbc.insets = new Insets(5, 20, 5, 20);
        wrapGbc.fill = GridBagConstraints.NONE;

        // TITLE
        JLabel lblTitle = new JLabel("RESET PASSWORD", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        wrapGbc.gridy = 0;
        wrapGbc.gridx = 0;
        wrapGbc.gridwidth = 2;
        wrapGbc.anchor = GridBagConstraints.CENTER;
        wrapGbc.insets = new Insets(20, 20, 20, 20);
        contentWrap.add(lblTitle, wrapGbc);

        wrapGbc.insets = new Insets(5, 20, 5, 20);
        wrapGbc.gridwidth = 1;

        txtUsername = new JTextField(20);
        txtContact = new JTextField(20);
        txtIdNum = new JTextField(20); // Fixed private modifier error
        txtNewPassword = new JPasswordField(20);

        addLabelAndField("Enter Username:", txtUsername, 1, wrapGbc, contentWrap);
        addLabelAndField("Registered Contact No:", txtContact, 2, wrapGbc, contentWrap);
        addLabelAndField("Registered ID Number:", txtIdNum, 3, wrapGbc, contentWrap);
        addLabelAndField("Enter New Password:", txtNewPassword, 4, wrapGbc, contentWrap);

        // RESET BUTTON
        JButton resetBtn = new JButton("CONFIRM");
        resetBtn.setFont(new Font("Arial", Font.BOLD, 18));
        resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        wrapGbc.gridy = 5;
        wrapGbc.gridx = 0;
        wrapGbc.gridwidth = 2;
        wrapGbc.anchor = GridBagConstraints.CENTER;
        wrapGbc.insets = new Insets(15, 20, 5, 20);
        resetBtn.setPreferredSize(new Dimension(200, 40));
        contentWrap.add(resetBtn, wrapGbc);

        // BACK TO LOGIN
        JLabel lblBack = new JLabel("Remembered your password? Login here", SwingConstants.CENTER);
        lblBack.setFont(new Font("Arial", Font.PLAIN, 14));
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        wrapGbc.gridy = 6;
        wrapGbc.gridx = 0;
        wrapGbc.gridwidth = 2;
        wrapGbc.insets = new Insets(5, 20, 20, 20);
        contentWrap.add(lblBack, wrapGbc);

        add(contentWrap);

        // BUTTON ACTIONS
        resetBtn.addActionListener(e -> {
            String user = txtUsername.getText().trim();
            String contact = txtContact.getText().trim();
            String idNum = txtIdNum.getText().trim();
            String newPass = new String(txtNewPassword.getPassword());

            if(user.isEmpty() || contact.isEmpty() || idNum.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            UserDB staffDB = new UserDB();
            boolean success = staffDB.resetPassword(user, contact, newPass);

            if (!success) {
                GuestDB guestDB = new GuestDB();
                success = guestDB.resetGuestPassword(user, contact, idNum, newPass);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
                parent.showCard("LOGIN_PAGE");
            } else {
                JOptionPane.showMessageDialog(this, "Verification failed! Check your Username, Contact, and ID Number.");
            }
        });

        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                parent.showCard("LOGIN_PAGE");
            }
        });

        this.revalidate();
        this.repaint();
    }

    private void addLabelAndField(String labelText, JComponent field, int row, GridBagConstraints gbc, JPanel container) {
        gbc.gridy = row;
        gbc.gridwidth = 1;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 18));

        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 0.1;
        gbc.insets = new Insets(5, 20, 5, 10);
        container.add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        field.setPreferredSize(new Dimension(250, 30));
        gbc.insets = new Insets(5, 0, 5, 20);
        container.add(field, gbc);

        gbc.fill = GridBagConstraints.NONE;
    }
}