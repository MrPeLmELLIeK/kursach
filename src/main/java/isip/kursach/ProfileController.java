package isip.kursach;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.ResultSet;

public class ProfileController {

    @FXML
    private TextField loginField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField numberPhoneField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField fullNameField;

    @FXML
    private DatePicker birthdayPicker;

    private static final String SCHEMA_NAME = "user01";

    private String login; // Логин пользователя, данные которого редактируются

    @FXML
    public void initialize() throws SQLException {
        // Получаем логин текущего пользователя из сессии
        this.login = Session.getLogin();

        // Загружаем данные пользователя
        if (this.login != null && !this.login.isEmpty()) { // Проверяем, что логин был установлен
            loadProfileData(this.login);
        } else {
            ErrorDialog.showError("Ошибка", "Пользователь не авторизован.");
        }
    }

    // Метод для установки данных пользователя
    public void setProfileData(String login, String email, String password, String numberPhone, String address, String fullName, LocalDate birthday) {
        this.login = login;
        loginField.setText(login);
        emailField.setText(email);
        passwordField.setText(password);
        numberPhoneField.setText(numberPhone);
        addressField.setText(address);
        fullNameField.setText(fullName);
        birthdayPicker.setValue(birthday);
    }

    // Метод для обновления данных пользователя
    @FXML
    public void updateProfile() {
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
            ErrorDialog.showError("Ошибка ввода", "Заполните все поля.");
            return;
        }

        // Формируем SQL-запрос
        String query = "UPDATE " + SCHEMA_NAME + ".users SET login=?, email=?, password=?, number=?, address=?, full_name=?, birthday=? WHERE login=?";

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://89.109.54.20:6543/user01", "user01", "83328");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, login);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, numberPhone);
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, fullName);
            preparedStatement.setDate(7, java.sql.Date.valueOf(birthday));
            preparedStatement.setString(8, this.login); // Используем текущий логин для поиска

            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println("Обновлено строк: " + rowsUpdated);

            // Оповещение об успешном обновлении
            showSuccess("Успех", "Данные успешно обновлены!");

            // Закрываем окно профиля
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении данных: " + e.getMessage());

            // Оповещение об ошибке
            ErrorDialog.showError("Ошибка", "Ошибка при обновлении данных: ");
        }
    }

    // Метод для отображения успешного сообщения
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Метод для загрузки данных пользователя по логину
    public void loadProfileData(String login) {
        String query = "SELECT * FROM " + SCHEMA_NAME + ".users WHERE login = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://89.109.54.20:6543/user01", "user01", "83328");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String numberPhone = resultSet.getString("number");
                String address = resultSet.getString("address");
                String fullName = resultSet.getString("full_name");
                LocalDate birthday = resultSet.getDate("birthday").toLocalDate();

                // Устанавливаем данные в поля
                setProfileData(login, email, password, numberPhone, address, fullName, birthday);
            } else {
                ErrorDialog.showError("Ошибка", "Пользователь с логином " + login + " не найден.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при загрузке данных пользователя: " + e.getMessage());
            ErrorDialog.showError("Ошибка", "Ошибка при загрузке данных пользователя: " + e.getMessage());
        }
    }

    public void setLogin(String login) {
        this.login = login;
        loadProfileData(login); // Загружаем данные пользователя
    }
}