package ui;

import dao.RoomDB;
import model.Room;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class RoomBookingPanel extends JPanel {
    private JPanel calendarContainer;
    private JLabel lblCheckIn, lblCheckOut, lblCartTotal, lblGuestsValue, lblCartItems;
    private JPanel dynamicContentPanel;
    private RoomDB roomDB;

    private int startMonthIndex = 4; // May 2026
    private double currentTotal = 0.0;
    private int cartItemCount = 0;
    private int currentGuestCount = 2;

    private Calendar checkInCal = null;
    private Calendar checkOutCal = null;

    private final Color AURELIA_GOLD = new Color(197, 160, 89);
    private final Color BG_COLOR = new Color(255, 255, 255);

    private Room selectedRoom = null;
    private int loggedInGuestId;

    private ViewBookingsPanel viewBookingsRef;

    public RoomBookingPanel(int guestId, ViewBookingsPanel vbp) {
        this.loggedInGuestId = guestId;
        this.viewBookingsRef = vbp;
        this.roomDB = new RoomDB();

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(BG_COLOR);
        mainContent.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel statusHeader = createStatusHeader();
        statusHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(statusHeader);
        mainContent.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel navPanel = createCalendarNavigation();
        navPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(navPanel);

        JPanel calWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        calWrapper.setBackground(BG_COLOR);
        calWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        calendarContainer = new JPanel(new GridLayout(1, 2, 40, 0));
        calendarContainer.setBackground(BG_COLOR);
        updateCalendarDisplay();

        calWrapper.add(calendarContainer);
        mainContent.add(calWrapper);

        mainContent.add(Box.createRigidArea(new Dimension(0, 35)));

        JLabel lblHeader = new JLabel("Available Accommodations & Packages");
        lblHeader.setFont(new Font("Serif", Font.ITALIC, 28));
        lblHeader.setForeground(AURELIA_GOLD);
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(lblHeader);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        dynamicContentPanel = new JPanel();
        dynamicContentPanel.setLayout(new BoxLayout(dynamicContentPanel, BoxLayout.Y_AXIS));
        dynamicContentPanel.setBackground(BG_COLOR);
        dynamicContentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loadRoomsByPackage();
        mainContent.add(dynamicContentPanel);

        JPanel scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.setBackground(BG_COLOR);
        scrollWrapper.add(mainContent, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(scrollWrapper);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(25);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scroll, BorderLayout.CENTER);
        add(createFixedCart(), BorderLayout.EAST);
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

        addPackageSection("Room Only", 0.0,
                "Basic room stay without additional meals or services.", roomOnlyList);

        addPackageSection("Room + Breakfast", 500.0,
                "Standard stay inclusive of daily breakfast service.", breakfastList);

        addPackageSection("Room + Amenities", 1000.0,
                "Premium stay with full access to hotel leisure facilities.", amenitiesList);

        dynamicContentPanel.revalidate();
        dynamicContentPanel.repaint();
    }
    private void addPackageSection(String title, double extraPrice, String desc, ArrayList<Room> rooms) {
        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setBackground(new Color(250, 250, 250));
        sectionHeader.setBorder(new EmptyBorder(15, 10, 10, 10));
        sectionHeader.setMaximumSize(new Dimension(1600, 85));
        sectionHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("<html><b style='font-size:14pt; color:#333;'>" + title + "</b><br><font color='gray'>" + desc + "</font></html>");
        sectionHeader.add(lblTitle, BorderLayout.WEST);

        boolean found = false;

        dao.ReservationDB resDB = new dao.ReservationDB();

        for (Room r : rooms) {
            boolean isDateAvailable = true;

            if (checkInCal != null && checkOutCal != null) {
                java.sql.Date sqlIn = new java.sql.Date(checkInCal.getTimeInMillis());
                java.sql.Date sqlOut = new java.sql.Date(checkOutCal.getTimeInMillis());

                isDateAvailable = resDB.isRoomAvailable(r.getId(), sqlIn, sqlOut);
            }

            if (isDateAvailable && r.getMaxGuest() >= currentGuestCount) {
                if (!found) dynamicContentPanel.add(sectionHeader);
                JPanel row = createRoomRow(r, extraPrice);
                row.setAlignmentX(Component.LEFT_ALIGNMENT);
                dynamicContentPanel.add(row);
                found = true;
            }
        }

        if (found) dynamicContentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
    }
    private JPanel createRoomRow(Room r, double extra) {
        JPanel row = new JPanel(new BorderLayout(25, 0));
        row.setBackground(BG_COLOR);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(20, 10, 20, 10)
        ));
        row.setMaximumSize(new Dimension(1600, 220));

        JPanel imgBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                String type = (r.getType() == null) ? "" : r.getType().toLowerCase();
                String filename = "superior.jpg"; // Default fallback

                if (type.contains("deluxe")) filename = "deluxe.jpg";
                else if (type.contains("family")) filename = "family.jpg";
                else if (type.contains("specialty")) filename = "specialty.jpg";

                ImageIcon icon = new ImageIcon("src/images/" + filename);
                if (icon.getImage() != null && icon.getIconWidth() > 0) {
                    g2.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setColor(new Color(240, 240, 240));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(Color.GRAY);
                    g2.setFont(new Font("Arial", Font.ITALIC, 12));
                    g2.drawString("Image Not Found", 20, getHeight() / 2);
                }
            }
        };
        imgBox.setPreferredSize(new Dimension(240, 150));
        imgBox.setBackground(new Color(240, 240, 240));
        imgBox.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 5));
        info.setBackground(BG_COLOR);
        JLabel lblName = new JLabel(r.getType() + " (Room " + r.getRoomNumber() + ")");
        lblName.setFont(new Font("Serif", Font.BOLD, 19));
        lblName.setForeground(AURELIA_GOLD);

        String amenities = getAmenitiesByType(r.getType());
        info.add(lblName);
        info.add(new JLabel("<html><div style='color:#777;'>" + amenities + "</div></html>"));
        info.add(new JLabel("<html><font color='#444'>Max Capacity: " + r.getMaxGuest() + " Guests | <b>Downpayment Required</b></font></html>"));

        long nights = getSelectedNights();
        double total = (r.getPrice() + extra) * nights;

        JPanel pricePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        pricePanel.setBackground(BG_COLOR);
        JLabel lblPriceValue = new JLabel("₱" + String.format("%,.0f", total));
        lblPriceValue.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblPriceValue.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton btnBook = new JButton("BOOK NOW");
        btnBook.setBackground(AURELIA_GOLD);
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.setFont(new Font("Arial", Font.BOLD, 12));

        btnBook.addActionListener(e -> {
            this.selectedRoom = r;
            long nightsCount = getSelectedNights();
            double totalForThisRoom = (r.getPrice() + extra) * nightsCount;
            this.currentTotal = totalForThisRoom;
            this.cartItemCount = 1;

            lblCartItems.setText("Your Cart: " + cartItemCount + " Items");
            lblCartTotal.setText("Total ₱" + String.format("%,.2f", currentTotal));

            JOptionPane.showMessageDialog(this, r.getType() + " selected for booking!");
        });

        pricePanel.add(lblPriceValue);
        pricePanel.add(btnBook);

        row.add(imgBox, BorderLayout.WEST);
        row.add(info, BorderLayout.CENTER);
        row.add(pricePanel, BorderLayout.EAST);
        return row;
    }
    private String getAmenitiesByType(String type) {
        String roomType = (type == null) ? "" : type.trim();
        if (roomType.equalsIgnoreCase("Standard Room")) return "• 1 Queen Bed, • Hot and Cold Shower, • LED TV & High-Speed Wi-Fi";
        else if (roomType.equalsIgnoreCase("Deluxe Room")) return "• 1 King Bed, • City View, • Mini-Fridge, • LED TV & High-Speed Wi-Fi";
        else if (roomType.equalsIgnoreCase("Family Room")) return "• 2 Queen Beds, • Pantry Area with Microwave, • Mini-Bar, • LED TV & High-Speed Wi-Fi";
        else if (roomType.equalsIgnoreCase("Specialty Room")) return "• 1 California King Bed, • Small Balcony, • Bathtub, • LED TV & High-Speed Wi-Fi";
        return "• Standard Amenities Included";
    }

    private JPanel createStatusHeader() {
        JPanel p = new JPanel(new GridLayout(1, 3));
        p.setBackground(BG_COLOR);
        p.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        p.setMaximumSize(new Dimension(1600, 70));
        lblCheckIn = new JLabel("<html><b>Check-in</b><br>SELECT DATE</html>", SwingConstants.CENTER);
        lblCheckOut = new JLabel("<html><b>Check-out</b><br>SELECT DATE</html>", SwingConstants.CENTER);
        lblGuestsValue = new JLabel("<html><b>Guests</b><br>" + currentGuestCount + " ADULTS</html>", SwingConstants.CENTER);
        lblGuestsValue.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblGuestsValue.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { showPaxPicker(lblGuestsValue); }});
        p.add(lblGuestsValue); p.add(lblCheckIn); p.add(lblCheckOut);
        return p;
    }

    private void showPaxPicker(JLabel parent) {
        JPopupMenu popup = new JPopupMenu();
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(BG_COLOR);
        JButton m = new JButton("—"); JButton p = new JButton("+");
        JLabel c = new JLabel(String.valueOf(currentGuestCount));
        m.addActionListener(e -> { if(currentGuestCount > 1) { currentGuestCount--; c.setText(String.valueOf(currentGuestCount)); }});
        p.addActionListener(e -> { if(currentGuestCount < 6) { currentGuestCount++; c.setText(String.valueOf(currentGuestCount)); }});
        JButton done = new JButton("DONE");
        done.addActionListener(e -> {
            lblGuestsValue.setText("<html><b>Guests</b><br>" + currentGuestCount + " ADULTS</html>");
            loadRoomsByPackage();
            popup.setVisible(false);
        });
        panel.add(new JLabel("Adults: ")); panel.add(m); panel.add(c); panel.add(p); panel.add(done);
        popup.add(panel);
        popup.show(parent, 0, parent.getHeight());
    }

    private long getSelectedNights() {
        if (checkInCal == null || checkOutCal == null) return 1;
        long diff = checkOutCal.getTimeInMillis() - checkInCal.getTimeInMillis();
        long n = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return n <= 0 ? 1 : n;
    }

    private void handleDateSelection(int day, int month) {
        Calendar selected = Calendar.getInstance();
        selected.set(2026, month, day, 0, 0, 0);
        selected.set(Calendar.MILLISECOND, 0);
        if (checkInCal == null || (checkInCal != null && checkOutCal != null)) {
            checkInCal = selected; checkOutCal = null;
        } else {
            if (selected.after(checkInCal)) checkOutCal = selected;
            else { JOptionPane.showMessageDialog(this, "Check-out must be after Check-in!"); return; }
        }
        updateCalendarDisplay();
        updateStatusLabels();
        loadRoomsByPackage();
    }

    private JPanel createCalendarNavigation() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(BG_COLOR);
        nav.setMaximumSize(new Dimension(1600, 40));
        JButton b1 = new JButton("❮"); JButton b2 = new JButton("❯");
        b1.addActionListener(e -> { if(startMonthIndex > 0) { startMonthIndex--; updateCalendarDisplay(); }});
        b2.addActionListener(e -> { if(startMonthIndex < 10) { startMonthIndex++; updateCalendarDisplay(); }});
        nav.add(b1, BorderLayout.WEST); nav.add(b2, BorderLayout.EAST);
        return nav;
    }

    private void updateCalendarDisplay() {
        calendarContainer.removeAll();
        ((GridLayout)calendarContainer.getLayout()).setHgap(80);
        calendarContainer.add(renderMonth(startMonthIndex));
        calendarContainer.add(renderMonth(startMonthIndex + 1));
        calendarContainer.setPreferredSize(new Dimension(850, 400));
        calendarContainer.revalidate();
        calendarContainer.repaint();
    }

    private JPanel renderMonth(int month) {
        JPanel p = new JPanel(new BorderLayout(0, 15));
        p.setBackground(BG_COLOR);

        String[] months = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        JLabel title = new JLabel(months[month] + " 2026", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 22));
        p.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 7, 5, 5));
        grid.setBackground(BG_COLOR);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar cal = Calendar.getInstance();
        cal.set(2026, month, 1);
        int startShift = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < startShift; i++) grid.add(new JLabel(""));

        for (int i = 1; i <= daysInMonth; i++) {
            JButton b = new JButton(String.valueOf(i));
            b.setFont(new Font("SansSerif", Font.BOLD, 14));
            b.setPreferredSize(new Dimension(45, 40));

            Calendar cur = Calendar.getInstance();
            cur.set(2026, month, i, 0, 0, 0);
            cur.set(Calendar.MILLISECOND, 0);

            if (cur.before(today)) {
                b.setEnabled(false);
                b.setBackground(new Color(245, 245, 245));
                b.setForeground(Color.LIGHT_GRAY);
            } else {
                if (checkInCal != null && cur.equals(checkInCal)) {
                    b.setBackground(AURELIA_GOLD);
                    b.setForeground(Color.WHITE);
                } else if (checkOutCal != null && cur.equals(checkOutCal)) {
                    b.setBackground(AURELIA_GOLD);
                    b.setForeground(Color.WHITE);
                } else {
                    b.setBackground(Color.WHITE);
                    b.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
                }

                final int d = i;
                final int m = month;
                b.addActionListener(e -> handleDateSelection(d, m));
            }
            grid.add(b);
        }
        p.add(grid, BorderLayout.CENTER);
        return p;
    }
    private void updateStatusLabels() {
        String in = (checkInCal == null) ? "SELECT DATE" : String.format("%tB %te, %tY", checkInCal, checkInCal, checkInCal);
        String out = (checkOutCal == null) ? "SELECT DATE" : String.format("%tB %te, %tY", checkOutCal, checkOutCal, checkOutCal);
        lblCheckIn.setText("<html><b>Check-in</b><br>" + in.toUpperCase() + "</html>");
        lblCheckOut.setText("<html><b>Check-out</b><br>" + out.toUpperCase() + "</html>");
    }

    private JPanel createFixedCart() {
        JPanel cart = new JPanel();
        cart.setLayout(new BoxLayout(cart, BoxLayout.Y_AXIS));
        cart.setPreferredSize(new Dimension(300, 0));
        cart.setBackground(new Color(252, 252, 252));
        cart.setBorder(new EmptyBorder(30, 20, 0, 20));

        lblCartItems = new JLabel("Your Cart: 0 Items");
        lblCartItems.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblCartTotal = new JLabel("Total ₱0.00");
        lblCartTotal.setFont(new Font("Serif", Font.BOLD, 22));

        cart.add(lblCartItems);
        cart.add(Box.createRigidArea(new Dimension(0, 10)));
        cart.add(lblCartTotal);

        JButton checkOut = new JButton("CHECKOUT");
        checkOut.setMaximumSize(new Dimension(260, 50));
        checkOut.setBackground(new Color(40, 40, 40));
        checkOut.setForeground(Color.WHITE);

        checkOut.addActionListener(e -> {
            // 1. UI VALIDATION CHECKS
            if (selectedRoom == null) {
                JOptionPane.showMessageDialog(this, "Please select a room first!");
                return;
            }
            if (checkInCal == null || checkOutCal == null) {
                JOptionPane.showMessageDialog(this, "Please select your stay dates!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirm reservation for " + selectedRoom.getType() + " (Room " + selectedRoom.getRoomNumber() + ")?",
                    "Confirm Booking", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                if (this.loggedInGuestId <= 0) {
                    JOptionPane.showMessageDialog(this, "No valid guest session found. Please re-login.");
                    return;
                }

                dao.BookingDB bookingDao = new dao.BookingDB();

                int packageId = 1;
                if (selectedRoom.getPackageType().contains("Breakfast")) packageId = 2;
                else if (selectedRoom.getPackageType().contains("Amenities")) packageId = 3;

                boolean success = bookingDao.finalizeBooking(
                        selectedRoom.getId(),
                        this.loggedInGuestId,
                        packageId,
                        checkInCal,
                        checkOutCal,
                        currentTotal
                );

                if (success) {
                    JOptionPane.showMessageDialog(this, "Success! Your room is now reserved.");

                    selectedRoom = null;
                    currentTotal = 0.0;
                    cartItemCount = 0;

                    lblCartItems.setText("Your Cart: 0 Items");
                    lblCartTotal.setText("Total ₱0.00");

                    loadRoomsByPackage();

                    if (viewBookingsRef != null) {
                        viewBookingsRef.refreshData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Booking failed. The room may have been taken or dates overlap.");
                }
            }
        });

        cart.add(Box.createRigidArea(new Dimension(0, 40)));
        cart.add(checkOut);
        return cart;
    }}