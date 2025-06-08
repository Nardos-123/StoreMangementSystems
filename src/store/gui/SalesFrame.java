package com.store.gui;

import com.store.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import javax.swing.table.DefaultTableModel;
import org.mindrot.jbcrypt.BCrypt;

public class SalesFrame extends JFrame {
    private JComboBox<String> customerCombo, productCombo;
    private JTextField quantityField, customerSearchField, productSearchField;
    private JTable salesTable;
    private DefaultTableModel tableModel;

    public SalesFrame() {
        setTitle("Sales Processing");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        getContentPane().setBackground(new Color(139, 69, 19));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(139, 69, 19));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Customer Search
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel customerSearchLabel = new JLabel("Search Customer:");
        customerSearchLabel.setForeground(Color.WHITE);
        formPanel.add(customerSearchLabel, gbc);

        gbc.gridx = 1;
        customerSearchField = new JTextField(15);
        customerSearchField.setBackground(Color.WHITE);
        customerSearchField.setForeground(Color.BLACK);
        customerSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchCustomers(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchCustomers(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchCustomers(); }
        });
        formPanel.add(customerSearchField, gbc);

        // Customer Combo
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel customerLabel = new JLabel("Customer:");
        customerLabel.setForeground(Color.WHITE);
        formPanel.add(customerLabel, gbc);

        gbc.gridx = 1;
        customerCombo = new JComboBox<>();
        customerCombo.setBackground(Color.WHITE);
        customerCombo.setForeground(Color.BLACK);
        formPanel.add(customerCombo, gbc);

        // Product Search
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel productSearchLabel = new JLabel("Search Product:");
        productSearchLabel.setForeground(Color.WHITE);
        formPanel.add(productSearchLabel, gbc);

        gbc.gridx = 1;
        productSearchField = new JTextField(15);
        productSearchField.setBackground(Color.WHITE);
        productSearchField.setForeground(Color.BLACK);
        productSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchProducts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchProducts(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchProducts(); }
        });
        formPanel.add(productSearchField, gbc);

        // Product Combo
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel productLabel = new JLabel("Product:");
        productLabel.setForeground(Color.WHITE);
        formPanel.add(productLabel, gbc);

        gbc.gridx = 1;
        productCombo = new JComboBox<>();
        productCombo.setBackground(Color.WHITE);
        productCombo.setForeground(Color.BLACK);
        formPanel.add(productCombo, gbc);

        // Quantity
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setForeground(Color.WHITE);
        formPanel.add(quantityLabel, gbc);

        gbc.gridx = 1;
        quantityField = new JTextField(10);
        quantityField.setBackground(Color.WHITE);
        quantityField.setForeground(Color.BLACK);
        formPanel.add(quantityField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(139, 69, 19));

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
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Customer", "Product", "Quantity", "Total Price", "Date"}, 0);
        salesTable = new JTable(tableModel);
        salesTable.setBackground(Color.GRAY);
        salesTable.setForeground(Color.WHITE);
        salesTable.setGridColor(Color.DARK_GRAY);
        salesTable.setSelectionBackground(new Color(100, 100, 100));
        salesTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBackground(new Color(139, 69, 19));
        add(scrollPane, BorderLayout.CENTER);

        loadCombos();
        loadSales();
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 102, 204));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void loadCombos() {
        searchCustomers();
        searchProducts();
    }

    private void searchCustomers() {
        customerCombo.removeAllItems();
        String searchText = customerSearchField.getText().trim();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, name FROM customers WHERE name LIKE ? OR id LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + searchText + "%");
            stmt.setString(2, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                customerCombo.addItem(rs.getInt("id") + ": " + rs.getString("name"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchProducts() {
        productCombo.removeAllItems();
        String searchText = productSearchField.getText().trim();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, name FROM products WHERE name LIKE ? OR id LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + searchText + "%");
            stmt.setString(2, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                productCombo.addItem(rs.getInt("id") + ": " + rs.getString("name"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean verifyCustomerPassword(int customerId, String inputPassword) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT password FROM customers WHERE id = ?");
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPasswordHash = rs.getString("password");
                if (storedPasswordHash != null && !storedPasswordHash.isEmpty()) {
                    return BCrypt.checkpw(inputPassword, storedPasswordHash);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error verifying password: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
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

            // Password verification
            JPasswordField passwordField = new JPasswordField(10);
            int option = JOptionPane.showConfirmDialog(this, 
                new Object[]{"Enter customer password:", passwordField}, 
                "Password Verification", 
                JOptionPane.OK_CANCEL_OPTION);
            
            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            String inputPassword = new String(passwordField.getPassword());
            if (!verifyCustomerPassword(customerId, inputPassword)) {
                JOptionPane.showMessageDialog(this, 
                    "Incorrect password! Please try again.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

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
            // Retrieve customer_id for password verification
            PreparedStatement stmt = conn.prepareStatement("SELECT customer_id, product_id, quantity FROM sales WHERE id = ?");
            stmt.setInt(1, saleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");

                // Password verification
                JPasswordField passwordField = new JPasswordField(10);
                int option = JOptionPane.showConfirmDialog(this, 
                    new Object[]{"Enter customer password:", passwordField}, 
                    "Password Verification", 
                    JOptionPane.OK_CANCEL_OPTION);
                
                if (option != JOptionPane.OK_OPTION) {
                    return;
                }

                String inputPassword = new String(passwordField.getPassword());
                if (!verifyCustomerPassword(customerId, inputPassword)) {
                    JOptionPane.showMessageDialog(this, 
                        "Incorrect password! Return aborted.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Proceed with return
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
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);
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