package bieudo;

import ketnoi.DBConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class ChartPanelExample extends JPanel {
    private JComboBox<String> chartSelector;
    private JPanel chartContainer;

    public ChartPanelExample() {
        setLayout(new BorderLayout());

        chartSelector = new JComboBox<>(new String[]{
                "Biểu đồ cột - Gói theo tháng",
                "Biểu đồ đường - Lượng khách theo tháng",
                "Biểu đồ tròn - Tổng người dùng theo gói"
        });

        chartSelector.addActionListener(e -> updateChart());

        chartContainer = new JPanel(new BorderLayout());
        chartContainer.add(createBarChart(), BorderLayout.CENTER);

        add(chartSelector, BorderLayout.NORTH);
        add(chartContainer, BorderLayout.CENTER);
    }

    private void updateChart() {
        chartContainer.removeAll();
        int selectedIndex = chartSelector.getSelectedIndex();

        switch (selectedIndex) {
            case 0 -> chartContainer.add(createBarChart(), BorderLayout.CENTER);
//			case 1 -> chartContainer.add(createLineChart(), BorderLayout.CENTER);
            case 2 -> chartContainer.add(createPieChart(), BorderLayout.CENTER);
        }

        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private ChartPanel createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String sql = """
                SELECT MONTH(i.time) AS month, p.name AS package, COUNT(DISTINCT i.transaction_id) AS count
                FROM transactions i
                JOIN packages p ON i.package_id = p.id
                GROUP BY MONTH(i.time), p.name
                ORDER BY month, p.name
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int month = rs.getInt("month");
                if (month < 1 || month > 12) continue;

                String packageName = rs.getString("package");
                int count = rs.getInt("count");
                String monthName = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault());

                dataset.addValue(count, packageName, monthName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Lượng người dùng từng gói theo tháng",
                "Tháng",
                "Số người dùng",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        return new ChartPanel(barChart);
    }

    // Biểu đồ đường: tổng số lượt giao dịch (distinct transaction_id) theo tháng
	private ChartPanel createLineChart() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

	String sql = """
               SELECT MONTH(i.time) AS month, c.name AS customers, COUNT(DISTINCT i.time) AS count
              FROM transactions i
              JOIN customers c ON i.username = c.name
                GROUP BY MONTH(i.time), c.name
             ORDER BY month, c.name
              """;

	try (Connection conn = DBConnection.getConnection();
	     PreparedStatement ps = conn.prepareStatement(sql);
	     ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            int month = rs.getInt("month");
            if (month < 1 || month > 12) continue; // ← Sửa ở đây

            int count = rs.getInt("count");
            String monthName = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault());

            dataset.addValue(count, "Lượt đăng ký", monthName);
        }

    } catch (Exception e) {
			e.printStackTrace();
		}

		JFreeChart lineChart = ChartFactory.createLineChart(
				"Tổng số lượt khách đăng ký theo tháng",
				"Tháng",
				"Số lượt đăng ký",
				dataset,
				PlotOrientation.VERTICAL,
				true, true, false
		);

		return new ChartPanel(lineChart);
	}

    private ChartPanel createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        String sql = """
                SELECT p.name AS package, COUNT(*) AS count
                FROM invoice_items i
                JOIN packages p ON i.package_id = p.id
                GROUP BY p.name
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String packageName = rs.getString("package");
                int count = rs.getInt("count");
                dataset.setValue(packageName, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Tỷ lệ người dùng theo gói cước",
                dataset,
                true, true, false
        );

        return new ChartPanel(pieChart);
    }
}