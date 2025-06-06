package com.store.gui;

import com.store.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import javax.swing.table.DefaultTableModel;

public class SalesFrame extends JFrame {
    private JComboBox<String> customerCombo, productCombo;
    private JTextField quantityField;
    private JTable salesTable;
    private DefaultTableModel tableModel;

    public SalesFrame() {
        setTitle("Sales Processing");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Set brown background for the frame
        getContentPane().setBackground(new Color(139, 69, 19)); // Brown color

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(139, 69, 19)); // Brown background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Increased spacing

        // Customer Label and Combo
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel customerLabel = new JLabel("Customer:");
        customerLabel.setForeground(Color.WHITE); // White label text
        formPanel.add(customerLabel, gbc);

        gbc.gridx = 1;
        customerCombo = new JComboBox<>();
        customerCombo.setBackground(Color.WHITE); // White background
        customerCombo.setForeground(Color.BLACK); // Black text
        formPanel.add(customerCombo, gbc);

        // Product Label and Combo
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel productLabel = new JLabel("Product:");
        productLabel.setForeground(Color.WHITE); // White label text
        formPanel.add(productLabel, gbc);

        gbc.gridx = 1;
        productCombo = new JComboBox<>();
        productCombo.setBackground(Color.WHITE); // White background
        productCombo.setForeground(Color.BLACK); // Black text
        formPanel.add(productCombo, gbc);

        // Quantity Label and Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setForeground(Color.WHITE); // White label text
        formPanel.add(quantityLabel, gbc);

        gbc.gridx = 1;
        quantityField = new JTextField(10);
        quantityField.setBackground(Color.WHITE); // White background
        quantityField.setForeground(Color.BLACK); // Black text
        formPanel.add(quantityField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); // Increased button spacing
        buttonPanel.setBackground(new Color(139, 69, 19)); // Brown background

        // Buttons with Blue background and White text
        JButton addSaleButton = new JButton("Add Sale");
        styleButton(addSaleButton);
        addSaleButton.addActionListener(e -> addSale());
        buttonPanel.add(addSaleButton);

        JButton returnButton = new JButton("Process Return");
        styleButton(returnButton);
        returnButton.addActionListener(e -> processReturn());
        buttonPanel.add(returnButton);

        JButton receiptButton = new JButton("Generate Receipt");
        styleButton(receiptButton);
        receiptButton.addActionListener(e -> generateReceipt());
        buttonPanel.add(receiptButton);

        JButton exitButton = new JButton("Exit to Dashboard");
        styleButton(exitButton);
        exitButton.addActionListener(e -> {
            dispose();
            new DashboardFrame().setVisible(true);
        });
        buttonPanel.add(exitButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Customer", "Product", "Quantity", "Total Price", "Date"}, 0);
        salesTable = new JTable(tableModel);
        salesTable.setBackground(Color.GRAY); // Gray table background
        salesTable.setForeground(Color.WHITE); // White table text
        salesTable.setGridColor(Color.DARK_GRAY); // Darker grid lines
        salesTable.setSelectionBackground(new Color(100, 100, 100)); // Darker gray selection
        salesTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBackground(new Color(139, 69, 19)); // Brown scroll pane background
        add(scrollPane, BorderLayout.CENTER);

        loadCombos();
        loadSales();
    }

