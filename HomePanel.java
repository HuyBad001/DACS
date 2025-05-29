package Users.panels;
import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    private Image backgroundImage;

    public HomePanel() {
        backgroundImage = new ImageIcon("C:/Users/Asus/background.jfif").getImage();
        setLayout(new BorderLayout());
        JLabel title = new JLabel("<html><div style='text-align:center;'>✨ Chào mừng đến với<br><span style='font-size:40px;'>HỆ THỐNG HDPhone</span> ✨</div></html>", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(25, 118, 210));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridLayout(2, 2, 20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
    }

    private JPanel createFeatureBox(String title, String iconPath) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245, 220)); // Nền mờ nhẹ
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel iconLabel = new JLabel();
        ImageIcon icon = new ImageIcon(iconPath);
        Image scaled = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        iconLabel.setIcon(new ImageIcon(scaled));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel textLabel = new JLabel(title, JLabel.CENTER);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        panel.add(iconLabel, BorderLayout.CENTER);
        panel.add(textLabel, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}