package ui;

import dao.RoomDB;
import model.Room;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class RoomPreviewPanel extends JPanel {
    private JPanel dynamicContentPanel;
    private RoomDB roomDB;
    private MainFrame mainFrame;

    private final Color AURELIA_GOLD = new Color(197, 160, 89);
    private final Color NAVY_BLUE = new Color(44, 62, 80);
    private final Color ROW_BG = new Color(255, 255, 255, 10);

    public RoomPreviewPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.roomDB = new RoomDB();

        setLayout(new BorderLayout());
        setBackground(NAVY_BLUE);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(30, 60, 30, 60));

        JPanel intrigueHeader = new JPanel(new BorderLayout());
        intrigueHeader.setBackground(AURELIA_GOLD);
        intrigueHeader.setMaximumSize(new Dimension(1600, 50));
        JLabel lblIntrigue = new JLabel("<html><b>AURELIA REWARDS:</b> SIGN UP TO LOCK IN EXCLUSIVE DISCOUNTS ON ALL PACKAGES!</html>", SwingConstants.CENTER);
        lblIntrigue.setForeground(Color.WHITE);
        lblIntrigue.setFont(new Font("SansSerif", Font.BOLD, 14));
        intrigueHeader.add(lblIntrigue);

        mainContent.add(intrigueHeader);
        mainContent.add(Box.createRigidArea(new Dimension(0, 40)));

        JLabel lblHeader = new JLabel("Available Accommodations & Packages");
        lblHeader.setFont(new Font("Serif", Font.ITALIC, 32));
        lblHeader.setForeground(AURELIA_GOLD);
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainContent.add(lblHeader);
        mainContent.add(Box.createRigidArea(new Dimension(0, 25)));

        dynamicContentPanel = new JPanel();
        dynamicContentPanel.setLayout(new BoxLayout(dynamicContentPanel, BoxLayout.Y_AXIS));
        dynamicContentPanel.setOpaque(false);

        loadRoomsByPackage();
        mainContent.add(dynamicContentPanel);

        JButton btnSignUp = new JButton("READY TO BOOK? SIGN UP NOW TO START");
        btnSignUp.setPreferredSize(new Dimension(450, 65));
        btnSignUp.setBackground(AURELIA_GOLD);
        btnSignUp.setForeground(Color.WHITE);
        btnSignUp.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnSignUp.setFocusPainted(false);
        btnSignUp.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        btnSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSignUp.addActionListener(e -> mainFrame.showCard("SIGNUP_PAGE"));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 60));
        footer.setOpaque(false);
        footer.add(btnSignUp);

        JPanel scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.setOpaque(false);
        scrollWrapper.add(mainContent, BorderLayout.NORTH);
        scrollWrapper.add(footer, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(scrollWrapper);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(25);
        add(scroll, BorderLayout.CENTER);
    }

    public void loadRoomsByPackage() {
        dynamicContentPanel.removeAll();
        ArrayList<Room> allRooms = roomDB.getAllRooms();

        ArrayList<Room> roomOnlyList = new ArrayList<>();
        ArrayList<Room> breakfastList = new ArrayList<>();
        ArrayList<Room> amenitiesList = new ArrayList<>();

        for (Room r : allRooms) {
            String pType = r.getPackageType();
            if (pType == null) continue;
            if (pType.equalsIgnoreCase("Room Only")) roomOnlyList.add(r);
            else if (pType.equalsIgnoreCase("Room + Breakfast")) breakfastList.add(r);
            else if (pType.equalsIgnoreCase("Room + Amenities")) amenitiesList.add(r);
        }

        addPackageSection("Room Only", 0.0, "Basic room stay without additional meals or services.", roomOnlyList);
        addPackageSection("Room + Breakfast", 500.0, "Standard stay inclusive of daily breakfast service.", breakfastList);
        addPackageSection("Room + Amenities", 1000.0, "Premium stay with full access to leisure facilities.", amenitiesList);

        dynamicContentPanel.revalidate();
        dynamicContentPanel.repaint();
    }

    private void addPackageSection(String title, double extraPrice, String desc, ArrayList<Room> rooms) {
        if (rooms.isEmpty()) return;

        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setOpaque(false);
        sectionHeader.setBorder(new EmptyBorder(30, 10, 10, 10));
        sectionHeader.setMaximumSize(new Dimension(1600, 85));

        JLabel lblTitle = new JLabel("<html><b style='font-size:16pt; color:#FFF;'>" + title + "</b><br><font color='#BBB'>" + desc + "</font></html>");
        sectionHeader.add(lblTitle, BorderLayout.WEST);

        dynamicContentPanel.add(sectionHeader);
        for (Room r : rooms) {
            dynamicContentPanel.add(createRoomRow(r, extraPrice));
        }
        dynamicContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    private JPanel createRoomRow(Room r, double extra) {
        JPanel row = new JPanel(new BorderLayout(30, 0));
        row.setOpaque(false);
        row.setBackground(ROW_BG);

        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 30)),
                new EmptyBorder(25, 20, 25, 20)
        ));
        row.setMaximumSize(new Dimension(1600, 250));

        JPanel imgBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                String type = (r.getType() == null) ? "" : r.getType().toLowerCase();
                String filename = "superior.jpg";
                if (type.contains("deluxe")) filename = "deluxe.jpg";
                else if (type.contains("family")) filename = "family.jpg";
                else if (type.contains("specialty")) filename = "specialty.jpg";
                ImageIcon icon = new ImageIcon("src/images/" + filename);
                g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        imgBox.setPreferredSize(new Dimension(280, 160));
        imgBox.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50)));

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 8));
        info.setOpaque(false);

        JLabel lblName = new JLabel(r.getType() + " (Room " + r.getRoomNumber() + ")");
        lblName.setFont(new Font("Serif", Font.BOLD, 22));
        lblName.setForeground(AURELIA_GOLD);

        info.add(lblName);
        info.add(new JLabel("<html><div style='color:#DDD;'>" + getFullSpecs(r.getType()) + "</div></html>"));
        info.add(new JLabel("<html><font color='#BBB'>Max Capacity: " + r.getMaxGuest() + " Guests | <b style='color:#DDD;'>Account Required</b></font></html>"));

        double standardTotal = r.getPrice() + extra;
        double memberTotal = standardTotal * 0.80;

        JPanel pricePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        pricePanel.setOpaque(false);

        JLabel lblStandard = new JLabel("<html><font color='#AAA'>Standard: <span style='text-decoration:line-through;'>₱" + String.format("%,.0f", standardTotal) + "</span></font></html>");
        lblStandard.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel lblMember = new JLabel("₱" + String.format("%,.0f", memberTotal));
        lblMember.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblMember.setForeground(AURELIA_GOLD);
        lblMember.setHorizontalAlignment(SwingConstants.RIGHT);

        pricePanel.add(lblStandard);
        pricePanel.add(lblMember);

        row.add(imgBox, BorderLayout.WEST);
        row.add(info, BorderLayout.CENTER);
        row.add(pricePanel, BorderLayout.EAST);
        return row;
    }

    private String getFullSpecs(String type) {
        String t = (type == null) ? "" : type.trim();
        if (t.equalsIgnoreCase("Standard Room"))
            return "• 1 Queen Bed, • Hot and Cold Shower, • LED TV & Wi-Fi";
        if (t.equalsIgnoreCase("Deluxe Room"))
            return "• 1 King Bed, • City View, • Mini-Fridge, • LED TV & Wi-Fi";
        if (t.equalsIgnoreCase("Family Room"))
            return "• 2 Queen Beds, • Pantry Area, • Mini-Bar, • LED TV & Wi-Fi";
        if (t.equalsIgnoreCase("Specialty Room"))
            return "• 1 California King, • Balcony, • Bathtub, • LED TV & Wi-Fi";
        return "• Standard Amenities Included";
    }
}