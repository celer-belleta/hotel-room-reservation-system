package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TermsPanel extends JPanel {
    private final Color AURELIA_GOLD = new Color(197, 160, 89);
    private final Color NAVY_BLUE = new Color(44, 62, 80);
    private final Color GLASS_WHITE = new Color(255, 255, 255, 15);

    public TermsPanel() {
        setLayout(new BorderLayout());
        setBackground(NAVY_BLUE);
        setBorder(new EmptyBorder(20, 60, 20, 60));

        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setOpaque(false);
        GridBagConstraints hGbc = new GridBagConstraints();
        hGbc.gridx = 0; hGbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblMainTitle = new JLabel("AURELIA GRAND HOTEL", SwingConstants.CENTER);
        lblMainTitle.setFont(new Font("Serif", Font.PLAIN, 16));
        lblMainTitle.setForeground(AURELIA_GOLD);

        JLabel lblSubTitle = new JLabel("GUEST POLICIES & TERMS", SwingConstants.CENTER);
        lblSubTitle.setFont(new Font("Serif", Font.BOLD, 32));
        lblSubTitle.setForeground(Color.WHITE);

        hGbc.gridy = 0;
        headerPanel.add(lblMainTitle, hGbc);
        hGbc.gridy = 1;
        hGbc.insets = new Insets(0, 0, 10, 0);
        headerPanel.add(lblSubTitle, hGbc);

        add(headerPanel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        gridPanel.add(createStyledCard("1. RESERVATION DEPOSIT",
                "To secure your booking, a 20% downpayment is required. This amount is non-refundable and will be deducted from your total bill upon checkout."), gbc);

        gbc.gridx = 1;
        gridPanel.add(createStyledCard("2. MEMBER DISCOUNTS",
                "The 20% Senior/PWD discount applies only to the room rate for registered members with valid government IDs."), gbc);

        gbc.gridx = 2;
        gridPanel.add(createStyledCard("3. ARRIVAL & DEPARTURE",
                "Check-in: 2:00 PM. Check-out: 12:00 PM. Late check-outs are billed hourly based on room availability."), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gridPanel.add(createStyledCard("4. ACCIDENTAL DAMAGE",
                "Guests are liable for hardware like LED TVs and Mini-Fridges. Repairs are billed to your account upon inspection."), gbc);

        gbc.gridx = 1;
        gridPanel.add(createStyledCard("5. MISSING ASSETS",
                "Towels, hair dryers, and remotes are hotel property. Missing items are charged at full replacement value."), gbc);

        gbc.gridx = 2;
        gridPanel.add(createStyledCard("6. HOUSE RULES",
                "Strictly Non-Smoking. A PHP 5,000 deep-cleaning fee applies to violations. No pets allowed in rooms."), gbc);

        add(gridPanel, BorderLayout.CENTER);

        JLabel lblFooter = new JLabel("Thank you for choosing Aurelia Grand. Enjoy your stay.", SwingConstants.CENTER);
        lblFooter.setFont(new Font("SansSerif", Font.ITALIC, 13));
        lblFooter.setForeground(new Color(180, 180, 180));
        lblFooter.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(lblFooter, BorderLayout.SOUTH);
    }

    private JPanel createStyledCard(String title, String text) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(255, 255, 255, 35));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(GLASS_WHITE);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setForeground(AURELIA_GOLD);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblTitle, BorderLayout.NORTH);

        JTextArea area = new JTextArea(text);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setOpaque(false);
        area.setForeground(Color.WHITE);
        area.setFocusable(false);

        area.setPreferredSize(new Dimension(180, 90));

        card.add(area, BorderLayout.CENTER);

        return card;
    }
}