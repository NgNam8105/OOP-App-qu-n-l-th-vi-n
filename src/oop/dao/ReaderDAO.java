package oop.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import quanlythuvien.Reader;
import java.sql.*;

public class ReaderDAO {
    private static final String URL = "jdbc:sqlite:library.db"; // file SQLite

    // Tạo bảng nếu chưa có
    public static void initDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Reader (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                sdt TEXT
            )
        """;
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy toàn bộ danh sách độc giả
    public static ObservableList<Reader> getAllReaders() {
        ObservableList<Reader> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Reader";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Reader(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("sdt")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- Thêm hoặc cập nhật độc giả ---
    public static void saveReader(Reader r) {
        String sql = "INSERT OR REPLACE INTO Reader(id, name, sdt) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getId());
            pstmt.setString(2, r.getName());
            pstmt.setString(3, r.getSdt());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Xóa độc giả ---
    public static void deleteReader(String id) {
        String sql = "DELETE FROM Reader WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
