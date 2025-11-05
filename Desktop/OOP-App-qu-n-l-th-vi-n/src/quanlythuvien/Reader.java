package quanlythuvien;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Reader {
    // Sử dụng JavaFX Properties để TableView tự động cập nhật
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty sdt;

    //Constructor
    public Reader(String id, String name, String sdt) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.sdt = new SimpleStringProperty(sdt);
    }

    //Getters
    public String getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getSdt() {
        return sdt.get();
    }

    // Setters
    public void setId(String id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setSdt(String sdt) {
        this.sdt.set(sdt);
    }

    // Property methods (Bắt buộc cho TableColumn.setCellValueFactory)
    public StringProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty sdtProperty() {
        return sdt;
    }
    @Override
    public String toString() {
        return getName() + " (" + getId() + ")";
    }


}