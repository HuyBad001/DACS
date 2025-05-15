package Users.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerManagerPanel extends JPanel {
    public CustomerManagerPanel() {
        setLayout(new BorderLayout());

        String[] columns = {
                "Họ đệm", "Tên", "SĐT", "Dịch vụ quan tâm", "Email", "Địa chỉ",
                "Trạng thái", "Ngày liên hệ", "Ghi chú", "Ngày gửi", "Điểm tiềm năng", "Gửi SMS", "Quản lý"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        model.addRow(new Object[]{
                "Nguyễn Văn", "An", "0909123456", "Internet", "an@example.com", "Hà Nội",
                "Đã liên hệ", "14/05/2025", "", "14/05/2025", "80.00", "✉", "admin"
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
