package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SpecialtyDetailsPanel extends JPanel {
    private final Color AURELIA_GOLD = new Color(197, 160, 89);

    public SpecialtyDetailsPanel(GuestDashboard parent) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("SPECIALTY ROOM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Serif", Font.PLAIN, 32));
        lblTitle.setBorder(new EmptyBorder(30, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        JLabel mainImage = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                ImageIcon icon = new ImageIcon("src/images/specialty.jpg");
                if (icon.getImage() != null) {
                    g2.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), null);
                }
            }
        };
        mainImage.setPreferredSize(new Dimension(800, 450));
        mainImage.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        JPanel imageWrapper = new JPanel(new BorderLayout());
        imageWrapper.setBackground(Color.WHITE);
        imageWrapper.setBorder(new EmptyBorder(0, 50, 0, 50));
        imageWrapper.add(mainImage, BorderLayout.CENTER);
        add(imageWrapper, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 50, 0));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(30, 100, 50, 100));

        String highlights = "<html><b style='color:#C5A059;'>HIGHLIGHTS</b><br><br>" +
                "• 1 California King Bed<br>" +
                "• Small Balcony<br>" +
                "• Bathtub<br>" +
                "• LED TV & High-Speed Wi-Fi</html>";

        String details = "<html><b style='color:#C5A059;'>ROOM DETAIL</b><br><br>" +
                "<b>ROOM SIZE:</b> 25 sqm / 269.1 sqft<br>" +
                "<b>CAPACITY:</b> Maximum 3 Adults & 2 Children<br>" +
                "<b>BED TYPE:</b> 1 California King Bed</html>";

        JLabel lblHigh = new JLabel(highlights);
        JLabel lblDet = new JLabel(details);

        lblHigh.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDet.setFont(new Font("Arial", Font.PLAIN, 14));

        infoPanel.add(lblHigh);
        infoPanel.add(lblDet);

        add(infoPanel, BorderLayout.SOUTH);
    }
}