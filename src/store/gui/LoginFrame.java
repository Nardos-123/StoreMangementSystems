package com.store.gui;

import com.store.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Store Management System - Login");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set black background for the frame's content pane
        getContentPane().setBackground(Color.BLACK);

        // Header
        JLabel headerLabel = new JLabel("Login", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE); // White text for visibility
        add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.BLACK); // Black background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE); // White text
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setBackground(Color.WHITE); // Dark gray for contrast
        usernameField.setForeground(Color.DARK_GRAY); // White text
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE); // White text
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setBackground(Color.WHITE); // Dark gray for contrast
        passwordField.setForeground(Color.DARK_GRAY); // White text
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK); // Black background

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(Color.GREEN); // Green background
        loginButton.setForeground(Color.BLACK); // Black text for visibility
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false); // Optional: removes border for cleaner look
        loginButton.addActionListener(e -> authenticate());
        buttonPanel.add(loginButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setBackground(Color.RED); // Red background
        exitButton.setForeground(Color.WHITE); // White text for visibility
        exitButton.setOpaque(true);
        exitButton.setBorderPainted(false); // Optional: removes border for cleaner look
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT role FROM admins WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password); // Note: In production, use hashed passwords
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                boolean isAdmin = "Admin".equalsIgnoreCase(role);
                dispose();
                new DashboardFrame(isAdmin).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}