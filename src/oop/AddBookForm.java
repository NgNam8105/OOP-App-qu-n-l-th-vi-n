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
import oop.dao.BookDAO;
import quanlythuvien.Book; // Import class Book

// import java.time.LocalDate; // Không cần nữa

public class AddBookForm {
    private Runnable refreshCallback;

    // Chỉ cần các TextField này
    private TextField txtMaSach, txtTenSach, txtTacGia, txtSoLuong;
    // private DatePicker dpNgayMua; // Không cần
    // private TextField txtXuatBan, txtGia; // Không cần

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    public BorderPane buildUI() {
        // --- Phần Bên Trái (Ảnh) - Giữ nguyên ---
        VBox leftPane = new VBox();
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(20));
        leftPane.setStyle("-fx-background-color: #FFFFFF;");
        ImageView addBooksImage = null;
        try {
            Image image = new Image(getClass().getResourceAsStream("add_book_image.png"));
            addBooksImage = new ImageView(image);
            addBooksImage.setFitHeight(200); addBooksImage.setPreserveRatio(true);
            leftPane.getChildren().add(addBooksImage);
        } catch (Exception e) { System.err.println("Không tìm thấy ảnh add_book_image.png"); leftPane.getChildren().add(new Label("Image not found")); }

        // --- Phần Bên Phải (Form) - Sửa lại ---
        GridPane rightPane = new GridPane();
        rightPane.setAlignment(Pos.CENTER_LEFT);
        rightPane.setPadding(new Insets(30, 50, 30, 50));
        rightPane.setHgap(15);
        rightPane.setVgap(15); // Giảm Vgap nếu muốn các dòng gần nhau hơn
        rightPane.setStyle("-fx-background-color: #E6E6FA;");

        // Tiêu đề
        Label titleLabel = new Label("Thêm Sách");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        rightPane.add(titleLabel, 0, 0, 2, 1);
        GridPane.setHalignment(titleLabel, Pos.CENTER.getHpos());
        GridPane.setMargin(titleLabel, new Insets(0, 0, 20, 0));

        // --- Chỉ còn các trường cần thiết ---
        Label lblMaSach = new Label("Mã Sách:"); // Thêm Mã Sách
        txtMaSach = new TextField(); txtMaSach.setPromptText("Nhập mã sách");

        Label lblTenSach = new Label("Tên Sách:");
        txtTenSach = new TextField(); txtTenSach.setPromptText("Nhập tên sách");

        Label lblTacGia = new Label("Tác Giả:"); // Sửa label
        txtTacGia = new TextField(); txtTacGia.setPromptText("Nhập tên tác giả");

        Label lblSoLuong = new Label("Số Lượng:"); // Sửa label
        txtSoLuong = new TextField(); txtSoLuong.setPromptText("Nhập số lượng");
        // Chỉ cho phép nhập số nguyên
        txtSoLuong.textProperty().addListener((obs, oldV, newV) -> { if (!newV.matches("\\d*")) txtSoLuong.setText(newV.replaceAll("[^\\d]", "")); });
        // ------------------------------------

        // --- Sắp xếp lại các control ---
        int currentRow = 1; // Bắt đầu từ hàng 1 sau tiêu đề
        rightPane.add(lblMaSach, 0, currentRow); rightPane.add(txtMaSach, 1, currentRow++);
        rightPane.add(lblTenSach, 0, currentRow); rightPane.add(txtTenSach, 1, currentRow++);
        rightPane.add(lblTacGia, 0, currentRow); rightPane.add(txtTacGia, 1, currentRow++);
        rightPane.add(lblSoLuong, 0, currentRow); rightPane.add(txtSoLuong, 1, currentRow++);
        // -----------------------------

