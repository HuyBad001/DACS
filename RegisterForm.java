package login;

import ketnoi.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterForm extends JFrame {
    private JTextField tfUsername, tfEmail, tfPhone, tfAddress;
    private JPasswordField pfPassword;
    private JLabel lbError;

    public RegisterForm() {
        setTitle("Đăng ký tài khoản");
        setSize(500, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        init();
        setVisible(true);
    }

    private void init() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(500, 460));

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);
        content.setBounds(50, 40, 400, 380);

        JLabel title = new JLabel("Tạo tài khoản mới", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(2, 240, 241));
        content.add(title, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Color labelColor = new Color(2, 240, 241);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lbUser = new JLabel("Tên đăng nhập:");
        lbUser.setForeground(labelColor);
        inputPanel.add(lbUser, gbc);

        gbc.gridx = 1;
        tfUsername = new JTextField();
        tfUsername.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(tfUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lbEmail = new JLabel("Email:");
        lbEmail.setForeground(labelColor);
        inputPanel.add(lbEmail, gbc);

        gbc.gridx = 1;
        tfEmail = new JTextField();
        inputPanel.add(tfEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lbPass = new JLabel("Mật khẩu:");
        lbPass.setForeground(labelColor);
        inputPanel.add(lbPass, gbc);

        gbc.gridx = 1;
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
        eyeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lbPhone = new JLabel("Số điện thoại:");
        lbPhone.setForeground(labelColor);
        inputPanel.add(lbPhone, gbc);

        gbc.gridx = 1;
        tfPhone = new JTextField();
        inputPanel.add(tfPhone, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lbAddress = new JLabel("Địa chỉ:");
        lbAddress.setForeground(labelColor);
        inputPanel.add(lbAddress, gbc);

        gbc.gridx = 1;
        tfAddress = new JTextField();
        inputPanel.add(tfAddress, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        lbError = new JLabel(" ");
        lbError.setForeground(Color.RED);
        lbError.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        inputPanel.add(lbError, gbc);

        JButton btnRegister = new JButton("Đăng ký");
        btnRegister.setBackground(new Color(41, 128, 185));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegister.addActionListener(e -> register());

        btnRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnRegister.setBackground(new Color(31, 97, 141));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnRegister.setBackground(new Color(41, 128, 185));
            }
        });

        content.add(inputPanel, BorderLayout.CENTER);
        content.add(btnRegister, BorderLayout.SOUTH);

        layeredPane.add(content, Integer.valueOf(2));
        setContentPane(layeredPane);
        getContentPane().setBackground(new Color(52, 73, 94));
    }

    private void register() {
        String username = tfUsername.getText().trim();
        String email = tfEmail.getText().trim();
        String password = new String(pfPassword.getPassword());
        String phone = tfPhone.getText().trim();
        String address = tfAddress.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            lbError.setText("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        if (!email.endsWith("@gmail.com")) {
            lbError.setText("Email phải kết thúc bằng @gmail.com");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            lbError.setText("Số điện thoại phải gồm đúng 10 chữ số!");
            return;
        }

        if (!isValidPassword(password)) {
            lbError.setText("Mật khẩu phải có ít nhất 6 ký tự, 1 chữ hoa, 1 số và 1 ký tự đặc biệt");
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String checkPhoneSql = "SELECT COUNT(*) FROM users WHERE sdt = ?";
            try (PreparedStatement checkPhoneStmt = conn.prepareStatement(checkPhoneSql)) {
                checkPhoneStmt.setString(1, phone);
                ResultSet rsPhone = checkPhoneStmt.executeQuery();
                if (rsPhone.next() && rsPhone.getInt(1) > 0) {
                    lbError.setText("Số điện thoại đã được sử dụng!");
                    return;
                }
            }

            String checkEmailSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            try (PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailSql)) {
                checkEmailStmt.setString(1, email);
                ResultSet rsEmail = checkEmailStmt.executeQuery();
                if (rsEmail.next() && rsEmail.getInt(1) > 0) {
                    lbError.setText("Email đã được sử dụng!");
                    return;
                }
            }

            String insertUserSql = "INSERT INTO users (username, password, name, email, sdt, address, role) VALUES (?, ?, ?, ?, ?, ?, 'User')";
            try (PreparedStatement ps = conn.prepareStatement(insertUserSql)) {
                ps.setString(1, username);
                ps.setString(2, Encryptor.encrypt(password));
                ps.setString(3, username);
                ps.setString(4, email);
                ps.setString(5, phone);
                ps.setString(6, address);
                ps.executeUpdate();
            }

            String insertCustomerSql = "INSERT INTO customers (id, name, address, phone, email) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement cs = conn.prepareStatement(insertCustomerSql)) {
                String customerId = generateCustomerId(); // tạo mã ID khách hàng
                cs.setString(1, customerId);
                cs.setString(2, username);
                cs.setString(3, address);
                cs.setString(4, phone);
                cs.setString(5, email);
                cs.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
            dispose();

        } catch (Exception ex) {
            try {
                if (conn != null) conn.rollback(); // rollback khi lỗi
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            ex.printStackTrace();
            lbError.setText("Lỗi đăng ký: " + ex.getMessage());
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    private String generateCustomerId() {
        return "CUST" + System.currentTimeMillis();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[!@#$%^&*()].*");
    }
}