    // Helper method to style buttons
    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 102, 204)); // Blue background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Padding
    }

    // Rest of the methods remain unchanged...
    private void loadCombos() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name FROM customers");
            while (rs.next()) {
                customerCombo.addItem(rs.getInt("id") + ": " + rs.getString("name"));
            }
            rs = stmt.executeQuery("SELECT id, name FROM products");
            while (rs.next()) {
                productCombo.addItem(rs.getInt("id") + ": " + rs.getString("name"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSales() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT s.id, c.name AS customer, p.name AS product, s.quantity, s.total_price, s.sale_date " +
                     "FROM sales s JOIN customers c ON s.customer_id = c.id JOIN products p ON s.product_id = p.id")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("customer"),
                    rs.getString("product"),
                    rs.getInt("quantity"),
                    rs.getDouble("total_price"),
                    rs.getTimestamp("sale_date")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading sales: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSale() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            int customerId = Integer.parseInt(customerCombo.getSelectedItem().toString().split(":")[0]);
            int productId = Integer.parseInt(productCombo.getSelectedItem().toString().split(":")[0]);
            int quantity = Integer.parseInt(quantityField.getText());

            PreparedStatement stmt = conn.prepareStatement("SELECT price, stock FROM products WHERE id = ?");
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int stock = rs.getInt("stock");
                double price = rs.getDouble("price");
                if (quantity > stock) {
                    JOptionPane.showMessageDialog(this, "Insufficient stock!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                stmt = conn.prepareStatement("INSERT INTO sales (customer_id, product_id, quantity, total_price) VALUES (?, ?, ?, ?)");
                stmt.setInt(1, customerId);
                stmt.setInt(2, productId);
                stmt.setInt(3, quantity);
                stmt.setDouble(4, quantity * price);
                stmt.executeUpdate();

                stmt = conn.prepareStatement("UPDATE products SET stock = stock - ? WHERE id = ?");
                stmt.setInt(1, quantity);
                stmt.setInt(2, productId);
                stmt.executeUpdate();

                loadSales();
                quantityField.setText("");
                JOptionPane.showMessageDialog(this, "Sale added successfully!");
            }
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error adding sale: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processReturn() {
        int row = salesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a sale to return!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int saleId = (int) tableModel.getValueAt(row, 0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT product_id, quantity FROM sales WHERE id = ?");
            stmt.setInt(1, saleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");

                stmt = conn.prepareStatement("UPDATE products SET stock = stock + ? WHERE id = ?");
                stmt.setInt(1, quantity);
                stmt.setInt(2, productId);
                stmt.executeUpdate();

                stmt = conn.prepareStatement("DELETE FROM sales WHERE id = ?");
                stmt.setInt(1, saleId);
                stmt.executeUpdate();

                loadSales();
                JOptionPane.showMessageDialog(this, "Sale returned successfully!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error processing return: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateReceipt() {
        int row = salesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a sale to generate receipt!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            List<String> receiptData = new ArrayList<>();
            receiptData.add("Sale ID: " + tableModel.getValueAt(row, 0));
            receiptData.add("Customer: " + tableModel.getValueAt(row, 1));
            receiptData.add("Product: " + tableModel.getValueAt(row, 2));
            receiptData.add("Quantity: " + tableModel.getValueAt(row, 3));
            receiptData.add("Total Price: $" + tableModel.getValueAt(row, 4));
            receiptData.add("Date: " + tableModel.getValueAt(row, 5));

            StringBuilder receiptText = new StringBuilder();
            receiptText.append("===== Sale Receipt =====\n");
            for (String line : receiptData) {
                receiptText.append(line).append("\n");
            }
            receiptText.append("=====================");

            JTextArea textArea = new JTextArea(receiptText.toString());
            textArea.setEditable(false);
            textArea.setBackground(Color.WHITE); // White background for receipt
            textArea.setForeground(Color.BLACK); // Black text
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(300, 200));
            JOptionPane.showMessageDialog(this, scrollPane, "Sale Receipt", JOptionPane.INFORMATION_MESSAGE);

            File receiptsDir = new File("receipts");
            if (!receiptsDir.exists()) {
                receiptsDir.mkdirs();
            }
            String fileName = "receipts/receipt_" + tableModel.getValueAt(row, 0) + ".txt";
            File file = new File(fileName);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String line : receiptData) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            System.out.println("Receipt saved to: " + file.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Receipt generated successfully as " + fileName + "!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error generating receipt: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}