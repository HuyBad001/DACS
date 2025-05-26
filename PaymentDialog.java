package Users.dialogs;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import Users.panels.TransactionHistoryPanel;

public class PaymentDialog extends JDialog {
    public PaymentDialog(Window parent, String packageName, int price, String qrImagePath, TransactionHistoryPanel historyPanel) {
        super(parent, "Thanh toán", ModalityType.APPLICATION_MODAL);
        setSize(400, 500);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(packageName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel(price + " VNĐ", SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        priceLabel.setForeground(Color.RED);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel qrLabel = new JLabel();
        qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon qrIcon = new ImageIcon(qrImagePath);
        Image scaledImage = qrIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        qrLabel.setIcon(new ImageIcon(scaledImage));

        JButton confirmButton = new JButton("Tôi đã thanh toán");
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.addActionListener(e -> {
            String phone = JOptionPane.showInputDialog(this, "Nhập số điện thoại đã thanh toán:");
            if (phone != null && !phone.isBlank()) {
                String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                historyPanel.addTransaction(phone, packageName, price, time);
                dispose();
            }
        });

        panel.add(Box.createVerticalStrut(20));
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(priceLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(qrLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(confirmButton);

        add(panel);
    }
}
