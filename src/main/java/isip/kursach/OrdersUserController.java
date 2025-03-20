package isip.kursach;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrdersUserController {
        @FXML
        private TableView<Orders> ordersTable;

        @FXML
        private TableColumn<Orders, Integer> orderId;

        @FXML
        private TableColumn<Orders, String> productName;

        @FXML
        private TableColumn<Orders, String> orderTime;

    private DatabaseManager databaseManager;
    private Connection connection;
    private static final String SCHEMA_NAME = "user01";

    // Добавлено поле userLogin
    private String userLogin;

    @FXML
    public void initialize() {
        try {
            databaseManager = new DatabaseManager();
            connection = DriverManager.getConnection("jdbc:postgresql://89.109.54.20:6543/user01", "user01", "83328");

            // Загрузить данные о заказах
            loadOrdersData();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Ошибка при инициализации: " + e.getMessage());
        }
    }

    // Метод для установки userLogin
    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
        if (userLogin != null && !userLogin.isEmpty()) {
            loadOrdersData(); // Загружаем данные только после установки логина
        } else {
            System.err.println("Логин не установлен или пуст.");
        }
    }

    private void loadOrdersData() {


        // Загрузить данные о заказах
        ObservableList<Orders> ordersData = fetchOrdersDataByUser(userLogin);

        // Настроить таблицу
        orderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        orderTime.setCellValueFactory(new PropertyValueFactory<>("orderTime"));

        // Установить данные в таблицу
        ordersTable.setItems(ordersData);
    }

    private ObservableList<Orders> fetchOrdersDataByUser(String userLogin) {
        ObservableList<Orders> ordersData = FXCollections.observableArrayList();
        String query = String.format(
                "SELECT o.id, p.name_products AS product_name, o.order_time \n" +
                        "FROM user01.orders o \n" +
                        "JOIN user01.users u ON o.user_id = u.id \n" +
                        "JOIN user01.products p ON o.product_id = p.id \n" +
                        "WHERE u.login = ?\n" +
                        "ORDER BY o.order_time DESC;",
                SCHEMA_NAME, SCHEMA_NAME, SCHEMA_NAME
        );

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userLogin);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String productName = resultSet.getString("product_name");
                String orderTime = resultSet.getString("order_time");

                ordersData.add(new Orders(id, userLogin, productName, orderTime));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
            e.printStackTrace();
        }

        return ordersData;
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) ordersTable.getScene().getWindow();
        stage.close();
    }
}