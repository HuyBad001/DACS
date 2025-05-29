package Users.panels;

import javax.swing.*;
import java.awt.*;

public class PaymentPanel extends JPanel {
    public PaymentPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Thanh toán", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
}