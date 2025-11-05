package oop;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.time.LocalDate;
import quanlythuvien.Book;
import quanlythuvien.Reader;
import quanlythuvien.Borrow;

public class BorrowReturnForm {
    private Runnable refreshCallback;
    private ObservableList<Book> availableBooks;
    private ObservableList<Reader> activeReaders;
    private ObservableList<Borrow> currentBorrows;
    private BorrowDAO borrowDAO;

    private ComboBox<Book> cbBook;
    private ComboBox<Reader> cbReader;
    private DatePicker dpBorrowDate;
    private ComboBox<Borrow> cbBorrowRecord;
    private DatePicker dpReturnDate;

    private String formType;

    public BorrowReturnForm(String formType, ObservableList<Book> books,
                            ObservableList<Reader> readers, ObservableList<Borrow> borrows) {
        this.formType = formType;
        this.availableBooks = books;
        this.activeReaders = readers;
        this.currentBorrows = borrows;
        this.borrowDAO = new BorrowDAO();
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    public BorderPane buildUI() {
        VBox leftPane = new VBox();
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(20));
        leftPane.setStyle("-fx-background-color: #FFFFFF;");

        ImageView formImage = null;
        try {
            String imageName = "add_book_image.png";
            Image image = new Image(getClass().getResourceAsStream(imageName));
            formImage = new ImageView(image);
            formImage.setFitHeight(200);
            formImage.setPreserveRatio(true);
            leftPane.getChildren().add(formImage);
        } catch (Exception e) {
            System.err.println("Không tìm thấy ảnh add_book_image.png");
            leftPane.getChildren().add(new Label("Image not found"));
        }

        GridPane rightPane = new GridPane();
        rightPane.setAlignment(Pos.CENTER_LEFT);
        rightPane.setPadding(new Insets(30, 50, 30, 50));
        rightPane.setHgap(15);
        rightPane.setVgap(15);
        rightPane.setStyle("-fx-background-color: #E6F3FF;");

        Label titleLabel = new Label(formType.equals("BORROW") ? "Mượn Sách" : "Trả Sách");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        rightPane.add(titleLabel, 0, 0, 2, 1);
        GridPane.setHalignment(titleLabel, Pos.CENTER.getHpos());
        GridPane.setMargin(titleLabel, new Insets(0, 0, 20, 0));

        int currentRow = 1;

        if (formType.equals("BORROW")) {
            // Form mượn sách
            Label lblBook = new Label("Chọn Sách:");
            cbBook = new ComboBox<>(availableBooks.filtered(b -> b.getQuantity() > 0));
            cbBook.setPromptText("Tìm hoặc chọn sách...");
            cbBook.setPrefWidth(300);

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

            Label lblReader = new Label("Chọn Độc Giả:");
            cbReader = new ComboBox<>(activeReaders);
            cbReader.setPromptText("Tìm hoặc chọn độc giả...");
            cbReader.setPrefWidth(300);

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

            Label lblBorrowDate = new Label("Ngày Mượn:");
            dpBorrowDate = new DatePicker(LocalDate.now());
            dpBorrowDate.setPrefWidth(300);

            rightPane.add(lblBook, 0, currentRow);
            rightPane.add(cbBook, 1, currentRow++);
            rightPane.add(lblReader, 0, currentRow);
            rightPane.add(cbReader, 1, currentRow++);
            rightPane.add(lblBorrowDate, 0, currentRow);
            rightPane.add(dpBorrowDate, 1, currentRow++);

        } else {
            // Form trả sách
            Label lblBorrowRecord = new Label("Chọn Phiếu Mượn:");
            ObservableList<Borrow> activeBorrows = currentBorrows.filtered(
                    borrow -> borrow.getStatus().equalsIgnoreCase("Đang mượn")
            );
            cbBorrowRecord = new ComboBox<>(activeBorrows);
            cbBorrowRecord.setPromptText("Chọn phiếu cần trả...");
            cbBorrowRecord.setPrefWidth(300);

            cbBorrowRecord.setCellFactory(new Callback<ListView<Borrow>, ListCell<Borrow>>() {
                @Override
                public ListCell<Borrow> call(ListView<Borrow> param) {
                    return new ListCell<Borrow>() {
                        @Override
                        protected void updateItem(Borrow item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setText(null);
                            } else {
                                Book book = availableBooks.stream()
                                        .filter(b -> b.getId().equals(item.getBookId()))
                                        .findFirst().orElse(null);
                                Reader reader = activeReaders.stream()
                                        .filter(r -> r.getId().equals(item.getReaderId()))
                                        .findFirst().orElse(null);

                                String bookName = (book != null) ? book.getName() : item.getBookId();
                                String readerName = (reader != null) ? reader.getName() : item.getReaderId();
                                setText(bookName + " - " + readerName + " (Mượn: " + item.getBorrowDate() + ")");
                            }
                        }
                    };
                }
            });

            cbBorrowRecord.setButtonCell(new ListCell<Borrow>() {
                @Override
                protected void updateItem(Borrow item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(cbBorrowRecord.getPromptText());
                    } else {
                        Book book = availableBooks.stream()
                                .filter(b -> b.getId().equals(item.getBookId()))
                                .findFirst().orElse(null);
                        String bookName = (book != null) ? book.getName() : item.getBorrowId();
                        setText(bookName);
                    }
                }
            });

            Label lblReturnDate = new Label("Ngày Trả:");
            dpReturnDate = new DatePicker(LocalDate.now());
            dpReturnDate.setPrefWidth(300);

