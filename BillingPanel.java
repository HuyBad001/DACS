package admin;

import ketnoi.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class BillingPanel extends JPanel {
    private JComboBox<String> customerBox, packageBox, transactionBox;
    private JTextArea invoiceArea;
    private Map<String, String> customerMap = new HashMap<>();
    private Map<String, PackageInfo> packageMap = new HashMap<>();
    private Map<String, Transaction> transactionMap = new HashMap<>();

    public BillingPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        customerBox = new JComboBox<>();
        packageBox = new JComboBox<>();
        transactionBox = new JComboBox<>();

        invoiceArea = new JTextArea(12, 40);
        invoiceArea.setEditable(false);
        invoiceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        loadCustomers();
        loadPackages();
        loadTransactions();

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Khách hàng:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(customerBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Gói cước:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(packageBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JButton saveBtn = new JButton("Lưu hóa đơn");
        inputPanel.add(saveBtn, gbc);

        gbc.gridx = 1;
        JButton exportBtn = new JButton("Xuất hóa đơn");
        inputPanel.add(exportBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Chọn giao dịch:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(transactionBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton exportFromHistoryBtn = new JButton("Xuất hóa đơn từ lịch sử");
        inputPanel.add(exportFromHistoryBtn, gbc);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(invoiceArea), BorderLayout.CENTER);

        saveBtn.addActionListener(e -> saveInvoice());
        exportBtn.addActionListener(e -> showInvoice());
        exportFromHistoryBtn.addActionListener(e -> showInvoiceFromTransaction());
    }

    private void loadCustomers() {
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name FROM customers");
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                customerMap.put(id, name);
                customerBox.addItem(id + " - " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPackages() {
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name, price FROM packages");
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                packageMap.put(id, new PackageInfo(name, price));
                packageBox.addItem(id + " - " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTransactions() {
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT transaction_id, username, package_id, amount, months, time FROM transactions");
            while (rs.next()) {
                String id = rs.getString("transaction_id");
                String username = rs.getString("username");
                String packageId = rs.getString("package_id");
                double amount = rs.getDouble("amount");
                int months = rs.getInt("months");
                String time = rs.getString("time");
                Transaction t = new Transaction(id, username, packageId, amount, months, time);
                transactionMap.put(id, t);
                transactionBox.addItem(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getIdFromComboItem(String item) {
        return item.split(" - ")[0];
    }

    private String generateTransactionId() {
        return "GD" + System.currentTimeMillis();
    }

    private void saveInvoice() {
        try (Connection conn = DBConnection.getConnection()) {
            String customerItem = (String) customerBox.getSelectedItem();
            String packageItem = (String) packageBox.getSelectedItem();

            String customerId = getIdFromComboItem(customerItem);
            String packageId = getIdFromComboItem(packageItem);

            PreparedStatement customerStmt = conn.prepareStatement(
                    "SELECT name, phone FROM customers WHERE id = ?"
            );
            customerStmt.setString(1, customerId);
            ResultSet crs = customerStmt.executeQuery();
            String customerName = "", phone = "";
            if (crs.next()) {
                customerName = crs.getString("name");
                phone = crs.getString("phone");
            }

            PackageInfo pkg = packageMap.get(packageId);
            if (pkg == null) {
                JOptionPane.showMessageDialog(this, "Gói cước không hợp lệ");
                return;
            }
            double total = pkg.price;

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            PreparedStatement invoiceStmt = conn.prepareStatement(
                    "INSERT INTO invoice_items (customer_id, package_id, months, total, created_at) VALUES (?, ?, ?, ?, ?)"
            );
            invoiceStmt.setString(1, customerId);
            invoiceStmt.setString(2, packageId);
            invoiceStmt.setInt(3, 0);
            invoiceStmt.setDouble(4, total);
            invoiceStmt.setString(5, now);
            invoiceStmt.executeUpdate();

            String transactionId = generateTransactionId();
            PreparedStatement transStmt = conn.prepareStatement(
                    "INSERT INTO transactions (transaction_id, username, package_id, amount, months, time) VALUES (?, ?, ?, ?, ?, ?)"
            );
            transStmt.setString(1, transactionId);
            transStmt.setString(2, customerName);
            transStmt.setString(3, packageId);
            transStmt.setDouble(4, total);
            transStmt.setInt(5, 0);
            transStmt.setString(6, now);
            transStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Đã lưu hóa đơn và giao dịch thành công");
            showInvoice();

            transactionBox.removeAllItems();
            transactionMap.clear();
            loadTransactions();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu hóa đơn: " + e.getMessage());
        }
    }

    private void showInvoice() {
        try (Connection conn = DBConnection.getConnection()) {
            String customerItem = (String) customerBox.getSelectedItem();
            String packageItem = (String) packageBox.getSelectedItem();

            String customerId = getIdFromComboItem(customerItem);
            String packageId = getIdFromComboItem(packageItem);

            PreparedStatement customerStmt = conn.prepareStatement(
                    "SELECT name, phone, email FROM customers WHERE id = ?"
            );
            customerStmt.setString(1, customerId);
            ResultSet crs = customerStmt.executeQuery();
            String customerName = "", phone = "", email = "";
            if (crs.next()) {
                customerName = crs.getString("name");
                phone = crs.getString("phone");
                email = crs.getString("email");
            }

            PackageInfo pkg = packageMap.get(packageId);
            if (pkg == null) {
                JOptionPane.showMessageDialog(this, "Gói cước không hợp lệ");
                return;
            }

            String transactionId = generateTransactionId();

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String invoiceText = """
                --- HÓA ĐƠN INTERNET ---
                Mã giao dịch: %s
                Khách hàng: %s (ID: %s)
                Số điện thoại: %s
                Email: %s
                Gói cước: %s (ID: %s)
                Tổng tiền: %.2f VNĐ
                Thời gian: %s
                -------------------------
                """.formatted(transactionId, customerName, customerId, phone, email,
                    pkg.name, packageId, pkg.price, now);

            // Lưu file hóa đơn
            String fileName = "hoadon_" + transactionId + ".txt";
            try (FileWriter writer = new FileWriter(fileName)) {
                writer.write(invoiceText);
            }

            invoiceArea.setText(invoiceText);
            JOptionPane.showMessageDialog(this, "Đã xuất hóa đơn có mã giao dịch và lưu vào CSDL. File: " + fileName);

            // Cập nhật lại combo transactionBox
            transactionBox.removeAllItems();
            transactionMap.clear();
            loadTransactions();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất hóa đơn: " + e.getMessage());
        }
    }


    private void showInvoiceFromTransaction() {
        try (Connection conn = DBConnection.getConnection()) {
            String transactionId = (String) transactionBox.getSelectedItem();
            if (transactionId == null) {
                JOptionPane.showMessageDialog(this, "Chưa chọn giao dịch");
                return;
            }

            Transaction t = transactionMap.get(transactionId);
            if (t == null) {
                JOptionPane.showMessageDialog(this, "Giao dịch không tồn tại");
                return;
            }

            PreparedStatement customerStmt = conn.prepareStatement(
                    "SELECT id, phone, email FROM customers WHERE name = ?"
            );
            customerStmt.setString(1, t.username);
            ResultSet crs = customerStmt.executeQuery();
            String customerId = "", phone = "", email = "";
            if (crs.next()) {
                customerId = crs.getString("id");
                phone = crs.getString("phone");
                email = crs.getString("email");
            }

            PreparedStatement pkgStmt = conn.prepareStatement(
                    "SELECT name, description FROM packages WHERE id = ?"
            );
            pkgStmt.setString(1, t.packageId);
            ResultSet prs = pkgStmt.executeQuery();
            String packageName = "", description = "";
            if (prs.next()) {
                packageName = prs.getString("name");
                description = prs.getString("description");
            }

            String invoiceText = """
                    --- HÓA ĐƠN INTERNET (Dựa trên lịch sử giao dịch) ---
                    Mã giao dịch: %s
                    Khách hàng: %s (ID: %s)
                    Số điện thoại: %s
                    Email: %s
                    Gói cước: %s (ID: %s)
                    Nội dung gói cước: %s
                    Tổng tiền: %.2f VNĐ
                    Thời gian giao dịch: %s
                    -------------------------
                    """.formatted(
                    t.transactionId,
                    t.username, customerId,
                    phone, email,
                    packageName, t.packageId,
                    description,
                    t.amount,
                    t.time);

            String fileName = "hoadon_" + t.transactionId + ".txt";
            FileWriter writer = new FileWriter(fileName);
            writer.write(invoiceText);
            writer.close();

            invoiceArea.setText(invoiceText);
            JOptionPane.showMessageDialog(this, "Đã xuất hóa đơn từ lịch sử giao dịch ra file: " + fileName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất hóa đơn từ lịch sử: " + e.getMessage());
        }
    }

    private static class PackageInfo {
        String name;
        double price;

        public PackageInfo(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }

    private static class Transaction {
        String transactionId;
        String username;
        String packageId;
        double amount;
        int months;
        String time;

        public Transaction(String transactionId, String username, String packageId, double amount, int months, String time) {
            this.transactionId = transactionId;
            this.username = username;
            this.packageId = packageId;
            this.amount = amount;
            this.months = months;
            this.time = time;
        }
    }
}