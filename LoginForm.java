package login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import Users.InternetCustomerManager;
import ketnoi.DBConnection;
import main.AdminDashboard;

public class LoginForm extends JFrame {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JLabel lbError;
    private JCheckBox cbRemember;

    public LoginForm() {
        setTitle("Đăng nhập hệ thống");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        init();
        setVisible(true);
    }

    private void init() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(500, 350));

        // Nền gradient
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                Color color1 = new Color(0, 102, 204);
                Color color2 = new Color(102, 178, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        background.setBounds(0, 0, 500, 350);
        layeredPane.add(background, Integer.valueOf(1));

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);
        content.setBounds(50, 40, 400, 250);

        JLabel title = new JLabel("Vui lòng đăng nhập", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        content.add(title, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lbUser = new JLabel("Số điện thoại:");
        lbUser.setForeground(Color.WHITE);
        inputPanel.add(lbUser, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        tfUsername = new JTextField();
        tfUsername.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(tfUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lbPass = new JLabel("Mật khẩu:");
        lbPass.setForeground(Color.WHITE);
        inputPanel.add(lbPass, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;

        JPanel passPanel = new JPanel(null);
        passPanel.setOpaque(false);
        passPanel.setPreferredSize(new Dimension(280, 30));

        pfPassword = new JPasswordField();
        pfPassword.setEchoChar('•');
        pfPassword.setBounds(0, 0, 250, 30);

        ImageIcon iconShow = new ImageIcon(new ImageIcon("G:/icons8-eye-100.png").getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
        ImageIcon iconHide = new ImageIcon(new ImageIcon("G:/icons8-invisible-80.png").getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));

        JButton eyeButton = new JButton(iconHide);
        eyeButton.setBounds(255, 6, 18, 18);
        eyeButton.setFocusPainted(false);
        eyeButton.setContentAreaFilled(false);
        eyeButton.setBorderPainted(false);

        eyeButton.addActionListener(e -> {
            if (pfPassword.getEchoChar() != (char) 0) {
                pfPassword.setEchoChar((char) 0);
                eyeButton.setIcon(iconShow);
            } else {
                pfPassword.setEchoChar('•');
                eyeButton.setIcon(iconHide);
            }
        });

        passPanel.add(pfPassword);
        passPanel.add(eyeButton);
        inputPanel.add(passPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        cbRemember = new JCheckBox("Ghi nhớ mật khẩu");
        cbRemember.setOpaque(false);
        cbRemember.setForeground(Color.WHITE);
        inputPanel.add(cbRemember, gbc);

        gbc.gridy = 3;
        JLabel forgotLabel = new JLabel("<html><u>Quên mật khẩu?</u></html>");
        forgotLabel.setForeground(Color.YELLOW);
        forgotLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        forgotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                forgotPasswordAction();
            }
        });
        inputPanel.add(forgotLabel, gbc);

        gbc.gridy = 4;
        lbError = new JLabel(" ");
        lbError.setForeground(Color.RED);
        lbError.setFont(new Font("Arial", Font.ITALIC, 12));
        inputPanel.add(lbError, gbc);

        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(new Color(30, 144, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> login());

        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(0, 120, 215));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(30, 144, 255));
            }
        });

        content.add(inputPanel, BorderLayout.CENTER);
        content.add(btnLogin, BorderLayout.SOUTH);

        layeredPane.add(content, Integer.valueOf(2));

        setContentPane(layeredPane);
    }

    private void login() {
        String phone = tfUsername.getText().trim();  // Số điện thoại
        String password = new String(pfPassword.getPassword());

        if (phone.isEmpty() || password.isEmpty()) {
            lbError.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            lbError.setText("Số điện thoại phải gồm 10 chữ số.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE sdt = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashedPass = rs.getString("password");
                String role = rs.getString("role");
                String username = rs.getString("username");

                if ("Admin".equalsIgnoreCase(role)) {
                    // Mã hóa mật khẩu admin để so sánh, hoặc so sánh thẳng nếu bạn muốn
                    String adminPhone = "0987654321";
                    String adminPasswordPlain = "CaoVanDat@06";
                    if (phone.equals(adminPhone) && password.equals(adminPasswordPlain)) {
                        JOptionPane.showMessageDialog(this, "Đăng nhập thành công Admin!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        new AdminDashboard(phone);
                        dispose();
                    } else {
                        lbError.setText("Chỉ tài khoản 0987654321 mới được phép đăng nhập Admin.");
                    }
                    return;
                }

                if (hashedPass.equals(Encryptor.encrypt(password))) {
                    JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    String customerName = rs.getString("name"); // hoặc "fullname", tuỳ tên cột
                    InternetCustomerManager manager = new InternetCustomerManager(customerName);
                    manager.setVisible(true);

                    dispose();
                } else {
                    lbError.setText("Sai mật khẩu.");
                }

            } else {
                int option = JOptionPane.showConfirmDialog(
                        this,
                        "Số điện thoại chưa đăng ký. Bạn có muốn đăng ký tài khoản mới không?",
                        "Đăng ký tài khoản",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    new RegisterForm();
                    dispose();
                } else {
                    lbError.setText("Bạn đã hủy đăng ký.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            lbError.setText("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void forgotPasswordAction() {
        String phone = tfUsername.getText().trim();

        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại trước khi yêu cầu lấy lại mật khẩu.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT email FROM users WHERE sdt = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");
                if (email == null || email.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tài khoản chưa đăng ký email, vui lòng liên hệ quản trị viên.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String newPassword = generateRandomPassword(8);
                String hashedNewPass = Encryptor.encrypt(newPassword);

                String updateSql = "UPDATE users SET password = ? WHERE sdt = ?";
                PreparedStatement psUpdate = conn.prepareStatement(updateSql);
                psUpdate.setString(1, hashedNewPass);
                psUpdate.setString(2, phone);
                psUpdate.executeUpdate();

                String subject = "Lấy lại mật khẩu hệ thống";
                String content = "Chào bạn,\n\nMật khẩu mới của bạn là: " + newPassword + "\nVui lòng đăng nhập và đổi mật khẩu sau khi đăng nhập.\n\nTrân trọng,\nAdmin";

                MailSender.sendEmail(email, subject, content);

                JOptionPane.showMessageDialog(this, "Mật khẩu mới đã được gửi vào email: " + email, "Thông báo", JOptionPane.INFORMATION_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(this, "Số điện thoại không tồn tại trong hệ thống.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}