            rightPane.add(lblBorrowRecord, 0, currentRow);
            rightPane.add(cbBorrowRecord, 1, currentRow++);
            rightPane.add(lblReturnDate, 0, currentRow);
            rightPane.add(dpReturnDate, 1, currentRow++);
        }

        Button btnSubmit = new Button(formType.equals("BORROW") ? "Xác Nhận Mượn" : "Xác Nhận Trả");
        Button btnCancel = new Button("Hủy bỏ");

        if (formType.equals("BORROW")) {
            btnSubmit.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        } else {
            btnSubmit.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        }
        btnCancel.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

        btnSubmit.setPrefSize(120, 30);
        btnCancel.setPrefSize(80, 30);

        HBox buttonBox = new HBox(15, btnSubmit, btnCancel);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        rightPane.add(buttonBox, 1, currentRow);

        BorderPane root = new BorderPane();
        root.setLeft(leftPane);
        root.setCenter(rightPane);

        btnSubmit.setOnAction(e -> {
            if (formType.equals("BORROW")) {
                handleBorrow();
            } else {
                handleReturn();
            }
        });

        btnCancel.setOnAction(e -> {
            Stage currentStage = (Stage) btnCancel.getScene().getWindow();
            currentStage.close();
        });

        return root;
    }

    private void handleBorrow() {
        Book selectedBook = cbBook.getValue();
        Reader selectedReader = cbReader.getValue();
        LocalDate borrowDate = dpBorrowDate.getValue();

        if (selectedBook == null || selectedReader == null || borrowDate == null) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng chọn đầy đủ Sách, Độc Giả và Ngày Mượn.");
            return;
        }

        if (selectedBook.getQuantity() <= 0) {
            showAlert(Alert.AlertType.ERROR, "Hết Sách", "Sách '" + selectedBook.getName() + "' đã hết.");
            ObservableList<Book> booksWithStock = availableBooks.filtered(book -> book.getQuantity() > 0);
            cbBook.setItems(booksWithStock);
            cbBook.getSelectionModel().clearSelection();
            return;
        }

        boolean isAlreadyBorrowing = currentBorrows.stream()
                .anyMatch(borrow -> borrow.getBookId().equals(selectedBook.getId())
                        && borrow.getReaderId().equals(selectedReader.getId())
                        && borrow.getStatus().equalsIgnoreCase("Đang mượn"));

        if (isAlreadyBorrowing) {
            showAlert(Alert.AlertType.WARNING, "Sách Đang Mượn",
                    "Độc giả '" + selectedReader.getName() + "' đang mượn cuốn sách này và chưa trả.");
            return;
        }

        try {
            String borrowId = BorrowDAO.generateNewBorrowId();
            Borrow newBorrow = new Borrow(
                    borrowId,
                    selectedBook.getId(),
                    selectedReader.getId(),
                    borrowDate.toString(),
                    "",
                    "Đang mượn"
            );

            // CHỈ GỌI insertBorrow
            borrowDAO.insertBorrow(newBorrow);

            // Giảm số lượng sách
            selectedBook.setQuantity(selectedBook.getQuantity() - 1);
            BookDAO.saveBook(selectedBook);

            System.out.println("Đã tạo phiếu mượn: " + borrowId);
            showAlert(Alert.AlertType.INFORMATION, "Thành Công",
                    "Đã tạo phiếu mượn sách thành công!\nMã phiếu: " + borrowId);

            clearForm();
            if (refreshCallback != null) {
                refreshCallback.run();
            }

            Stage currentStage = (Stage) cbBook.getScene().getWindow();
            currentStage.close();

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Hệ Thống",
                    "Không thể tạo phiếu mượn: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleReturn() {
        Borrow selectedBorrow = cbBorrowRecord.getValue();
        LocalDate returnDate = dpReturnDate.getValue();

        if (selectedBorrow == null || returnDate == null) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin",
                    "Vui lòng chọn Phiếu Mượn và Ngày Trả.");
            return;
        }

        try {
            // Cập nhật thông tin phiếu
            selectedBorrow.setReturnDate(returnDate.toString());
            selectedBorrow.setStatus("Đã trả");

            // CHỈ GỌI updateBorrow (KHÔNG gọi saveBorrow nữa vì bị trùng)
            borrowDAO.updateBorrow(selectedBorrow);

            // Tăng số lượng sách
            Book book = availableBooks.stream()
                    .filter(b -> b.getId().equals(selectedBorrow.getBookId()))
                    .findFirst().orElse(null);

            if (book != null) {
                book.setQuantity(book.getQuantity() + 1);
                BookDAO.saveBook(book);
                System.out.println("Đã hoàn trả sách: " + book.getName());
            } else {
                System.err.println("Không tìm thấy sách " + selectedBorrow.getBookId() + " để hoàn trả số lượng.");
            }

            System.out.println("Đã cập nhật phiếu: " + selectedBorrow.getBorrowId());
            showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Đã trả sách thành công!");

            clearForm();
            if (refreshCallback != null) {
                refreshCallback.run();
            }

            Stage currentStage = (Stage) cbBorrowRecord.getScene().getWindow();
            currentStage.close();

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Hệ Thống",
                    "Không thể trả sách: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearForm() {
        if (formType.equals("BORROW")) {
            if (cbBook != null) cbBook.getSelectionModel().clearSelection();
            if (cbReader != null) cbReader.getSelectionModel().clearSelection();
            if (dpBorrowDate != null) dpBorrowDate.setValue(LocalDate.now());
        } else {
            if (cbBorrowRecord != null) cbBorrowRecord.getSelectionModel().clearSelection();
            if (dpReturnDate != null) dpReturnDate.setValue(LocalDate.now());
        }
    }
}