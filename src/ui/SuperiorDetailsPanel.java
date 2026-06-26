package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SuperiorDetailsPanel extends JPanel {
    private final Color AURELIA_GOLD = new Color(197, 160, 89);
    private final Color NAVY_BLUE = new Color(44, 62, 80);

    public SuperiorDetailsPanel(GuestDashboard parent) {
        setLayout(new BorderLayout());
        setBackground(NAVY_BLUE);

        JLabel lblTitle = new JLabel("SUPERIOR ROOM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Serif", Font.PLAIN, 36));
        lblTitle.setForeground(AURELIA_GOLD);
        lblTitle.setBorder(new EmptyBorder(40, 0, 30, 0));
        add(lblTitle, BorderLayout.NORTH);

        JLabel mainImage = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ImageIcon icon = new ImageIcon("src/images/superior.jpg");
                if (icon.getImage() != null) {
                    g2.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), null);
                }
            }
        };
        mainImage.setPreferredSize(new Dimension(850, 480));
        mainImage.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1));

        JPanel imageWrapper = new JPanel(new BorderLayout());
        imageWrapper.setOpaque(false);
        imageWrapper.setBorder(new EmptyBorder(0, 80, 0, 80));
        imageWrapper.add(mainImage, BorderLayout.CENTER);
        add(imageWrapper, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 100, 0));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(40, 120, 60, 120));

        String highlights = "<html><b style='color:#C5A059; font-size:14pt;'>HIGHLIGHTS</b><br><br>" +
                "<font color='white'>• 1 Queen Bed<br>" +
                "• Hot and Cold Shower<br>" +
                "• LED TV & High-Speed Wi-Fi</font></html>";

        String details = "<html><b style='color:#C5A059; font-size:14pt;'>ROOM DETAIL</b><br><br>" +
                "<font color='white'><b>ROOM SIZE:</b> 25 sqm / 269.1 sqft<br>" +
                "<b>CAPACITY:</b> Maximum 3 Adults & 2 Children<br>" +
                "<b>BED TYPE:</b> 1 Queen Bed, or 2 Single Beds</font></html>";

        JLabel lblHigh = new JLabel(highlights);
        JLabel lblDet = new JLabel(details);
        lblHigh.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblDet.setFont(new Font("SansSerif", Font.PLAIN, 15));

        infoPanel.add(lblHigh);
        infoPanel.add(lblDet);

        add(infoPanel, BorderLayout.SOUTH);
    }
}