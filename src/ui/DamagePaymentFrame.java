package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DamagePaymentFrame extends JFrame {
    private final double damageAmount;
    private final int resId;
    private final JTextField txtCash;
    private final JLabel lblChange;

    public DamagePaymentFrame(int resId, String guestName, double damageAmount, ReservationTablePanel parent) {
        this.resId = resId;
        this.damageAmount = damageAmount;

        setTitle("Damage Settlement - Res #" + resId);
        setSize(400, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setBackground(new Color(255, 235, 235));
        top.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel lblTitle = new JLabel("DAMAGE SETTLEMENT DUE:");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel lblValue = new JLabel("₱" + String.format("%,.2f", damageAmount));
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblValue.setForeground(new Color(180, 0, 0));

        top.add(lblTitle);
        top.add(lblValue);
        add(top, BorderLayout.NORTH);

        // --- CENTER PANEL: PAYMENT INPUT ---
        JPanel center = new JPanel(null);
        center.setOpaque(false);

        JLabel lblCashLabel = new JLabel("ENTER CASH RECEIVED:");
        lblCashLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblCashLabel.setBounds(40, 20, 200, 20);
        center.add(lblCashLabel);

        txtCash = new JTextField();
        txtCash.setBounds(40, 45, 305, 50);
        txtCash.setFont(new Font("SansSerif", Font.BOLD, 22));
        txtCash.setHorizontalAlignment(JTextField.CENTER);
        center.add(txtCash);

        lblChange = new JLabel("CHANGE: ₱0.00", SwingConstants.CENTER);
        lblChange.setBounds(40, 110, 305, 40);
        lblChange.setFont(new Font("SansSerif", Font.BOLD, 18));
        center.add(lblChange);
        add(center, BorderLayout.CENTER);

        txtCash.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                try {
                    String input = txtCash.getText().trim();
                    if (input.isEmpty()) { lblChange.setText("CHANGE: ₱0.00"); return; }
                    double cash = Double.parseDouble(input);
                    lblChange.setText("CHANGE: ₱" + String.format("%,.2f", Math.max(0, cash - damageAmount)));
                } catch (Exception ex) { lblChange.setText("CHANGE: ₱0.00"); }
            }
        });

        JButton btnConfirm = new JButton("ACCEPT PAYMENT & PRINT RECEIPT");
        btnConfirm.setPreferredSize(new Dimension(0, 65));
        btnConfirm.setBackground(new Color(40, 40, 40));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnConfirm.addActionListener(e -> {
            try {
                double cash = Double.parseDouble(txtCash.getText().trim());
                if (cash < damageAmount) {
                    JOptionPane.showMessageDialog(this, "Insufficient amount paid.");
                    return;
                }

                dao.ReservationDB resDB = new dao.ReservationDB();

                boolean success = resDB.processCheckOut(resId, 0);

                if (success) {
                    String invoice = "DMG-" + (System.currentTimeMillis() / 1000);

                    new DamageReceipt(resId, guestName, damageAmount, invoice, cash).setVisible(true);

                    parent.refreshTable();
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Database Error: Could not finalize checkout.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
            }
        });        add(btnConfirm, BorderLayout.SOUTH);
    }

    class DamageReceipt extends JFrame {
        public DamageReceipt(int resId, String guestName, double amount, String inv, double cash) {
            setTitle("Aurelia Grand - Damage Penalty Record");
            setSize(360, 520);
            setLocationRelativeTo(null);

            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font("Monospaced", Font.PLAIN, 12));
            area.setBorder(new EmptyBorder(25, 25, 25, 25));

            area.setText(
                    "       AURELIA GRAND HOTEL\n" +
                            "     DAMAGE SETTLEMENT RECORD\n" +
                            "====================================\n" +
                            "INVOICE:    " + inv + "\n" +
                            "DATE:       " + java.time.LocalDate.now() + "\n" +
                            "GUEST:      " + guestName.toUpperCase() + "\n" +
                            "RES ID:     #" + resId + "\n" +
                            "------------------------------------\n" +
                            "DESCRIPTION: Property Damage\n" +
                            "Assessed during room inspection.\n" +
                            "------------------------------------\n" +
                            "TOTAL PENALTY:  P " + String.format("%,12.2f", amount) + "\n" +
                            "CASH RECEIVED:  P " + String.format("%,12.2f", cash) + "\n" +
                            "CHANGE GIVEN:   P " + String.format("%,12.2f", cash - amount) + "\n" +
                            "------------------------------------\n" +
                            "STATUS:         PAID IN FULL\n" +
                            "====================================\n" +
                            "   Account Cleared for Guest Exit\n" +
                            "===================================="
            );
            add(new JScrollPane(area));

            JButton btnClose = new JButton("Close Record");
            btnClose.addActionListener(e -> this.dispose());
            add(btnClose, BorderLayout.SOUTH);
        }
    }
}