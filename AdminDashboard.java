package main;// AdminDashboard.java
import javax.swing.*;
import java.awt.*;

import bieudo.ChartPanelExample;
import goicuoc.PackagePanel;
//import login.LoginForm;
import admin.BillingPanel;
import admin.CustomerPanel;
import admin.ReportPanel;
import admin.TransactionHistory;
import login.LoginForm;

public class AdminDashboard extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public AdminDashboard(String username) {
        setTitle("Hệ thống quản trị Internet");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === MENU BAR ===
        JMenuBar menuBar = new JMenuBar();
        JMenu menuManage = new JMenu("Quản lý");
        JMenuItem miCustomer = new JMenuItem("Khách hàng");
        JMenuItem miPackage = new JMenuItem("Gói cước");

        JMenu menuBill = new JMenu("Thanh toán");
        JMenuItem miBilling = new JMenuItem("Tính tiền");

        JMenu menuReport = new JMenu("Báo cáo");
        JMenuItem miReport = new JMenuItem("Thống kê");
        JMenuItem miBieudo = new JMenuItem("Biểu Đồ");
        JMenuItem miHistory = new JMenuItem("Lịch Sử Giao Dịch");

        JMenu menuExit = new JMenu("Hệ thống");
        JMenuItem miLogout = new JMenuItem("Đăng xuất");

        // === Menu hành động ===
        miCustomer.addActionListener(e -> cardLayout.show(mainPanel, "customer"));
        miPackage.addActionListener(e -> cardLayout.show(mainPanel, "package"));
        miBilling.addActionListener(e -> cardLayout.show(mainPanel, "billing"));
        miReport.addActionListener(e -> cardLayout.show(mainPanel, "report"));
        miHistory.addActionListener(e -> cardLayout.show(mainPanel, "history"));
        miBieudo.addActionListener(e -> cardLayout.show(mainPanel, "bieudo"));
        miLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Bạn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginForm();
            }
        });

        // === Thêm vào menu bar ===
        menuManage.add(miCustomer);
        menuManage.add(miPackage);

        menuBill.add(miBilling);

        menuReport.add(miReport);
        menuReport.add(miHistory);
        menuReport.add(miBieudo);

        menuExit.add(miLogout);

        menuBar.add(menuManage);
        menuBar.add(menuBill);
        menuBar.add(menuReport);
        menuBar.add(menuExit);
        setJMenuBar(menuBar);

        // === MAIN PANEL ===
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        mainPanel.add(new CustomerPanel(), "customer");
        mainPanel.add(new PackagePanel(), "package");
        mainPanel.add(new BillingPanel(), "billing");
        mainPanel.add(new ReportPanel(), "report");
        mainPanel.add(new TransactionHistory(), "history" );
        mainPanel.add(new ChartPanelExample(),"bieudo");

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }


}