package oop;

import javafx.collections.ObservableList;
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
import javafx.util.Callback;
import oop.dao.BookDAO;
import oop.dao.ReaderDAO;
import oop.dao.BorrowDAO;
import quanlythuvien.Book;
import quanlythuvien.Reader;
import quanlythuvien.Borrow;

import java.time.LocalDate;

public class BorrowBookForm {
    private Runnable borrowSuccessCallback; // Callback khi mượn thành công

    // Danh sách sách và độc giả được truyền từ MainFrame
    private ObservableList<Book> availableBooks;
    private ObservableList<Reader> activeReaders;

    // Controls
    private ComboBox<Book> cbBook;
    private ComboBox<Reader> cbReader;
    private DatePicker dpBorrowDate;

    private BorrowDAO borrowDAO; // Để thực hiện thao tác DB

    // Constructor để nhận danh sách từ MainFrame
    public BorrowBookForm(ObservableList<Book> books, ObservableList<Reader> readers) {
        // Chỉ lấy sách còn > 0 cuốn
        this.availableBooks = books.filtered(book -> book.getQuantity() > 0);
        this.activeReaders = readers; // Giả sử tất cả độc giả đều active
        this.borrowDAO = new BorrowDAO();
    }

    public void setBorrowSuccessCallback(Runnable callback) {
        this.borrowSuccessCallback = callback;
    }

    // Hàm tạo giao diện - THAY ĐỔI: Trả về BorderPane
    public BorderPane buildUI() {

        // --- 1. Phần Bên Trái (Ảnh) - Giống hệt AddBookForm ---
        VBox leftPane = new VBox();
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(20));
        leftPane.setStyle("-fx-background-color: #FFFFFF;");
        ImageView borrowImageView = null;
        try {
            Image image = new Image(getClass().getResourceAsStream("add_book_image.png"));
            borrowImageView = new ImageView(image);
            borrowImageView.setFitHeight(200);
            borrowImageView.setPreserveRatio(true);
            leftPane.getChildren().add(borrowImageView);
        } catch (Exception e) {
            System.err.println("Không tìm thấy ảnh add_book_image.png");
            leftPane.getChildren().add(new Label("Image not found"));
        }

        // --- 2. Phần Bên Phải (Form) ---
        GridPane rightPane = new GridPane();
        rightPane.setAlignment(Pos.CENTER_LEFT);
        rightPane.setPadding(new Insets(30, 50, 30, 50));
        rightPane.setHgap(15);
        rightPane.setVgap(15);
        rightPane.setStyle("-fx-background-color: #F0F8FF;");

        // Tiêu đề
        Label titleLabel = new Label("Mượn Sách");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        rightPane.add(titleLabel, 0, 0, 2, 1);
        GridPane.setHalignment(titleLabel, Pos.CENTER.getHpos());
        GridPane.setMargin(titleLabel, new Insets(0, 0, 20, 0));

        // --- Chọn Sách ---
        Label lblBook = new Label("Chọn Sách:");
        cbBook = new ComboBox<>(availableBooks);
        cbBook.setPromptText("Tìm hoặc chọn sách...");
        setupBookComboBox();
        cbBook.setPrefWidth(250);

        // --- Chọn Độc Giả ---
        Label lblReader = new Label("Chọn Độc Giả:");
        cbReader = new ComboBox<>(activeReaders);
        cbReader.setPromptText("Tìm hoặc chọn độc giả...");
        setupReaderComboBox();
        cbReader.setPrefWidth(250);

        // --- Ngày Mượn ---
        Label lblBorrowDate = new Label("Ngày Mượn:");
        dpBorrowDate = new DatePicker(LocalDate.now());
        dpBorrowDate.setPrefWidth(250);

        // Thêm vào GridPane (rightPane)
        int currentRow = 1;
        rightPane.add(lblBook, 0, currentRow);
        rightPane.add(cbBook, 1, currentRow++);
        rightPane.add(lblReader, 0, currentRow);
        rightPane.add(cbReader, 1, currentRow++);
        rightPane.add(lblBorrowDate, 0, currentRow);
        rightPane.add(dpBorrowDate, 1, currentRow++);

