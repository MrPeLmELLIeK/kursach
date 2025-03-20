package isip.kursach;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;


public class CategoryControl {
    private DatabaseManager primaryDatabaseManager;
    private Connection connection;
    private static final String SCHEMA_NAME = "user01";

    @FXML
    private TableView<Category> CategoryTable;

    @FXML
    private TableColumn<Category, Integer> id;

    @FXML
    private TableColumn<Category, String> name_category;

    @FXML
    private Button update;

    @FXML
    private Button CategotyTab;

    @FXML
    private Button tableManufacturers;

    @FXML
    private Button AdminsTab;

    @FXML
    private Button ProductsTab;

    @FXML
    private TextField idFields;

    @FXML
    private TextField nameField;
    @FXML
    private Button usersTable;

    @FXML
    private Button ordersTab;

    @FXML
    public void initialize() throws SQLException {
        primaryDatabaseManager = new DatabaseManager(); // Инициализация DatabaseManager
        primaryDatabaseManager.connect();
        connection = DriverManager.getConnection("jdbc:postgresql://89.109.54.20:6543/user01", "user01", "83328");
        // Настройка колонок таблицы
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name_category.setCellValueFactory(new PropertyValueFactory<>("name_category"));

    }

    public void tableManufacturers(ActionEvent event) throws IOException {
        Stage stage = (Stage) tableManufacturers.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("manufacturers-table.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle("Окно пользователя");
        stage.setScene(new Scene(root1));
        WindowUtil.setCloseHandler(stage, primaryDatabaseManager);
        stage.show();

    }

    public void TableCategory(ActionEvent event) {
        ErrorDialog.showError("Предупреждение!", "Вы и так находитесь в данном окне.");
    }

    public void ProductsTab(ActionEvent event) throws IOException {
        Stage stage = (Stage) ProductsTab.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("products-tab.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle("Окно пользователя");
        stage.setScene(new Scene(root1));
        WindowUtil.setCloseHandler(stage, primaryDatabaseManager);
        stage.show();
    }

    public void UpdateTable(ActionEvent event) {
        try {
            // Получение данных из базы данных
            ObservableList<Category> data = primaryDatabaseManager.fetchCategoryData();

            // Установка данных в таблицу
            CategoryTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Ошибка", "Не удалось загрузить данные из базы данных.");
        }
    }

    public void AdminsTable(ActionEvent event) throws IOException {
        Stage stage = (Stage) AdminsTab.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("admins-table.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle("Окно пользователя");
        stage.setScene(new Scene(root1));
        WindowUtil.setCloseHandler(stage, primaryDatabaseManager);
        stage.show();
    }
    public void UsersTab(ActionEvent event) throws IOException {
        Stage stage = (Stage) usersTable.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("user-table.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle("Окно пользователя");
        stage.setScene(new Scene(root1));
        WindowUtil.setCloseHandler(stage, primaryDatabaseManager);
        stage.show();
    }

    // Метод для вставки данных в таблицу
    public void insertData(ActionEvent event) {
        if (idFields.getText().isEmpty() || nameField.getText().isEmpty()) {
            Platform.runLater(() -> ErrorDialog.showError("Ошибка ввода", "Поля ID и название категории не могут быть пустыми."));
            return;
        }
        int id = Integer.parseInt(idFields.getText());
        String name_category = nameField.getText();
        String query = String.format("INSERT INTO %s.category (id, name_category) VALUES (%d, '%s')", SCHEMA_NAME, id, name_category);

        try (var preparedStatement = connection.prepareStatement(query)) {
            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println("Добавлено строк: " + rowsInserted);
            UpdateTable(event);
        } catch (SQLException e) {
            System.out.println("Ошибка при вставке данных: " + e.getMessage());
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при вставке данных: ", e.getMessage()));
        }
    }

    // Метод для обновления данных в таблице
    public void updateData(ActionEvent event) {
        if (idFields.getText().isEmpty() || nameField.getText().isEmpty()) {
            Platform.runLater(() -> ErrorDialog.showError("Ошибка ввода", "Поля ID и название категории не могут быть пустыми."));
            return;
        }
        int id = Integer.parseInt(idFields.getText());
        String name_category = nameField.getText();
        String query = String.format("UPDATE %s.category SET name_category='%s' WHERE id=%d", SCHEMA_NAME, name_category, id);

        try (var preparedStatement = connection.prepareStatement(query)) {
            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println("Обновлено строк: " + rowsUpdated);
            UpdateTable(event);
        } catch (SQLException e) {
            System.out.println("Ошибка при изменении данных: " + e.getMessage());
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при изменении данных: ", e.getMessage()));
        }
    }

    // Метод для удаления данных из таблицы
    public void deleteData(ActionEvent event) {
        // Проверка на пустое поле ID
        if (idFields.getText().isEmpty()) {
            Platform.runLater(() -> ErrorDialog.showError("Ошибка ввода", "Поле ID не может быть пустым."));
            return;
        }

        // Проверка на корректный формат числа
        int id;
        try {
            id = Integer.parseInt(idFields.getText());
        } catch (NumberFormatException e) {
            Platform.runLater(() -> ErrorDialog.showError("Ошибка ввода", "Поле ID должно содержать число."));
            return;
        }

        // Запрос подтверждения удаления
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Подтверждение удаления");
        confirmationAlert.setHeaderText("Вы уверены, что хотите удалить эту запись?");
        confirmationAlert.setContentText("ID: " + id);

        // Ожидание ответа пользователя
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        // Если пользователь подтвердил удаление
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String query = String.format("DELETE FROM %s.category WHERE id=%d", SCHEMA_NAME, id);

            try (var preparedStatement = connection.prepareStatement(query)) {
                int rowsDeleted = preparedStatement.executeUpdate();
                System.out.println("Удалено строк: " + rowsDeleted);

                // Очистка полей после успешного удаления
                idFields.clear();
                nameField.clear();

                // Обновление таблицы
                UpdateTable(event);
            } catch (SQLException e) {
                System.out.println("Ошибка при удалении данных: " + e.getMessage());
                Platform.runLater(() -> ErrorDialog.showError("Ошибка при удалении данных: ", e.getMessage()));
            }
        } else {
            // Пользователь отменил удаление
            System.out.println("Удаление отменено.");
        }
    }



    public void OrdersTab(ActionEvent event) throws IOException {
        Stage stage = (Stage) ordersTab.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("orders-table.fxml"));
        Parent root1 = fxmlLoader.load();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle("Окно заказов");
        stage.setScene(new Scene(root1));
        WindowUtil.setCloseHandler(stage, primaryDatabaseManager);
        stage.show();
    }
}
