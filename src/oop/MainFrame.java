package oop;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import oop.dao.BookDAO;
import oop.dao.ReaderDAO;
import oop.dao.BorrowDAO;
import quanlythuvien.Book;
import quanlythuvien.Reader;
import quanlythuvien.Borrow;
import javafx.scene.layout.Priority;
import javafx.scene.control.SplitPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;

public class MainFrame extends Application {

    private final ObservableList<Book> books = FXCollections.observableArrayList();
    private final ObservableList<Reader> readers = FXCollections.observableArrayList();
    private final BorrowDAO borrowDAO = new BorrowDAO();
    private final ObservableList<Borrow> borrows = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Library Management - Nhóm QNT");

        // Khởi tạo database
        BookDAO.initDatabase();
        ReaderDAO.initDatabase();

        // Load dữ liệu
        books.setAll(BookDAO.getAllBooks());
        readers.setAll(ReaderDAO.getAllReaders());
        borrows.setAll(BorrowDAO.getAllBorrows());

        // Tạo TabPane
        TabPane tabPane = new TabPane();
        Tab tabBook = new Tab("Quản lý Sách", createBookTab(stage));
        Tab tabReader = new Tab("Quản lý Độc giả", createReaderTab(stage));
        Tab tabBorrow = new Tab("Mượn - Trả Sách", createBorrowTab(stage));

        tabBook.setClosable(false);
        tabReader.setClosable(false);
        tabBorrow.setClosable(false);

        tabPane.getTabs().addAll(tabBook, tabReader, tabBorrow);

