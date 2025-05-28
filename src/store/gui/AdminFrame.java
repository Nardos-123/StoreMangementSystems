package com.store.gui;

import com.store.db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminFrame extends JFrame {
    private DefaultTableModel tableModel;
    private JTable adminTable;
    private final boolean isAdmin;

    public AdminFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setTitle("Store Management System - Admin Management");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(255, 165, 0)); // Orange background

        // Header
        JLabel headerLabel = new JLabel("Admin Page", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.BLUE); // Red header text
        add(headerLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "SSN", "Name", "Username", "Email", "Password", "Role", "Contact"};
        tableModel = new DefaultTableModel(columns, 0);
        adminTable = new JTable(tableModel);
        adminTable.setBackground(Color.GRAY); // Gray table background
        adminTable.setForeground(Color.WHITE); // White table text for contrast
        loadAdminData();
        JScrollPane scrollPane = new JScrollPane(adminTable);
        scrollPane.getViewport().setBackground(Color.GRAY); // Match scroll pane background
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel (Add, Edit, Delete)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(255, 165, 0)); // Orange panel background

        JButton addAdminButton = new JButton("Add New Admin");
        styleButton(addAdminButton, Color.BLUE, Color.WHITE); // Blue button, white text
        addAdminButton.addActionListener(e -> showAddAdminDialog());
        buttonPanel.add(addAdminButton);

        JButton editAdminButton = new JButton("Edit Admin");
        styleButton(editAdminButton, Color.BLUE, Color.WHITE); // Blue button, white text
        editAdminButton.addActionListener(e -> showEditAdminDialog());
        buttonPanel.add(editAdminButton);

        JButton deleteAdminButton = new JButton("Delete Admin");
        styleButton(deleteAdminButton, Color.RED, Color.WHITE); // Red button, white text
        deleteAdminButton.addActionListener(e -> deleteAdmin());
        buttonPanel.add(deleteAdminButton);

        // Navigation Panel (Return to Dashboard)
        JPanel navigationPanel = new JPanel();
        navigationPanel.setBackground(new Color(255, 165, 0)); // Orange panel background
        JButton returnButton = new JButton("Return to Dashboard");
        styleButton(returnButton, Color.BLUE, Color.WHITE); // Blue button, white text
        returnButton.addActionListener(e -> {
            dispose();
            try {
                new DashboardFrame(isAdmin).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening DashboardFrame: " + ex.getMessage());
            }
        });
        navigationPanel.add(returnButton);

        // Combine buttons in SOUTH
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(255, 165, 0)); // Orange panel background
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(navigationPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    public AdminFrame() {
        this(true); // Default to admin for backward compatibility
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    private void loadAdminData() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM admins");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("ssn"),
                    rs.getString("name"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("contact")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading admin data: " + ex.getMessage());
        }
    }

    private void showAddAdminDialog() {
        JTextField ssnField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField passwordField = new JTextField();
        JTextField roleField = new JTextField();
        JTextField contactField = new JTextField();

        Object[] fields = {
            "SSN:", ssnField,
            "Name:", nameField,
            "Username:", usernameField,
            "Email:", emailField,
            "Password:", passwordField,
            "Role:", roleField,
            "Contact:", contactField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add New Admin", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO admins (ssn, name, username, email, password, role, contact) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                stmt.setString(1, ssnField.getText());
                stmt.setString(2, nameField.getText());
                stmt.setString(3, usernameField.getText());
                stmt.setString(4, emailField.getText());
                stmt.setString(5, passwordField.getText());
                stmt.setString(6, roleField.getText());
                stmt.setString(7, contactField.getText());
                stmt.executeUpdate();
                loadAdminData();
                JOptionPane.showMessageDialog(this, "Admin added successfully.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding admin: " + ex.getMessage());
            }
        }
    }

    private void showEditAdminDialog() {
        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an admin to edit.");
            return;
        }

        JTextField ssnField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        JTextField nameField = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
        JTextField usernameField = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
        JTextField emailField = new JTextField((String) tableModel.getValueAt(selectedRow, 4));
        JTextField passwordField = new JTextField((String) tableModel.getValueAt(selectedRow, 5));
        JTextField roleField = new JTextField((String) tableModel.getValueAt(selectedRow, 6));
        JTextField contactField = new JTextField((String) tableModel.getValueAt(selectedRow, 7));

        Object[] fields = {
            "SSN:", ssnField,
            "Name:", nameField,
            "Username:", usernameField,
            "Email:", emailField,
            "Password:", passwordField,
            "Role:", roleField,
            "Contact:", contactField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Edit Admin", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE admins SET ssn = ?, name = ?, username = ?, email = ?, password = ?, role = ?, contact = ? WHERE id = ?")) {
                stmt.setString(1, ssnField.getText());
                stmt.setString(2, nameField.getText());
                stmt.setString(3, usernameField.getText());
                stmt.setString(4, emailField.getText());
                stmt.setString(5, passwordField.getText());
                stmt.setString(6, roleField.getText());
                stmt.setString(7, contactField.getText());
                stmt.setInt(8, (Integer) tableModel.getValueAt(selectedRow, 0));
                stmt.executeUpdate();
                loadAdminData();
                JOptionPane.showMessageDialog(this, "Admin updated successfully.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating admin: " + ex.getMessage());
            }
        }
    }

    private void deleteAdmin() {
        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an admin to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this admin?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM admins WHERE id = ?")) {
                stmt.setInt(1, (Integer) tableModel.getValueAt(selectedRow, 0));
                stmt.executeUpdate();
                loadAdminData();
                JOptionPane.showMessageDialog(this, "Admin deleted successfully.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting admin: " + ex.getMessage());
            }
        }
    }
}