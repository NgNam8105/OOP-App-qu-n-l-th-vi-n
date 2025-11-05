package oop.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import quanlythuvien.Borrow;

public class BorrowDAO {
    private static final String DB_URL = "jdbc:sqlite:library.db";

    public BorrowDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS borrow (
                    borrowId TEXT PRIMARY KEY,
                    bookId TEXT,
                    readerId TEXT,
                    borrowDate TEXT,
                    returnDate TEXT,
                    status TEXT
                )
                """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  Lấy toàn bộ borrow
    public static ObservableList<Borrow> getAllBorrows() {
        ObservableList<Borrow> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM borrow";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Borrow borrow = new Borrow(
                        rs.getString("borrowId"),
                        rs.getString("bookId"),
                        rs.getString("readerId"),
                        rs.getString("borrowDate"),
                        rs.getString("returnDate"),
                        rs.getString("status")
                );
                list.add(borrow);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm borrow
    public void insertBorrow(Borrow borrow) {
        String sql = "INSERT INTO borrow (borrowId, bookId, readerId, borrowDate, returnDate, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);

            pstmt.setString(1, borrow.getBorrowId());
            pstmt.setString(2, borrow.getBookId());
            pstmt.setString(3, borrow.getReaderId());
            pstmt.setString(4, borrow.getBorrowDate());
            pstmt.setString(5, borrow.getReturnDate());
            pstmt.setString(6, borrow.getStatus());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật borrow
    public void updateBorrow(Borrow borrow) {
        String sql = "UPDATE borrow SET bookId=?, readerId=?, borrowDate=?, returnDate=?, status=? WHERE borrowId=?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);

            pstmt.setString(1, borrow.getBookId());
            pstmt.setString(2, borrow.getReaderId());
            pstmt.setString(3, borrow.getBorrowDate());
            pstmt.setString(4, borrow.getReturnDate());
            pstmt.setString(5, borrow.getStatus());
            pstmt.setString(6, borrow.getBorrowId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Xóa borrow ---
    public void deleteBorrow(String borrowId) {
        String sql = "DELETE FROM borrow WHERE borrowId=?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);

            pstmt.setString(1, borrowId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Lưu Borrow (tự động insert hoặc update nếu đã tồn tại) ---
    public static void saveBorrow(Borrow borrow) {
        String checkSql = "SELECT COUNT(*) FROM borrow WHERE borrowId = ?";
        String insertSql = "INSERT INTO borrow (borrowId, bookId, readerId, borrowDate, returnDate, status) VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE borrow SET bookId=?, readerId=?, borrowDate=?, returnDate=?, status=? WHERE borrowId=?";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(true);
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, borrow.getBorrowId());
            ResultSet rs = checkStmt.executeQuery();

            boolean exists = rs.next() && rs.getInt(1) > 0;

            if (exists) {
                PreparedStatement pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, borrow.getBookId());
                pstmt.setString(2, borrow.getReaderId());
                pstmt.setString(3, borrow.getBorrowDate());
                pstmt.setString(4, borrow.getReturnDate());
                pstmt.setString(5, borrow.getStatus());
                pstmt.setString(6, borrow.getBorrowId());
                pstmt.executeUpdate();
            } else {
                PreparedStatement pstmt = conn.prepareStatement(insertSql);
                pstmt.setString(1, borrow.getBorrowId());
                pstmt.setString(2, borrow.getBookId());
                pstmt.setString(3, borrow.getReaderId());
                pstmt.setString(4, borrow.getBorrowDate());
                pstmt.setString(5, borrow.getReturnDate());
                pstmt.setString(6, borrow.getStatus());
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String generateNewBorrowId() {
        String sql = "SELECT MAX(CAST(SUBSTR(borrowId, 3) AS INTEGER)) AS maxId FROM borrow";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int maxId = rs.next() ? rs.getInt("maxId") : 0;
            return "BR" + String.format("%03d", maxId + 1);

        } catch (SQLException e) {
            e.printStackTrace();
            return "BR001"; // fallback nếu lỗi
        }
    }

}
