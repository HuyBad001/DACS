package Users.panels;
import ketnoi.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class TransactionHistoryPanel extends JPanel {
    private DefaultTableModel tableModel;

    public TransactionHistoryPanel() {
        setLayout(new BorderLayout());

        String[] columns = {"Số điện thoại", "Tên gói", "Giá tiền", "Thời gian thanh toán"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void addTransaction(String phone, String packageName, int price, String time) {
        tableModel.addRow(new Object[]{phone, packageName, price + " VNĐ", time});
    }

    public void loadTransactionsForUser(String phone) {
        tableModel.setRowCount(0);

        String sql = """
        SELECT i.customer_id, i.package_name, i.price,
               DATE_FORMAT(i.created_at, '%Y-%m-%d %H:%i:%s') AS time
        FROM invoice_items i
        JOIN customers c ON i.customer_id = c.id
        WHERE c.phone = ?
        ORDER BY i.created_at DESC
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String customerId = rs.getString("id");
                String packageName = rs.getString("package_name");
                int price = (int) rs.getDouble("price");
                String time = rs.getString("time");

                addTransaction(customerId, packageName, price, time);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải lịch sử giao dịch!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

}