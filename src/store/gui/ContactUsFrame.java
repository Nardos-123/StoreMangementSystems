package com.store.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ContactUsFrame extends JFrame {
    public ContactUsFrame() {
        setTitle("Store Management System - Contact Us");
        setSize(1200, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set frame icon (replace with your icon path)
        try {
            ImageIcon icon = new ImageIcon("path/to/your/icon.png");
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Icon not found, using default");
        }

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(100, 150, 255), // Light blue
                    getWidth(), getHeight(), new Color(255, 100, 150) // Pinkish
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);
        setContentPane(mainPanel);

        // Header
        JLabel headerLabel = new JLabel("Contact Us", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Contact Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 10, 15));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Style labels
        Font infoFont = new Font("Arial", Font.PLAIN, 16);
        JLabel[] labels = {
            new JLabel("<html><b>Email:</b> support@store.com</html>"),
            new JLabel("<html><b>Phone:</b> (123) 456-7890</html>"),
            new JLabel("<html><b>Address:</b> 123 Store St, Debre birhan, Ethiopa</html>"),
            new JLabel("<html><b>Hours:</b> Mon-Fri, 9 AM - 5 PM</html>")
        };
        for (JLabel label : labels) {
            label.setFont(infoFont);
            label.setForeground(Color.WHITE);
            infoPanel.add(label);
        }
        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Close Button
        JButton closeButton = new JButton("Close") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(200, 50, 100));
                } else {
                    g2d.setColor(new Color(255, 100, 150));
                }
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // No border
            }
        };
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setForeground(Color.WHITE);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(100, 40));
        closeButton.addActionListener(e -> dispose());

        JPanel closePanel = new JPanel();
        closePanel.setOpaque(false);
        closePanel.add(closeButton);
        closePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(closePanel, BorderLayout.SOUTH);
    }
}