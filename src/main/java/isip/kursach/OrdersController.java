package isip.kursach;

import javafx.collections.FXCollections;
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
import java.sql.SQLException;
import java.time.LocalDate;

public class OrdersController {
    private Connection connection;
    private static final String SCHEMA_NAME = "user01";

    @FXML
    private TableView<Users> UsersTable;

    @FXML
    private TableColumn<Users, Integer> userId;

    @FXML
    private TableColumn<Users, String> userLogin;

    @FXML
    private TableView<Products> ProductsTable;

    @FXML
    private TableColumn<Products, Integer> productId;

    @FXML
    private TableColumn<Products, String> productName;

    @FXML
    private TableColumn<Users, String> userEmail;
    @FXML
    private TableColumn<Users, String> userPassword;
    @FXML
    private TableColumn<Users, String> userPhone;
    @FXML
    private TableColumn<Users, String> userAddress;
    @FXML
    private TableColumn<Users, String> userFullName;
    @FXML
    private TableColumn<Users, LocalDate> userBirthday;

    @FXML
    private TableColumn<Products, Double> productPrice;
    @FXML
    private TableColumn<Products, String> productRating;
    @FXML
    private TableColumn<Products, String> productDescription;
    @FXML
    private TableColumn<Products, Integer> productCategory;
    @FXML
    private TableColumn<Products, Integer> productManufacturer;

    @FXML
    private TableView<Orders> OrdersTable;

    @FXML
    private TableColumn<Orders, Integer> orderId;
    @FXML
    private TableColumn<Orders, String> orderUserLogin;
    @FXML
    private TableColumn<Orders, String> orderProductName;
    @FXML
    private TableColumn<Orders, String> orderTime;

    @FXML
    private Button ordersTab;

    private DatabaseManager primaryDatabaseManager;


    @FXML
    public void initialize() throws SQLException {
        primaryDatabaseManager = new DatabaseManager(); // Инициализация DatabaseManager
        primaryDatabaseManager.connect();
        connection = DriverManager.getConnection("jdbc:postgresql://89.109.54.20:6543/user01", "user01", "83328");

        // Настройка колонок таблицы пользователей
        userId.setCellValueFactory(new PropertyValueFactory<>("id"));
        userLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        userEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        userPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        userPhone.setCellValueFactory(new PropertyValueFactory<>("number_phone"));
        userAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        userFullName.setCellValueFactory(new PropertyValueFactory<>("full_name"));
        userBirthday.setCellValueFactory(new PropertyValueFactory<>("birthday"));

        // Настройка колонок таблицы товаров
        productId.setCellValueFactory(new PropertyValueFactory<>("id"));
        productName.setCellValueFactory(new PropertyValueFactory<>("name_products"));
        productPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        productRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        productDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        productCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        productManufacturer.setCellValueFactory(new PropertyValueFactory<>("manufacturers"));

        orderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderUserLogin.setCellValueFactory(new PropertyValueFactory<>("userLogin"));
        orderProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        orderTime.setCellValueFactory(new PropertyValueFactory<>("orderTime"));

        // Загрузка данных
        loadUsers();
        loadProducts();
        loadOrders();
    }

    private void loadUsers() throws SQLException {
        ObservableList<Users> users = FXCollections.observableArrayList();
        String query = "SELECT id, login, email, password, number, address, full_name, birthday FROM " + SCHEMA_NAME + ".users";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Users user = new Users(
                        resultSet.getInt("id"),
                        resultSet.getString("login"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("number"),
                        resultSet.getString("address"),
                        resultSet.getString("full_name"),
                        resultSet.getDate("birthday").toLocalDate()
                );
                users.add(user);
            }
        }
        UsersTable.setItems(users);
    }

    private void loadProducts() throws SQLException {
        ObservableList<Products> products = FXCollections.observableArrayList();
        String query = "SELECT id, name_products, price, rating, description, category, manufacturer FROM " + SCHEMA_NAME + ".products";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Products product = new Products(
                        resultSet.getInt("id"),
                        resultSet.getString("name_products"),
                        resultSet.getDouble("price"),
                        resultSet.getString("rating"),
                        resultSet.getString("description"),
                        resultSet.getInt("category"),
                        resultSet.getInt("manufacturer")
                );
                products.add(product);
            }
        }
        ProductsTable.setItems(products);
    }

    private void loadOrders() throws SQLException {
        try {
            ObservableList<Orders> data = primaryDatabaseManager.fetchOrdersData();
            OrdersTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Ошибка", "Не удалось загрузить данные из базы данных.");
        }
    }

    // Создание заказа
    @FXML
    public void createOrder() {
        Users selectedUser = UsersTable.getSelectionModel().getSelectedItem();
        Products selectedProduct = ProductsTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null || selectedProduct == null) {
            ErrorDialog.showError("Ошибка", "Выберите пользователя и товар.");
            return;
        }

        String query = "INSERT INTO " + SCHEMA_NAME + ".orders (user_id, product_id) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, selectedUser.getId());
            preparedStatement.setInt(2, selectedProduct.getId());
            preparedStatement.executeUpdate();

            // Обновляем таблицу заказов
            loadOrders();
            System.out.println("Заказ успешно создан.");
        } catch (SQLException e) {
            System.out.println("Ошибка при создании заказа: " + e.getMessage());
            ErrorDialog.showError("Ошибка", "Ошибка при создании заказа: " + e.getMessage());
        }
    }

    @FXML
    public void updateOrder() {
        // Получаем выбранный заказ из таблицы
        Orders selectedOrder = OrdersTable.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            ErrorDialog.showError("Ошибка", "Выберите заказ для обновления.");
            return;
        }

        // Пример обновления: меняем user_id и product_id на новые значения
        Users selectedUser = UsersTable.getSelectionModel().getSelectedItem();
        Products selectedProduct = ProductsTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null || selectedProduct == null) {
            ErrorDialog.showError("Ошибка", "Выберите пользователя и товар для обновления заказа.");
            return;
        }

        String query = "UPDATE " + SCHEMA_NAME + ".orders SET user_id = ?, product_id = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, selectedUser.getId());
            preparedStatement.setInt(2, selectedProduct.getId());
            preparedStatement.setInt(3, selectedOrder.getId());
            preparedStatement.executeUpdate();

            // Обновляем таблицу заказов
            loadOrders();
            System.out.println("Заказ успешно обновлен.");
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении заказа: " + e.getMessage());
            ErrorDialog.showError("Ошибка", "Ошибка при обновлении заказа: " + e.getMessage());
        }
    }

    @FXML
    public void deleteOrder() {
        // Получаем выбранный заказ из таблицы
        Orders selectedOrder = OrdersTable.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            ErrorDialog.showError("Ошибка", "Выберите заказ для удаления.");
            return;
        }

        String query = "DELETE FROM " + SCHEMA_NAME + ".orders WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, selectedOrder.getId());
            preparedStatement.executeUpdate();

            // Обновляем таблицу заказов
            loadOrders();
            System.out.println("Заказ успешно удален.");
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении заказа: " + e.getMessage());
            ErrorDialog.showError("Ошибка", "Ошибка при удалении заказа: " + e.getMessage());
        }
    }

    public void OrdersTab(ActionEvent event) throws IOException {
        ErrorDialog.showError("Предупреждение!", "Вы и так находитесь в данном окне.");
    }


}
