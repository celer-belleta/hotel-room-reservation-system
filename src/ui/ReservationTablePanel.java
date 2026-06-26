package ui;

import dao.ReservationDB;
import model.Reservation;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ReservationTablePanel extends JPanel {
    private final JTable resTable;
    private final DefaultTableModel tableModel;
    private final ReservationDB resDB = new ReservationDB();
    private ArrayList<Reservation> reservationList = new ArrayList<>();

    private final Color AURELIA_GOLD = new Color(197, 160, 89);
    private final Color BG_COLOR = new Color(245, 246, 247);
    private final Color SELECTION_COLOR = new Color(245, 230, 200);

    private JFrame parentFrame;

    public ReservationTablePanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setBackground(BG_COLOR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_COLOR);

        JLabel title = new JLabel("Manage Reservations");
        title.setFont(new Font("Serif", Font.BOLD, 30));
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        actions.setBackground(BG_COLOR);
        actions.add(createActionLabel("New Booking", "ADD"));
        actions.add(createActionLabel("Process Payment", "PAY"));
        actions.add(createActionLabel("Check-In", "IN"));
        actions.add(createActionLabel("Check-Out", "OUT"));
        actions.add(createActionLabel("Cancel Booking", "CANCEL"));
        header.add(actions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "Guest ID", "Room ID", "Nights", "Check-In", "Check-Out", "Status", "Processed By", "Time In", "Time Out"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        resTable = new JTable(tableModel);
        resTable.setRowHeight(45);
        resTable.setSelectionBackground(SELECTION_COLOR);
        resTable.setSelectionForeground(Color.BLACK);
        resTable.setShowGrid(false);

        resTable.getTableHeader().setBackground(Color.WHITE);
        resTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        resTable.getTableHeader().setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < resTable.getColumnCount(); i++) {
            resTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        refreshTable();

        JScrollPane scrollPane = new JScrollPane(resTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createActionLabel(String text, String action) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (action) {
                    case "ADD":
                        String inputId = JOptionPane.showInputDialog(null, "Enter Guest ID:");
                        if (inputId != null && !inputId.trim().isEmpty()) {
                            new GuestDashboard(inputId.trim()).setVisible(true);
                            if (parentFrame != null) {
                                parentFrame.dispose();
                            }
                        }
                        break;
                    case "PAY":
                        handlePayment(resTable.getSelectedRow());
                        break;
                    case "IN":
                        handleCheckIn(resTable.getSelectedRow());
                        break;
                    case "OUT":
                        handleCheckOut(resTable.getSelectedRow());
                        break;
                    case "CANCEL":
                        handleCancel(resTable.getSelectedRow());
                        break;
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) { label.setForeground(AURELIA_GOLD); }
            @Override
            public void mouseExited(MouseEvent e) { label.setForeground(Color.BLACK); }
        });
        return label;
    }

    private void handlePayment(int row) {
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a reservation.");
            return;
        }

        int resId = (int) tableModel.getValueAt(row, 0);
        Reservation selectedRes = null;
        for (Reservation res : reservationList) {
            if (res.getResId() == resId) { selectedRes = res; break; }
        }

        if (selectedRes != null) {
            String guestName = new dao.GuestDB().getGuestNameById(selectedRes.getGuestId());
            double total = selectedRes.getTotalAmount();
            double paid = selectedRes.getAmountPaid();

            new PaymentProcessingFrame(resId, guestName, selectedRes.getGuestId(), total, paid, this).setVisible(true);
        }
    }

    private void handleCheckOut(int row) {
        if (row == -1) return;

        int resId = (int) tableModel.getValueAt(row, 0);
        int guestId = (int) tableModel.getValueAt(row, 1);
        int roomId = (int) tableModel.getValueAt(row, 2);

        String[] options = {"No Damages", "Damages Found"};
        int damageChoice = JOptionPane.showOptionDialog(this, "Room Inspection for Room " + roomId, "Aurelia Grand",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (damageChoice == 1) {
            String input = JOptionPane.showInputDialog(this, "Enter Damage Fee Amount (₱):");
            if (input != null && !input.trim().isEmpty()) {
                double fee = Double.parseDouble(input);
                resDB.addDamageFee(resId, fee);
                String guestName = new dao.GuestDB().getGuestNameById(guestId);
                new DamagePaymentFrame(resId, guestName, fee, this).setVisible(true);
                return;
            }
        }

        Reservation selectedRes = null;
        for (Reservation res : reservationList) {
            if (res.getResId() == resId) { selectedRes = res; break; }
        }

        if (selectedRes != null) {
            double total = selectedRes.getTotalAmount();
            double paid = selectedRes.getAmountPaid();
            String guestName = new dao.GuestDB().getGuestNameById(guestId);

            if (selectedRes.getRemainingBalance() > 0.01) {
                new PaymentProcessingFrame(resId, guestName, guestId, total, paid, this).setVisible(true);
            } else {
                resDB.processCheckOut(resId, roomId);
                refreshTable();
            }
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        reservationList = resDB.getAllReservations();
        for (Reservation res : reservationList) {
            tableModel.addRow(new Object[]{res.getResId(), res.getGuestId(), res.getRoomId(), res.getNights(),
                    res.getCheckIn(), res.getCheckOut(), res.getStatus(), res.getManagedBy(), res.getTimeIn(), res.getTimeOut()});
        }
    }

    private void handleCheckIn(int row) {
        if (row == -1) return;
        int resId = (int) tableModel.getValueAt(row, 0);
        int roomId = (int) tableModel.getValueAt(row, 2);
        if (resDB.processCheckIn(resId, roomId)) {
            JOptionPane.showMessageDialog(this, "Checked-In!");
            refreshTable();
        }
    }

    private void handleCancel(int row) {
        if (row == -1) return;
        int resId = (int) tableModel.getValueAt(row, 0);
        if (resDB.deleteReservation(resId)) { refreshTable(); }
    }

    public ArrayList<Reservation> getReservationList() { return this.reservationList; }
}