        // --- Nút Bấm ---
        Button btnXacNhan = new Button("Xác Nhận Mượn");
        Button btnHuy = new Button("Hủy");
        btnXacNhan.setStyle("-fx-background-color: #6495ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnHuy.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnXacNhan.setPrefSize(120, 30);
        btnHuy.setPrefSize(80, 30);

        HBox buttonBox = new HBox(15, btnXacNhan, btnHuy);
        buttonBox.setAlignment(Pos.CENTER_RIGHT); // Căn phải giống AddBookForm
        rightPane.add(buttonBox, 1, currentRow);

        // --- 3. Layout Gốc (BorderPane) ---
        BorderPane root = new BorderPane();
        root.setLeft(leftPane);
        root.setCenter(rightPane);

        // --- Xử lý sự kiện ---
        btnXacNhan.setOnAction(e -> handleBorrowAction());
        btnHuy.setOnAction(e -> {
            Stage stage = (Stage) btnHuy.getScene().getWindow();
            stage.close();
        });

        return root;
    }

    // Tách hàm setup ComboBox Sách
    private void setupBookComboBox() {
        cbBook.setCellFactory(new Callback<ListView<Book>, ListCell<Book>>() {
            @Override
            public ListCell<Book> call(ListView<Book> param) {
                return new ListCell<Book>() {
                    @Override
                    protected void updateItem(Book item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getName() + " (ID: " + item.getId() + ", SL: " + item.getQuantity() + ")");
                        }
                    }
                };
            }
        });
        cbBook.setButtonCell(new ListCell<Book>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(cbBook.getPromptText());
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    // Tách hàm setup ComboBox Độc Giả
    private void setupReaderComboBox() {
        cbReader.setCellFactory(new Callback<ListView<Reader>, ListCell<Reader>>() {
            @Override
            public ListCell<Reader> call(ListView<Reader> param) {
                return new ListCell<Reader>() {
                    @Override
                    protected void updateItem(Reader item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getName() + " (ID: " + item.getId() + ")");
                        }
                    }
                };
            }
        });
        cbReader.setButtonCell(new ListCell<Reader>() {
            @Override
            protected void updateItem(Reader item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(cbReader.getPromptText());
                } else {
                    setText(item.getName());
                }
            }
        });
    }


    // Hàm xử lý logic mượn sách
    private void handleBorrowAction() {
        Book selectedBook = cbBook.getValue();
        Reader selectedReader = cbReader.getValue();
        LocalDate borrowDate = dpBorrowDate.getValue();

        // 1. Kiểm tra đầu vào
        if (selectedBook == null || selectedReader == null || borrowDate == null) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng chọn Sách, Độc Giả và Ngày Mượn.");
            return;
        }

        // 2. Kiểm tra sách còn không
        if (selectedBook.getQuantity() <= 0) {
            showAlert(Alert.AlertType.ERROR, "Hết Sách", "Sách '" + selectedBook.getName() + "' đã hết.");
            availableBooks.setAll(BookDAO.getAllBooks().filtered(book -> book.getQuantity() > 0));
            cbBook.getSelectionModel().clearSelection();
            return;
        }

        // 3. Kiểm tra độc giả đã mượn sách này chưa trả?
        try {
            ObservableList<Borrow> currentBorrows = BorrowDAO.getAllBorrows();
            boolean isAlreadyBorrowing = currentBorrows.stream()
                    .anyMatch(borrow -> borrow.getBookId().equals(selectedBook.getId())
                            && borrow.getReaderId().equals(selectedReader.getId())
                            && borrow.getStatus().equalsIgnoreCase("Đang mượn"));

            if (isAlreadyBorrowing) {
                showAlert(Alert.AlertType.WARNING, "Sách Đang Mượn", "Độc giả '" + selectedReader.getName() + "' đang mượn cuốn sách này và chưa trả.");
                return;
            }

            // 4. Tạo đối tượng Borrow mới
            String borrowId = BorrowDAO.generateNewBorrowId(); // Cần hàm này trong DAO
            Borrow newBorrow = new Borrow(borrowId, selectedBook.getId(), selectedReader.getId(), borrowDate.toString(), "", "Đang mượn");

            // 5. Lưu vào DB và cập nhật số lượng sách
            borrowDAO.insertBorrow(newBorrow);
            selectedBook.setQuantity(selectedBook.getQuantity() - 1);
            BookDAO.saveBook(selectedBook);

            // 6. Thông báo thành công, gọi callback và đóng cửa sổ
            showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Đã tạo phiếu mượn sách thành công! Mã phiếu: " + borrowId);
            if (borrowSuccessCallback != null) {
                borrowSuccessCallback.run();
            }
            Stage stage = (Stage) cbBook.getScene().getWindow();
            stage.close();

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Hệ Thống", "Không thể tạo phiếu mượn: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // --- Các hàm helper ---
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}