package oop;

import javafx.geometry.Insets;
import oop.dao.ReaderDAO; // <-- Đã thêm import
import quanlythuvien.Reader;        // <-- Đã thêm import
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException; // <-- Đã thêm import

public class RegistrationForm {

    private UserStorage userStorage;

    public RegistrationForm(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void display() {

        Stage stage = new Stage();
        stage.setTitle("Đăng ký tài khoản");
        stage.initModality(Modality.APPLICATION_MODAL);

        // --- Layout Mới (VBox) ---
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.getStyleClass().add("root-pane");

        // --- Tiêu đề ---
        Label lblTitle = new Label("Tạo tài khoản mới");
        lblTitle.getStyleClass().add("title-label");

        // --- Form (GridPane) ---
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.getStyleClass().add("form-container");

        // (Các trường của bạn giữ nguyên)
        Label lblName = new Label("Họ và tên:");
        TextField txtName = new TextField();
        Label lblStudentID = new Label("Mã sinh viên:");
        TextField txtStudentID = new TextField();
        Label lblPhone = new Label("Số điện thoại:");
        TextField txtPhone = new TextField();

        Label lblUser = new Label("Username:");
        TextField txtUser = new TextField();
        txtUser.setPromptText("At least 4 characters");
        Label lblPass = new Label("Password:");
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("At least 6 characters");
        Label lblConfirm = new Label("Confirm Password:");
        PasswordField txtConfirm = new PasswordField();

        Button btnSubmit = new Button("Submit");
        btnSubmit.getStyleClass().add("submit-button");
        btnSubmit.setPrefWidth(Double.MAX_VALUE);

        // (Layout GridPane giữ nguyên)
        grid.add(lblName, 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(lblStudentID, 0, 1);
        grid.add(txtStudentID, 1, 1);
        grid.add(lblPhone, 0, 2);
        grid.add(txtPhone, 1, 2);
        grid.add(lblUser, 0, 3);
        grid.add(txtUser, 1, 3);
        grid.add(lblPass, 0, 4);
        grid.add(txtPass, 1, 4);
        grid.add(lblConfirm, 0, 5);
        grid.add(txtConfirm, 1, 5);
        grid.add(btnSubmit, 0, 6, 2, 1);

        GridPane.setHalignment(btnSubmit, HPos.CENTER);

        // --- Thêm Tiêu đề và Grid vào layout VBox ---
        root.getChildren().addAll(lblTitle, grid);

        // --- ĐÂY LÀ PHẦN SỬA LỖI QUAN TRỌNG ---
        // Toàn bộ logic phải nằm BÊN TRONG .setOnAction
        btnSubmit.setOnAction(e -> {
            String name = txtName.getText();
            String studentID = txtStudentID.getText();
            String phone = txtPhone.getText();
            String user = txtUser.getText();
            String pass = txtPass.getText();
            String confirm = txtConfirm.getText();

            // Validation (Kiểm tra đầu vào)
            if (name.isEmpty() || studentID.isEmpty() || phone.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Thiếu thông tin", "Vui lòng điền đầy đủ họ tên, MSSV và SĐT.");
                return;
            }
            if (user.length() < 4 || pass.length() < 6) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Username must be > 4 chars, Password > 6 chars.");
                return;
            }
            if (!pass.equals(confirm)) {
                showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match!");
                return;
            }

            // Khối try...catch đúng phải nằm ở đây (BÊN TRONG .setOnAction)
            try {
                // 1. Kiểm tra xem Username đã tồn tại trong file user.dat chưa
                if (userStorage.isUserExists(user)) {
                    showAlert(Alert.AlertType.ERROR, "Registration Failed", "Thất Bại! Tên đăng nhập đã tồn tại.");
                    return; // Dừng lại nếu tên đăng nhập đã có
                }

                // 2. Kiểm tra xem Mã Sinh Viên đã tồn tại trong database Độc Giả chưa
                ReaderDAO.initDatabase(); // Khởi tạo DB độc giả (để chắc chắn bảng đã tồn tại)

                // Lấy tất cả độc giả và kiểm tra trùng ID (Mã SV)
                boolean readerIdExists = ReaderDAO.getAllReaders().stream()
                        .anyMatch(r -> r.getId().equalsIgnoreCase(studentID));

                if (readerIdExists) {
                    showAlert(Alert.AlertType.ERROR, "Registration Failed",
                            "Thất Bại! Mã Sinh Viên này đã được đăng ký cho một độc giả khác.");
                    return; // Dừng lại nếu Mã SV đã có
                }

                // 3. Nếu tất cả đều hợp lệ: Tạo cả User và Reader

                // Tạo tài khoản đăng nhập (lưu vào file)
                userStorage.registerUser(user, pass, name, studentID, phone);

                // Tạo hồ sơ độc giả (lưu vào CSDL)
                Reader newReader = new Reader(studentID, name, phone);
                ReaderDAO.saveReader(newReader);

                // Thông báo thành công và đóng cửa sổ
                showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Đăng ký thành công! Tài khoản và hồ sơ độc giả đã được tạo.");
                stage.close();

            } catch (IOException ex) { // Lỗi liên quan đến file user.dat
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Storage Error", "Could not save user data: " + ex.getMessage());
            } catch (Exception dbEx) { // Lỗi liên quan đến CSDL (ReaderDAO)
                dbEx.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Không thể lưu hồ sơ độc giả: " + dbEx.getMessage());
            }

        }); // <-- Dấu } của .setOnAction nằm ở đây

        // --- Tạo Scene và tải CSS (Phần này nằm BÊN NGOÀI .setOnAction) ---
        Scene scene = new Scene(root, 450, 550);

        // <-- DÒNG QUAN TRỌNG NHẤT: Tải file CSS -->
        String cssPath = getClass().getResource("style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.setScene(scene);
        stage.showAndWait();
    }

    // (Hàm showAlert giữ nguyên)
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}