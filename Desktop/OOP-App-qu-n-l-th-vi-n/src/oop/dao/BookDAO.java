package oop.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import quanlythuvien.Book;
import java.sql.*;

public class BookDAO {
    private static final String URL = "jdbc:sqlite:library.db";

    public static void initDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Book(
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                author TEXT,
                quantity INTEGER DEFAULT 0
            )
        """;
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Book> getAllBooks() {
        ObservableList<Book> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Book";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Book(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveBook(Book b) {
        String sql = "INSERT OR REPLACE INTO Book(id, name, author, quantity) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, b.getId());
            pstmt.setString(2, b.getName());
            pstmt.setString(3, b.getAuthor());
            pstmt.setInt(4, b.getQuantity());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteBook(String id) {
        String sql = "DELETE FROM Book WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
