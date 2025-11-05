package oop;

import javafx.animation.Interpolator;
import javafx.scene.layout.HBox;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
// --- XÓA --- (Không cần 2 import này nữa)
// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.Random;

public class dangnhap1 extends Application {

    private final Random random = new Random();
    private static final int NUM_PARTICLES = 100;
    private UserStorage userStorage = new UserStorage();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Library Management");

        // --- 1. TẠO FORM ĐĂNG NHẬP ---

        // --- XÓA --- (Toàn bộ khối try-catch tạo ImageView logoView bị xóa)
        // (Vì logo đã là một phần của ảnh nền)

        Label lblUser = new Label("Username:");
        Label lblPass = new Label("Password:");
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        Button login = new Button("Login");
        Button register = new Button("Register");
        String buttonStyle = "-fx-font-size: 18px; " +
                "-fx-background-color: #1E90FF; " + // Màu nền xanh (DodgerBlue)
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5;";

        // Style cho nút Register (ví dụ: màu xám)
        register.setStyle(
                "-fx-font-size: 18px; " +
                        "-fx-background-color: #808080; " + // Màu xám
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5;"
        );
        register.setStyle(buttonStyle);

        // --- SỬA --- (Phóng to chữ, nhưng đổi màu chữ thành MÀU ĐEN)
        // (Vì nền ảnh gốc ở giữa là MÀU TRẮNG)
        // --- SỬA: Phóng to chữ và ô nhập liệu ---

// Tạo một chuỗi style để dùng chung
// Tạo một chuỗi style để dùng chung
        String labelStyle = "-fx-font-size: 24px; " +    // Cỡ chữ 24
                "-fx-font-weight: bold; " +
                "-fx-text-fill: black; " +   // <-- ĐỔI THÀNH MÀU ĐEN
                // Hiệu ứng đổ bóng TRẮNG (để chữ nổi bật)
                "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.9), 5, 0, 0, 1);";

        lblUser.setStyle(labelStyle);
        lblPass.setStyle(labelStyle);

// Đặt kích thước cho ô nhập liệu
        username.setPrefWidth(350);
        password.setPrefWidth(350);

// Phóng to chữ bên trong ô nhập liệu và nút bấm
        username.setStyle("-fx-font-size: 18px;");
        password.setStyle("-fx-font-size: 18px;");
// Thay thế dòng setStyle cũ bằng dòng này:
        login.setStyle(
                "-fx-font-size: 18px; " +         // Cỡ chữ
                        "-fx-background-color: #1E90FF; " + // Màu nền xanh (DodgerBlue)
                        "-fx-text-fill: white; " +         // Chữ màu trắng
                        "-fx-font-weight: bold; " +       // Chữ đậm (tùy chọn)
                        "-fx-background-radius: 5;"        // Bo góc (tùy chọn)
        );

        // --- 2. BỐ TRÍ GRIDPANE (Form) ---
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(25));
        grid.setVgap(15);
        grid.setHgap(10);

        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: transparent;"); // Form trong suốt

        // --- XÓA --- (Khối if (logoView != null) bị xóa)

        // --- SỬA --- (Bắt đầu từ hàng 0)
        int currentRow = 0;
        grid.add(lblUser, 0, currentRow);
        grid.add(username, 1, currentRow);
        grid.add(lblPass, 0, currentRow + 1);
        grid.add(password, 1, currentRow + 1);

        // Thêm nút Register vào cột 0, hàng 2
        HBox buttonBox = new HBox(10); // 10px là khoảng cách giữa 2 nút
        buttonBox.setAlignment(Pos.CENTER_RIGHT); // Căn lề CẢ CỤM sang bên phải
        buttonBox.getChildren().addAll(register, login); // Thêm cả 2 nút vào HBox

        // Thêm HBox (chứa 2 nút) vào Cột 1, Hàng 2
        grid.add(buttonBox, 1, currentRow + 2);

        // --- 3. TẠO NỀN ---
        // --- 3. TẠO NỀN ---

// --- SỬA: Tách nền mờ và hiệu ứng ---

