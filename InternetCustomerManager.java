package Users;

import Users.panels.HomePanel;
import Users.panels.CustomerManagerPanel;
import Users.panels.TransactionHistoryPanel;
import Users.panels.ServicePanel;
import Users.panels.NotificationPanel;
import ketnoi.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class InternetCustomerManager extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public InternetCustomerManager(String customerPhone) throws SQLException {
        setTitle("Quản Lý Thuê Bao Internet");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 118, 210));
        header.setPreferredSize(new Dimension(getWidth(), 70));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        leftHeader.setOpaque(false);
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(UIManager.getIcon("FileView.computerIcon"));
        JLabel titleLabel = new JLabel("HDPhone");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        leftHeader.add(iconLabel);
        leftHeader.add(titleLabel);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        rightHeader.setOpaque(false);
        JLabel greetingLabel = new JLabel("Xin chào bạn!");
        greetingLabel.setForeground(Color.WHITE);
        greetingLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        rightHeader.add(greetingLabel);

        header.add(leftHeader, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(30, 136, 229));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        String[] menuItems = {
                "Trang chủ",
                "Quản lý thuê bao của bạn",
                "Gói cước và Dịch vụ",
                "Lịch sử giao dịch",
                "Thông báo và nhắc hạn",
        };

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        Connection conn = DBConnection.getConnection();

        HomePanel home = new HomePanel();
        CustomerManagerPanel customerPanel = new CustomerManagerPanel(conn, customerPhone);
        TransactionHistoryPanel historyPanel = new TransactionHistoryPanel();
        ServicePanel servicePanel = new ServicePanel(historyPanel);
        NotificationPanel notifyPanel = new NotificationPanel();

        cardPanel.add(home, "Trang chủ");
        cardPanel.add(customerPanel, "Quản lý thuê bao của bạn");
        cardPanel.add(servicePanel, "Gói cước và Dịch vụ");
        cardPanel.add(historyPanel, "Lịch sử giao dịch");
        cardPanel.add(notifyPanel, "Thông báo và nhắc hạn");

        for (String item : menuItems) {
            JButton menuButton = new JButton(item);
            menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            menuButton.setMaximumSize(new Dimension(180, 40));
            menuButton.setFocusPainted(false);
            menuButton.addActionListener(e -> cardLayout.show(cardPanel, item));
            sidebar.add(Box.createVerticalStrut(5));
            sidebar.add(menuButton);
        }

        add(header, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);
    }
}
