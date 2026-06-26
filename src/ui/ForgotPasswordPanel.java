package ui;

import dao.GuestDB;
import dao.UserDB;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ForgotPasswordPanel extends JPanel {
    private MainFrame parent;
    private JTextField txtUsername;
    private JTextField txtContact;
    private JTextField txtIdNum;
    private JPasswordField txtNewPassword;
    private final Color MATCHED_NAVY = new Color(44, 62, 80);
    private final Color GOLD = new Color(197, 160, 89);

    public ForgotPasswordPanel(MainFrame parent) {
        this.parent = parent;

        setBackground(MATCHED_NAVY);
        setLayout(new GridBagLayout());

        JPanel contentWrap = new JPanel(new GridBagLayout());
        contentWrap.setBackground(Color.WHITE);
        contentWrap.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        contentWrap.setPreferredSize(new Dimension(500, 500));

        GridBagConstraints wrapGbc = new GridBagConstraints();
        wrapGbc.insets = new Insets(5, 20, 5, 20);
        wrapGbc.fill = GridBagConstraints.NONE;

        JLabel lblTitle = new JLabel("RESET PASSWORD", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(MATCHED_NAVY);
        wrapGbc.gridy = 0;
        wrapGbc.gridx = 0;
        wrapGbc.gridwidth = 2;
        wrapGbc.anchor = GridBagConstraints.CENTER;
        wrapGbc.insets = new Insets(20, 20, 25, 20);
        contentWrap.add(lblTitle, wrapGbc);

        wrapGbc.insets = new Insets(5, 20, 5, 20);
        wrapGbc.gridwidth = 1;

        txtUsername = new JTextField(20);
        txtContact = new JTextField(20);
        txtIdNum = new JTextField(20);
        txtNewPassword = new JPasswordField(20);

        addLabelAndField("Username:", txtUsername, 1, wrapGbc, contentWrap);
        addLabelAndField("Contact No:", txtContact, 2, wrapGbc, contentWrap);
        addLabelAndField("ID Number:", txtIdNum, 3, wrapGbc, contentWrap);
        addLabelAndField("New Password:", txtNewPassword, 4, wrapGbc, contentWrap);

        JButton resetBtn = new JButton("CONFIRM");
        resetBtn.setFont(new Font("Arial", Font.BOLD, 16));
        resetBtn.setBackground(MATCHED_NAVY);
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setOpaque(true);
        resetBtn.setBorderPainted(false);
        resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetBtn.setPreferredSize(new Dimension(200, 45));

        wrapGbc.gridy = 5;
        wrapGbc.gridx = 0;
        wrapGbc.gridwidth = 2;
        wrapGbc.anchor = GridBagConstraints.CENTER;
        wrapGbc.insets = new Insets(25, 20, 10, 20);
        contentWrap.add(resetBtn, wrapGbc);

        JLabel lblBack = new JLabel("Remembered your password? Login here", SwingConstants.CENTER);
        lblBack.setFont(new Font("Arial", Font.PLAIN, 14));
        lblBack.setForeground(Color.BLACK);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        wrapGbc.gridy = 6;
        wrapGbc.insets = new Insets(5, 20, 20, 20);
        contentWrap.add(lblBack, wrapGbc);

        add(contentWrap);

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

        lblBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                parent.showCard("LOGIN_PAGE");
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                lblBack.setForeground(Color.BLACK);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                lblBack.setForeground(Color.BLACK);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        super.paintComponent(g);
    }

    private void addLabelAndField(String labelText, JComponent field, int row, GridBagConstraints gbc, JPanel container) {
        gbc.gridy = row;
        gbc.gridwidth = 1;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(MATCHED_NAVY);

        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 0.1;
        gbc.insets = new Insets(8, 20, 8, 10);
        container.add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 35));
        gbc.insets = new Insets(8, 0, 8, 20);
        container.add(field, gbc);

        gbc.fill = GridBagConstraints.NONE;
    }
}