// 3a. TẠO NỀN ẢNH MỜ (ImageView)
        ImageView backgroundView = new ImageView();
        try {
            String imageUrl = getClass().getResource("background_full.jpg").toExternalForm();
            Image backgroundImage = new Image(imageUrl);
            backgroundView.setImage(backgroundImage);

            // === ÁP DỤNG HIỆU ỨNG MỜ ===
            // Bạn có thể thay đổi số 8 thành 10 (mờ hơn) hoặc 5 (rõ hơn)
            backgroundView.setEffect(new GaussianBlur(8));

            // Giữ tỷ lệ ảnh (giống -fx-background-size: contain)
            backgroundView.setPreserveRatio(true);

        } catch (Exception e) {
            System.err.println("LỖI: Không tìm thấy file background_full.jpg!");
        }

// 3b. TẠO PANE HIỆU ỨNG (TRONG SUỐT)
// Pane này bây giờ chỉ dùng để chứa các hạt bụi
        Pane animationPane = new Pane();
        animationPane.setStyle("-fx-background-color: transparent;");


// --- 4. TẠO STACKPANE ĐỂ CĂN GIỮA ---
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;"); // Nền đen dự phòng nếu ảnh lỗi

// --- SỬA: Căn chỉnh ImageView để fill màn hình ---
// Bind kích thước ImageView với StackPane
        backgroundView.fitWidthProperty().bind(root.widthProperty());
        backgroundView.fitHeightProperty().bind(root.heightProperty());

// Đặt các lớp theo đúng thứ tự (Sau đè lên trước):
// 1. (DƯỚI CÙNG): Ảnh nền mờ
// 2. (Ở GIỮA): Các hạt bụi bay
// 3. (TRÊN CÙNG): Form đăng nhập
        root.getChildren().addAll(backgroundView, animationPane, grid);
        // --- 5. XỬ LÝ NÚT ĐĂNG NHẬP (Giữ nguyên) ---
        // --- Logic Nút Login (Sửa lại) ---
        login.setOnAction(e -> {
            String user = username.getText();
            String pass = password.getText();

            try {
                // Gọi lớp UserStorage để kiểm tra
                if (userStorage.checkLogin(user, pass)) {
                    // Đăng nhập thành công
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Login");
                    alert.setHeaderText(null);
                    alert.setContentText("Thành Công!");
                    alert.showAndWait();

                    primaryStage.close();
                    new MainFrame().start(new Stage());
                } else {
                    // Đăng nhập thất bại
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed! Invalid username or password.");
                    alert.showAndWait();
                }
            } catch (IOException ex) {
                // Lỗi không đọc được file
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Could not access user storage.");
                alert.showAndWait();
            }
        });
        register.setOnAction(e -> {
            // Mở cửa sổ đăng ký
            RegistrationForm registerForm = new RegistrationForm(userStorage);
            registerForm.display();
        });

        // --- 6. TẠO SCENE VÀ CHẠY HIỆU ÚNG ---
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        // Chạy hiệu ứng hạt bụi (vẫn giữ lại cho đẹp)
        for (int i = 0; i < NUM_PARTICLES; i++) {
            Circle particle = createParticle(scene);
            animationPane.getChildren().add(particle);
            animateParticle(particle, scene);
        }

        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // --- (Các hàm createParticle và animateParticle giữ nguyên) ---

    private Circle createParticle(Scene scene) {
        Circle particle = new Circle(random.nextDouble() * 3 + 1);
        particle.setFill(Color.web("white", random.nextDouble() * 0.1 + 0.1));
        particle.setEffect(new GaussianBlur(2));
        particle.setTranslateX(random.nextDouble() * scene.getWidth());
        particle.setTranslateY(random.nextDouble() * scene.getHeight());
        return particle;
    }

    private void animateParticle(Circle particle, Scene scene) {
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(particle);
        tt.setInterpolator(Interpolator.LINEAR);
        tt.setDuration(Duration.seconds(random.nextDouble() * 20 + 15));
        tt.setByY(-scene.getHeight() - 10);
        tt.setByX((random.nextDouble() - 0.5) * 100);

        tt.setOnFinished(e -> {
            particle.setTranslateY(scene.getHeight() + 5);
            particle.setTranslateX(random.nextDouble() * scene.getWidth());
            animateParticle(particle, scene);
        });
        tt.play();
    }
}