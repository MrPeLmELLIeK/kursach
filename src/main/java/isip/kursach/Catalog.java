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
import java.sql.*;

public class Catalog {

    private DatabaseManager primaryDatabaseManager;
    @FXML
    private TableView<Products> ProductsTable;
    @FXML
    private TableColumn<Products, String> column_name_products;
    @FXML
    private TableColumn<Products, Double> price;
    @FXML
    private TableColumn<Products, String> rating;
    @FXML
    private TableColumn<Products, Integer> category;
    @FXML
    private TableColumn<Products, Integer> manufacturers;
    @FXML
    private TableColumn<Products, String> description;

    @FXML
    private ComboBox<String> ratingFilter;
    @FXML
    private ComboBox<String> categoryFilter;
    @FXML
    private ComboBox<String> manufacturerFilter;
    @FXML
    private TextField searchField;
    @FXML
    private Button backInMenu;

    private Connection connection;
    private static final String SCHEMA_NAME = "user01";

    @FXML
    public void initialize() throws SQLException {
        primaryDatabaseManager = new DatabaseManager();
        // Подключение к базе данных
        connection = DriverManager.getConnection("jdbc:postgresql://89.109.54.20:6543/user01", "user01", "83328");

        // Настройка колонок таблицы
        column_name_products.setCellValueFactory(new PropertyValueFactory<>("name_products"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        rating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        category.setCellValueFactory(new PropertyValueFactory<>("category"));
        manufacturers.setCellValueFactory(new PropertyValueFactory<>("manufacturers"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Загрузка данных
        loadProducts();

        // Заполнение ComboBox фильтрами
        loadFilters();
    }

    private void loadProducts() throws SQLException {
        ObservableList<Products> products = FXCollections.observableArrayList();
        String query = "SELECT * FROM " + SCHEMA_NAME + ".products";

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

    private void loadFilters() {
        // Заполнение ComboBox для фильтрации
        ratingFilter.getItems().addAll("Все", "5", "4", "3", "2", "1");
        categoryFilter.getItems().addAll("Все", "Категория 1", "Категория 2", "Категория 3");
        manufacturerFilter.getItems().addAll("Все", "Производитель 1", "Производитель 2", "Производитель 3");

        // Установка значений по умолчанию
        ratingFilter.setValue("Все");
        categoryFilter.setValue("Все");
        manufacturerFilter.setValue("Все");
    }

    @FXML
    private void applyFilters() {
        String selectedRating = ratingFilter.getValue();
        String selectedCategory = categoryFilter.getValue();
        String selectedManufacturer = manufacturerFilter.getValue();

        String query = "SELECT * FROM " + SCHEMA_NAME + ".products WHERE 1=1";

        if (!selectedRating.equals("Все")) {
            query += " AND rating = '" + selectedRating + "'";
        }
        if (!selectedCategory.equals("Все")) {
            query += " AND category = " + getCategoryId(selectedCategory);
        }
        if (!selectedManufacturer.equals("Все")) {
            query += " AND manufacturer = " + getManufacturerId(selectedManufacturer);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            ObservableList<Products> filteredProducts = FXCollections.observableArrayList();
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
                filteredProducts.add(product);
            }
            ProductsTable.setItems(filteredProducts);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getCategoryId(String categoryName) {
        // Преобразуйте название категории в ID (например, из базы данных)
        return 1; // Заглушка, замените на реальную логику
    }

    private int getManufacturerId(String manufacturerName) {
        // Преобразуйте название производителя в ID (например, из базы данных)
        return 1; // Заглушка, замените на реальную логику
    }

    @FXML
    private void searchProducts() {
        String searchText = searchField.getText().trim();

        String query = "SELECT * FROM " + SCHEMA_NAME + ".products WHERE name_products ILIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + searchText + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<Products> searchResults = FXCollections.observableArrayList();
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
                searchResults.add(product);
            }
            ProductsTable.setItems(searchResults);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void back(ActionEvent event) throws IOException {
        Stage stage = (Stage) backInMenu.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("user-menu.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle("Окно пользователя");
        stage.setScene(new Scene(root1));
        WindowUtil.setCloseHandler(stage, primaryDatabaseManager);
        stage.show();
    }
}
