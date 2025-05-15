package Users.panels;

import javax.swing.*;
import java.awt.*;

public class TransactionHistoryPanel extends JPanel {
    public TransactionHistoryPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Lịch sử giao dịch", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
}
