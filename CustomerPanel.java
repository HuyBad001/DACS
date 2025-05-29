package admin;

import ketnoi.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomerPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField idField, nameField, addressField, phoneField, emailField, searchField;
    private JComboBox<String> sortComboBox;

    public CustomerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("QUẢN LÝ KHÁCH HÀNG", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 60, 114));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Mã KH", "Tên", "Địa chỉ", "SĐT", "Email"}, 0);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(220, 230, 250));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(235, 242, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField();
        nameField = new JTextField();
        addressField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        searchField = new JTextField(15);

        JButton addBtn = createStyledButton("Thêm", new Color(76, 175, 80));
        JButton upBtn = createStyledButton("Sửa", new Color(255, 152, 0));
        JButton delBtn = createStyledButton("Xóa", new Color(244, 67, 54));
        JButton refreshBtn = createStyledButton("Làm mới", new Color(33, 150, 243));
        JButton searchBtn = createStyledButton("Tìm kiếm", new Color(0, 188, 212));

        String[] sortOptions = {
                "Sắp xếp Mã KH ↑", "Sắp xếp Mã KH ↓",
                "Sắp xếp Tên ↑", "Sắp xếp Tên ↓",
                "Sắp xếp Địa chỉ ↑", "Sắp xếp Địa chỉ ↓",
                "Sắp xếp SĐT ↑", "Sắp xếp SĐT ↓",
                "Sắp xếp Email ↑", "Sắp xếp Email ↓"
        };
        sortComboBox = new JComboBox<>(sortOptions);

        int y = 0;
        addFormRow(form, gbc, y++, "Mã KH", idField);
        addFormRow(form, gbc, y++, "Tên", nameField);
        addFormRow(form, gbc, y++, "Địa chỉ", addressField);
        addFormRow(form, gbc, y++, "SĐT", phoneField);
        addFormRow(form, gbc, y++, "Email", emailField);

        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("Tìm kiếm"), gbc);
        gbc.gridx = 1;
        form.add(searchField, gbc);

        gbc.gridx = 2;
        form.add(searchBtn, gbc);
        gbc.gridx = 3;
        form.add(sortComboBox, gbc);
        gbc.gridx = 4;
        form.add(refreshBtn, gbc);
        y++;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(235, 242, 255));
        buttonPanel.add(addBtn);
        buttonPanel.add(upBtn);
        buttonPanel.add(delBtn);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 5;
        form.add(buttonPanel, gbc);

        add(form, BorderLayout.SOUTH);

        loadData(null, null);

        addBtn.addActionListener(e -> insertCustomer());
        upBtn.addActionListener(e -> updateCustomer());
        delBtn.addActionListener(e -> deleteCustomer());
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadData(null, null);
        });
        searchBtn.addActionListener(e -> {
            loadData(searchField.getText().trim(), (String) sortComboBox.getSelectedItem());
        });
        sortComboBox.addActionListener(e -> {
            loadData(searchField.getText().trim(), (String) sortComboBox.getSelectedItem());
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                idField.setText(model.getValueAt(row, 0).toString());
                nameField.setText(model.getValueAt(row, 1).toString());
                addressField.setText(model.getValueAt(row, 2).toString());
                phoneField.setText(model.getValueAt(row, 3).toString());
                emailField.setText(model.getValueAt(row, 4).toString());
            }
        });
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int y, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 4;
        panel.add(field, gbc);
        gbc.gridwidth = 1;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        return button;
    }

    private void loadData(String keyword, String sortOption) {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM customers";
            boolean hasKeyword = keyword != null && !keyword.isEmpty();
            if (hasKeyword) {
                sql += " WHERE id LIKE ? OR name LIKE ?";
            }
            if (sortOption != null) {
                switch (sortOption) {
                    case "Sắp xếp Mã KH ↑" -> sql += " ORDER BY id ASC";
                    case "Sắp xếp Mã KH ↓" -> sql += " ORDER BY id DESC";
                    case "Sắp xếp Tên ↑" -> sql += " ORDER BY name ASC";
                    case "Sắp xếp Tên ↓" -> sql += " ORDER BY name DESC";
                    case "Sắp xếp Địa chỉ ↑" -> sql += " ORDER BY address ASC";
                    case "Sắp xếp Địa chỉ ↓" -> sql += " ORDER BY address DESC";
                    case "Sắp xếp SĐT ↑" -> sql += " ORDER BY phone ASC";
                    case "Sắp xếp SĐT ↓" -> sql += " ORDER BY phone DESC";
                    case "Sắp xếp Email ↑" -> sql += " ORDER BY email ASC";
                    case "Sắp xếp Email ↓" -> sql += " ORDER BY email DESC";
                }
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (hasKeyword) {
                String kw = "%" + keyword + "%";
                stmt.setString(1, kw);
                stmt.setString(2, kw);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void insertCustomer() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || address.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin khách hàng!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement check = conn.prepareStatement("SELECT id FROM customers WHERE id = ?");
            check.setString(1, id);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Mã khách hàng đã tồn tại!");
                return;
            }

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO customers (id, name, address, phone, email) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, address);
            stmt.setString(4, phone);
            stmt.setString(5, email);
            stmt.executeUpdate();

            loadData(null, null);
            clearForm();

            JOptionPane.showMessageDialog(this, "Đã thêm khách hàng.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm khách hàng: " + e.getMessage());
        }
    }

    private void updateCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng để sửa.");
            return;
        }

        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || address.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin khách hàng!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE customers SET name = ?, address = ?, phone = ?, email = ? WHERE id = ?");
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, phone);
            stmt.setString(4, email);
            stmt.setString(5, id);
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                loadData(null, null);
                JOptionPane.showMessageDialog(this, "Đã cập nhật khách hàng.");
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng để cập nhật.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật khách hàng: " + e.getMessage());
        }
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xóa.");
            return;
        }

        String id = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM customers WHERE id = ?");
            stmt.setString(1, id);
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                loadData(null, null);
                clearForm();
                JOptionPane.showMessageDialog(this, "Đã xóa khách hàng.");
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng để xóa.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa khách hàng: " + e.getMessage());
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        addressField.setText("");
        phoneField.setText("");
        emailField.setText("");
    }
}