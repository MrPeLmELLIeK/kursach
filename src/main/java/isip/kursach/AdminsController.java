package isip.kursach;

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
import java.sql.ResultSet;

public class AdminsController {
    private DatabaseManager primaryDatabaseManager;

    @FXML
    private Button ProductsTab;

    @FXML
    private Button usersTable;

    @FXML
    private Button tableManufacturers;

    @FXML
    private Button CategotyTable;

    @FXML
    private TableView<Admins> AdminsTable;

    @FXML
    private TableColumn<Admins, Integer> id;

    @FXML
    private TableColumn<Admins, String> login;

    @FXML
    private TableColumn<Admins, Integer> code;

    @FXML
    private TableColumn<Admins, String> password;

    @FXML
    private Button AdminsTab;

    @FXML
    private Button ordersTab;


    @FXML
    public void initialize() {
        primaryDatabaseManager = new DatabaseManager(); // Инициализация DatabaseManager
        primaryDatabaseManager.connect();

        // Настройка колонок таблицы
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        login.setCellValueFactory(new PropertyValueFactory<>("login"));
        code.setCellValueFactory(new PropertyValueFactory<>("code"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));


    }

    @FXML
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

    @FXML
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

    @FXML
    public void TableCategory(ActionEvent event) throws IOException {
        Stage stage = (Stage) CategotyTable.getScene().getWindow();
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

    @FXML
    public void AdminsTable(ActionEvent event) {
        ErrorDialog.showError("Предупреждение!", "Вы и так находитесь в данном окне.");
    }

    public void UpdateTable(ActionEvent event) {
        try {
            // Получение данных из базы данных
            ObservableList<Admins> data = primaryDatabaseManager.fetchAdminsData();

            // Установка данных в таблицу
            AdminsTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Ошибка", "Не удалось загрузить данные из базы данных.");
        }
    }
}