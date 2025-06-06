package com.store.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class WelcomeFrame extends JFrame {
    public WelcomeFrame() {
        setTitle("Welcome to Store Management");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(600, 400)); // Minimum size for responsiveness

        // Custom panel with gradient background
        JPanel backgroundPanel = new GradientPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        // Banner
        JLabel banner = new JLabel("Online Store Management");
        banner.setFont(new Font("Arial", Font.BOLD, 36));
        banner.setForeground(new Color(255, 215, 0)); // Gold color for banner text
        banner.setHorizontalAlignment(SwingConstants.CENTER);
        banner.setOpaque(true);
        banner.setBackground(new Color(0, 0, 128)); // Navy blue background for banner
        banner.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel for buttons and descriptions
        JPanel overlayPanel = new JPanel(new GridBagLayout());
        overlayPanel.setOpaque(false); // Transparent to show gradient
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Increased spacing for better look

        // Login Button
        JButton loginButton = new JButton();
        URL loginIconUrl = WelcomeFrame.class.getResource("/resources/icons/login.png"); // Adjusted path
        if (loginIconUrl != null) {
            loginButton.setIcon(new ImageIcon(loginIconUrl));
        } else {
            loginButton.setText("Login");
            System.err.println("Login icon not found at /resources/icons/login.png");
        }
        styleButton(loginButton);
        loginButton.addActionListener(e -> {
            System.out.println("Login button clicked - Attempting to open LoginFrame");
            try {
                dispose();
                LoginFrame loginFrame = new LoginFrame(); // Explicitly instantiate
                loginFrame.setVisible(true); // Redirect to the actual LoginFrame
            } catch (Exception ex) {
                System.err.println("Error redirecting to LoginFrame: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error opening Login page: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(255, 165, 0)); // Orange on hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(0, 191, 255)); // Reset to light blue
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        overlayPanel.add(loginButton, gbc);

        JLabel loginDesc = new JLabel("<html><font color='white'>Login for existing customers</font></html>");
        loginDesc.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.5;
        overlayPanel.add(loginDesc, gbc);

        // Register Button
        JButton registerButton = new JButton();
        URL registerIconUrl = WelcomeFrame.class.getResource("/resources/icons/customer.png"); // Adjusted path
        if (registerIconUrl != null) {
            registerButton.setIcon(new ImageIcon(registerIconUrl));
        } else {
            registerButton.setText("Register");
            System.err.println("Register icon not found at /resources/icons/customer.png");
        }
        styleButton(registerButton);
        registerButton.addActionListener(e -> {
            try {
                dispose();
                new CustomerRegistrationFrame().setVisible(true);
            } catch (Exception ex) {
                System.err.println("Error redirecting to CustomerRegistrationFrame: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error opening Registration page: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(new Color(255, 165, 0)); // Orange on hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(new Color(0, 191, 255)); // Reset to light blue
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        overlayPanel.add(registerButton, gbc);

        JLabel registerDesc = new JLabel("<html><font color='white'>Register for new customers</font></html>");
        registerDesc.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.5;
        overlayPanel.add(registerDesc, gbc);

        // Layout setup
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        backgroundPanel.add(banner, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(overlayPanel, gbc);

        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setForeground(Color.WHITE); // White text
        button.setBackground(new Color(0, 191, 255)); // Light blue background
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20))); // Padding and border
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover
    }

    // Custom panel for gradient background
    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, new Color(0, 0, 128), // Dark blue
                                               0, height, new Color(75, 0, 130)); // Indigo
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }

  
}