package admin;

import ketnoi.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;

public class TransactionForm extends JDialog {
    private JTextField tfTransactionId;
    private JTextField tfUsername;
    private JTextField tfPhone;
    private JTextField tfEmail;
    private JComboBox<String> cbPackage;
    private JTextField tfAmount;
    private JTextField tfMonths;
    private JFormattedTextField tfTime;

    private TransactionHistory parent;
    private String transactionId;

    public TransactionForm(TransactionHistory parent, String transactionId) {
        super((Frame) null, true);
        this.parent = parent;
        this.transactionId = transactionId;

        setTitle(transactionId == null ? "Thêm giao dịch" : "Sửa giao dịch");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        initUI();
        loadPackages();

        if (transactionId != null) {
            loadTransactionData();
            tfTransactionId.setEditable(false);
        } else {
            tfTransactionId.setEditable(true);
        }

        setVisible(true);
    }

    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Mã giao dịch:"), gbc);
        tfTransactionId = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        add(tfTransactionId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Tên người dùng:"), gbc);
        tfUsername = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1;
        add(tfUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Số điện thoại:"), gbc);
        tfPhone = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        add(tfPhone, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Email:"), gbc);
        tfEmail = new JTextField();
        gbc.gridx = 1; gbc.gridy = 3;
        add(tfEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Gói cước:"), gbc);
        cbPackage = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 4;
        add(cbPackage, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("Số tiền (VNĐ):"), gbc);
        tfAmount = new JTextField();
        gbc.gridx = 1; gbc.gridy = 5;
        add(tfAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        add(new JLabel("Số tháng:"), gbc);
        tfMonths = new JTextField();
        gbc.gridx = 1; gbc.gridy = 6;
        add(tfMonths, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        add(new JLabel("Thời gian (yyyy-MM-dd HH:mm:ss):"), gbc);
        tfTime = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        tfTime.setValue(new java.util.Date());
        gbc.gridx = 1; gbc.gridy = 7;
        add(tfTime, gbc);

        JPanel btnPanel = new JPanel();
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> saveTransaction());
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        add(btnPanel, gbc);
    }

    private void loadPackages() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT name FROM packages";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cbPackage.addItem(rs.getString("name"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải gói cước: " + e.getMessage());
        }
    }

    private void loadTransactionData() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT t.transaction_id, t.username, t.phone, t.email, p.name AS package_name, t.amount, t.time, t.months " +
                    "FROM transactions t JOIN packages p ON t.package_id = p.id WHERE t.transaction_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, transactionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tfTransactionId.setText(rs.getString("transaction_id"));
                tfUsername.setText(rs.getString("username"));
                tfPhone.setText(rs.getString("phone"));
                tfEmail.setText(rs.getString("email"));
                cbPackage.setSelectedItem(rs.getString("package_name"));
                tfAmount.setText(rs.getString("amount"));
                tfMonths.setText(String.valueOf(rs.getInt("months")));
                tfTime.setValue(rs.getTimestamp("time"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu giao dịch: " + e.getMessage());
        }
    }

    private void saveTransaction() {
        String id = tfTransactionId.getText().trim();
        String username = tfUsername.getText().trim();
        String phone = tfPhone.getText().trim();
        String email = tfEmail.getText().trim();
        String packageName = (String) cbPackage.getSelectedItem();
        String amountStr = tfAmount.getText().trim();
        String monthsStr = tfMonths.getText().trim();
        java.util.Date timeValue = (java.util.Date) tfTime.getValue();

        if (id.isEmpty() || username.isEmpty() || phone.isEmpty() || email.isEmpty() || packageName == null
                || amountStr.isEmpty() || monthsStr.isEmpty() || timeValue == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        try {
            int amount = Integer.parseInt(amountStr);
            int months = Integer.parseInt(monthsStr);
            Timestamp time = new Timestamp(timeValue.getTime());

            try (Connection conn = DBConnection.getConnection()) {
                // Lấy package_id từ tên gói cước
                String sqlPkg = "SELECT id FROM packages WHERE name = ?";
                PreparedStatement psPkg = conn.prepareStatement(sqlPkg);
                psPkg.setString(1, packageName);
                ResultSet rsPkg = psPkg.executeQuery();
                String packageId = null;
                if (rsPkg.next()) {
                    packageId = rsPkg.getString("id");
                } else {
                    JOptionPane.showMessageDialog(this, "Gói cước không hợp lệ.");
                    return;
                }

                if (transactionId == null) {
                    // Insert mới
                    String sqlInsert = "INSERT INTO transactions (transaction_id, username, phone, email, package_id, amount, time, months) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
                    psInsert.setString(1, id);
                    psInsert.setString(2, username);
                    psInsert.setString(3, phone);
                    psInsert.setString(4, email);
                    psInsert.setString(5, packageId);
                    psInsert.setInt(6, amount);
                    psInsert.setTimestamp(7, time);
                    psInsert.setInt(8, months);
                    psInsert.executeUpdate();
                } else {
                    // Update
                    String sqlUpdate = "UPDATE transactions SET username = ?, phone = ?, email = ?, package_id = ?, amount = ?, time = ?, months = ? " +
                            "WHERE transaction_id = ?";
                    PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
                    psUpdate.setString(1, username);
                    psUpdate.setString(2, phone);
                    psUpdate.setString(3, email);
                    psUpdate.setString(4, packageId);
                    psUpdate.setInt(5, amount);
                    psUpdate.setTimestamp(6, time);
                    psUpdate.setInt(7, months);
                    psUpdate.setString(8, id);
                    psUpdate.executeUpdate();
                }

                parent.loadTransactions();
                JOptionPane.showMessageDialog(this, "Lưu thành công!");
                dispose();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền và số tháng phải là số nguyên hợp lệ.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + ex.getMessage());
        }
    }
}