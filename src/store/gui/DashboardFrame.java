package com.store.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardFrame extends JFrame {
    private final boolean isAdmin;
    private final String currentUsername; // Store logged-in admin's username

    public DashboardFrame(boolean isAdmin, String currentUsername) {
        this.isAdmin = isAdmin;
        this.currentUsername = currentUsername;
        initialize();
    }

    public DashboardFrame() {
        this.isAdmin = false;
        this.currentUsername = "defaultUser"; // Default for backward compatibility
        initialize();
    }

    private void initialize() {
        setTitle("Store Management System - Dashboard");
        setSize(600, 400); // Stable size
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Stable close operation
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set dark gray background for the content pane
        getContentPane().setBackground(Color.DARK_GRAY);

        // Header with gradient background
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 102, 204), getWidth(), 0, Color.DARK_GRAY);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setBackground(Color.DARK_GRAY);
        headerPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2)); // White border
        JLabel headerLabel = new JLabel("Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Larger, bold font
        headerLabel.setForeground(Color.WHITE); // White text
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setBackground(Color.DARK_GRAY); // Dark gray background
        buttonPanel.setOpaque(true);

        // Button styling
        Color buttonBackground = new Color(0, 153, 255); // Brighter blue
        Color buttonHoverBackground = new Color(51, 181, 255); // Lighter blue for hover

        // Load and scale external icons
        ImageIcon inventoryIcon = createScaledIcon("/resources/icons/inventory.png");
        ImageIcon salesIcon = createScaledIcon("/resources/icons/sales.png");
        ImageIcon customerIcon = createScaledIcon("/resources/icons/customer.png");
        ImageIcon reportIcon = createScaledIcon("/resources/icons/report.png");
        ImageIcon adminIcon = createScaledIcon("/resources/icons/admin.png");
        ImageIcon contactIcon = createScaledIcon("/resources/icons/contact.png");
        ImageIcon logoutIcon = createScaledIcon("/resources/icons/logout.png");

        JButton inventoryButton = new JButton("Inventory Management", inventoryIcon);
        styleButton(inventoryButton, buttonBackground, buttonHoverBackground);
        inventoryButton.addActionListener(e -> {
            dispose();
            try {
                new InventoryFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening InventoryFrame: " + ex.getMessage());
            }
        });
        buttonPanel.add(inventoryButton);

        JButton salesButton = new JButton("Sales Processing", salesIcon);
        styleButton(salesButton, buttonBackground, buttonHoverBackground);
        salesButton.addActionListener(e -> {
            dispose();
            try {
                new SalesFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening SalesFrame: " + ex.getMessage());
            }
        });
        buttonPanel.add(salesButton);

        JButton customerButton = new JButton("Customer Management", customerIcon);
        styleButton(customerButton, buttonBackground, buttonHoverBackground);
        customerButton.addActionListener(e -> {
            dispose();
            try {
                new CustomerFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening CustomerFrame: " + ex.getMessage());
            }
        });
        buttonPanel.add(customerButton);

        JButton reportButton = new JButton("Reporting", reportIcon);
        styleButton(reportButton, buttonBackground, buttonHoverBackground);
        reportButton.addActionListener(e -> {
            dispose();
            try {
                new ReportFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening ReportFrame: " + ex.getMessage());
            }
        });
        buttonPanel.add(reportButton);

        // Admin Management Button (only for admins)
        if (isAdmin) {
            JButton adminButton = new JButton("Admin Management", adminIcon);
            styleButton(adminButton, buttonBackground, buttonHoverBackground);
            adminButton.addActionListener(e -> {
                dispose();
                try {
                    new AdminFrame(isAdmin, currentUsername).setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error opening AdminFrame: " + ex.getMessage());
                }
            });
            buttonPanel.add(adminButton);
        } else {
            buttonPanel.add(new JLabel());
        }

        // Contact Us Button
        JButton contactButton = new JButton("Contact Us", contactIcon);
        styleButton(contactButton, buttonBackground, buttonHoverBackground);
        contactButton.addActionListener(e -> {
            try {
                new ContactUsFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening ContactUsFrame: " + ex.getMessage());
            }
        });
        buttonPanel.add(contactButton);

        add(buttonPanel, BorderLayout.CENTER);

        // Exit Button
        JButton exitButton = new JButton("Logout", logoutIcon);
        styleButton(exitButton, buttonBackground, buttonHoverBackground);
        exitButton.addActionListener(e -> {
            dispose();
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening LoginFrame: " + ex.getMessage());
            }
        });
        JPanel exitPanel = new JPanel();
        exitPanel.setBackground(Color.DARK_GRAY); // Dark gray background
        exitPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        exitPanel.add(exitButton);
        add(exitPanel, BorderLayout.SOUTH);
    }

    // Helper method to style buttons
    private void styleButton(JButton button, Color background, Color hoverBackground) {
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1)); // Subtle white border
        button.setFocusPainted(false); // Remove focus border
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Bold, larger font
        button.setPreferredSize(new Dimension(200, 50)); // Larger button size
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