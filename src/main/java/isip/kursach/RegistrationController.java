package isip.kursach;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class RegistrationController {

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

    @FXML
    public void registerUser() {
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

        if (!isValidEmail(email)) {
            ErrorDialog.showError("Ошибка ввода", "Некорректный email.");
            return;
        }

        // Проверка номера телефона на 11 цифр
        if (!isValidPhoneNumber(numberPhone)) {
            ErrorDialog.showError("Ошибка ввода", "Номер телефона должен состоять из 11 цифр.");
            return;
        }

        // Формируем SQL-запрос
        String query = "INSERT INTO " + SCHEMA_NAME + ".users (login, email, password, number, address, full_name, birthday) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://89.109.54.20:6543/user01", "user01", "83328");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

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
            showSuccess("Успех", "Данные успешно добавлены!");

            // Закрываем окно регистрации
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении данных: " + e.getMessage());

            // Оповещение об ошибке
            ErrorDialog.showError( "Ошибка","Ошибка при добавлении данных: ");
        }
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{11}");
    }
}