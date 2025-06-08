package com.store.gui;

import com.store.db.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Store Management System - Login");
        setSize(1200, 700); // Stable size
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Stable close operation
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
        headerLabel.setFont(new Font("Arial", Font.BOLD, 32)); // Larger, bold font
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

        // Load and scale external icons
        ImageIcon loginIcon = createScaledIcon("/resources/icons/login.png");
        ImageIcon exitIcon = createScaledIcon("/resources/icons/exit.png");

        // Button styling
        Color buttonBackground = new Color(0, 204, 0); // Brighter green
        Color buttonHoverBackground = new Color(51, 255, 51); // Lighter green for hover

        JButton loginButton = new JButton("Login", loginIcon);
        styleButton(loginButton, buttonBackground, buttonHoverBackground);
        loginButton.addActionListener(e -> authenticate());
        buttonPanel.add(loginButton);

        JButton exitButton = new JButton("Exit", exitIcon);
        styleButton(exitButton, new Color(255, 51, 51), new Color(255, 102, 102)); // Brighter red, lighter red for hover
        exitButton.addActionListener(e -> {
               dispose();
            try {
                new WelcomeFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error returning to Welcome page: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
          );
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Helper method to authenticate user
    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT role, password FROM admins WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                String role = rs.getString("role");
                // Verify password using BCrypt
                if (BCrypt.checkpw(password, storedHash)) {
                    boolean isAdmin = "Admin".equalsIgnoreCase(role);
                    dispose();
                    new DashboardFrame(isAdmin, username).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to style buttons
    private void styleButton(JButton button, Color background, Color hoverBackground) {
        button.setBackground(background);
        button.setForeground(button.getBackground() == Color.BLACK ? Color.WHITE : Color.BLACK); // Black text for green, white for red
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1)); // Subtle white border
        button.setFocusPainted(false); // Remove focus border
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Bold, larger font
        button.setPreferredSize(new Dimension(120, 40)); // Larger button size
        button.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon to the left
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBackground);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(background);
            }
        });
    }

    // Helper method to load and scale external icons
    private ImageIcon createScaledIcon(String path) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            if (icon.getIconWidth() == -1) { // Check if icon failed to load
                System.err.println("Icon not found: " + path);
                return new ImageIcon(((ImageIcon) UIManager.getIcon("OptionPane.informationIcon")).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            }
            return new ImageIcon(icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.err.println("Error loading icon " + path + ": " + e.getMessage());
            return new ImageIcon(((ImageIcon) UIManager.getIcon("OptionPane.informationIcon")).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        }
    }
}