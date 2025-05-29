package admin;

import ketnoi.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReportPanel extends JPanel {
    private JTextArea reportArea;
    private JButton loadBtn;

    public ReportPanel() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("THỐNG KÊ DOANH THU THÁNG HIỆN TẠI", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        reportArea = new JTextArea();
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        reportArea.setEditable(false);
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        loadBtn = new JButton("Tải báo cáo doanh thu tháng này");
        loadBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loadBtn.addActionListener(e -> loadReport());
        JPanel btnPanel = new JPanel();
        btnPanel.add(loadBtn);
        add(btnPanel, BorderLayout.SOUTH);

        loadReport();
    }

    private void loadReport() {
        try (Connection conn = DBConnection.getConnection()) {
            java.time.LocalDate today = java.time.LocalDate.now();
            int currentYear = today.getYear();
            int currentMonth = today.getMonthValue();

            String sql = """
                SELECT SUM(amount) AS doanhthu
                FROM transactions
                WHERE YEAR(time) = ? AND MONTH(time) = ?
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentYear);
            stmt.setInt(2, currentMonth);

            ResultSet rs = stmt.executeQuery();

            double revenue = 0;
            if (rs.next()) {
                revenue = rs.getDouble("doanhthu");
            }

            String result = String.format("Tổng doanh thu tháng %d/%d là: %, .0f VNĐ", currentMonth, currentYear, revenue);
            reportArea.setText(result);
        } catch (Exception e) {
            e.printStackTrace();
            reportArea.setText("Lỗi truy vấn dữ liệu!");
        }
    }
}