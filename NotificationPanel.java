package Users.panels;

import javax.swing.*;
import java.awt.*;

public class NotificationPanel extends JPanel {
    public NotificationPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Thông báo và nhắc hạn", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
}
