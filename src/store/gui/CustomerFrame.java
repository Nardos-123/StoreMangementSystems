package com.store.gui;

import com.store.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import org.mindrot.jbcrypt.BCrypt;

public class CustomerFrame extends JFrame {
    private JTextField nameField, emailField, phoneField, searchField;
    private JPasswordField passwordField;
    private JTable customerTable;
    private DefaultTableModel customerTableModel;

    public CustomerFrame() {
        setTitle("Customer Management");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(139, 69, 19)); // Brown background

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(139, 69, 19)); // Brown background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Increased spacing

        // Search Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel searchLabel = new JLabel("Search Customer:");
        searchLabel.setForeground(Color.WHITE); // White label text
        formPanel.add(searchLabel, gbc);

        gbc.gridx = 1;
        searchField = new JTextField(20); // Increased width
        searchField.setBackground(Color.WHITE); // White background
        searchField.setForeground(Color.BLACK); // Black text
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchCustomers(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchCustomers(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchCustomers(); }
        });
        formPanel.add(searchField, gbc);

        // Name Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE); // White label text
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20); // Increased width
        nameField.setBackground(Color.WHITE); // White background
        nameField.setForeground(Color.BLACK); // Black text
        formPanel.add(nameField, gbc);

        // Email Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE); // White label text
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20); // Increased width
        emailField.setBackground(Color.WHITE); // White background
        emailField.setForeground(Color.BLACK); // Black text
        formPanel.add(emailField, gbc);

        // Phone Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setForeground(Color.WHITE); // White label text
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(20); // Increased width
        phoneField.setBackground(Color.WHITE); // White background
        phoneField.setForeground(Color.BLACK); // Black text
        formPanel.add(phoneField, gbc);

        // Password Field
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE); // White label text
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20); // Increased width
        passwordField.setBackground(Color.WHITE); // White background
        passwordField.setForeground(Color.BLACK); // Black text
        formPanel.add(passwordField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15)); // Increased spacing
        buttonPanel.setBackground(new Color(139, 69, 19)); // Brown background

        JButton addButton = new JButton("Add Customer");
        styleButton(addButton);
        addButton.addActionListener(e -> addCustomer());
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update Customer");
        styleButton(updateButton);
        updateButton.addActionListener(e -> updateCustomer());
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Customer");
        styleButton(deleteButton);
        deleteButton.addActionListener(e -> deleteCustomer());
        buttonPanel.add(deleteButton);

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

        // Customer Table
        customerTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone", "Password"}, 0);
        customerTable = new JTable(customerTableModel);
        customerTable.setBackground(Color.GRAY); // Gray table background
        customerTable.setForeground(Color.BLACK);
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            int row = customerTable.getSelectedRow();
            if (row >= 0) {
                nameField.setText(customerTableModel.getValueAt(row, 1).toString());
                emailField.setText(customerTableModel.getValueAt(row, 2).toString());
                phoneField.setText(customerTableModel.getValueAt(row, 3).toString());
                passwordField.setText(""); // Clear password field for security
            }
        });
        JScrollPane customerScrollPane = new JScrollPane(customerTable);
        customerScrollPane.setPreferredSize(new Dimension(600, 300));
        add(customerScrollPane, BorderLayout.CENTER);

        // Load customers after UI is initialized
        SwingUtilities.invokeLater(this::searchCustomers);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 0, 255)); // Blue background
        button.setForeground(Color.WHITE); // White text
        button.setPreferredSize(new Dimension(150, 40)); // Larger buttons
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
    }

    private void searchCustomers() {
        customerTableModel.setRowCount(0);
        String searchText = searchField.getText().trim();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, name, email, phone, password FROM customers WHERE name LIKE ? OR email LIKE ? OR id LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + searchText + "%");
            stmt.setString(2, "%" + searchText + "%");
            stmt.setString(3, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();
            int rowCount = 0;
            while (rs.next()) {
                Object[] rowData = new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("password") != null ? "********" : "" // Mask password in UI
                };
                customerTableModel.addRow(rowData);
                rowCount++;
            }
            System.out.println("Loaded " + rowCount + " customers");
        } catch (SQLException ex) {
            System.err.println("Error loading customers: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading customers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean verifyAdminPassword(String inputPassword) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT password FROM admins");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String storedPasswordHash = rs.getString("password");
                if (storedPasswordHash != null && !storedPasswordHash.isEmpty()) {
                    if (BCrypt.checkpw(inputPassword, storedPasswordHash)) {
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error verifying admin password: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error verifying admin password: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void loadCustomers() {
        searchCustomers(); // Use search method with empty search text to load all customers
    }

    private void addCustomer() {
        if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, email, and phone are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String password = new String(passwordField.getPassword()).trim();
        String hashedPassword = password.isEmpty() ? null : BCrypt.hashpw(password, BCrypt.gensalt());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO customers (name, email, phone, password) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, nameField.getText().trim());
            stmt.setString(2, emailField.getText().trim());
            stmt.setString(3, phoneField.getText().trim());
            stmt.setString(4, hashedPassword);
            stmt.executeUpdate();
            loadCustomers();
            clearFields();
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
        } catch (SQLException ex) {
            System.err.println("Error adding customer: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error adding customer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        int row = customerTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, email, and phone are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Admin password verification
        JPasswordField adminPasswordField = new JPasswordField(10);
        int option = JOptionPane.showConfirmDialog(this,
                new Object[]{"Enter admin password:", adminPasswordField},
                "Admin Verification",
                JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String inputAdminPassword = new String(adminPasswordField.getPassword());
        if (!verifyAdminPassword(inputAdminPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Incorrect admin password! Update aborted.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) customerTableModel.getValueAt(row, 0);
        String password = new String(passwordField.getPassword()).trim();
        String hashedPassword = password.isEmpty() ? null : BCrypt.hashpw(password, BCrypt.gensalt());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE customers SET name = ?, email = ?, phone = ?, password = ? WHERE id = ?")) {
            stmt.setString(1, nameField.getText().trim());
            stmt.setString(2, emailField.getText().trim());
            stmt.setString(3, phoneField.getText().trim());
            stmt.setString(4, hashedPassword);
            stmt.setInt(5, id);
            stmt.executeUpdate();
            loadCustomers();
            clearFields();
            JOptionPane.showMessageDialog(this, "Customer updated successfully!");
        } catch (SQLException ex) {
            System.err.println("Error updating customer: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error updating customer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        int row = customerTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id = (int) customerTableModel.getValueAt(row, 0);
        String customerName = customerTableModel.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete customer '" + customerName + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM customers WHERE id = ?")) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                loadCustomers();
                clearFields();
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Customer not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            System.err.println("Error deleting customer: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error deleting customer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passwordField.setText("");
    }
}