        // --- Nút bấm ---
        Button btnLuu = new Button("Lưu");
        Button btnHuy = new Button("Hủy bỏ");
        // Style nút giữ nguyên
        btnLuu.setStyle("-fx-background-color: #6495ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnHuy.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnLuu.setPrefSize(80, 30); btnHuy.setPrefSize(80, 30);
        HBox buttonBox = new HBox(15, btnLuu, btnHuy);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        rightPane.add(buttonBox, 1, currentRow); // Thêm vào hàng cuối cùng
        // ---------------

        // Layout gốc
        BorderPane root = new BorderPane();
        root.setLeft(leftPane);
        root.setCenter(rightPane);

        // --- Xử lý nút Lưu (Sửa lại) ---
        btnLuu.setOnAction(e -> {
            try {
                // Lấy dữ liệu từ các ô cần thiết
                String maSach = txtMaSach.getText().trim(); // Lấy mã sách
                String tenSach = txtTenSach.getText().trim();
                String tacGia = txtTacGia.getText().trim();
                String soLuongStr = txtSoLuong.getText().trim();
                // Kiểm tra dữ liệu
                if (maSach.isEmpty() || tenSach.isEmpty() || tacGia.isEmpty() || soLuongStr.isEmpty()) { // Thêm kiểm tra mã sách
                    showAlert(Alert.AlertType.ERROR,"Lỗi", "Vui lòng nhập đầy đủ Mã Sách, Tên Sách, Tác Giả và Số Lượng.");
                    return;
                }
                // --- Thêm kiểm tra trùng Mã Sách (QUAN TRỌNG) ---
                // Cần truy cập danh sách `books` từ MainFrame hoặc gọi DAO để kiểm tra
                // Cách 1: Gọi DAO (nếu bạn có hàm kiểm tra tồn tại)
                // if (BookDAO.bookExists(maSach)) {
                //     showAlert(Alert.AlertType.ERROR,"Lỗi", "Mã Sách đã tồn tại!");
                //     return;
                // }
                // Cách 2: Truyền ObservableList<Book> vào (phức tạp hơn)
                // Tạm thời bỏ qua kiểm tra trùng lặp ở đây, bạn có thể thêm sau
                // ------------------------------------------------

                int soLuong = Integer.parseInt(soLuongStr);
                if (soLuong < 0) { showAlert(Alert.AlertType.ERROR,"Lỗi", "Số lượng phải là số không âm."); return; }

                // Tạo đối tượng Book với đúng constructor của bạn
                Book newBook = new Book(maSach, tenSach, tacGia, soLuong); // Dùng constructor có mã sách

                // Lưu vào DB
                BookDAO.initDatabase();
                BookDAO.saveBook(newBook);
                // Thông báo, xóa form, gọi callback, đóng cửa sổ
                showAlert(Alert.AlertType.INFORMATION,"Thành công", "Đã lưu sách thành công!");
                clearForm();
                if (refreshCallback != null) { refreshCallback.run(); }
                Stage currentStage = (Stage) btnLuu.getScene().getWindow(); currentStage.close();
            } catch (NumberFormatException ex) { showAlert(Alert.AlertType.ERROR,"Lỗi", "Số lượng phải là số nguyên hợp lệ.");
            } catch (Exception ex) { showAlert(Alert.AlertType.ERROR,"Lỗi", "Lỗi khi lưu: " + ex.getMessage()); ex.printStackTrace(); }
        });
        // -----------------------------

        btnHuy.setOnAction(e -> {
            Stage currentStage = (Stage) btnHuy.getScene().getWindow(); currentStage.close();
        });

        return root;
    }

    // --- Các hàm helper ---
    private void showAlert(Alert.AlertType alertType, String title, String content) { /*...*/ }
    private String generateUniqueBookId() { /*...*/ return "B" + System.currentTimeMillis(); } // Có thể không cần nữa nếu nhập tay

    // Sửa clearForm
    private void clearForm() {
        if(txtMaSach!=null) txtMaSach.clear(); // Thêm clear mã sách
        if(txtTenSach!=null) txtTenSach.clear();
        if(txtTacGia!=null) txtTacGia.clear();
        // if(txtXuatBan!=null) txtXuatBan.clear(); // Bỏ
        // if(dpNgayMua!=null) dpNgayMua.setValue(LocalDate.now()); // Bỏ
        // if(txtGia!=null) txtGia.clear(); // Bỏ
        if(txtSoLuong!=null) txtSoLuong.clear();
    }

    void start(Stage addBookStage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}