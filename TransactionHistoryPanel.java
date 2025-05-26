package Users.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

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
}
