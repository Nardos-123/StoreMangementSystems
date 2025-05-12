package com.store.gui;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private final boolean isAdmin;

    public DashboardFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;
        initialize();
    }

    public DashboardFrame() {
        this.isAdmin = false;
        initialize();
    }

    private void initialize() {
        setTitle("Store Management System - Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set dark gray background for the content pane
        getContentPane().setBackground(Color.DARK_GRAY);

        // Header
        JLabel headerLabel = new JLabel("Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE); // White text for visibility
        add(headerLabel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setBackground(Color.DARK_GRAY); // Dark gray background
        buttonPanel.setOpaque(true);

        // Button styling
        Color buttonBackground = new Color(0, 102, 204); // Blue background
        Color buttonForeground = Color.WHITE; // White text

        JButton inventoryButton = new JButton("Inventory Management");
        styleButton(inventoryButton, buttonBackground, buttonForeground);
        inventoryButton.addActionListener(e -> {
            dispose();
            try {
                new InventoryFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening InventoryFrame: " + ex.getMessage());
            }
        });
        buttonPanel.add(inventoryButton);

        JButton salesButton = new JButton("Sales Processing");
        styleButton(salesButton, buttonBackground, buttonForeground);
        salesButton.addActionListener(e -> {
            dispose();
            try {
                new SalesFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening SalesFrame: " + ex.getMessage());
            }
        });
        buttonPanel.add(salesButton);

        JButton customerButton = new JButton("Customer Management");
        styleButton(customerButton, buttonBackground, buttonForeground);
        customerButton.addActionListener(e -> {
            dispose();
            try {
                new CustomerFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening CustomerFrame: " + ex.getMessage());
            }
        });
        buttonPanel.add(customerButton);

        JButton reportButton = new JButton("Reporting");
        styleButton(reportButton, buttonBackground, buttonForeground);
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
            JButton adminButton = new JButton("Admin Management");
            styleButton(adminButton, buttonBackground, buttonForeground);
            adminButton.addActionListener(e -> {
                dispose();
                try {
                    new AdminFrame(isAdmin).setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error opening AdminFrame: " + ex.getMessage());
                }
            });
            buttonPanel.add(adminButton);
        } else {
            buttonPanel.add(new JLabel());
        }

        // Contact Us Button
        JButton contactButton = new JButton("Contact Us");
        styleButton(contactButton, buttonBackground, buttonForeground);
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
        JButton exitButton = new JButton("Logout");
        styleButton(exitButton, buttonBackground, buttonForeground);
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
        exitPanel.add(exitButton);
        add(exitPanel, BorderLayout.SOUTH);
    }

    // Helper method to style buttons
    private void styleButton(JButton button, Color background, Color foreground) {
        button.setBackground(background);
        button.setForeground(foreground);
        button.setOpaque(true);
        button.setBorderPainted(false); // Remove border for cleaner look
        button.setFocusPainted(false); // Remove focus border
        button.setFont(new Font("Arial", Font.PLAIN, 14)); // Consistent font
    }
}