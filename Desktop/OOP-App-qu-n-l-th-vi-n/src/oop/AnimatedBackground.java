/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oop;

/**
 *
 * @author vuvan
 */
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class AnimatedBackground extends Application {

    private static final int NUM_PARTICLES = 200; // Số lượng hạt bụi
    private final Random random = new Random();

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 1000, 650); // Kích thước giống app của bạn

        // --- 1. ĐẶT ẢNH NỀN (VÂN GỖ) ---
        // !!! BẠN PHẢI CÓ ẢNH TÊN LÀ "wood.jpg" TRONG CÙNG THƯ MỤC
        // Hoặc thay "wood.jpg" bằng đường dẫn tới ảnh của bạn
        // DÒNG CODE MỚI (TỐT HƠN):
        String imageUrl = getClass().getResource("wood.jpg").toExternalForm();
        root.setStyle("-fx-background-image: url('" + imageUrl + "'); -fx-background-size: cover;");

        // --- 2. TẠO VÀ CHẠY HOẠT ẢNH CHO CÁC HẠT ---
        for (int i = 0; i < NUM_PARTICLES; i++) {
            Circle particle = createParticle(scene);
            root.getChildren().add(particle);
            animateParticle(particle, scene);
        }

        stage.setTitle("Hiệu ứng Hạt Bụi Sống Động");
        stage.setScene(scene);
        stage.show();
    }

    
    private Circle createParticle(Scene scene) {
        // Kích thước ngẫu nhiên (từ 1 đến 4px)
        Circle particle = new Circle(random.nextDouble() * 3 + 1);

        // Màu trắng, độ mờ rất thấp (10-20%)
        particle.setFill(Color.web("white", random.nextDouble() * 0.1 + 0.1));
        
        // Hiệu ứng làm mờ (cho giống hạt bụi)
        particle.setEffect(new GaussianBlur(2));

        // Vị trí X, Y ban đầu ngẫu nhiên trên màn hình
        particle.setTranslateX(random.nextDouble() * scene.getWidth());
        particle.setTranslateY(random.nextDouble() * scene.getHeight());
        
        return particle;
    }

    
    private void animateParticle(Circle particle, Scene scene) {
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(particle);
        tt.setInterpolator(Interpolator.LINEAR); // Di chuyển đều
        
        // Thời gian di chuyển ngẫu nhiên (15-35 giây)
        tt.setDuration(Duration.seconds(random.nextDouble() * 20 + 15));
        
        // Di chuyển theo Y (lên trên)
        // Di chuyển một quãng bằng chiều cao màn hình
        tt.setByY(-scene.getHeight() - 10); 
        
        // Di chuyển theo X (ngang) một chút cho tự nhiên (lệch trái/phải 100px)
        tt.setByX((random.nextDouble() - 0.5) * 100);

        // --- KHI HOẠT ẢNH KẾT THÚC ---
        tt.setOnFinished(e -> {
            // 1. Đặt lại hạt bụi về vị trí dưới đáy màn hình
            particle.setTranslateY(scene.getHeight() + 5);
            // 2. Cho nó một vị trí X ngẫu nhiên mới
            particle.setTranslateX(random.nextDouble() * scene.getWidth());
            // 3. Chạy lại hoạt ảnh (để tạo vòng lặp vô tận)
            animateParticle(particle, scene); 
        });

        tt.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}