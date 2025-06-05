package com.store.gui;

import com.store.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportFrame extends JFrame {
    public ReportFrame() {
        setTitle("Reporting");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(139, 69, 19)); // Brown background

        // Header
        JLabel headerLabel = new JLabel("Generate Reports", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE); // White text for label
        add(headerLabel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 15, 15)); // Increased spacing
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // More padding
        buttonPanel.setBackground(new Color(139, 69, 19)); // Brown background

        // Buttons with blue background and white text
        JButton salesReportButton = new JButton("Sales Report");
        styleButton(salesReportButton);
        salesReportButton.addActionListener(e -> generateSalesReport());
        buttonPanel.add(salesReportButton);

        JButton inventoryReportButton = new JButton("Inventory Report");
        styleButton(inventoryReportButton);
        inventoryReportButton.addActionListener(e -> generateInventoryReport());
        buttonPanel.add(inventoryReportButton);

        JButton financialReportButton = new JButton("Financial Summary");
        styleButton(financialReportButton);
        financialReportButton.addActionListener(e -> generateFinancialReport());
        buttonPanel.add(financialReportButton);

        JButton exitButton = new JButton("Exit to Dashboard");
        styleButton(exitButton);
        exitButton.addActionListener(e -> {
            dispose();
            new DashboardFrame().setVisible(true);
        });
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    // Helper method to style buttons
    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 102, 204)); // Blue background
        button.setForeground(Color.WHITE); // White text
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
    }

    private void generateSalesReport() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT s.id, c.name AS customer, p.name AS product, s.quantity, s.total_price, s.sale_date " +
                     "FROM sales s " +
                     "INNER JOIN customers c ON s.customer_id = c.id " +
                     "INNER JOIN products p ON s.product_id = p.id")) {
            List<String> reportData = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            reportData.add("Generated on: " + now.format(formatter));
            reportData.add("Sales Report");
            reportData.add("=================");
            while (rs.next()) {
                reportData.add("Sale ID: " + rs.getInt("id"));
                reportData.add("Customer: " + rs.getString("customer"));
                reportData.add("Product: " + rs.getString("product"));
                reportData.add("Quantity: " + rs.getInt("quantity"));
                reportData.add("Total Price: $" + rs.getDouble("total_price"));
                reportData.add("Date: " + rs.getTimestamp("sale_date"));
                reportData.add("-----------------");
            }

            String fileName = "sales_report_" + now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                for (String line : reportData) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            displayReport("Sales Report", reportData);
            JOptionPane.showMessageDialog(this, "Sales report generated successfully as " + fileName + " and displayed!");
        } catch (SQLException ex) {
            String errorMsg = "SQL Error: " + ex.getMessage() + "\nSQL State: " + ex.getSQLState() + "\nError Code: " + ex.getErrorCode();
            JOptionPane.showMessageDialog(this, errorMsg, "SQL Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "IO Error writing report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void generateInventoryReport() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
            List<String> reportData = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            reportData.add("Generated on: " + now.format(formatter));
            reportData.add("Inventory Report");
            reportData.add("=================");
            while (rs.next()) {
                reportData.add("Product ID: " + rs.getInt("id"));
                reportData.add("Name: " + rs.getString("name"));
                reportData.add("Category: " + rs.getString("category"));
                reportData.add("Price: $" + rs.getDouble("price"));
                reportData.add("Stock: " + rs.getInt("stock"));
                reportData.add("-----------------");
            }

            String fileName = "inventory_report_" + now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                for (String line : reportData) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            displayReport("Inventory Report", reportData);
            JOptionPane.showMessageDialog(this, "Inventory report generated successfully as " + fileName + " and displayed!");
        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateFinancialReport() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(total_price) AS total FROM sales")) {
            List<String> reportData = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            reportData.add("Generated on: " + now.format(formatter));
            reportData.add("Financial Summary");
            reportData.add("=================");
            if (rs.next()) {
                reportData.add("Total Sales Revenue: $" + rs.getDouble("total"));
            }

            String fileName = "financial_summary_" + now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                for (String line : reportData) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            displayReport("Financial Summary", reportData);
            JOptionPane.showMessageDialog(this, "Financial summary generated successfully as " + fileName + " and displayed!");
        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayReport(String title, List<String> reportData) {
        JFrame reportFrame = new JFrame(title);
        reportFrame.setSize(500, 400);
        reportFrame.setLocationRelativeTo(this);
        reportFrame.setLayout(new BorderLayout());
        reportFrame.getContentPane().setBackground(new Color(139, 69, 19)); // Brown background

        // Create a JTextArea for the report (interpreted as the "table")
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setBackground(Color.LIGHT_GRAY); // Gray background for "table"
        reportArea.setForeground(Color.BLACK); // Black text
        reportArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        for (String line : reportData) {
            reportArea.append(line + "\n");
        }

        // Add the JTextArea to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(reportArea);
        reportFrame.add(scrollPane, BorderLayout.CENTER);

        // Add a Close button
        JButton closeButton = new JButton("Close");
        styleButton(closeButton); // Apply blue and white styling
        closeButton.addActionListener(e -> reportFrame.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(139, 69, 19)); // Brown background
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(closeButton);
        reportFrame.add(buttonPanel, BorderLayout.SOUTH);

        reportFrame.setVisible(true);
    }
}