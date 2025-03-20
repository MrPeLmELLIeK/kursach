package isip.kursach;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ManufacturersControl {

    @FXML
    private TableView<Manufacturers> ManufacturersTable;

    @FXML
    private TableColumn<Manufacturers, Integer> id;

    @FXML
    private TableColumn<Manufacturers, String> Name_manufacturers;

    @FXML
    private TableColumn<Manufacturers, String> Organization_name;

    private DatabaseManager primaryDatabaseManager;
    private Connection connection;
    private static final String SCHEMA_NAME = "user01";

    @FXML
    private TextField idFields;

    @FXML
    private TextField name_manufacturers;

    @FXML
    private TextField organization_name;

    @FXML
    private Button CategotyTable;

    @FXML
    private Button usersTable;

    @FXML
    private Button AdminsTab;

    @FXML
    private Button ProductsTab;

    @FXML
    private Button ordersTab;

    @FXML
    public void initialize() throws SQLException {
        primaryDatabaseManager = new DatabaseManager(); // Инициализация DatabaseManager
        primaryDatabaseManager.connect();
        connection = DriverManager.getConnection("jdbc:postgresql://89.109.54.20:6543/user01", "user01", "83328");
        // Настройка колонок таблицы
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        Name_manufacturers.setCellValueFactory(new PropertyValueFactory<>("Name_manufacturers"));
        Organization_name.setCellValueFactory(new PropertyValueFactory<>("Organization_name"));
    }

    public void UpdateTable(ActionEvent event) {


        try {
            // Получение данных из базы данных
            ObservableList<Manufacturers> data = primaryDatabaseManager.fetchManufacturersData();

            // Установка данных в таблицу
            ManufacturersTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Ошибка", "Не удалось загрузить данные из базы данных.");
        }

    }

    public void tableManufacturers(ActionEvent event) {
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

    public void AdminsTab(ActionEvent event) throws IOException {
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

    // Метод для вставки данных в таблицу
    public void insertData(ActionEvent event) {
        int id = Integer.parseInt(idFields.getText());
        String name_manufacturer = name_manufacturers.getText();
        String organization = organization_name.getText();
        String query = String.format("INSERT INTO %s.manufacturers (id, name_manufacturers, organization_name) VALUES (%d, '%s', '%s')", SCHEMA_NAME, id, name_manufacturer, organization);

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
    public void updateData(ActionEvent event){
        int id = Integer.parseInt(idFields.getText());
        String name_manufacturer = name_manufacturers.getText();
        String organization = organization_name.getText();
        String query = String.format("UPDATE %s.manufacturers SET name_manufacturers='%s', organization_name='%s' WHERE id=%d", SCHEMA_NAME, name_manufacturer, organization, id);

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
        int id = Integer.parseInt(idFields.getText());
        String query = String.format("DELETE FROM %s.manufacturers WHERE id=%d", SCHEMA_NAME, id);

        try (var preparedStatement = connection.prepareStatement(query)) {
            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println("Удалено строк: " + rowsDeleted);
            UpdateTable(event);
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении данных: " + e.getMessage());
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при удалении данных: ", e.getMessage()));
        }
    }

}
