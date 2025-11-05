package oop;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import oop.dao.ReaderDAO; // <-- Sửa import
import quanlythuvien.Reader; // <-- Sửa import

// import java.time.LocalDate; // Không cần

public class AddReaderForm { // <-- Đổi tên class
    private Runnable refreshCallback;

    // --- Sửa: Khai báo TextField cho Độc Giả ---
    private TextField txtReaderId, txtReaderName, txtReaderSDT;
    // ------------------------------------------

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    public BorderPane buildUI() {
        // --- Phần Bên Trái (Ảnh) - Có thể giữ hoặc thay ảnh khác ---
        VBox leftPane = new VBox();
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(20));
        leftPane.setStyle("-fx-background-color: #FFFFFF;");
        ImageView addImage = null; // Đổi tên biến ảnh
        try {
            // Có thể dùng ảnh khác cho thêm độc giả, ví dụ "add_reader_image.png"
            Image image = new Image(getClass().getResourceAsStream("add_reader_image.png")); // <-- Thay tên ảnh nếu muốn
            addImage = new ImageView(image);
            addImage.setFitHeight(200); addImage.setPreserveRatio(true);
            leftPane.getChildren().add(addImage);
        } catch (Exception e) { System.err.println("Không tìm thấy ảnh add_reader_image.png"); leftPane.getChildren().add(new Label("Image not found")); }

        // --- Phần Bên Phải (Form) - Sửa lại ---
        GridPane rightPane = new GridPane();
        rightPane.setAlignment(Pos.CENTER_LEFT);
        rightPane.setPadding(new Insets(30, 50, 30, 50));
        rightPane.setHgap(15);
        rightPane.setVgap(15);
        rightPane.setStyle("-fx-background-color: #E6E6FA;"); // Nền Lavender

        // --- Sửa Tiêu đề ---
        Label titleLabel = new Label("Thêm Độc Giả"); // <-- Sửa tiêu đề
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        rightPane.add(titleLabel, 0, 0, 2, 1);
        GridPane.setHalignment(titleLabel, Pos.CENTER.getHpos());
        GridPane.setMargin(titleLabel, new Insets(0, 0, 20, 0));

        // --- Sửa lại các trường nhập liệu ---
        Label lblReaderId = new Label("ID Độc Giả:");
        txtReaderId = new TextField(); txtReaderId.setPromptText("Nhập ID");

        Label lblReaderName = new Label("Tên Độc Giả:");
        txtReaderName = new TextField(); txtReaderName.setPromptText("Nhập tên độc giả");

        Label lblReaderSDT = new Label("SĐT:");
        txtReaderSDT = new TextField(); txtReaderSDT.setPromptText("Nhập số điện thoại");
        // Chỉ cho phép nhập số
        txtReaderSDT.textProperty().addListener((obs, oldV, newV) -> { if (!newV.matches("\\d*")) txtReaderSDT.setText(newV.replaceAll("[^\\d]", "")); });
        // ------------------------------------

        // --- Sửa lại cách thêm control vào GridPane ---
        int currentRow = 1;
        rightPane.add(lblReaderId, 0, currentRow); rightPane.add(txtReaderId, 1, currentRow++);
        rightPane.add(lblReaderName, 0, currentRow); rightPane.add(txtReaderName, 1, currentRow++);
        rightPane.add(lblReaderSDT, 0, currentRow); rightPane.add(txtReaderSDT, 1, currentRow++);
        // ------------------------------------------

        // --- Nút bấm (Giữ nguyên) ---
        Button btnLuu = new Button("Lưu");
        Button btnHuy = new Button("Hủy bỏ");
        // ... (Style nút giữ nguyên)
        HBox buttonBox = new HBox(15, btnLuu, btnHuy);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        rightPane.add(buttonBox, 1, currentRow); // Thêm vào hàng cuối

        // Layout gốc (Giữ nguyên)
        BorderPane root = new BorderPane();
        root.setLeft(leftPane);
        root.setCenter(rightPane);

        // --- Sửa lại Xử lý nút Lưu ---
        btnLuu.setOnAction(e -> {
             try {
                 // Lấy dữ liệu Độc Giả
                 String id = txtReaderId.getText().trim();
                 String name = txtReaderName.getText().trim();
                 String sdt = txtReaderSDT.getText().trim();

                 // Kiểm tra dữ liệu
                 if (id.isEmpty() || name.isEmpty() || sdt.isEmpty()) {
                     showAlert(Alert.AlertType.ERROR,"Lỗi", "Vui lòng nhập đầy đủ ID, Tên và SĐT.");
                     return;
                 }
                 // --- Thêm kiểm tra trùng ID Độc Giả ---
                 // Cần truy cập danh sách `readers` từ MainFrame hoặc gọi DAO
                 // Tạm thời bỏ qua, bạn cần thêm logic này
                 // if (ReaderDAO.readerExists(id)) { showAlert(...); return; }
                 // -------------------------------------
                 if (!sdt.matches("\\d{10,11}")) {
                     showAlert(Alert.AlertType.ERROR, "Lỗi", "SĐT phải có 10 hoặc 11 chữ số.");
                     return;
                 }

                 // Tạo đối tượng Reader
                 Reader newReader = new Reader(id, name, sdt); // Đảm bảo class Reader có constructor này

                 // Lưu vào DB
                 ReaderDAO.initDatabase(); // Có thể không cần
                 ReaderDAO.saveReader(newReader); // Gọi DAO của Reader

                 // Thông báo, xóa form, gọi callback, đóng cửa sổ
                 showAlert(Alert.AlertType.INFORMATION,"Thành công", "Đã lưu độc giả thành công!");
                 clearForm(); // Gọi hàm xóa form
                 if (refreshCallback != null) { refreshCallback.run(); } // Gọi lại MainFrame
                 Stage currentStage = (Stage) btnLuu.getScene().getWindow(); currentStage.close(); // Đóng form
             } catch (Exception ex) {
                 showAlert(Alert.AlertType.ERROR,"Lỗi", "Lỗi khi lưu độc giả: " + ex.getMessage());
                 ex.printStackTrace();
             }
        });
        // --------------------------

        // Nút Hủy (Giữ nguyên)
        btnHuy.setOnAction(e -> { /* ... code đóng cửa sổ ... */ });

        return root;
    }

    // --- Các hàm helper (Giữ nguyên showAlert, sửa clearForm) ---
    private void showAlert(Alert.AlertType alertType, String title, String content) { /*...*/ }
    // private String generateUniqueBookId() { /*...*/ } // Không cần nữa

    // Sửa clearForm cho Reader
    private void clearForm() {
        if(txtReaderId!=null) txtReaderId.clear();
        if(txtReaderName!=null) txtReaderName.clear();
        if(txtReaderSDT!=null) txtReaderSDT.clear();
    }
}