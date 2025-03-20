package isip.kursach;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class UsersController {

    @FXML
    private TableView<RecommendedProducts> recommendedTable;
    @FXML
    private TableColumn<RecommendedProducts, Integer> id;
    @FXML
    private TableColumn<RecommendedProducts, String> recommendedName;
    @FXML
    private TableColumn<RecommendedProducts, String> recommendedDescription;
    @FXML
    private TableColumn<RecommendedProducts, String> recommendedRating;
    @FXML
    private TableColumn<RecommendedProducts, String> recommendedCategory;
    @FXML
    private TableColumn<RecommendedProducts, Double> recommendedPrice;



    private DatabaseManager primaryDatabaseManager;
    private Connection connection;

    @FXML
    private TextField loginField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button catalog;

 /*   private int userId; // ID пользователя
    private String login;
    private String email;
    private String password;
    private String numberPhone; // Номер телефона
    private String address; // Адрес
    private String fullName; // Полное имя
    private LocalDate birthday; */

    @FXML
    public void initialize() throws SQLException {
        primaryDatabaseManager = new DatabaseManager(); // Инициализация DatabaseManager
        primaryDatabaseManager.connect();

        recommendedName.setCellValueFactory(new PropertyValueFactory<>("name_products"));
        recommendedDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        recommendedRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        recommendedCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        recommendedPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        UpdateTable();
    }

    public void openCatalog(ActionEvent event) throws IOException {
        Stage stage = (Stage) catalog.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("catalog.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle("Окно пользователя");
        stage.setScene(new Scene(root1));
        WindowUtil.setCloseHandler(stage, primaryDatabaseManager);
        stage.show();
    }


    public void inputProfile(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("profile.fxml"));
        Parent root1 = fxmlLoader.load();

        ProfileController profileController = fxmlLoader.getController();
        profileController.setLogin(Session.getLogin());

        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle("Профиль");
        stage.setScene(new Scene(root1));
        WindowUtil.setCloseHandler(stage, primaryDatabaseManager);
        stage.show();
    }


    public void UpdateTable() {
        try {
            // Получение данных из базы данных
            ObservableList<RecommendedProducts> data = primaryDatabaseManager.fetchRecommendedProductsData();

            // Установка данных в таблицу
            recommendedTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Ошибка", "Не удалось загрузить данные из базы данных.");
        }
    }
}
