package admin;

import ketnoi.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class TransactionHistory extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtUsername;
    private JTextField txtFromDate;
    private JTextField txtToDate;

    public TransactionHistory() {
        setLayout(new BorderLayout());
        initUI();
        loadTransactions();
    }

    private void initUI() {

        String[] columns = { "Mã giao dịch", "Tên người dùng", "Số điện thoại", "Email", "Gói cước", "Số tiền", "Thời gian" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);

        JLabel title = new JLabel("LỊCH SỬ GIAO DỊCH", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(52, 73, 94));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // khoảng cách giữa các ô

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Tên người dùng:"), gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        searchPanel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        searchPanel.add(new JLabel("Từ ngày (dd/MM/yyyy):"), gbc);

        gbc.gridx = 1;
        txtFromDate = new JTextField(10);
        searchPanel.add(txtFromDate, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        searchPanel.add(new JLabel("Đến ngày:"), gbc);

        gbc.gridx = 1;
        txtToDate = new JTextField(10);
        searchPanel.add(txtToDate, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String fromDate = txtFromDate.getText().trim();
            String toDate = txtToDate.getText().trim();
            searchTransactions(username, fromDate, toDate);
        });
        searchPanel.add(btnSearch, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnAdd = new JButton("Thêm");
//		JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnReload = new JButton("Tải lại");

        btnAdd.addActionListener(e -> new TransactionForm(this, null));
//		btnEdit.addActionListener(e -> editTransaction());
        btnDelete.addActionListener(e -> deleteTransaction());
        btnReload.addActionListener(e -> loadTransactions());

        btnPanel.add(btnAdd);
//		btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnReload);

        JPanel topContent = new JPanel(new BorderLayout());
        topContent.add(searchPanel, BorderLayout.CENTER);
        topContent.add(btnPanel, BorderLayout.EAST);

        topPanel.add(topContent, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadTransactions() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT t.transaction_id, t.username, u.phone, u.email, p.name AS package_name, t.amount, t.time " +
                    "FROM transactions t " +
                    "JOIN packages p ON t.package_id = p.id " +
                    "JOIN customers u ON t.username = u.name " +
                    "ORDER BY t.time DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            while (rs.next()) {
                String transactionId = rs.getString("transaction_id");
                String username = rs.getString("username");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String packageName = rs.getString("package_name");
                String amount = String.format("%,d VNĐ", rs.getInt("amount"));
                Timestamp time = rs.getTimestamp("time");
                String formattedTime = (time != null) ? sdf.format(time) : "";

                tableModel.addRow(new Object[]{
                        transactionId,
                        username,
                        phone,
                        email,
                        packageName,
                        amount,
                        formattedTime,
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void searchTransactions(String username, String fromDate, String toDate) {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder sql = new StringBuilder(
                    "SELECT t.transaction_id, t.username, u.phone, u.email, p.name AS package_name, t.amount, t.time " +
                            "FROM transactions t " +
                            "JOIN packages p ON t.package_id = p.id " +
                            "JOIN customers u ON t.username = u.name WHERE 1=1 "
            );

            if (!username.isEmpty()) {
                sql.append("AND t.username LIKE ? ");
            }
            if (!fromDate.isEmpty()) {
                sql.append("AND t.time >= ? ");
            }
            if (!toDate.isEmpty()) {
                sql.append("AND t.time <= ? ");
            }

            sql.append("ORDER BY t.time DESC");
            PreparedStatement ps = conn.prepareStatement(sql.toString());

            int paramIndex = 1;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            if (!username.isEmpty()) {
                ps.setString(paramIndex++, "%" + username + "%");
            }
            if (!fromDate.isEmpty()) {
                java.util.Date from = sdf.parse(fromDate);
                ps.setTimestamp(paramIndex++, new Timestamp(from.getTime()));
            }
            if (!toDate.isEmpty()) {
                java.util.Date to = sdf.parse(toDate);
                to.setHours(23);
                to.setMinutes(59);
                to.setSeconds(59);
                ps.setTimestamp(paramIndex++, new Timestamp(to.getTime()));
            }

            ResultSet rs = ps.executeQuery();
            SimpleDateFormat sdfOutput = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            while (rs.next()) {
                String transactionId = rs.getString("transaction_id");
                String uname = rs.getString("username");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String packageName = rs.getString("package_name");
                String amount = String.format("%,d VNĐ", rs.getInt("amount"));
                Timestamp time = rs.getTimestamp("time");
                String formattedTime = (time != null) ? sdfOutput.format(time) : "";

                tableModel.addRow(new Object[]{
                        transactionId,
                        uname,
                        phone,
                        email,
                        packageName,
                        amount,
                        formattedTime
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void editTransaction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String transactionId = (String) tableModel.getValueAt(selectedRow, 0);
            new TransactionForm(this, transactionId);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một giao dịch để sửa.");
        }
    }

    private void deleteTransaction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String transactionId = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa giao dịch này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "DELETE FROM transactions WHERE transaction_id = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, transactionId);
                    ps.executeUpdate();
                    loadTransactions();
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một giao dịch để xóa.");
        }
    }
}