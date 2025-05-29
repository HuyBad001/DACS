package goicuoc;

import ketnoi.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PackagePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField idField, nameField, typeField, durationField, dataField, priceField, descField;

    public PackagePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("QUẢN LÝ GÓI CƯỚC", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 60, 114));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
                "Mã gói", "Tên gói", "Loại", "Số ngày", "Dung lượng (GB)", "Giá (VNĐ)", "Mô tả"
        }, 0);

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(220, 230, 250));
        loadData();
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                idField.setText(model.getValueAt(row, 0).toString());
                nameField.setText(model.getValueAt(row, 1).toString());
                typeField.setText(model.getValueAt(row, 2).toString());
                durationField.setText(model.getValueAt(row, 3).toString());
                dataField.setText(model.getValueAt(row, 4).toString());
                priceField.setText(model.getValueAt(row, 5).toString());
                descField.setText(model.getValueAt(row, 6).toString());
            }
        });

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(235, 242, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField();
        nameField = new JTextField();
        typeField = new JTextField();
        durationField = new JTextField();
        dataField = new JTextField();
        priceField = new JTextField();
        descField = new JTextField();

        JButton addBtn = createStyledButton("Thêm", new Color(76, 175, 80));
        addBtn.addActionListener(e -> insertPackage());

        JButton delBtn = createStyledButton("Xóa", new Color(244, 67, 54));
        delBtn.addActionListener(e -> deletePackage());

        JButton upBtn = createStyledButton("Cập nhật", new Color(255, 152, 0));
        upBtn.addActionListener(e -> updatePackage());

        JButton clearBtn = createStyledButton("Làm mới", new Color(33, 150, 243));
        clearBtn.addActionListener(e -> {
            idField.setText("");
            nameField.setText("");
            typeField.setText("");
            durationField.setText("");
            dataField.setText("");
            priceField.setText("");
            descField.setText("");
            table.clearSelection();
        });

        int y = 0;
        addFormRow(formPanel, gbc, y++, "Mã gói", idField);
        addFormRow(formPanel, gbc, y++, "Tên gói", nameField);
        addFormRow(formPanel, gbc, y++, "Loại", typeField);
        addFormRow(formPanel, gbc, y++, "Số ngày", durationField);
        addFormRow(formPanel, gbc, y++, "Dung lượng (GB)", dataField);
        addFormRow(formPanel, gbc, y++, "Giá", priceField);
        addFormRow(formPanel, gbc, y++, "Mô tả", descField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(235, 242, 255));
        buttonPanel.add(addBtn);
        buttonPanel.add(upBtn);
        buttonPanel.add(delBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int y, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        return button;
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM packages");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getInt("duration_days"),
                        rs.getDouble("data_gb"),
                        rs.getDouble("price"),
                        rs.getString("description")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải gói cước");
        }
    }

    private void insertPackage() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO packages (id, name, type, duration_days, data_gb, price, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, idField.getText());
            stmt.setString(2, nameField.getText());
            stmt.setString(3, typeField.getText());
            stmt.setInt(4, Integer.parseInt(durationField.getText()));
            stmt.setDouble(5, Double.parseDouble(dataField.getText()));
            stmt.setDouble(6, Double.parseDouble(priceField.getText()));
            stmt.setString(7, descField.getText());
            stmt.executeUpdate();
            loadData();
            JOptionPane.showMessageDialog(this, "Đã thêm gói cước.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm gói cước");
        }
    }

    private void updatePackage() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng để cập nhật.");
            return;
        }

        String id = idField.getText();
        String name = nameField.getText();
        String type = typeField.getText();
        String durationText = durationField.getText();
        String dataText = dataField.getText();
        String priceText = priceField.getText();
        String desc = descField.getText();

        if (id.isEmpty() || name.isEmpty() || type.isEmpty() || durationText.isEmpty() || dataText.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE packages SET name = ?, type = ?, duration_days = ?, data_gb = ?, price = ?, description = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, type);
            stmt.setInt(3, Integer.parseInt(durationText));
            stmt.setDouble(4, Double.parseDouble(dataText));
            stmt.setDouble(5, Double.parseDouble(priceText));
            stmt.setString(6, desc);
            stmt.setString(7, id);
            stmt.executeUpdate();
            loadData();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật gói cước");
        }
    }

    private void deletePackage() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng để xóa.");
            return;
        }

        String id = (String) model.getValueAt(row, 0);
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM packages WHERE id = ?");
            stmt.setString(1, id);
            stmt.executeUpdate();
            loadData();
            JOptionPane.showMessageDialog(this, "Đã xóa gói cước.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa gói cước");
        }
    }
}