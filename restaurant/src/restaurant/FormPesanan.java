package restaurant;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class FormPesanan extends JFrame {
    JTextField tfId, tfNama, tfMenu, tfBanyak, tfHarga, tfTanggal, tfCari;
    JButton btnSimpan, btnUbah, btnHapus, btnCari;
    JTable tabelPesanan;
    DefaultTableModel model;

    public FormPesanan() {
        setTitle("Form Pesanan Restoran");
        setSize(850, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Label dan TextField
        JLabel lblId = new JLabel("ID Pesanan:");
        JLabel lblNama = new JLabel("Nama Pemesan:");
        JLabel lblMenu = new JLabel("Menu Pesanan:");
        JLabel lblBanyak = new JLabel("Banyak Pesanan:");
        JLabel lblHarga = new JLabel("Harga:");
        JLabel lblTanggal = new JLabel("Tanggal (yyyy-mm-dd):");
        JLabel lblCari = new JLabel("Cari Nama:");

        tfId = new JTextField();
        tfNama = new JTextField();
        tfMenu = new JTextField();
        tfBanyak = new JTextField();
        tfHarga = new JTextField();
        tfTanggal = new JTextField();
        tfCari = new JTextField();

        btnSimpan = new JButton("Simpan");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnCari = new JButton("Cari");

        // Tabel
        model = new DefaultTableModel(new String[]{"ID", "Nama", "Menu", "Banyak", "Harga", "Tanggal"}, 0);
        tabelPesanan = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tabelPesanan);

        // Set Bounds (layout manual)
        int labelWidth = 150, fieldWidth = 200, height = 25;
        int y = 20;
        lblId.setBounds(20, y, labelWidth, height); tfId.setBounds(180, y, fieldWidth, height); y += 35;
        lblNama.setBounds(20, y, labelWidth, height); tfNama.setBounds(180, y, fieldWidth, height); y += 35;
        lblMenu.setBounds(20, y, labelWidth, height); tfMenu.setBounds(180, y, fieldWidth, height); y += 35;
        lblBanyak.setBounds(20, y, labelWidth, height); tfBanyak.setBounds(180, y, fieldWidth, height); y += 35;
        lblHarga.setBounds(20, y, labelWidth, height); tfHarga.setBounds(180, y, fieldWidth, height); y += 35;
        lblTanggal.setBounds(20, y, labelWidth, height); tfTanggal.setBounds(180, y, fieldWidth, height); y += 35;

        btnSimpan.setBounds(450, 30, 100, height);
        btnUbah.setBounds(450, 70, 100, height);
        btnHapus.setBounds(450, 110, 100, height);

        scrollPane.setBounds(20, y, 780, 250);

        // Tambah ke frame
        add(lblId); add(tfId);
        add(lblNama); add(tfNama);
        add(lblMenu); add(tfMenu);
        add(lblBanyak); add(tfBanyak);
        add(lblHarga); add(tfHarga);
        add(lblTanggal); add(tfTanggal);
        add(btnSimpan); add(btnUbah); add(btnHapus);
        add(scrollPane);

        // Event listeners
        tampilData();

        btnSimpan.addActionListener(e -> simpanData());
        btnUbah.addActionListener(e -> ubahData());
        btnHapus.addActionListener(e -> hapusData());

        tabelPesanan.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int baris = tabelPesanan.getSelectedRow();
                tfId.setText(model.getValueAt(baris, 0).toString());
                tfNama.setText(model.getValueAt(baris, 1).toString());
                tfMenu.setText(model.getValueAt(baris, 2).toString());
                tfBanyak.setText(model.getValueAt(baris, 3).toString());
                tfHarga.setText(model.getValueAt(baris, 4).toString());
                tfTanggal.setText(model.getValueAt(baris, 5).toString());
            }
        });
    }

    private void tampilData() {
        model.setRowCount(0);
        try (Connection c = Koneksi.getConnection()) {
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("SELECT * FROM pesanan");
            while (r.next()) {
                Object[] data = {
                    r.getInt("id_pesanan"),
                    r.getString("nama_pemesan"),
                    r.getString("menu_pesanan"),
                    r.getInt("banyak_pesanan"),
                    r.getDouble("harga"),
                    r.getDate("tanggal_pesanan")
                };
                model.addRow(data);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
        private void simpanData() {
    try (Connection c = Koneksi.getConnection()) {
        String tanggalInput = tfTanggal.getText().trim();

        if (!tanggalInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                "Format yang Anda masukkan salah!",
                "Format Tanggal Salah", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int banyak, harga;
        try {
            banyak = Integer.parseInt(tfBanyak.getText());
            harga = Integer.parseInt(tfHarga.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Hanya dapat input angka!",
                "Input Salah", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO pesanan (nama_pemesan, menu_pesanan, banyak_pesanan, harga, tanggal_pesanan) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement p = c.prepareStatement(sql);
        p.setString(1, tfNama.getText());
        p.setString(2, tfMenu.getText());
        p.setInt(3, banyak);
        p.setDouble(4, harga);
        p.setDate(5, Date.valueOf(tanggalInput));

        p.executeUpdate();
        tampilData();
        JOptionPane.showMessageDialog(this, "Data Tersimpan");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
}
    private void ubahData() {
        try (Connection c = Koneksi.getConnection()) {
            String sql = "UPDATE pesanan SET nama_pemesan=?, menu_pesanan=?, banyak_pesanan=?, harga=?, tanggal_pesanan=? WHERE id_pesanan=?";
            PreparedStatement p = c.prepareStatement(sql);
            p.setString(1, tfNama.getText());
            p.setString(2, tfMenu.getText());
            p.setInt(3, Integer.parseInt(tfBanyak.getText()));
            p.setDouble(4, Double.parseDouble(tfHarga.getText()));
            p.setDate(5, Date.valueOf(tfTanggal.getText()));
            p.setInt(6, Integer.parseInt(tfId.getText()));
            p.executeUpdate();
            tampilData();
            JOptionPane.showMessageDialog(this, "Data Diubah");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void hapusData() {
        try (Connection c = Koneksi.getConnection()) {
            String sql = "DELETE FROM pesanan WHERE id_pesanan=?";
            PreparedStatement p = c.prepareStatement(sql);
            p.setInt(1, Integer.parseInt(tfId.getText()));
            p.executeUpdate();
            tampilData();
            JOptionPane.showMessageDialog(this, "Data Dihapus");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormPesanan().setVisible(true));
    }
}