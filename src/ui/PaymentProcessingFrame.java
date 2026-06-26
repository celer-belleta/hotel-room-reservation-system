package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Calendar;

public class PaymentProcessingFrame extends JFrame {
    private final Color BG_COLOR = new Color(245, 246, 247);
    private double remainingBalance;
    private final JTextField txtCash;
    private final JTextField txtPromo;
    private final JLabel lblChange;
    private final JLabel lblBalValue;

    private final String guestName;
    private final int guestId;
    private final double totalAmount;
    private final double depositPaid;


    public PaymentProcessingFrame(int resId, String guestName, int guestId, double total, double paid,
                                  ReservationTablePanel parent) {
        this.guestName = guestName;
        this.guestId = guestId;
        this.totalAmount = total;
        this.depositPaid = paid;
        this.remainingBalance = total - paid;

        this.lblBalValue = new JLabel();
        this.lblChange = new JLabel("₱0.00");
        this.txtCash = new JTextField();
        this.txtPromo = new JTextField();

        setTitle("Payment Processing - Reservation #" + resId);
        setSize(400, 600); // Increased height slightly to fit new info
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // VAT CALCULATIONS
        double vatRate = 0.12;
        double vatableSales = totalAmount / (1 + vatRate);
        double vatAmount = totalAmount - vatableSales;

        // Changed GridLayout from 3 to 4 to accommodate VAT row
        JPanel topPanel = new JPanel(new GridLayout(4, 1, 0, 8));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(25, 40, 25, 40)
        ));

        JPanel row1 = new JPanel(new BorderLayout());
        row1.setOpaque(false);
        row1.add(new JLabel("Total Amount Due:"), BorderLayout.WEST);
        row1.add(new JLabel("₱" + String.format("%,.2f", totalAmount)), BorderLayout.EAST);

        JPanel row2 = new JPanel(new BorderLayout());
        row2.setOpaque(false);
        row2.add(new JLabel("Downpayment Paid:"), BorderLayout.WEST);
        row2.add(new JLabel("₱" + String.format("%,.2f", depositPaid)), BorderLayout.EAST);

        // NEW: VAT BREAKDOWN ROW
        JPanel rowVat = new JPanel(new BorderLayout());
        rowVat.setOpaque(false);
        JLabel lblVatInfo = new JLabel("Inclusive of 12% VAT:");
        lblVatInfo.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblVatInfo.setForeground(Color.BLACK);
        rowVat.add(lblVatInfo, BorderLayout.WEST);

        JLabel lblVatValue = new JLabel("₱" + String.format("%,.2f", vatAmount));
        lblVatValue.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblVatValue.setForeground(Color.BLACK);
        rowVat.add(lblVatValue, BorderLayout.EAST);

        JPanel row3 = new JPanel(new BorderLayout());
        row3.setOpaque(false);
        JLabel lblBalTitle = new JLabel("OUTSTANDING BALANCE:");
        lblBalTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        row3.add(lblBalTitle, BorderLayout.WEST);

        lblBalValue.setText("₱" + String.format("%,.2f", remainingBalance));
        lblBalValue.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblBalValue.setForeground(new Color(180, 0, 0));
        row3.add(lblBalValue, BorderLayout.EAST);

        topPanel.add(row1);
        topPanel.add(row2);
        topPanel.add(rowVat); // Added the new VAT row
        topPanel.add(row3);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(null);
        centerPanel.setOpaque(false);

        JLabel lblCashLabel = new JLabel("ENTER CASH RECEIVED:");
        lblCashLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblCashLabel.setBounds(40, 20, 200, 20);
        centerPanel.add(lblCashLabel);

        txtCash.setBounds(40, 45, 310, 50); // Adjusted width to fit margin
        txtCash.setFont(new Font("SansSerif", Font.BOLD, 22));
        txtCash.setHorizontalAlignment(JTextField.CENTER);
        txtCash.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        centerPanel.add(txtCash);

        JLabel lblChangeTitle = new JLabel("CHANGE DUE:");
        lblChangeTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblChangeTitle.setBounds(40, 110, 100, 20);
        centerPanel.add(lblChangeTitle);

        lblChange.setBounds(40, 135, 310, 40);
        lblChange.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblChange.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(lblChange);

        JLabel lblPromoLabel = new JLabel("PROMO CODE:");
        lblPromoLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblPromoLabel.setBounds(40, 200, 100, 20);
        centerPanel.add(lblPromoLabel);

        txtPromo.setBounds(40, 225, 200, 35);
        centerPanel.add(txtPromo);

        JButton btnApplyPromo = new JButton("APPLY");
        btnApplyPromo.setBounds(255, 225, 95, 35);
        btnApplyPromo.setFocusPainted(false);
        centerPanel.add(btnApplyPromo);

        add(centerPanel, BorderLayout.CENTER);

        txtCash.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateChange();
            }
        });

        JButton btnConfirm = new JButton("Confirm Payment & Print Receipt");
        btnConfirm.setPreferredSize(new Dimension(0, 60));
        btnConfirm.setBackground(new Color(40, 40, 40));
        btnConfirm.setForeground(Color.WHITE);
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
                calculateChange();
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
            String cashStr = txtCash.getText().trim();
            if (cashStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter cash received!");
                return;
            }

            double cashReceived = Double.parseDouble(cashStr);
            dao.PaymentDB payDB = new dao.PaymentDB();

            double discountAmount = payDB.calculateFinalDiscount(totalAmount, txtPromo.getText().trim(), false);

            if (cashReceived < remainingBalance) {
                JOptionPane.showMessageDialog(this, "Insufficient cash amount!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String invoiceNo = "INV-" + (System.currentTimeMillis() / 1000) + "-" + resId;

            boolean paymentSuccess = payDB.processPayment(
                    resId, remainingBalance, totalAmount, discountAmount,
                    "Cash", "Full Payment", invoiceNo
            );

            if (paymentSuccess) {
                new dao.ReservationDB().updateCartStatusByResId(resId, "Checked-In");
                JOptionPane.showMessageDialog(this, "Payment successful.");

                String downpaymentDate = payDB.getDownpaymentDate(resId);

                new OfficialReceiptFrame(resId, guestName, guestId, totalAmount, depositPaid,
                        cashReceived, downpaymentDate, invoiceNo).setVisible(true);

                parent.refreshTable();
                this.dispose();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
        }
    }
    class OfficialReceiptFrame extends JFrame {
        public OfficialReceiptFrame(int resId, String guestName, int guestId, double total,
                                    double paid, double cash, String downpaymentDate, String invoiceNo) {
            setTitle("Official Receipt - Aurelia Grand");
            setSize(420, 650);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel paper = new JPanel();
            paper.setBackground(Color.WHITE);
            paper.setBorder(new EmptyBorder(30, 30, 30, 30));
            paper.setLayout(new BoxLayout(paper, BoxLayout.Y_AXIS));

            JLabel header = new JLabel("AURELIA GRAND HOTEL");
            header.setFont(new Font("Serif", Font.BOLD, 22));
            header.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel subHeader = new JLabel("Official Payment Receipt");
            subHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

            JTextArea details = new JTextArea();
            details.setEditable(false);
            details.setFont(new Font("Monospaced", Font.PLAIN, 12));

            // VAT CALCULATIONS (Philippine Standard 12%)
            double vatRate = 0.12;
            double vatableSales = total / (1 + vatRate);
            double vatAmount = total - vatableSales;

            dao.PaymentDB payDB = new dao.PaymentDB();
            double promoDiscount = payDB.calculateFinalDiscount(total, txtPromo.getText().trim(), false);
            double finalBalancePaid = remainingBalance;

            details.setText(
                    "========================================\n" +
                            " INVOICE NO:    " + invoiceNo + "\n" +
                            " DATE:          " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
                            "========================================\n" +
                            " GUEST: " + guestName.toUpperCase() + "\n" +
                            " RES ID: #" + resId + "\n" +
                            "----------------------------------------\n" +
                            " VATABLE SALES:  P " + String.format("%,12.2f", vatableSales) + "\n" +
                            " VAT AMOUNT(12%):P " + String.format("%,12.2f", vatAmount) + "\n" +
                            " TOTAL AMOUNT:   P " + String.format("%,12.2f", total) + "\n" +
                            "----------------------------------------\n" +
                            " DOWNPAYMENT ON: " + downpaymentDate + "\n" +
                            " BALANCE PAID:   P " + String.format("%,12.2f", finalBalancePaid) + "\n" +
                            "----------------------------------------\n" +
                            " CASH RECEIVED:  P " + String.format("%,12.2f", cash) + "\n" +
                            " CHANGE GIVEN:   P " + String.format("%,12.2f", cash - finalBalancePaid) + "\n" +
                            "========================================\n" +
                            "    THANK YOU FOR STAYING WITH US!\n" +
                            "========================================\n"
            );

            paper.add(header);
            paper.add(subHeader);
            paper.add(Box.createRigidArea(new Dimension(0, 20)));
            paper.add(details);

            add(new JScrollPane(paper), BorderLayout.CENTER);

            JButton btnClose = new JButton("Close & Finish");
            btnClose.addActionListener(e -> this.dispose());
            add(btnClose, BorderLayout.SOUTH);
        }
    }
}