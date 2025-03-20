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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class UsersTableController {
    private DatabaseManager primaryDatabaseManager;
    private Connection connection;
    private static final String SCHEMA_NAME = "user01";

    @FXML
    private TableView <Users> UsersTable;
    @FXML
    private TableColumn <Users, Integer> id;
    @FXML
    private TableColumn <Users, String> login;
    @FXML
    private TableColumn <Users, String> email;
    @FXML
    private TableColumn <Users, String> password;
    @FXML
    private TableColumn <Users, String> number_phone;
    @FXML
    private TableColumn <Users, String> address;
    @FXML
    private TableColumn <Users, String> full_name;
    @FXML
    private TableColumn <Users, LocalDate> birthday;

    @FXML
    private Button AdminsTab;
    @FXML
    private Button ProductsTab;
    @FXML
    private Button CategotyTab;
    @FXML
    private Button tableManufacturers;
    @FXML
    private Button usersTable;

    @FXML
    private TextField idFields;
    @FXML
    private TextField loginField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField numberPhoneField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField fullNameField;
    @FXML
    private DatePicker birthdayPicker;

    @FXML
    private Button ordersTab;

    @FXML
    public void initialize() throws SQLException {
        primaryDatabaseManager = new DatabaseManager(); // Инициализация DatabaseManager
        primaryDatabaseManager.connect();
        connection = DriverManager.getConnection("jdbc:postgresql://89.109.54.20:6543/user01", "user01", "83328");

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        login.setCellValueFactory(new PropertyValueFactory<>("login"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));
        number_phone.setCellValueFactory(new PropertyValueFactory<>("number_phone"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        full_name.setCellValueFactory(new PropertyValueFactory<>("full_name"));
        birthday.setCellValueFactory(new PropertyValueFactory<>("birthday"));
    }


    public void UsersTab(ActionEvent event) throws IOException {
        ErrorDialog.showError("Предупреждение!", "Вы и так находитесь в данном окне.");
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
    public void TableCategory(ActionEvent event) throws IOException {
        Stage stage = (Stage) CategotyTab.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("category-tab.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle("Окно пользователя");
        stage.setScene(new Scene(root1));
        WindowUtil.setCloseHandler(stage, primaryDatabaseManager);
        stage.show();
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

    public void UpdateTable(ActionEvent event) {
        try {
            // Получение данных из базы данных
            ObservableList<Users> data = primaryDatabaseManager.fetchUsersData();

            // Установка данных в таблицу
            UsersTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Ошибка", "Не удалось загрузить данные из базы данных.");
        }
    }


    public void insertData(ActionEvent event) {
        // Получаем данные из текстовых полей
        String login = loginField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String numberPhone = numberPhoneField.getText();
        String address = addressField.getText();
        String fullName = fullNameField.getText();
        LocalDate birthday = birthdayPicker.getValue();

        // Проверка на пустые поля
        if (login.isEmpty() || email.isEmpty() || password.isEmpty() || numberPhone.isEmpty() || address.isEmpty() || fullName.isEmpty() || birthday == null) {
            Platform.runLater(() -> ErrorDialog.showError("Ошибка ввода", "Заполните все поля."));
            return;
        }

        // Формируем SQL-запрос
        String query = "INSERT INTO " + SCHEMA_NAME + ".users (login, email, password, number, address, full_name, birthday) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, numberPhone);
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, fullName);
            preparedStatement.setDate(7, java.sql.Date.valueOf(birthday));

            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println("Добавлено строк: " + rowsInserted);

            // Оповещение об успешном добавлении
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Успех");
                alert.setHeaderText(null);
                alert.setContentText("Данные успешно добавлены!");
                alert.showAndWait();
            });

            // Обновление таблицы
            UpdateTable(event);
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении данных: " + e.getMessage());

            // Оповещение об ошибке
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при добавлении данных: ", e.getMessage()));
        }
    }

    public void updateData(ActionEvent event) {
        // Получаем данные из текстовых полей
        int id = Integer.parseInt(idFields.getText());
        String login = loginField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String numberPhone = numberPhoneField.getText();
        String address = addressField.getText();
        String fullName = fullNameField.getText();
        LocalDate birthday = birthdayPicker.getValue();

        // Проверка на пустые поля
        if (login.isEmpty() || email.isEmpty() || password.isEmpty() || numberPhone.isEmpty() || address.isEmpty() || fullName.isEmpty() || birthday == null) {
            Platform.runLater(() -> ErrorDialog.showError("Ошибка ввода", "Заполните все поля."));
            return;
        }

        // Формируем SQL-запрос
        String query = "UPDATE " + SCHEMA_NAME + ".users SET login=?, email=?, password=?, number=?, address=?, full_name=?, birthday=? WHERE id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, numberPhone);
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, fullName);
            preparedStatement.setDate(7, java.sql.Date.valueOf(birthday));
            preparedStatement.setInt(8, id);

            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println("Обновлено строк: " + rowsUpdated);

            // Оповещение об успешном обновлении
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Успех");
                alert.setHeaderText(null);
                alert.setContentText("Данные успешно обновлены!");
                alert.showAndWait();
            });

            // Обновление таблицы
            UpdateTable(event);
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении данных: " + e.getMessage());

            // Оповещение об ошибке
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при обновлении данных: ", e.getMessage()));
        }
    }

    public void deleteData(ActionEvent event) {
        // Получаем ID из текстового поля
        String idText = idFields.getText();

        // Проверка на пустое значение
        if (idText.isEmpty()) {
            Platform.runLater(() -> ErrorDialog.showError("Ошибка ввода", "Введите ID пользователя."));
            return;
        }

        String query = "DELETE FROM " + SCHEMA_NAME + ".users WHERE id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int id = Integer.parseInt(idText);
            preparedStatement.setInt(1, id);

            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println("Удалено строк: " + rowsDeleted);

            // Оповещение об успешном удалении
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Успех");
                alert.setHeaderText(null);
                alert.setContentText("Данные успешно удалены!");
                alert.showAndWait();
            });

            idFields.clear();
            loginField.clear();
            emailField.clear();
            passwordField.clear();
            numberPhoneField.clear();
            addressField.clear();
            fullNameField.clear();
            birthdayPicker.setValue(null);

            // Обновление таблицы
            UpdateTable(event);
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении данных: " + e.getMessage());

            // Оповещение об ошибке
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при удалении данных: ", e.getMessage()));
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат ID: " + idText);

            // Оповещение об ошибке
            Platform.runLater(() -> ErrorDialog.showError("Ошибка ввода", "Некорректный формат ID."));
        }
    }

}
