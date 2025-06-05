package com.store.gui;

import com.store.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class InventoryFrame extends JFrame {
    private JTextField nameField, categoryField, priceField, stockField;
    private JTable productTable;
    private DefaultTableModel tableModel;

    public InventoryFrame() {
        setTitle("Inventory Management");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set black background for the content pane
        getContentPane().setBackground(Color.BLACK);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Increased spacing

        // Label and text field styling
        Color labelForeground = Color.WHITE; // White text for labels
        Color textFieldBackground = Color.WHITE; // White background for text fields
        Color textFieldForeground = Color.BLACK; // Black text for text fields

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(labelForeground);
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20); // Increased width
        styleTextField(nameField, textFieldBackground, textFieldForeground);
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setForeground(labelForeground);
        formPanel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        categoryField = new JTextField(20);
        styleTextField(categoryField, textFieldBackground, textFieldForeground);
        formPanel.add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setForeground(labelForeground);
        formPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        priceField = new JTextField(20);
        styleTextField(priceField, textFieldBackground, textFieldForeground);
        formPanel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel stockLabel = new JLabel("Stock:");
        stockLabel.setForeground(labelForeground);
        formPanel.add(stockLabel, gbc);

        gbc.gridx = 1;
        stockField = new JTextField(20);
        styleTextField(stockField, textFieldBackground, textFieldForeground);
        formPanel.add(stockField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); // Increased spacing
        buttonPanel.setBackground(Color.BLACK);

        // Button styling
        Color buttonBackground = new Color(0, 102, 204); // Blue background
        Color buttonForeground = Color.WHITE; // White text

        JButton addButton = new JButton("Add Product");
        styleButton(addButton, buttonBackground, buttonForeground);
        addButton.addActionListener(e -> addProduct());
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update Product");
        styleButton(updateButton, buttonBackground, buttonForeground);
        updateButton.addActionListener(e -> updateProduct());
        buttonPanel.add(updateButton);

        JButton removeButton = new JButton("Remove Product");
        styleButton(removeButton, buttonBackground, buttonForeground);
        removeButton.addActionListener(e -> removeProduct());
        buttonPanel.add(removeButton);

        JButton exitButton = new JButton("Exit to Dashboard");
        styleButton(exitButton, buttonBackground, buttonForeground);
        exitButton.addActionListener(e -> {
            dispose();
            new DashboardFrame().setVisible(true);
        });
        buttonPanel.add(exitButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Price", "Stock"}, 0);
        productTable = new JTable(tableModel);
        productTable.setBackground(Color.GRAY); // Gray background for table
        productTable.setForeground(Color.WHITE); // White text for visibility
        productTable.setGridColor(Color.DARK_GRAY); // Dark gray grid lines
        productTable.getTableHeader().setBackground(Color.DARK_GRAY);
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.getSelectionModel().addListSelectionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                categoryField.setText(tableModel.getValueAt(row, 2).toString());
                priceField.setText(tableModel.getValueAt(row, 3).toString());
                stockField.setText(tableModel.getValueAt(row, 4).toString());
            }
        });
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.getViewport().setBackground(Color.BLACK); // Black background for scroll pane
        add(scrollPane, BorderLayout.CENTER);

        loadProducts();
    }

    // Helper method to style text fields
    private void styleTextField(JTextField textField, Color background, Color foreground) {
        textField.setBackground(background);
        textField.setForeground(foreground);
        textField.setCaretColor(foreground);
        textField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    // Helper method to style buttons
    private void styleButton(JButton button, Color background, Color foreground) {
        button.setBackground(background);
        button.setForeground(foreground);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(150, 35)); // Increased button size
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addProduct() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO products (name, category, price, stock) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, nameField.getText());
            stmt.setString(2, categoryField.getText());
            stmt.setDouble(3, Double.parseDouble(priceField.getText()));
            stmt.setInt(4, Integer.parseInt(stockField.getText()));
            stmt.executeUpdate();
            loadProducts();
            clearFields();
            JOptionPane.showMessageDialog(this, "Product added successfully!");
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error adding product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE products SET name = ?, category = ?, price = ?, stock = ? WHERE id = ?")) {
            stmt.setString(1, nameField.getText());
            stmt.setString(2, categoryField.getText());
            stmt.setDouble(3, Double.parseDouble(priceField.getText()));
            stmt.setInt(4, Integer.parseInt(stockField.getText()));
            stmt.setInt(5, id);
            stmt.executeUpdate();
            loadProducts();
            clearFields();
            JOptionPane.showMessageDialog(this, "Product updated successfully!");
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error updating product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeProduct() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product to remove!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            loadProducts();
            clearFields();
            JOptionPane.showMessageDialog(this, "Product removed successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error removing product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
        categoryField.setText("");
        priceField.setText("");
        stockField.setText("");
    }
}