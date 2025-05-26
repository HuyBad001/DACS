package Users.panels;

import Users.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomerManagerPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public CustomerManagerPanel() {
        setLayout(new BorderLayout());

        String[] columns = {"Mã KH", "Họ tên", "Địa chỉ", "SĐT", "Email"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        loadCustomerData();

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadCustomerData() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối CSDL", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT * FROM customers";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                String phone = rs.getString("phone");
                String email = rs.getString("email");

                model.addRow(new Object[]{id, name, address, phone, email});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
