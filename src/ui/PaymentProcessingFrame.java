package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PaymentProcessingFrame extends JFrame {
    private final Color BG_COLOR = new Color(245, 246, 247);
    private double remainingBalance;
    private JTextField txtCash;
    private JTextField txtPromo;
    private JLabel lblChange;
    private JLabel lblBalValue;

    private String guestName;
    private int guestId;
    private double totalAmount;
    private double depositPaid;

    public PaymentProcessingFrame(int resId, String guestName, int guestId, double total, double paid, ReservationTablePanel parent) {
        this.remainingBalance = total - paid;
        this.guestName = guestName;
        this.guestId = guestId;
        this.totalAmount = total;
        this.depositPaid = paid;

        setTitle("Payment Processing - Reservation #" + resId);
        setSize(400, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        JPanel topPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(25, 25, 25, 25)
        ));

        topPanel.add(new JLabel("Total Amount:"));
        topPanel.add(new JLabel("₱" + String.format("%,.2f", total)));
        topPanel.add(new JLabel("Downpayment Paid:"));
        topPanel.add(new JLabel("₱" + String.format("%,.2f", paid)));

        JLabel lblBalTitle = new JLabel("TOTAL TO PAY:");
        lblBalTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        topPanel.add(lblBalTitle);

        lblBalValue = new JLabel("₱" + String.format("%,.2f", remainingBalance));
        lblBalValue.setFont(new Font("SansSerif", Font.BOLD, 15));
        topPanel.add(lblBalValue);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(null);
        centerPanel.setOpaque(false);

        JLabel lblCashLabel = new JLabel("Cash Received:");
        lblCashLabel.setBounds(40, 30, 200, 25);
        centerPanel.add(lblCashLabel);

        txtCash = new JTextField();
        txtCash.setBounds(40, 60, 300, 45);
        txtCash.setFont(new Font("SansSerif", Font.BOLD, 20));
        txtCash.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        centerPanel.add(txtCash);

        JLabel lblPromoLabel = new JLabel("Promo Code:");
        lblPromoLabel.setBounds(40, 210, 150, 25);
        centerPanel.add(lblPromoLabel);

        txtPromo = new JTextField();
        txtPromo.setBounds(40, 240, 200, 40);
        centerPanel.add(txtPromo);

        JButton btnApplyPromo = new JButton("Apply");
        btnApplyPromo.setBounds(250, 240, 70, 40);
        centerPanel.add(btnApplyPromo);

        JLabel lblChangeTitle = new JLabel("Change:");
        lblChangeTitle.setBounds(40, 125, 100, 25);
        centerPanel.add(lblChangeTitle);

        lblChange = new JLabel("₱0.00");
        lblChange.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblChange.setBounds(40, 155, 300, 35);
        centerPanel.add(lblChange);
        add(centerPanel, BorderLayout.CENTER);

        txtCash.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateChange();
            }
        });

        JButton btnConfirm = new JButton("Confirm Payment & Print Receipt");
        btnConfirm.setPreferredSize(new Dimension(0, 60));
        btnConfirm.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnConfirm.addActionListener(e -> handleConfirmation(resId, parent));
        add(btnConfirm, BorderLayout.SOUTH);

        btnApplyPromo.addActionListener(e -> {
            dao.PaymentDB payDB = new dao.PaymentDB();
            double discount = payDB.calculateFinalDiscount(totalAmount, txtPromo.getText().trim(), false);

            if (discount > 0) {
                this.remainingBalance -= discount;
                lblBalValue.setText("₱" + String.format("%,.2f", remainingBalance));
                JOptionPane.showMessageDialog(this, String.format("Discount of ₱%,.2f applied!", discount));
                btnApplyPromo.setEnabled(false);
                txtPromo.setEditable(false);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Promo Code.");
            }
        });
    }

    private void calculateChange() {
        try {
            String input = txtCash.getText().trim();
            if (input.isEmpty()) {
                lblChange.setText("₱0.00");
                return;
            }
            double cash = Double.parseDouble(input);
            double change = cash - remainingBalance;
            lblChange.setText("₱" + String.format("%,.2f", Math.max(0, change)));
        } catch (NumberFormatException e) {
            lblChange.setText("₱0.00");
        }
    }

    private void handleConfirmation(int resId, ReservationTablePanel parent) {
        try {
            double cashReceived = Double.parseDouble(txtCash.getText().trim());
            dao.PaymentDB payDB = new dao.PaymentDB();

            double discountAmount = payDB.calculateFinalDiscount(totalAmount, txtPromo.getText().trim(), false);

            if (cashReceived < remainingBalance) {
                JOptionPane.showMessageDialog(this, "Insufficient cash amount!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String invoiceNo = "INV-" + (System.currentTimeMillis() / 1000) + "-" + resId;

            boolean paymentSuccess = payDB.processPayment(
                    resId,
                    remainingBalance,
                    totalAmount,
                    discountAmount,
                    "Cash",
                    "Full Payment",
                    invoiceNo
            );

            if (paymentSuccess) {
                new dao.ReservationDB().updateStatus(resId, "Confirmed");
                JOptionPane.showMessageDialog(this, "Full payment received.");

                new OfficialReceiptFrame(resId, guestName, guestId, totalAmount, depositPaid, cashReceived).setVisible(true);

                parent.refreshTable();
                this.dispose();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
        }
    }

    class OfficialReceiptFrame extends JFrame {
        public OfficialReceiptFrame(int resId, String guestName, int guestId, double total, double paid, double cash) {
            setTitle("Official Receipt - Aurelia Grand");
            setSize(400, 600);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            String invoiceNo = "INV-" + (System.currentTimeMillis() / 1000) + "-" + resId;

            JPanel paper = new JPanel();
            paper.setBackground(Color.WHITE);
            paper.setBorder(new EmptyBorder(30, 30, 30, 30));
            paper.setLayout(new BoxLayout(paper, BoxLayout.Y_AXIS));

            JLabel header = new JLabel("AURELIA GRAND HOTEL");
            header.setFont(new Font("Serif", Font.BOLD, 20));
            header.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel subHeader = new JLabel("Official Payment Receipt");
            subHeader.setFont(new Font("SansSerif", Font.PLAIN, 12));
            subHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

            JTextArea details = new JTextArea();
            details.setEditable(false);
            details.setFont(new Font("Monospaced", Font.PLAIN, 12));

            dao.PaymentDB payDB = new dao.PaymentDB();
            double discountAmount = payDB.calculateFinalDiscount(total, txtPromo.getText().trim(), false);
            double finalBalance = (total - paid) - discountAmount;

            details.setText(
                    "========================================\n" +
                            " INVOICE NO:    " + invoiceNo + "\n" +
                            " DATE:          " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
                            "========================================\n" +
                            " GUEST DETAILS:\n" +
                            " Name:          " + guestName.toUpperCase() + "\n" +
                            " Guest ID:      " + guestId + "\n" +
                            " Reservation ID:" + resId + "\n" +
                            "----------------------------------------\n" +
                            " PAYMENT SUMMARY:\n" +
                            " Total Amount:   P " + String.format("%,12.2f", total) + "\n" +
                            " Downpayment:    P " + String.format("%,12.2f", paid) + "\n" +
                            " DISCOUNT:       P " + String.format("%,12.2f", discountAmount) + "\n" + // Add this line!
                            "----------------------------------------\n" +
                            " BALANCE PAID:   P " + String.format("%,12.2f", finalBalance) + "\n" +
                            " CASH RECEIVED:  P " + String.format("%,12.2f", cash) + "\n" +
                            " CHANGE GIVEN:   P " + String.format("%,12.2f", (cash - finalBalance)) + "\n" +
                            "========================================\n\n" +
                            "       THANK YOU FOR CHOOSING\n" +
                            "           AURELIA GRAND!"
            );

            paper.add(header);
            paper.add(subHeader);
            paper.add(Box.createRigidArea(new Dimension(0, 20)));
            paper.add(details);

            add(new JScrollPane(paper), BorderLayout.CENTER);

            JButton btnClose = new JButton("Close Receipt");
            btnClose.setPreferredSize(new Dimension(0, 40));
            btnClose.addActionListener(e -> this.dispose());
            add(btnClose, BorderLayout.SOUTH);
        }
    }
}