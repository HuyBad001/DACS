package Users;

import Users.panels.*;
import javax.swing.*;
import java.awt.*;

public class InternetCustomerManager extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public InternetCustomerManager() {
        setTitle("Quản Lý Thuê Bao Internet");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header (Top Bar with Logo/Text)
        JPanel header = new JPanel();
        header.setBackground(new Color(25, 118, 210));
        header.setPreferredSize(new Dimension(getWidth(), 70));
        header.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));

        // Optional: Add icon
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(UIManager.getIcon("FileView.computerIcon")); // Placeholder icon

        JLabel titleLabel = new JLabel("HDPhone");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        header.add(iconLabel);
        header.add(titleLabel);

        // Sidebar
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
                "Thanh toán"
        };

        // CardLayout Panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Add views
        cardPanel.add(new HomePanel(), "Trang chủ");
        cardPanel.add(new CustomerManagerPanel(), "Quản lý thuê bao của bạn");
        cardPanel.add(new ServicePanel(), "Gói cước và Dịch vụ");
        cardPanel.add(new TransactionHistoryPanel(), "Lịch sử giao dịch");
        cardPanel.add(new NotificationPanel(), "Thông báo và nhắc hạn");
        cardPanel.add(new PaymentPanel(), "Thanh toán");

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