package ui;

import dao.PackageDB;
import model.Package;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PackageTablePanel extends JPanel {
    private JTable packageTable;
    private DefaultTableModel tableModel;
    private PackageDB packageDB;

    public PackageTablePanel() {
        this.packageDB = new PackageDB();

        setBackground(new Color(245, 246, 247));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // Header Section
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 246, 247));

        JLabel title = new JLabel("Amenities & Packages");
        title.setFont(new Font("Arial", Font.PLAIN, 30));
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(new Color(245, 246, 247));
        actions.add(createActionLabel("Edit Package", "EDIT"));
        header.add(actions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"ID", "Package Name", "Price (P)", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        packageTable = new JTable(tableModel);
        packageTable.setRowHeight(40);
        packageTable.setShowGrid(false);
        packageTable.setSelectionBackground(new Color(230, 220, 200));

        packageTable.getTableHeader().setBackground(Color.WHITE);
        packageTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        ((DefaultTableCellRenderer)packageTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        loadPackages();

        JScrollPane scrollPane = new JScrollPane(packageTable);
        scrollPane.getViewport().setBackground(new Color(245, 246, 247));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadPackages() {
        tableModel.setRowCount(0);
        ArrayList<Package> list = packageDB.getAllPackages();
        for (Package p : list) {
            tableModel.addRow(new Object[]{
                    p.getPackageId(),
                    p.getPackageName(),
                    p.getPrice(),
                    p.getDescription()
            });
        }
    }

    private JLabel createActionLabel(String text, String action) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = packageTable.getSelectedRow();
                if (action.equals("EDIT")) {
                    handleEdit(row);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) { label.setForeground(new Color(197, 160, 89)); }
            @Override
            public void mouseExited(MouseEvent e) { label.setForeground(Color.BLACK); }
        });
        return label;
    }

    private void handleEdit(int row) {
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a package to edit!");
            return;
        }

        // Get current data from table
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        double price = (double) tableModel.getValueAt(row, 2);
        String desc = (String) tableModel.getValueAt(row, 3);

        JTextField txtName = new JTextField(name);
        JTextField txtPrice = new JTextField(String.valueOf(price));
        JTextArea txtDesc = new JTextArea(desc, 3, 20);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);

        Object[] message = {
                "Package Name:", txtName,
                "Price (P):", txtPrice,
                "Description:", new JScrollPane(txtDesc)
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Package Details", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                double newPrice = Double.parseDouble(txtPrice.getText().trim());
                if (packageDB.updatePackage(id, txtName.getText().trim(), newPrice, txtDesc.getText().trim())) {
                    loadPackages(); // Refresh the table[cite: 17]
                    JOptionPane.showMessageDialog(this, "Package updated successfully!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid price.");
            }
        }
    }
}