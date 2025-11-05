package quanlythuvien;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

public class Book {
    // Sử dụng JavaFX Properties để TableView có thể tự động cập nhật
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty author;
    private final IntegerProperty quantity;

    //Constructor
    public Book(String id, String name, String author, int quantity) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.author = new SimpleStringProperty(author);
        this.quantity = new SimpleIntegerProperty(quantity);
    }

    // --- Getters ---
    public String getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getAuthor() {
        return author.get();
    }

    public int getQuantity() {
        return quantity.get();
    }

    // Setters
    public void setId(String id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    // Property methods (Bắt buộc cho TableColumn.setCellValueFactory)
    public StringProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty authorProperty() {
        return author;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }
    @Override
    public String toString() {
        return getName() + " (" + getId() + ")";
    }
}