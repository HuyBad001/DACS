package main;

import javax.swing.*;
import java.awt.*;
import login.LoginForm;
import login.RegisterForm;

public class MainForm extends JFrame {
    private JButton btnRegister;
    private JButton btnLogin;
    private JPanel buttonPanel;

    public MainForm() {
        setTitle("Trang chính");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        Image backgroundImage = new ImageIcon("C:/Users/Asus/background.jfif").getImage();

        JLabel title = new JLabel("Chào mừng bạn đến HDPhone", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(37, 114, 192));

        String[] roles = {"Users", "Admin"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        roleComboBox.setMaximumSize(new Dimension(200, 30));
        roleComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setOpaque(false);

        btnRegister = new JButton("Đăng ký");
        btnLogin = new JButton("Đăng nhập");

        formatButton(btnRegister, new Color(46, 204, 113));
        formatButton(btnLogin, new Color(52, 152, 219));

        btnRegister.addActionListener(e -> new RegisterForm());
        btnLogin.addActionListener(e -> new LoginForm());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(roleComboBox);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);

        updateButtons((String) roleComboBox.getSelectedItem());

        roleComboBox.addActionListener(e -> {
            String selectedRole = (String) roleComboBox.getSelectedItem();
            updateButtons(selectedRole);
        });

        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void updateButtons(String role) {
        buttonPanel.removeAll();
        if ("Users".equals(role)) {
            buttonPanel.add(btnRegister);
            buttonPanel.add(Box.createVerticalStrut(15));
            buttonPanel.add(btnLogin);
        } else if ("Admin".equals(role)) {
            buttonPanel.add(btnLogin);
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void formatButton(JButton button, Color bgColor) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainForm::new);
    }
}