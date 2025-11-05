package quanlythuvien;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Borrow {
    // Sử dụng JavaFX Properties để TableView tự động cập nhật
    private final StringProperty borrowId;
    private final StringProperty bookId;
    private final StringProperty readerId;
    private final StringProperty borrowDate;
    private final StringProperty returnDate;
    private final StringProperty status;

    //Constructor
    public Borrow(String borrowId, String bookId, String readerId,
                  String borrowDate, String returnDate, String status) {
        this.borrowId = new SimpleStringProperty(borrowId);
        this.bookId = new SimpleStringProperty(bookId);
        this.readerId = new SimpleStringProperty(readerId);
        this.borrowDate = new SimpleStringProperty(borrowDate);
        this.returnDate = new SimpleStringProperty(returnDate);
        this.status = new SimpleStringProperty(status);
    }

    //Getters
    public String getBorrowId() {
        return borrowId.get();
    }
    public String getBookId() {
        return bookId.get();
    }
    public String getReaderId() {
        return readerId.get();
    }
    public String getBorrowDate() {
        return borrowDate.get();
    }
    public String getReturnDate() {
        return returnDate.get();
    }
    public String getStatus() {
        return status.get();
    }

    // Setters
    public void setBorrowId(String borrowId) {
        this.borrowId.set(borrowId);
    }
    public void setBookId(String bookId) {
        this.bookId.set(bookId);
    }
    public void setReaderId(String readerId) {
        this.readerId.set(readerId);
    }
    public void setBorrowDate(String borrowDate) {
        this.borrowDate.set(borrowDate);
    }
    public void setReturnDate(String returnDate) {
        this.returnDate.set(returnDate);
    }
    public void setStatus(String status) {
        this.status.set(status);
    }

    //Property methods (Bắt buộc cho TableColumn.setCellValueFactory)
    public StringProperty borrowIdProperty() {
        return borrowId;
    }
    public StringProperty bookIdProperty() {
        return bookId;
    }
    public StringProperty readerIdProperty() {
        return readerId;
    }
    public StringProperty borrowDateProperty() {
        return borrowDate;
    }
    public StringProperty returnDateProperty() {
        return returnDate;
    }
    public StringProperty statusProperty() {
        return status;
    }
}