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

        // Header with enhanced Welcome message
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 102, 204), getWidth(), 0, Color.BLACK);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setBackground(Color.BLACK);
        headerPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2)); // White border for effect
        JLabel headerLabel = new JLabel("Welcome to Store Management System", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Larger, bold font
        headerLabel.setForeground(Color.WHITE); // White text
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.BLACK); // Black background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE); // White text
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Bold for visibility
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setBackground(Color.WHITE);
        usernameField.setForeground(Color.DARK_GRAY);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE); // White text
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Bold for visibility
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(Color.DARK_GRAY);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK); // Black background
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Add spacing

        // Scale icons for better visibility
        ImageIcon loginIcon = new ImageIcon(((ImageIcon) UIManager.getIcon("OptionPane.informationIcon")).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        ImageIcon exitIcon = new ImageIcon(((ImageIcon) UIManager.getIcon("OptionPane.errorIcon")).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

        JButton loginButton = new JButton("Login", loginIcon);
        loginButton.setBackground(new Color(0, 204, 0)); // Brighter green
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16)); // Bold, larger font
        loginButton.setOpaque(true);
        loginButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1)); // Subtle border
        loginButton.setPreferredSize(new Dimension(120, 40)); // Larger button
        loginButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon to the left
        loginButton.addActionListener(e -> authenticate());
        buttonPanel.add(loginButton);

        JButton exitButton = new JButton("Exit", exitIcon);
        exitButton.setBackground(new Color(255, 51, 51)); // Brighter red
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("Arial", Font.BOLD, 16)); // Bold, larger font
        exitButton.setOpaque(true);
        exitButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1)); // Subtle border
        exitButton.setPreferredSize(new Dimension(120, 40)); // Larger button
        exitButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon to the left
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