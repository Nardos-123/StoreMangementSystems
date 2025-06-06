package com.store.gui;

import com.store.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class CustomerRegistrationFrame extends JFrame {
    private JTextField nameField, emailField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private static final boolean REQUIRE_PASSWORD = false; // Set to true to enforce passwords for new users

    public CustomerRegistrationFrame() {
        setTitle("Customer Registration");
        setSize(1200, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(139, 69, 19)); // Brown background

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(139, 69, 19)); // Brown background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing

        // Labels and Text Fields
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE); // White label text
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        nameField.setBackground(Color.WHITE); // White background
        nameField.setForeground(Color.BLACK); // Black text
        formPanel.add(nameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE); // White label text
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setBackground(Color.WHITE); // White background
        emailField.setForeground(Color.BLACK); // Black text
        formPanel.add(emailField, gbc);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setForeground(Color.WHITE); // White label text
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(20);
        phoneField.setBackground(Color.WHITE); // White background
        phoneField.setForeground(Color.BLACK); // Black text
        formPanel.add(phoneField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE); // White label text
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setBackground(Color.WHITE); // White background
        passwordField.setForeground(Color.BLACK); // Black text
        formPanel.add(passwordField, gbc);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setForeground(Color.WHITE); // White label text
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setBackground(Color.WHITE); // White background
        confirmPasswordField.setForeground(Color.BLACK); // Black text
        formPanel.add(confirmPasswordField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(139, 69, 19)); // Brown background

        JButton registerButton = new JButton("Register");
        styleButton(registerButton);
        registerButton.addActionListener(e -> registerCustomer());
        buttonPanel.add(registerButton);

        JButton clearButton = new JButton("Clear");
        styleButton(clearButton);
        clearButton.addActionListener(e -> clearFields());
        buttonPanel.add(clearButton);

        JButton returnButton = new JButton("Return");
        styleButton(returnButton);
        returnButton.addActionListener(e -> {
            dispose();
            new WelcomeFrame().setVisible(true); // Assumes DashboardFrame exists
        });
        buttonPanel.add(returnButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 0, 255)); // Blue background
        button.setForeground(Color.WHITE); // White text
        button.setPreferredSize(new Dimension(120, 40)); // Consistent button size
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
    }

    private void registerCustomer() {
        // Validate required fields
        if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, email, and phone are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate password
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (REQUIRE_PASSWORD && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.isEmpty() || !confirmPassword.isEmpty()) {
            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Optional: Password strength validation
            if (password.length() < 8) {
                JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.matches(".*[A-Z].*")) {
                JOptionPane.showMessageDialog(this, "Password must contain at least one uppercase letter!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.matches(".*[a-z].*")) {
                JOptionPane.showMessageDialog(this, "Password must contain at least one lowercase letter!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.matches(".*[0-9].*")) {
                JOptionPane.showMessageDialog(this, "Password must contain at least one number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.matches(".*[!@#$%^&*()].*")) {
                JOptionPane.showMessageDialog(this, "Password must contain at least one special character (!@#$%^&*())!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Hash password if provided
        String hashedPassword = password.isEmpty() ? null : BCrypt.hashpw(password, BCrypt.gensalt());

        // Insert into database
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO customers (name, email, phone, password) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, nameField.getText().trim());
            stmt.setString(2, emailField.getText().trim());
            stmt.setString(3, phoneField.getText().trim());
            stmt.setString(4, hashedPassword);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer registered successfully!");
            clearFields();
        } catch (SQLException ex) {
            System.err.println("Error registering customer: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error registering customer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new CustomerRegistrationFrame().setVisible(true));
//    }
}