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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Consistent spacing
        gbc.fill = GridBagConstraints.BOTH;

        // Logo
        JLabel logoLabel = new JLabel();
        URL logoUrl = WelcomeFrame.class.getResource("/resources/icons/store_logo.png");
        if (logoUrl != null) {
            ImageIcon logoIcon = new ImageIcon(logoUrl);
            Image scaledImage = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            logoLabel.setText("Store Logo");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
            logoLabel.setForeground(Color.WHITE);
            System.err.println("Logo not found at /resources/icons/store_logo.png");
        }
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        backgroundPanel.add(logoLabel, gbc);

        // Banner
        JLabel banner = new JLabel("Online Store Management");
        banner.setFont(new Font("Arial", Font.BOLD, 40));
        banner.setForeground(new Color(255, 215, 0)); // Gold color
        banner.setHorizontalAlignment(SwingConstants.CENTER);
        banner.setOpaque(true);
        banner.setBackground(new Color(0, 0, 128, 180)); // Semi-transparent navy blue
        banner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        backgroundPanel.add(banner, gbc);

        // Welcome Message
        JLabel welcomeMessage = new JLabel("<html><center>Welcome to our Store Management System!<br>Login as a manager or register as a customer to get started.</center></html>");
        welcomeMessage.setFont(new Font("Arial", Font.PLAIN, 20));
        welcomeMessage.setForeground(Color.WHITE);
        welcomeMessage.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0.1;
        backgroundPanel.add(welcomeMessage, gbc);

        // Panel for buttons and descriptions
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false); // Transparent to show gradient
        gbc.gridy = 3;
        gbc.weighty = 0.7;
        backgroundPanel.add(buttonPanel, gbc);

        // Button styling properties
        Color buttonBackground = new Color(0, 191, 255); // Light blue
        Color buttonHoverBackground = new Color(255, 165, 0); // Orange on hover

        // Login Button
        JButton loginButton = new JButton("Login");
        URL loginIconUrl = WelcomeFrame.class.getResource("/resources/icons/login.png");
        if (loginIconUrl != null) {
            ImageIcon loginIcon = new ImageIcon(loginIconUrl);
            Image scaledImage = loginIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            loginButton.setIcon(new ImageIcon(scaledImage));
        } else {
            System.err.println("Login icon not found at /resources/icons/login.png");
        }
        styleButton(loginButton, buttonBackground, buttonHoverBackground);
        loginButton.setToolTipText("Login for store managers and admins");
        loginButton.addActionListener(e -> {
            System.out.println("Login button clicked - Attempting to open LoginFrame");
            try {
                dispose();
                new LoginFrame().setVisible(true);
            } catch (Exception ex) {
                System.err.println("Error redirecting to LoginFrame: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error opening Login page: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(10, 20, 10, 20);
        buttonPanel.add(loginButton, gbc);

        JLabel loginDesc = new JLabel("<html><font color='white'>Login for Store Managers</font></html>");
        loginDesc.setHorizontalAlignment(SwingConstants.CENTER);
        loginDesc.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.0;
        buttonPanel.add(loginDesc, gbc);

        // Register Button
        JButton registerButton = new JButton("Register");
        URL registerIconUrl = WelcomeFrame.class.getResource("/resources/icons/customer.png");
        if (registerIconUrl != null) {
            ImageIcon registerIcon = new ImageIcon(registerIconUrl);
            Image scaledImage = registerIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            registerButton.setIcon(new ImageIcon(scaledImage));
        } else {
            System.err.println("Register icon not found at /resources/icons/customer.png");
        }
        styleButton(registerButton, buttonBackground, buttonHoverBackground);
        registerButton.setToolTipText("Register as a new customer");
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
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        buttonPanel.add(registerButton, gbc);

        JLabel registerDesc = new JLabel("<html><font color='white'>Register for New Customers</font></html>");
        registerDesc.setHorizontalAlignment(SwingConstants.CENTER);
        registerDesc.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 0.0;
        buttonPanel.add(registerDesc, gbc);
    }

    private void styleButton(JButton button, Color background, Color hoverBackground) {
        button.setForeground(Color.WHITE);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(12, 24, 12, 24)
        ));
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBackground);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.YELLOW, 2),
                    BorderFactory.createEmptyBorder(12, 24, 12, 24)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(background);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 2),
                    BorderFactory.createEmptyBorder(12, 24, 12, 24)
                ));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(255, 140, 0)); // Darker orange on click
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(hoverBackground);
            }
        });
        button.setPreferredSize(new Dimension(200, 60));
        button.setMinimumSize(new Dimension(150, 50));
    }

    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, new Color(0, 128, 128), // Teal
                                               0, height, new Color(128, 0, 128)); // Purple
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }

   
}