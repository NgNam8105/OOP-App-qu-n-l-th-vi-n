package oop; // Phải cùng package

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserStorage {

    // Tên file để lưu tài khoản
    private static final String FILE_NAME = "users.txt";

    public UserStorage() {
        // Đảm bảo file tồn tại khi khởi động
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error creating user file: " + e.getMessage());
        }
    }

    /**
     * Đọc tất cả user từ file vào một Map
     */
    private Map<String, String> loadUsers() throws IOException {
        Map<String, String> users = new HashMap<>();
        // Sử dụng try-with-resources để tự động đóng file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {

                // <-- THAY ĐỔI QUAN TRỌNG
                String[] parts = line.split(":"); // Tách bởi TẤT CẢ dấu :

                // Dù file có 5 cột, ta chỉ cần 2 cột đầu (user, pass) cho vào Map để check login
                // Kiểm tra >= 2 để tránh lỗi nếu có dòng trống và tương thích với file cũ
                if (parts.length >= 2) {
                    users.put(parts[0], parts[1]); // parts[0] = username, parts[1] = password
                }
            }
        }
        return users;
    }


    public boolean isUserExists(String username) throws IOException {
        Map<String, String> users = loadUsers();
        return users.containsKey(username);
    }


    public boolean checkLogin(String username, String password) throws IOException {
        Map<String, String> users = loadUsers();
        if (users.containsKey(username)) {
            // Nếu user tồn tại, kiểm tra xem password có khớp không
            // Hàm loadUsers() đã đảm bảo users.get(username) CHỈ là password
            return users.get(username).equals(password);
        }
        return false; // User không tồn tại
    }


    // <-- THAY ĐỔI 1: Thêm các tham số mới
    public void registerUser(String username, String password, String name, String studentID, String phone) throws IOException {
        // Mở file ở chế độ "append" (ghi tiếp vào cuối)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {

            // <-- THAY ĐỔI 2: Ghi tất cả 5 trường thông tin
            String userData = String.join(":", username, password, name, studentID, phone);

            writer.write(userData);
            writer.newLine(); // Thêm một dòng mới
        }
    }
}