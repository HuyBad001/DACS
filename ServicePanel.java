    package Users.panels;
    import ketnoi.DBConnection;

    import javax.swing.*;
    import java.awt.*;
    import java.sql.*;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;

    public class ServicePanel extends JPanel {
        private JPanel contentPanel;
        private JTextField phoneField;
        private java.util.List<PackageItem> cart = new ArrayList<>();
        private TransactionHistoryPanel historyPanel;

        public ServicePanel(TransactionHistoryPanel historyPanel) {
            this.historyPanel = historyPanel;

            setLayout(new BorderLayout());

            JPanel topMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            String[] menuItems = {"Gói ngày", "Gói tháng", "Gói năm"};
            for (String item : menuItems) {
                JButton button = new JButton(item);
                button.setPreferredSize(new Dimension(150, 40));
                button.addActionListener(e -> {
                    String type = item.replace("Gói ", "").toLowerCase(); // Lấy 'ngày', 'tháng', 'năm'
                    loadPackages(type);
                });
                topMenu.add(button);
            }


            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));

            JLabel phoneLabel = new JLabel("Số điện thoại");
            phoneField = new JTextField();
            phoneField.setPreferredSize(new Dimension(300, 30));

            JButton viewCartBtn = new JButton("🛒 Giỏ hàng");
            viewCartBtn.addActionListener(e -> showCart());

            JButton payBtn = new JButton("Thanh toán");
            payBtn.addActionListener(e -> payCart());

            infoPanel.add(phoneLabel);
            infoPanel.add(phoneField);
            infoPanel.add(viewCartBtn);
            infoPanel.add(payBtn);

            contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBorder(BorderFactory.createTitledBorder("Danh sách gói cước"));

            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setPreferredSize(new Dimension(1000, 400));

            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.add(infoPanel);
            centerPanel.add(Box.createVerticalStrut(10));
            centerPanel.add(scrollPane);

            add(topMenu, BorderLayout.NORTH);
            add(centerPanel, BorderLayout.CENTER);
        }

        private void loadPackages(String type) {
            contentPanel.removeAll();

            String query = "SELECT * FROM packages WHERE type = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, type);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String name = rs.getString("name");
                    String duration = rs.getString("duration_days");
                    double data = rs.getDouble("data_gb");
                    double price = rs.getDouble("price");
                    String description = rs.getString("description");

                    JPanel packagePanel = new JPanel();
                    packagePanel.setLayout(new BoxLayout(packagePanel, BoxLayout.Y_AXIS));
                    packagePanel.setBackground(Color.WHITE);
                    packagePanel.setMaximumSize(new Dimension(900, 200));
                    packagePanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                            BorderFactory.createEmptyBorder(10, 15, 10, 15)
                    ));

                    Font infoFont = new Font("SansSerif", Font.BOLD, 14);

                    JLabel nameLabel = new JLabel("Tên gói: " + name);
                    JLabel durationLabel = new JLabel("Thời lượng: " + duration + " ngày");
                    JLabel dataLabel = new JLabel("Dung lượng Data: " + data + " GB");
                    JLabel descriptionLabel = new JLabel("Mô tả: " + description);

                    nameLabel.setFont(infoFont);
                    durationLabel.setFont(infoFont);
                    dataLabel.setFont(infoFont);
                    descriptionLabel.setFont(infoFont);

                    packagePanel.add(nameLabel);
                    packagePanel.add(durationLabel);
                    packagePanel.add(dataLabel);
                    packagePanel.add(descriptionLabel);

                    JLabel priceLabel = new JLabel("Giá: " + String.format("%,.0f VND", price));
                    priceLabel.setForeground(new Color(200, 0, 0));
                    priceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

                    JButton addToCartBtn = new JButton("Thêm vào giỏ hàng");
                    addToCartBtn.addActionListener(e -> {
                        String phone = phoneField.getText().trim();
                        if (!phone.matches("\\d{10}")) {
                            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại hợp lệ (10 chữ số) trước khi thêm vào giỏ hàng.");
                            return;
                        }
                        cart.add(new PackageItem(name, price));
                        JOptionPane.showMessageDialog(this, "Đã thêm vào giỏ hàng!");
                    });

                    JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    bottomRow.setBackground(Color.WHITE);
                    bottomRow.add(priceLabel);
                    bottomRow.add(Box.createHorizontalStrut(20));
                    bottomRow.add(addToCartBtn);

                    packagePanel.add(Box.createVerticalStrut(10));
                    packagePanel.add(bottomRow);

                    contentPanel.add(Box.createVerticalStrut(10));
                    contentPanel.add(packagePanel);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                contentPanel.add(new JLabel("Không thể tải dữ liệu từ cơ sở dữ liệu."));
            }

            contentPanel.revalidate();
            contentPanel.repaint();
        }


        private void showCart() {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giỏ hàng hiện đang trống.");
                return;
            }

            JPanel cartPanel = new JPanel();
            cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));

            for (int i = 0; i < cart.size(); i++) {
                int index = i;
                PackageItem item = cart.get(i);

                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                row.add(new JLabel(item.name + " - " + String.format("%,.0f VND", item.price)));

                JButton removeBtn = new JButton("Xóa");
                removeBtn.addActionListener(e -> {
                    cart.remove(index);
                    JOptionPane.showMessageDialog(this, "Đã xóa khỏi giỏ hàng.");
                    showCart(); // Refresh
                });

                row.add(removeBtn);
                cartPanel.add(row);
            }

            JOptionPane.showMessageDialog(this, cartPanel, "Giỏ hàng", JOptionPane.PLAIN_MESSAGE);
        }
        private void payCart() {
            String phone = phoneField.getText().trim();
            if (!phone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại phải gồm đúng 10 chữ số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bạn chưa chọn gói cước nào để thanh toán.");
                return;
            }

            double total = cart.stream().mapToDouble(item -> item.price).sum();

            // Giao diện xác nhận thanh toán
            JPanel payPanel = new JPanel();
            payPanel.setLayout(new BoxLayout(payPanel, BoxLayout.Y_AXIS));
            payPanel.setPreferredSize(new Dimension(350, 450));

            JLabel phoneLabel = new JLabel("SĐT: " + phone);
            phoneLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            phoneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel packagesPanel = new JPanel();
            packagesPanel.setLayout(new BoxLayout(packagesPanel, BoxLayout.Y_AXIS));
            packagesPanel.setBorder(BorderFactory.createTitledBorder("Gói đã chọn"));
            for (PackageItem item : cart) {
                JLabel pkgLabel = new JLabel("- " + item.name + " (" + String.format("%,.0f VND", item.price) + ")");
                packagesPanel.add(pkgLabel);
            }

            JLabel totalLabel = new JLabel("Tổng tiền: " + String.format("%,.0f VND", total));
            totalLabel.setForeground(Color.RED);
            totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel qrLabel = new JLabel();
            try {
                ImageIcon qrIcon = new ImageIcon("C:/Users/Asus/thanhtoan1.jpg");
                Image img = qrIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                qrLabel.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                qrLabel.setText("Không thể tải mã QR");
                qrLabel.setForeground(Color.RED);
            }
            qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            payPanel.add(Box.createVerticalStrut(10));
            payPanel.add(phoneLabel);
            payPanel.add(Box.createVerticalStrut(10));
            payPanel.add(packagesPanel);
            payPanel.add(Box.createVerticalStrut(10));
            payPanel.add(totalLabel);
            payPanel.add(Box.createVerticalStrut(15));
            payPanel.add(qrLabel);

            int result = JOptionPane.showOptionDialog(
                    this,
                    payPanel,
                    "Thanh toán qua mã QR",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new Object[]{"Tôi đã thanh toán"},
                    "Tôi đã thanh toán"
            );

            if (result == 0) {
                String transactionId = "TXN" + System.currentTimeMillis();
                String insertTransactionSQL = "INSERT INTO transactions (transaction_id, username, amount, time, status) VALUES (?, ?, ?, NOW(), ?)";
                String insertItemSQL = "INSERT INTO transaction_items (transaction_id, package_name, price) VALUES (?, ?, ?)";

                try (Connection conn = DBConnection.getConnection()) {
                    conn.setAutoCommit(false);

                    // Ghi vào bảng transactions
                    try (PreparedStatement stmt = conn.prepareStatement(insertTransactionSQL)) {
                        stmt.setString(1, transactionId);
                        stmt.setString(2, phone);
                        stmt.setDouble(3, total);
                        stmt.setString(4, "Đã thanh toán");
                        stmt.executeUpdate();
                    }

                    // Ghi vào bảng transaction_items
                    try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSQL)) {
                        for (PackageItem item : cart) {
                            itemStmt.setString(1, transactionId);
                            itemStmt.setString(2, item.name);
                            itemStmt.setDouble(3, item.price);
                            itemStmt.addBatch();
                        }
                        itemStmt.executeBatch();
                    }

                    conn.commit();

                    // Hiển thị lại trong bảng lịch sử giao dịch
                    String time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
                    for (PackageItem item : cart) {
                        historyPanel.addTransaction(phone, item.name, (int) item.price, time);
                    }

                    JOptionPane.showMessageDialog(this, "Cảm ơn bạn đã thanh toán!");
                    cart.clear();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi khi lưu giao dịch. Vui lòng thử lại.");
                }
            }
        }

        // ==== LỚP NỘI BỘ LƯU GÓI CƯỚC TRONG GIỎ HÀNG ====
        static class PackageItem {
            String name;
            double price;

            public PackageItem(String name, double price) {
                this.name = name;
                this.price = price;
            }
        }
    }