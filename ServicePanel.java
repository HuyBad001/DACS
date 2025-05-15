package Users.panels;

import javax.swing.*;
import java.awt.*;

public class ServicePanel extends JPanel {

    public ServicePanel() {
        setLayout(new BorderLayout());

        // ===== MENU TRÊN CÙNG =====
        JPanel topMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        String[] menuItems = {"Mã thẻ", "Nạp điện thoại", "Nạp Data", "Combo", "Thẻ Sim"};
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setPreferredSize(new Dimension(150, 40));
            topMenu.add(button);
        }

        // ===== VÙNG NHẬP THÔNG TIN =====
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));

        JLabel networkLabel = new JLabel("Nhà mạng");
        String[] networks = {"Viettel", "MobiPhone", "VinaPhone", "HDPhone"};
        JComboBox<String> networkComboBox = new JComboBox<>(networks);
        networkComboBox.setPreferredSize(new Dimension(150, 30));

        JLabel phoneLabel = new JLabel("Số điện thoại");
        JTextField phoneField = new JTextField();
        phoneField.setPreferredSize(new Dimension(300, 30));

        infoPanel.add(networkLabel);
        infoPanel.add(networkComboBox);
        infoPanel.add(phoneLabel);
        infoPanel.add(phoneField);

        // ===== VÙNG HIỂN THỊ NỘI DUNG CHÍNH =====
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        contentPanel.setPreferredSize(new Dimension(1000, 400));
        contentPanel.add(new JLabel("Nội dung hiển thị ở đây..."));

        // ===== GHÉP CÁC PHẦN =====
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(infoPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(contentPanel);

        add(topMenu, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
}
