package isip.kursach;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HelloController {
    private Stage primaryStage;
    private DatabaseManager primaryDatabaseManager;

    @FXML
    private TextField CodeAdmin;

    @FXML
    private TextField LoginUser;
    @FXML
    private TextField EmailUser;
    @FXML
    private PasswordField PasswordUser;


    @FXML
    private PasswordField PasswordAdmins;

    @FXML
    private Button Login;

    @FXML
    private Button LoginAdmin;

    @FXML
    private TextField LogAdminText;

    private String login;
    private String email;
    private String password;

    // Геттеры для данных
    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @FXML
    private Button AdminLog;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setPrimaryDatabaseManager(DatabaseManager primaryDatabaseManager) {
        this.primaryDatabaseManager = primaryDatabaseManager;
    }

    @FXML
    public void initialize() {
        primaryDatabaseManager = new DatabaseManager(); // Инициализация DatabaseManager
        primaryDatabaseManager.connect();
    }

    @FXML
    public void Login(ActionEvent event) throws IOException {
        String loginUser = LoginUser.getText();
        String emailUser = EmailUser.getText();
        String passwordUser = PasswordUser.getText();

        // Проверка на пустые поля
        if (loginUser.isEmpty() || emailUser.isEmpty() || passwordUser.isEmpty()) {
            ErrorDialog.showError("Ошибка!", "Введите логин, email и пароль");
            return; // Выход из метода, если поля пустые
        }

        if (validateCredentialsUsers(loginUser, emailUser, passwordUser)) {
            // Сохраняем логин в Session
            Session.setLogin(loginUser);

            // Закрываем текущее окно
            Stage stage = (Stage) Login.getScene().getWindow();
            stage.close();

            // Загрузка user-menu.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("user-menu.fxml"));
            Parent root1 = fxmlLoader.load();

            // Открытие нового окна
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Окно пользователя");
            stage.setScene(new Scene(root1));
            WindowUtil.setCloseHandler(stage, primaryDatabaseManager);
            stage.show();
        } else {
            ErrorDialog.showError("Ошибка!", "Неверный логин, email или пароль.");
        }
    }

    @FXML
    public void LoginAdmin(ActionEvent event) throws IOException {
        Stage stage = (Stage) LoginAdmin.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("admin-log-in.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle("Окно пользователя");
        stage.setScene(new Scene(root1));
        WindowUtil.setCloseHandler(stage, primaryDatabaseManager);
        stage.show();
    }

    @FXML
    private void AdminLog(ActionEvent event) throws IOException {
        // Получаем данные из полей
        String loginText = LogAdminText.getText();
        String codeText = CodeAdmin.getText();
        String password = PasswordAdmins.getText();

        // Проверка на пустые поля
        if (loginText.isEmpty() || codeText.isEmpty() || password.isEmpty()) {
            ErrorDialog.showError("Ошибка!", "Введите логин, код или пароль");
            return; // Выход из метода, если поля пустые
        }

        // Проверка данных в базе данных
        if (validateCredentialsAdmins(loginText, codeText, password)) {
            Stage stage = (Stage) AdminLog.getScene().getWindow();
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
        } else {
            ErrorDialog.showError("Ошибка!", "Неверный логин, код или пароль.");
        }
    }

    private boolean validateCredentialsAdmins(String login, String code, String password) {
        String sql = "SELECT * FROM admins WHERE login = ? AND code = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://89.109.54.20:6543/user01", "user01", "83328");
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            statement.setInt(2, Integer.parseInt(code));
            statement.setString(3, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Вернуть true, если данные найдены
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Ошибка", "Не удалось подключиться к базе данных.");
            return false;
        }
    }
    private boolean validateCredentialsUsers(String login, String email, String password) {
        String sql = "SELECT * FROM users WHERE login = ? AND email = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://89.109.54.20:6543/user01", "user01", "83328");
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            statement.setString(2, email);
            statement.setString(3, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Вернуть true, если данные найдены
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Ошибка", "Не удалось подключиться к базе данных.");
            return false;
        }
    }

    @FXML
    public void registration(ActionEvent event) throws IOException {
            Stage stage = (Stage) Login.getScene().getWindow();
            stage.close();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("registration.fxml"));
            Parent root1 = fxmlLoader.load();
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Окно пользователя");
            stage.setScene(new Scene(root1));
            WindowUtil.setCloseHandler(stage, primaryDatabaseManager);
            stage.show();
    }

}