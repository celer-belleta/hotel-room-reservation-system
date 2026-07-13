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

    private int startMonthIndex = Calendar.getInstance().get(Calendar.MONTH);
    private double currentTotal = 0.0;
    private int cartItemCount = 0;

    private int countAdults = 2;
    private int countSeniors = 0;
    private int countPWDs = 0;
    private int countChildren = 0;

    private Calendar checkInCal = null;
    private Calendar checkOutCal = null;

    private final Color AURELIA_GOLD = new Color(197, 160, 89);
    private final Color NAVY_BLUE = new Color(44, 62, 80);
    private final Color BG_COLOR = new Color(255, 255, 255);

    private final ArrayList<CartItem> selectedRoomsList = new ArrayList<>();

    private int loggedInGuestId;

    private ViewBookingsPanel viewBookingsRef;

    private JLabel lblNightsHeader;

    private JTextArea txtCartList;

    public RoomBookingPanel(int guestId, ViewBookingsPanel vbp) {
        this.loggedInGuestId = guestId;
        this.viewBookingsRef = vbp;
        this.roomDB = new RoomDB();

        if (this.startMonthIndex > 10) {
            this.startMonthIndex = 10;
        }

        setLayout(new BorderLayout());
        setBackground(NAVY_BLUE);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(NAVY_BLUE);
        mainContent.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel statusHeader = createStatusHeader();
        statusHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(statusHeader);
        mainContent.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel navPanel = createCalendarNavigation();
        navPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(navPanel);

        JPanel calWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        calWrapper.setBackground(NAVY_BLUE);
        calWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        calendarContainer = new JPanel(new GridLayout(1, 2, 40, 0));
        calendarContainer.setBackground(NAVY_BLUE);
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
        dynamicContentPanel.setBackground(NAVY_BLUE);
        dynamicContentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loadRoomsByPackage();
        mainContent.add(dynamicContentPanel);

        JPanel scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.setBackground(NAVY_BLUE);
        scrollWrapper.add(mainContent, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(scrollWrapper);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(25);
        scroll.getViewport().setBackground(NAVY_BLUE);
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
        sectionHeader.setBackground(new Color(245, 245, 245));
        sectionHeader.setBorder(new EmptyBorder(15, 10, 10, 10));
        sectionHeader.setMaximumSize(new Dimension(1600, 85));
        sectionHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("<html><b style='font-size:14pt; color:#2C3E50;'>" + title + "</b><br><font color='gray'>" + desc + "</font></html>");
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

            int totalPeople = countAdults + countSeniors + countPWDs + countChildren;
            if (isDateAvailable && r.getMaxGuest() >= totalPeople) {
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
                String filename = "superior.jpg";

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
        imgBox.setBorder(BorderFactory.createLineBorder(NAVY_BLUE));

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 5));
        info.setBackground(BG_COLOR);
        JLabel lblName = new JLabel(r.getType() + " (Room " + r.getRoomNumber() + ")");
        lblName.setFont(new Font("Serif", Font.BOLD, 19));
        lblName.setForeground(NAVY_BLUE);

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
        lblPriceValue.setForeground(AURELIA_GOLD);
        lblPriceValue.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton btnBook = new JButton("BOOK NOW");
        btnBook.setBackground(NAVY_BLUE);
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.setFont(new Font("Arial", Font.BOLD, 12));

        btnBook.addActionListener(e -> {
            if (checkInCal == null || checkOutCal == null) {
                JOptionPane.showMessageDialog(this, "Please select stay dates first!");
                return;
            }

            long n = getSelectedNights();
            selectedRoomsList.add(new CartItem(r, checkInCal, checkOutCal, n));

            updateCartUI();
            JOptionPane.showMessageDialog(this, r.getType() + " added for " + n + " nights!");
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
        JPanel p = new JPanel(new GridLayout(1, 4));
        p.setBackground(BG_COLOR);
        p.setBorder(BorderFactory.createLineBorder(NAVY_BLUE));
        p.setMaximumSize(new Dimension(1600, 70));
        lblCheckIn = new JLabel("<html><b>Check-in</b><br>SELECT DATE</html>", SwingConstants.CENTER);
        lblCheckOut = new JLabel("<html><b>Check-out</b><br>SELECT DATE</html>", SwingConstants.CENTER);
        lblNightsHeader = new JLabel("<html><b>Nights</b><br>0</html>", SwingConstants.CENTER);
        int totalAdultTypes = countAdults + countSeniors + countPWDs;
        lblGuestsValue = new JLabel("<html><b>Guests</b><br>" + totalAdultTypes + " ADULTS, " + countChildren + " CHILD</html>", SwingConstants.CENTER);

        lblCheckIn.setForeground(NAVY_BLUE);
        lblCheckOut.setForeground(NAVY_BLUE);
        lblNightsHeader.setForeground(NAVY_BLUE);
        lblGuestsValue.setForeground(NAVY_BLUE);

        lblGuestsValue.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblGuestsValue.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { showPaxPicker(lblGuestsValue); }});
        p.add(lblGuestsValue); p.add(lblCheckIn); p.add(lblCheckOut); p.add(lblNightsHeader);
        return p;
    }

    private void showPaxPicker(JLabel parent) {
        JPopupMenu popup = new JPopupMenu();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(createPaxRow("Adults", countAdults, v -> countAdults = v, 1, 10));
        panel.add(createPaxRow("Seniors", countSeniors, v -> countSeniors = v, 0, 10));
        panel.add(createPaxRow("PWDs", countPWDs, v -> countPWDs = v, 0, 10));
        panel.add(createPaxRow("Children", countChildren, v -> countChildren = v, 0, 10));

        JButton done = new JButton("DONE");
        done.setBackground(NAVY_BLUE);
        done.setForeground(Color.WHITE);
        done.setAlignmentX(Component.CENTER_ALIGNMENT);

        done.addActionListener(e -> {
            int totalAdults = countAdults + countSeniors + countPWDs;
            String text = String.format("<html><b>Guests</b><br>%d ADULTS, %d CHILD</html>", totalAdults, countChildren);
            lblGuestsValue.setText(text);

            updateCartUI();
            loadRoomsByPackage();
            popup.setVisible(false);
        });
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(done);
        popup.add(panel);
        popup.show(parent, 0, parent.getHeight());
    }

    private JPanel createPaxRow(String label, int currentVal, java.util.function.Consumer<Integer> updater, int min, int max) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        row.setBackground(BG_COLOR);
        JLabel lbl = new JLabel(label + ": ");
        JButton m = new JButton("—");
        JButton p = new JButton("+");
        JLabel c = new JLabel(String.valueOf(currentVal));
        c.setPreferredSize(new Dimension(20, 20));
        c.setHorizontalAlignment(SwingConstants.CENTER);

        m.addActionListener(e -> {
            int val = Integer.parseInt(c.getText());
            if (val > min) {
                val--;
                c.setText(String.valueOf(val));
                updater.accept(val);
            }
        });

        p.addActionListener(e -> {
            int val = Integer.parseInt(c.getText());
            if (val < max) {
                val++;
                c.setText(String.valueOf(val));
                updater.accept(val);
            }
        });

        row.add(lbl); row.add(m); row.add(c); row.add(p);
        return row;
    }

    private long getSelectedNights() {
        if (checkInCal == null || checkOutCal == null) return 1;
        long diff = checkOutCal.getTimeInMillis() - checkInCal.getTimeInMillis();
        long n = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return n <= 0 ? 1 : n;
    }

    private void handleCheckout() {
        if (selectedRoomsList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm booking for " + selectedRoomsList.size() + " room(s)?",
                "Aurelia Grand - Checkout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dao.BookingDB bookingDao = new dao.BookingDB();
            boolean allSuccess = true;

            String uniqueCartId = "CART-" + System.currentTimeMillis();

            for (CartItem item : selectedRoomsList) {
                int packageId = 1;
                if (item.room.getPackageType().contains("Breakfast")) packageId = 2;
                else if (item.room.getPackageType().contains("Amenities")) packageId = 3;

                boolean success = bookingDao.finalizeBooking(
                        item.room.getId(),
                        this.loggedInGuestId,
                        packageId,
                        item.in,
                        item.out,
                        currentTotal,
                        countAdults,
                        countSeniors,
                        countPWDs,
                        countChildren,
                        uniqueCartId
                );
                if (!success) allSuccess = false;
            }

            if (allSuccess) {
                JOptionPane.showMessageDialog(this, "All rooms successfully reserved!");
                selectedRoomsList.clear();
                updateCartUI();
                loadRoomsByPackage();
                if (viewBookingsRef != null) viewBookingsRef.refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Booking failed. Please check your database connection.");
            }
        }
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
        nav.setBackground(NAVY_BLUE);
        nav.setMaximumSize(new Dimension(1600, 40));

        JButton b1 = new JButton("❮");
        JButton b2 = new JButton("❯");

        int currentSystemMonth = Calendar.getInstance().get(Calendar.MONTH);

        b1.addActionListener(e -> {
            if(startMonthIndex > currentSystemMonth) {
                startMonthIndex--;
                updateCalendarDisplay();
            }
        });

        b2.addActionListener(e -> {
            if(startMonthIndex < 10) {
                startMonthIndex++;
                updateCalendarDisplay();
            }
        });

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
        p.setBackground(NAVY_BLUE);

        String[] months = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        JLabel title = new JLabel(months[month] + " 2026", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 22));
        title.setForeground(AURELIA_GOLD);
        p.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 7, 5, 5));
        grid.setBackground(NAVY_BLUE);

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
                b.setBackground(new Color(60, 80, 100));
                b.setForeground(Color.LIGHT_GRAY);
            } else {
                if (checkInCal != null && cur.equals(checkInCal)) {
                    b.setBackground(AURELIA_GOLD);
                    b.setForeground(Color.WHITE);
                } else if (checkOutCal != null && cur.equals(checkOutCal)) {
                    b.setBackground(AURELIA_GOLD);
                    b.setForeground(Color.WHITE);
                } else if (checkInCal != null && checkOutCal != null && cur.after(checkInCal) && cur.before(checkOutCal)) {
                    b.setBackground(new Color(197, 160, 89, 150));
                    b.setForeground(Color.WHITE);
                    b.setBorder(null);
                } else {
                    b.setBackground(Color.WHITE);
                    b.setForeground(NAVY_BLUE);
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

        lblCheckIn.setText("<html><center><b>Check-in</b><br>" + in.toUpperCase() + "</center></html>");
        lblCheckOut.setText("<html><center><b>Check-out</b><br>" + out.toUpperCase() + "</center></html>");

        if (lblNightsHeader != null) {
            long nights = getSelectedNights();
            if (checkInCal != null && checkOutCal != null) {
                lblNightsHeader.setText("<html><center><b>Nights</b><br>" + nights + "</center></html>");
            } else {
                lblNightsHeader.setText("<html><center><b>Nights</b><br>0</center></html>");
            }
        }
    }

    private JPanel createFixedCart() {
        JPanel cart = new JPanel();
        cart.setLayout(new BoxLayout(cart, BoxLayout.Y_AXIS));
        cart.setPreferredSize(new Dimension(300, 0));
        cart.setBackground(NAVY_BLUE);
        cart.setBorder(new EmptyBorder(30, 20, 0, 20));

        lblCartItems = new JLabel("Your Cart: 0 Items");
        lblCartItems.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblCartItems.setForeground(AURELIA_GOLD);
        lblCartItems.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtCartList = new JTextArea(10, 20);
        txtCartList.setEditable(false);
        txtCartList.setLineWrap(true);
        txtCartList.setWrapStyleWord(true);
        txtCartList.setBackground(new Color(248, 248, 248));
        txtCartList.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtCartList.setMargin(new Insets(15, 15, 15, 15));
        txtCartList.setText("No rooms selected.");

        JScrollPane cartScroll = new JScrollPane(txtCartList);
        cartScroll.setBorder(BorderFactory.createLineBorder(AURELIA_GOLD, 1));
        cartScroll.setMaximumSize(new Dimension(260, 250));
        cartScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        cartScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        lblCartTotal = new JLabel("Total ₱0.00");
        lblCartTotal.setFont(new Font("Serif", Font.BOLD, 24));
        lblCartTotal.setForeground(Color.WHITE);
        lblCartTotal.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton clearCartBtn = new JButton("CLEAR CART");
        clearCartBtn.setMaximumSize(new Dimension(260, 35));
        clearCartBtn.setBackground(AURELIA_GOLD);
        clearCartBtn.setForeground(Color.WHITE);
        clearCartBtn.setFocusPainted(false);
        clearCartBtn.setBorderPainted(false);
        clearCartBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearCartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearCartBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearCartBtn.addActionListener(e -> {
            selectedRoomsList.clear();
            updateCartUI();
        });

        JButton checkOut = new JButton("CHECKOUT");
        checkOut.setMaximumSize(new Dimension(260, 50));
        checkOut.setBackground(Color.BLACK);
        checkOut.setForeground(Color.WHITE);
        checkOut.setFocusPainted(false);
        checkOut.setFont(new Font("Arial", Font.BOLD, 14));
        checkOut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkOut.setAlignmentX(Component.LEFT_ALIGNMENT);

        checkOut.addActionListener(e -> handleCheckout());

        cart.add(lblCartItems);
        cart.add(Box.createRigidArea(new Dimension(0, 15)));
        cart.add(cartScroll);
        cart.add(Box.createRigidArea(new Dimension(0, 20)));
        cart.add(lblCartTotal);
        cart.add(Box.createRigidArea(new Dimension(0, 30)));
        cart.add(clearCartBtn);
        cart.add(Box.createRigidArea(new Dimension(0, 10)));
        cart.add(checkOut);

        return cart;
    }

    private void updateCartUI() {
        StringBuilder listBuilder = new StringBuilder();
        double totalBeforeDiscount = 0;
        double childFeePerNight = 250.0;

        for (CartItem item : selectedRoomsList) {
            listBuilder.append(item.room.getType().toUpperCase()).append("\n");
            listBuilder.append(item.room.getPackageType()).append("\n");
            String dateRange = String.format("%tb %te - %tb %te, %tY",
                    item.in, item.in, item.out, item.out, item.out);
            listBuilder.append(dateRange).append("\n");
            listBuilder.append(item.nights).append(item.nights > 1 ? " Nights stay" : " Night stay").append("\n");
            listBuilder.append("----------------------------\n");
            double extra = 0;
            String pkg = item.room.getPackageType();
            if (pkg != null) {
                if (pkg.contains("Breakfast")) extra = 500.0;
                else if (pkg.contains("Amenities")) extra = 1000.0;
            }
            totalBeforeDiscount += (item.room.getPrice() + extra) * item.nights;
            totalBeforeDiscount += (countChildren * childFeePerNight * item.nights);
        }

        if (txtCartList != null) {
            txtCartList.setText(selectedRoomsList.isEmpty() ? "No rooms selected." : listBuilder.toString());
        }

        double totalGuestShare = countAdults + countSeniors + countPWDs;
        double seniorDiscount = 0;
        if (totalGuestShare > 0) {
            seniorDiscount = (totalBeforeDiscount / totalGuestShare) * (countSeniors + countPWDs) * 0.20;
        }

        this.currentTotal = totalBeforeDiscount - seniorDiscount;
        lblCartItems.setText("Your Cart: " + selectedRoomsList.size() + " Items");
        lblCartTotal.setText("Total ₱" + String.format("%,.2f", currentTotal));
    }

    private static class CartItem {
        Room room;
        Calendar in;
        Calendar out;
        long nights;

        CartItem(Room r, Calendar in, Calendar out, long n) {
            this.room = r;
            this.in = (Calendar) in.clone();
            this.out = (Calendar) out.clone();
            this.nights = n;
        }
    }
}