        // Listener để refresh dữ liệu khi chuyển tab
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                if (newTab.getText().equals("Quản lý Sách")) {
                    books.setAll(BookDAO.getAllBooks());
                } else if (newTab.getText().equals("Quản lý Độc giả")) {
                    readers.setAll(ReaderDAO.getAllReaders());
                } else if (newTab.getText().equals("Mượn - Trả Sách")) {
                    borrows.setAll(BorrowDAO.getAllBorrows());
                    books.setAll(BookDAO.getAllBooks());
                    readers.setAll(ReaderDAO.getAllReaders());
                }
            }
        });

        Scene scene = new Scene(tabPane, 1000, 650);
        stage.setScene(scene);
        stage.show();
    }

    // ==================== PHẦN BỊ THIẾU ĐÃ ĐƯỢC THÊM VÀO ====================
    // Méthode helper pour styliser les boutons
    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8 20; " +
                "-fx-cursor: hand; " +
                "-fx-background-radius: 5;");

        // Effet hover
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: derive(" + color + ", -10%); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5;"));

        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5;"));
    }
    // ==================== KẾT THÚC PHẦN THÊM VÀO ====================


    // ==================== TAB QUẢN LÝ SÁCH ====================
    private BorderPane createBookTab(Stage stage) {
        TableView<Book> table = new TableView<>(books);

        // Các cột
        TableColumn<Book, String> colId = new TableColumn<>("Mã sách");
        colId.setCellValueFactory(data -> data.getValue().idProperty());

        TableColumn<Book, String> colName = new TableColumn<>("Tên sách");
        colName.setCellValueFactory(data -> data.getValue().nameProperty());

        TableColumn<Book, String> colAuthor = new TableColumn<>("Tác giả");
        colAuthor.setCellValueFactory(data -> data.getValue().authorProperty());

        TableColumn<Book, Integer> colQty = new TableColumn<>("Số lượng");
        colQty.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());

        table.getColumns().addAll(colId, colName, colAuthor, colQty);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Input fields
        TextField txtId = new TextField();
        txtId.setPromptText("Mã sách");
        TextField txtName = new TextField();
        txtName.setPromptText("Tên sách");
        TextField txtAuthor = new TextField();
        txtAuthor.setPromptText("Tác giả");
        TextField txtQty = new TextField();
        txtQty.setPromptText("Số lượng");

        HBox inputs = new HBox(10, txtId, txtName, txtAuthor, txtQty);
        inputs.setPadding(new Insets(10));

        // Buttons
        Button btnAdd = new Button("ADD");
        Button btnEdit = new Button("FIX");
        Button btnDelete = new Button("DELETE");
        Button btnExport = new Button("EXPORT");

        // ==================== PHẦN BỊ THIẾU ĐÃ ĐƯỢC THÊM VÀO ====================
        styleButton(btnAdd, "#28a745");      // Vert
        styleButton(btnEdit, "#ffc107");     // Jaune/Orange
        styleButton(btnDelete, "#dc3545");   // Rouge
        styleButton(btnExport, "#007bff");   // Bleu
        // ==================== KẾT THÚC PHẦN THÊM VÀO ====================

        HBox buttons = new HBox(10, btnAdd, btnEdit, btnDelete, btnExport);
        buttons.setPadding(new Insets(10));

        // Xử lý nút ADD - Mở form thêm sách
        btnAdd.setOnAction(e -> {
            try {
                Stage addBookStage = new Stage();
                addBookStage.setTitle("Thêm Sách Mới");
                addBookStage.initModality(Modality.APPLICATION_MODAL);
                addBookStage.initOwner(stage);

                AddBookForm addBookForm = new AddBookForm();

                final TableView<Book> finalTable = table;
                addBookForm.setRefreshCallback(() -> {
                    try {
                        books.setAll(BookDAO.getAllBooks());
                        if (finalTable != null) {
                            finalTable.refresh();
                        }
                        System.out.println("Bảng sách đã được làm mới!");
                    } catch (Exception refreshEx) {
                        showAlert(Alert.AlertType.ERROR, "Lỗi Refresh",
                                "Không thể làm mới: " + refreshEx.getMessage());
                        refreshEx.printStackTrace();
                    }
                });

                BorderPane addBookRootPane = addBookForm.buildUI();
                if (addBookRootPane == null) {
                    throw new Exception("Hàm buildUI() của AddBookForm trả về null!");
                }

                // ===== ĐÃ CHỈNH SỬA KÍCH THƯỚC Ở ĐÂY =====
                Scene addBookScene = new Scene(addBookRootPane, 750, 450);
                addBookStage.setScene(addBookScene);
                addBookStage.showAndWait();

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi",
                        "Không thể mở cửa sổ Thêm Sách: " + ex.getMessage());
            }
        });

        // Xử lý nút EDIT
        btnEdit.setOnAction(e -> {
            Book selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String id = txtId.getText().trim();
                String name = txtName.getText().trim();
                String author = txtAuthor.getText().trim();
                String qtyStr = txtQty.getText().trim();

                if (id.isEmpty() || name.isEmpty() || author.isEmpty() || qtyStr.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập đầy đủ thông tin!");
                    return;
                }

                try {
                    int qty = Integer.parseInt(qtyStr);
                    if (qty < 0) {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Số lượng phải là số không âm!");
                        return;
                    }

                    // Kiểm tra trùng ID (chỉ khi ID bị thay đổi)
                    if (!selected.getId().equalsIgnoreCase(id)) {
                        boolean isDuplicate = books.stream()
                                .anyMatch(book -> book.getId().equalsIgnoreCase(id));
                        if (isDuplicate) {
                            showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã sách đã tồn tại!");
                            return;
                        }
                    }

                    selected.setId(id);
                    selected.setName(name);
                    selected.setAuthor(author);
                    selected.setQuantity(qty);
                    table.refresh();
                    BookDAO.saveBook(selected);
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã sửa sách!");
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Số lượng phải là số nguyên hợp lệ!");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Thông báo", "Chọn dòng cần sửa!");
            }
        });

        // Xử lý nút DELETE
        btnDelete.setOnAction(e -> {
            Book selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Kiểm tra xem sách có đang được mượn không
                boolean isBeingBorrowed = borrows.stream()
                        .anyMatch(borrow -> borrow.getBookId().equals(selected.getId())
                                && borrow.getStatus().equalsIgnoreCase("Đang mượn"));

                if (isBeingBorrowed) {
                    showAlert(Alert.AlertType.WARNING, "Không thể xóa",
                            "Sách này đang được mượn, không thể xóa!");
                    return;
                }

                books.remove(selected);
                BookDAO.deleteBook(selected.getId());
                clearInputs(txtId, txtName, txtAuthor, txtQty);
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa sách!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Thông báo", "Chọn dòng cần xóa!");
            }
        });

        // Xử lý nút EXPORT
        btnExport.setOnAction(e -> exportToExcel(stage, "Books"));

        // Listener khi chọn dòng trong bảng
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtId.setText(newSel.getId());
                txtName.setText(newSel.getName());
                txtAuthor.setText(newSel.getAuthor());
                txtQty.setText(String.valueOf(newSel.getQuantity()));
            } else {
                clearInputs(txtId, txtName, txtAuthor, txtQty);
            }
        });

        VBox bottom = new VBox(inputs, buttons);
        BorderPane pane = new BorderPane();
        pane.setCenter(table);
        pane.setBottom(bottom);
        return pane;
    }

    // ==================== TAB QUẢN LÝ ĐỘC GIẢ ====================
    private BorderPane createReaderTab(Stage stage) {
        BorderPane mainPane = new BorderPane();

        // SplitPane để chia màn hình làm 2 phần
        SplitPane splitPane = new SplitPane();

        // === PHẦN TRÁI: BẢNG ĐỘC GIẢ ===
        VBox leftPane = new VBox(10);

        TableView<Reader> table = new TableView<>(readers);

        TableColumn<Reader, String> colId = new TableColumn<>("ID Độc giả");
        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colId.setPrefWidth(100);

        TableColumn<Reader, String> colName = new TableColumn<>("Tên Độc giả");
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        colName.setPrefWidth(200);

        TableColumn<Reader, String> colSDT = new TableColumn<>("SĐT");
        colSDT.setCellValueFactory(data -> data.getValue().sdtProperty());
        colSDT.setPrefWidth(120);

        table.getColumns().addAll(colId, colName, colSDT);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        leftPane.getChildren().add(table);
        VBox.setVgrow(table, Priority.ALWAYS);

        // === PHẦN PHẢI: LỊCH SỬ MƯỢN ===
        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(0, 0, 0, 10));

        // Thông tin độc giả
        Label lblReaderInfo = new Label("Chọn độc giả để xem lịch sử mượn sách");
        lblReaderInfo.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblReaderInfo.setWrapText(true);
        lblReaderInfo.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 10; -fx-border-color: #2196F3; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Bảng lịch sử mượn
        Label lblHistoryTitle = new Label("Lịch sử mượn sách");
        lblHistoryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        TableView<Borrow> historyTable = new TableView<>();
        historyTable.setPlaceholder(new Label("Chưa có lịch sử mượn sách"));

        TableColumn<Borrow, String> colBookId = new TableColumn<>("Mã sách");
        colBookId.setCellValueFactory(data -> data.getValue().bookIdProperty());
        colBookId.setPrefWidth(80);

        TableColumn<Borrow, String> colBookName = new TableColumn<>("Tên sách");
        colBookName.setCellFactory(col -> new TableCell<Borrow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Borrow borrow = getTableRow().getItem();
                    Book book = books.stream()
                            .filter(b -> b.getId().equals(borrow.getBookId()))
                            .findFirst().orElse(null);
                    setText(book != null ? book.getName() : borrow.getBookId());
                }
            }
        });
        colBookName.setPrefWidth(150);

        TableColumn<Borrow, String> colBorrowDate = new TableColumn<>("Ngày mượn");
        colBorrowDate.setCellValueFactory(data -> data.getValue().borrowDateProperty());
        colBorrowDate.setPrefWidth(100);

        TableColumn<Borrow, String> colReturnDate = new TableColumn<>("Ngày trả");
        colReturnDate.setCellValueFactory(data -> data.getValue().returnDateProperty());
        colReturnDate.setPrefWidth(100);

        TableColumn<Borrow, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());
        colStatus.setCellFactory(col -> new TableCell<Borrow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equalsIgnoreCase("Đang mượn")) {
                        setStyle("-fx-background-color: #FFF3CD; -fx-text-fill: #856404; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: #D4EDDA; -fx-text-fill: #155724; -fx-font-weight: bold;");
                    }
                }
            }
        });
        colStatus.setPrefWidth(100);

        historyTable.getColumns().addAll(colBookId, colBookName, colBorrowDate, colReturnDate, colStatus);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Thống kê
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        statsBox.setStyle("-fx-background-color: #F5F5F5; -fx-padding: 10; -fx-border-color: #DDD; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label lblTotalBorrowed = new Label("Tổng số lần mượn: 0");
        lblTotalBorrowed.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        lblTotalBorrowed.setTextFill(Color.web("#2196F3"));

        Label lblCurrentBorrowing = new Label("Đang mượn: 0");
        lblCurrentBorrowing.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        lblCurrentBorrowing.setTextFill(Color.web("#FF9800"));

        statsBox.getChildren().addAll(lblTotalBorrowed, lblCurrentBorrowing);

        rightPane.getChildren().addAll(lblReaderInfo, lblHistoryTitle, historyTable, statsBox);
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.4);

        // === INPUT PANEL (Dưới cùng) ===
        TextField txtId = new TextField();
        txtId.setPromptText("ID");
        TextField txtName = new TextField();
        txtName.setPromptText("Tên");
        TextField txtSDT = new TextField();
        txtSDT.setPromptText("SĐT");

        HBox inputs = new HBox(10, txtId, txtName, txtSDT);
        inputs.setPadding(new Insets(10));

        Button btnAddReader = new Button("ADD");
        Button btnEdit = new Button("FIX");
        Button btnDelete = new Button("DELETE");
        Button btnExport = new Button("EXPORT");

        styleButton(btnAddReader, "#28a745");
        styleButton(btnEdit, "#ffc107");
        styleButton(btnDelete, "#dc3545");
        styleButton(btnExport, "#007bff");

        HBox buttons = new HBox(10, btnAddReader, btnEdit, btnDelete, btnExport);
        buttons.setPadding(new Insets(10));

        // === LISTENER KHI CHỌN ĐỘC GIẢ ===
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                // Cập nhật input fields
                txtId.setText(newSel.getId());
                txtName.setText(newSel.getName());
                txtSDT.setText(newSel.getSdt());

                // Hiển thị thông tin độc giả
                lblReaderInfo.setText(String.format("📋 ID: %s | 👤 Tên: %s | 📞 SĐT: %s",
                        newSel.getId(), newSel.getName(), newSel.getSdt()));

                // Lọc lịch sử mượn của độc giả này
                ObservableList<Borrow> readerBorrows = borrows.filtered(
                        borrow -> borrow.getReaderId().equals(newSel.getId())
                );
                historyTable.setItems(readerBorrows);

                // Cập nhật thống kê
                long totalBorrowed = readerBorrows.size();
                long currentBorrowing = readerBorrows.stream()
                        .filter(b -> b.getStatus().equalsIgnoreCase("Đang mượn"))
                        .count();

                lblTotalBorrowed.setText("📚 Tổng số lần mượn: " + totalBorrowed);
                lblCurrentBorrowing.setText("📖 Đang mượn: " + currentBorrowing);
            } else {
                clearInputs(txtId, txtName, txtSDT);
                lblReaderInfo.setText("Chọn độc giả để xem lịch sử mượn sách");
                historyTable.getItems().clear();
                lblTotalBorrowed.setText("📚 Tổng số lần mượn: 0");
                lblCurrentBorrowing.setText("📖 Đang mượn: 0");
            }
        });

        // === XỬ LÝ CÁC NÚT ===
        btnAddReader.setOnAction(e -> {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String sdt = txtSDT.getText().trim();

            if (id.isEmpty() || name.isEmpty() || sdt.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ ID, Tên và SĐT.");
                return;
            }

            if (readers.stream().anyMatch(r -> r.getId().equalsIgnoreCase(id))) {
                showAlert(Alert.AlertType.ERROR, "Trùng ID", "ID Độc giả này đã tồn tại!");
                return;
            }

            if (!sdt.matches("\\d{10,11}")) {
                showAlert(Alert.AlertType.ERROR, "SĐT không hợp lệ", "SĐT phải có 10 hoặc 11 chữ số.");
                return;
            }

            try {
                Reader newReader = new Reader(id, name, sdt);
                ReaderDAO.saveReader(newReader);
                readers.add(newReader);
                table.refresh();
                clearInputs(txtId, txtName, txtSDT);
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm độc giả mới!");
            } catch (Exception dbEx) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể thêm độc giả: " + dbEx.getMessage());
                dbEx.printStackTrace();
            }
        });

        btnEdit.setOnAction(e -> {
            Reader selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String id = txtId.getText().trim();
                String name = txtName.getText().trim();
                String sdt = txtSDT.getText().trim();

                if (id.isEmpty() || name.isEmpty() || sdt.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập đầy đủ thông tin!");
                    return;
                }

                if (!selected.getId().equalsIgnoreCase(id)) {
                    boolean isDuplicate = readers.stream()
                            .anyMatch(r -> r.getId().equalsIgnoreCase(id));
                    if (isDuplicate) {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "ID Độc giả đã tồn tại!");
                        return;
                    }
                }

                if (!sdt.matches("\\d{10,11}")) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "SĐT phải có 10-11 chữ số!");
                    return;
                }

                selected.setId(id);
                selected.setName(name);
                selected.setSdt(sdt);
                table.refresh();
                ReaderDAO.saveReader(selected);
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã sửa độc giả!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Thông báo", "Chọn dòng cần sửa!");
            }
        });

        btnDelete.setOnAction(e -> {
            Reader selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                boolean isBorrowing = borrows.stream()
                        .anyMatch(borrow -> borrow.getReaderId().equals(selected.getId())
                                && borrow.getStatus().equalsIgnoreCase("Đang mượn"));

                if (isBorrowing) {
                    showAlert(Alert.AlertType.WARNING, "Không thể xóa", "Độc giả này đang mượn sách, không thể xóa!");
                    return;
                }

                readers.remove(selected);
                ReaderDAO.deleteReader(selected.getId());
                clearInputs(txtId, txtName, txtSDT);
                historyTable.getItems().clear();
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa độc giả!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Thông báo", "Chọn dòng cần xóa!");
            }
        });

        btnExport.setOnAction(e -> exportToExcel(stage, "Readers"));

        VBox bottom = new VBox(inputs, buttons);
        mainPane.setCenter(splitPane);
        mainPane.setBottom(bottom);
        return mainPane;
    }

    // ==================== TAB MƯỢN - TRẢ SÁCH ====================
    private BorderPane createBorrowTab(Stage stage) {
        TableView<Borrow> table = new TableView<>(borrows);

        // Các cột
        TableColumn<Borrow, String> colBookId = new TableColumn<>("Mã sách");
        colBookId.setCellValueFactory(data -> data.getValue().bookIdProperty());

        TableColumn<Borrow, String> colReaderId = new TableColumn<>("Mã sinh viên");
        colReaderId.setCellValueFactory(data -> data.getValue().readerIdProperty());

        TableColumn<Borrow, String> colBorrowDate = new TableColumn<>("Ngày mượn");
        colBorrowDate.setCellValueFactory(data -> data.getValue().borrowDateProperty());

        TableColumn<Borrow, String> colReturnDate = new TableColumn<>("Ngày trả");
        colReturnDate.setCellValueFactory(data -> data.getValue().returnDateProperty());

        TableColumn<Borrow, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());

        table.getColumns().addAll(colBookId, colReaderId, colBorrowDate, colReturnDate, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ComboBox và DatePicker (cho giao diện cũ, nhưng không dùng nữa)
        ComboBox<Book> cbBook = new ComboBox<>(books);
        cbBook.setCellFactory(new Callback<ListView<Book>, ListCell<Book>>() {
            @Override
            public ListCell<Book> call(ListView<Book> param) {
                return new ListCell<Book>() {
                    @Override
                    protected void updateItem(Book item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName() + " (" + item.getId() + ")");
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        cbBook.setPromptText("Chọn sách");

        ComboBox<Reader> cbReader = new ComboBox<>(readers);
        cbReader.setCellFactory(new Callback<ListView<Reader>, ListCell<Reader>>() {
            @Override
            public ListCell<Reader> call(ListView<Reader> param) {
                return new ListCell<Reader>() {
                    @Override
                    protected void updateItem(Reader item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName() + " (" + item.getId() + ")");
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        cbReader.setPromptText("Chọn độc giả");

        DatePicker dpBorrowDate = new DatePicker();
        dpBorrowDate.setValue(LocalDate.now());
        DatePicker dpReturnDate = new DatePicker();
        dpReturnDate.setValue(LocalDate.now().plusDays(7));

        HBox inputs = new HBox(10, cbBook, cbReader, dpBorrowDate, dpReturnDate);
        inputs.setPadding(new Insets(10));

        // Buttons
        Button btnBorrow = new Button("MƯỢN");
        Button btnReturn = new Button("TRẢ");
        Button btnDelete = new Button("DELETE");
        Button btnExport = new Button("EXPORT");

        // ==================== PHẦN BỊ THIẾU ĐÃ ĐƯỢC THÊM VÀO ====================
        styleButton(btnBorrow, "#17a2b8");     // Cyan
        styleButton(btnReturn, "#28a745");     // Vert
        styleButton(btnDelete, "#dc3545");     // Rouge
        styleButton(btnExport, "#007bff");     // Bleu
        // ==================== KẾT THÚC PHẦN THÊM VÀO ====================

        HBox buttons = new HBox(10, btnBorrow, btnReturn, btnDelete, btnExport);
        buttons.setPadding(new Insets(10));

        // Xử lý nút MƯỢN - Mở form mượn sách
        btnBorrow.setOnAction(e -> {
            try {
                Stage borrowStage = new Stage();
                borrowStage.setTitle("Mượn Sách");
                borrowStage.initModality(Modality.APPLICATION_MODAL);
                borrowStage.initOwner(stage);

                BorrowReturnForm borrowForm = new BorrowReturnForm("BORROW", books, readers, borrows);

                final TableView<Borrow> finalTable = table;
                borrowForm.setRefreshCallback(() -> {
                    try {
                        books.setAll(BookDAO.getAllBooks());
                        readers.setAll(ReaderDAO.getAllReaders());
                        borrows.setAll(BorrowDAO.getAllBorrows());
                        if (finalTable != null) finalTable.refresh();
                        System.out.println("Đã làm mới dữ liệu sau khi mượn sách!");
                    } catch (Exception refreshEx) {
                        showAlert(Alert.AlertType.ERROR, "Lỗi Refresh",
                                "Không thể làm mới: " + refreshEx.getMessage());
                        refreshEx.printStackTrace();
                    }
                });

                BorderPane borrowRootPane = borrowForm.buildUI();
                if (borrowRootPane == null) {
                    throw new Exception("Không thể tạo giao diện mượn sách!");
                }

                // ===== ĐÃ CHỈNH SỬA KÍCH THƯỚC Ở ĐÂY =====
                Scene borrowScene = new Scene(borrowRootPane, 750, 450);
                borrowStage.setScene(borrowScene);
                borrowStage.showAndWait();

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi",
                        "Không thể mở cửa sổ Mượn Sách: " + ex.getMessage());
            }
        });

        // Xử lý nút TRẢ - Mở form trả sách
        btnReturn.setOnAction(e -> {
            try {
                Stage returnStage = new Stage();
                returnStage.setTitle("Trả Sách");
                returnStage.initModality(Modality.APPLICATION_MODAL);
                returnStage.initOwner(stage);

                BorrowReturnForm returnForm = new BorrowReturnForm("RETURN", books, readers, borrows);

                final TableView<Borrow> finalTable = table;
                returnForm.setRefreshCallback(() -> {
                    try {
                        books.setAll(BookDAO.getAllBooks());
                        readers.setAll(ReaderDAO.getAllReaders());
                        borrows.setAll(BorrowDAO.getAllBorrows());
                        if (finalTable != null) finalTable.refresh();
                        System.out.println("Đã làm mới dữ liệu sau khi trả sách!");
                    } catch (Exception refreshEx) {
                        showAlert(Alert.AlertType.ERROR, "Lỗi Refresh",
                                "Không thể làm mới: " + refreshEx.getMessage());
                        refreshEx.printStackTrace();
                    }
                });

                BorderPane returnRootPane = returnForm.buildUI();
                if (returnRootPane == null) {
                    throw new Exception("Không thể tạo giao diện trả sách!");
                }

                // ===== ĐÃ CHỈNH SỬA KÍCH THƯỚC Ở ĐÂY =====
                Scene returnScene = new Scene(returnRootPane, 750, 450);
                returnStage.setScene(returnScene);
                returnStage.showAndWait();

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi",
                        "Không thể mở cửa sổ Trả Sách: " + ex.getMessage());
            }
        });

        // Xử lý nút DELETE
        btnDelete.setOnAction(e -> {
            Borrow selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Hoàn trả sách nếu phiếu đang mượn
                if ("Đang mượn".equals(selected.getStatus())) {
                    books.stream().filter(b -> b.getId().equals(selected.getBookId()))
                            .findFirst().ifPresent(b -> {
                                b.setQuantity(b.getQuantity() + 1);
                                BookDAO.saveBook(b);
                            });
                }

                borrows.remove(selected);
                borrowDAO.deleteBorrow(selected.getBorrowId());
                books.setAll(BookDAO.getAllBooks());
                borrows.setAll(BorrowDAO.getAllBorrows());
                table.refresh();

                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa phiếu!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Thông báo", "Chọn dòng cần xóa!");
            }
        });

        // Xử lý nút EXPORT
        btnExport.setOnAction(e -> exportToExcel(stage, "Borrows"));

        // Listener khi chọn dòng
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cbBook.setValue(books.stream()
                        .filter(b -> b.getId().equals(newSel.getBookId()))
                        .findFirst().orElse(null));
                cbReader.setValue(readers.stream()
                        .filter(r -> r.getId().equals(newSel.getReaderId()))
                        .findFirst().orElse(null));
            } else {
                clearInputs(cbBook, cbReader, dpBorrowDate, dpReturnDate);
            }
        });

        VBox bottom = new VBox(inputs, buttons);
        BorderPane pane = new BorderPane();
        pane.setCenter(table);
        pane.setBottom(bottom);
        return pane;
    }

    // ==================== CÁC HÀM HELPER ====================

    private void clearInputs(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    private void clearInputs(ComboBox cbBook, ComboBox cbReader,
                             DatePicker dpBorrowDate, DatePicker dpReturnDate) {
        cbBook.setValue(null);
        cbReader.setValue(null);
        dpBorrowDate.setValue(LocalDate.now());
        dpReturnDate.setValue(LocalDate.now().plusDays(7));
    }

    private void exportToExcel(Stage stage, String sheetName) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn nơi lưu file Excel");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = chooser.showSaveDialog(stage);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet(sheetName);

                if ("Books".equals(sheetName)) {
                    Row header = sheet.createRow(0);
                    header.createCell(0).setCellValue("Mã sách");
                    header.createCell(1).setCellValue("Tên sách");
                    header.createCell(2).setCellValue("Tác giả");
                    header.createCell(3).setCellValue("Số lượng");

                    int rowNum = 1;
                    for (Book b : books) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(b.getId());
                        row.createCell(1).setCellValue(b.getName());
                        row.createCell(2).setCellValue(b.getAuthor());
                        row.createCell(3).setCellValue(b.getQuantity());
                    }
                    for (int i = 0; i < 4; i++) sheet.autoSizeColumn(i);

                } else if ("Readers".equals(sheetName)) {
                    Row header = sheet.createRow(0);
                    header.createCell(0).setCellValue("ID Độc giả");
                    header.createCell(1).setCellValue("Tên Độc giả");
                    header.createCell(2).setCellValue("SĐT");

                    int rowNum = 1;
                    for (Reader r : readers) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(r.getId());
                        row.createCell(1).setCellValue(r.getName());
                        row.createCell(2).setCellValue(r.getSdt());
                    }
                    for (int i = 0;i < 3; i++) sheet.autoSizeColumn(i);

                } else if ("Borrows".equals(sheetName)) {
                    Row header = sheet.createRow(0);
                    header.createCell(0).setCellValue("Mã phiếu");
                    header.createCell(1).setCellValue("Mã sách");
                    header.createCell(2).setCellValue("ID Độc giả");
                    header.createCell(3).setCellValue("Ngày mượn");
                    header.createCell(4).setCellValue("Ngày trả");
                    header.createCell(5).setCellValue("Trạng thái");

                    int rowNum = 1;
                    for (Borrow b : borrows) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(b.getBorrowId());
                        row.createCell(1).setCellValue(b.getBookId());
                        row.createCell(2).setCellValue(b.getReaderId());
                        row.createCell(3).setCellValue(b.getBorrowDate());
                        row.createCell(4).setCellValue(b.getReturnDate());
                        row.createCell(5).setCellValue(b.getStatus());
                    }
                    for (int i = 0; i < 6; i++) sheet.autoSizeColumn(i);
                }

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xuất file Excel!");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi",
                        "Không thể tạo file Excel: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // Hàm showAlert với 2 tham số (dùng cho code cũ)
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Hàm showAlert với 3 tham số (dùng cho code mới)
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}