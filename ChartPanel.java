package bieudo;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import ketnoi.DBConnection;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;

public class ChartPanel extends JPanel {

    public ChartPanel() {
        setLayout(new GridLayout(1, 2, 10, 10));
        add(createPackageChartPanel());
        add(createRevenueChartPanel());
    }

    private JPanel createPackageChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT p.name, COUNT(*) AS count " +
                    "FROM invoice_items i JOIN packages p ON i.package_id = p.id " +
                    "GROUP BY p.name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String packageName = rs.getString("name");
                int count = rs.getInt("count");
                dataset.addValue(count, "Số khách", packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Thống kê gói cước",
                "Gói cước", "Số lượng khách",
                dataset, PlotOrientation.VERTICAL,
                false, true, false
        );

        return new ChartPanelFX(chart);
    }

    private JPanel createRevenueChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, SUM(total) AS revenue " +
                    "FROM invoice_items GROUP BY month ORDER BY month";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String month = rs.getString("month");
                double revenue = rs.getDouble("revenue");
                dataset.addValue(revenue, "Doanh thu", month);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Doanh thu theo tháng",
                "Tháng", "Doanh thu (VNĐ)",
                dataset, PlotOrientation.VERTICAL,
                false, true, false
        );

        return new ChartPanelFX(chart);
    }
    private static class ChartPanelFX extends JPanel {
        public ChartPanelFX(JFreeChart chart) {
            setLayout(new BorderLayout());
            add(new org.jfree.chart.ChartPanel(chart), BorderLayout.CENTER);
        }
    }
}