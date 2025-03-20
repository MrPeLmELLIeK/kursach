package isip.kursach;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.util.StringConverter;

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
    private TableColumn<Products, String> category;
    @FXML
    private TableColumn<Products, String> manufacturers;
    @FXML
    private TableColumn<Products, String> description;

    @FXML
    private ComboBox<String> ratingFilter; // Оставляем как есть, так как это строки
    @FXML
    private ComboBox<Category> categoryFilter; // Изменяем на ComboBox<Category>
    @FXML
    private ComboBox<Manufacturers> manufacturerFilter; // Изменяем на ComboBox<Manufacturers>
    @FXML
    private TextField searchField;
    @FXML
    private Button backInMenu;

    private Connection connection;
    private static final String SCHEMA_NAME = "user01";

    @FXML
    public void initialize() {
        try {
            primaryDatabaseManager = new DatabaseManager();
            connection = DriverManager.getConnection("jdbc:postgresql://89.109.54.20:6543/user01", "user01", "83328");

            // Настройка колонок таблицы
            column_name_products.setCellValueFactory(new PropertyValueFactory<>("name_products"));
            price.setCellValueFactory(new PropertyValueFactory<>("price"));
            rating.setCellValueFactory(new PropertyValueFactory<>("rating"));
            category.setCellValueFactory(cellData -> {
                int categoryId = cellData.getValue().getCategory();
                String categoryName = getCategoryNameById(categoryId);
                return new SimpleStringProperty(categoryName);
            });
            manufacturers.setCellValueFactory(cellData -> {
                int manufacturerId = cellData.getValue().getManufacturers();
                String manufacturerName = getManufacturerNameById(manufacturerId);
                return new SimpleStringProperty(manufacturerName);
            });
            description.setCellValueFactory(new PropertyValueFactory<>("description"));

            // Загрузка данных
            loadProducts();

            // Заполнение ComboBox фильтрами
            loadFilters();

            // Обработчик двойного клика на строку таблицы
            ProductsTable.setRowFactory(tv -> {
                TableRow<Products> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && !row.isEmpty()) {
                        Products selectedProduct = row.getItem();
                        openProductDetails(selectedProduct); // Открываем окно с деталями товара
                    }
                });
                return row;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Ошибка при инициализации: " + e.getMessage());
        }
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
        try {
            // Загрузка данных для категорий и производителей
            ObservableList<Category> categories = primaryDatabaseManager.fetchCategoryData();
            ObservableList<Manufacturers> manufacturers = primaryDatabaseManager.fetchManufacturersData();

            // Добавление значения "Все" в начало списка
            categories.add(0, new Category(-1, "Все"));
            manufacturers.add(0, new Manufacturers(-1, "Все", "Все организации"));

            // Установка данных в ComboBox
            categoryFilter.setItems(categories);
            manufacturerFilter.setItems(manufacturers);

            // Настройка отображения имен в ComboBox
            categoryFilter.setConverter(new StringConverter<Category>() {
                @Override
                public String toString(Category category) {
                    return category != null ? category.getName_category() : "";
                }

                @Override
                public Category fromString(String string) {
                    return categoryFilter.getItems().stream()
                            .filter(c -> c.getName_category().equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });

            manufacturerFilter.setConverter(new StringConverter<Manufacturers>() {
                @Override
                public String toString(Manufacturers manufacturers) {
                    return manufacturers != null ? manufacturers.getName_manufacturers() : "";
                }

                @Override
                public Manufacturers fromString(String string) {
                    return manufacturerFilter.getItems().stream()
                            .filter(m -> m.getName_manufacturers().equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });

            // Установка значений по умолчанию
            ratingFilter.getItems().addAll("Все", "5", "4", "3", "2", "1");
            ratingFilter.setValue("Все");
            categoryFilter.setValue(categories.get(0)); // "Все"
            manufacturerFilter.setValue(manufacturers.get(0)); // "Все"
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Ошибка при загрузке фильтров: " + e.getMessage());
        }
    }

    @FXML
    private void applyFilters() {
        String selectedRating = ratingFilter.getValue();
        Category selectedCategory = categoryFilter.getValue();
        Manufacturers selectedManufacturer = manufacturerFilter.getValue();

        String query = "SELECT * FROM " + SCHEMA_NAME + ".products WHERE 1=1";

        if (!selectedRating.equals("Все")) {
            query += " AND rating = '" + selectedRating + "'";
        }
        if (selectedCategory.getId() != -1) {
            query += " AND category = " + selectedCategory.getId();
        }
        if (selectedManufacturer.getId() != -1) {
            query += " AND manufacturer = " + selectedManufacturer.getId();
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

    @FXML
    private void addOrder(ActionEvent event) {
        // Получить выбранный продукт из таблицы
        Products selectedProduct = ProductsTable.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            ErrorDialog.showError("Ошибка", "Не выбран продукт. Пожалуйста, выберите продукт из таблицы.");
            return;
        }

        // Получить логин текущего пользователя из Session
        String userLogin = Session.getLogin();

        if (userLogin == null || userLogin.isEmpty()) {
            ErrorDialog.showError("Ошибка", "Пользователь не авторизован. Пожалуйста, войдите в систему.");
            return;
        }

        // Получить ID пользователя по логину
        int userId = getUserIdByLogin(userLogin);

        if (userId == -1) {
            ErrorDialog.showError("Ошибка", "Пользователь не найден. Не удалось найти пользователя в базе данных.");
            return;
        }

        // Вставить заказ в таблицу orders
        String query = String.format(
                "INSERT INTO %s.orders (user_id, product_id, order_time) VALUES (?, ?, ?)",
                SCHEMA_NAME
        );

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId); // ID пользователя
            preparedStatement.setInt(2, selectedProduct.getId()); // ID продукта
            preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // Текущее время

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Успех", "Заказ добавлен", "Заказ успешно добавлен в базу данных.");
            } else {
                ErrorDialog.showError("Ошибка", "Не удалось добавить заказ. Попробуйте снова.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorDialog.showError("Ошибка", "Ошибка базы данных");
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
    private String getCategoryNameById(int categoryId) {
        for (Category category : categoryFilter.getItems()) {
            if (category.getId() == categoryId) {
                return category.getName_category();
            }
        }
        return "Неизвестная категория";
    }

    private String getManufacturerNameById(int manufacturerId) {
        for (Manufacturers manufacturer : manufacturerFilter.getItems()) {
            if (manufacturer.getId() == manufacturerId) {
                return manufacturer.getName_manufacturers();
            }
        }
        return "Неизвестный производитель";
    }
    private int getUserIdByLogin(String login) {
        String query = String.format(
                "SELECT id FROM %s.users WHERE login = ?",
                SCHEMA_NAME
        );

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Если пользователь не найден
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void openProductDetails(Products product) {
        try {
            // Загрузка FXML-файла для окна с деталями товара
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("products-details.fxml"));
            Parent root = fxmlLoader.load();

            // Получение контроллера для нового окна
            ProductsDetailsController productsDetailsController = fxmlLoader.getController();

            // Передача данных о товаре в контроллер
            productsDetailsController.setProduct(product);

            // Настройка и отображение нового окна
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Информация о товаре");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            ErrorDialog.showError("Ошибка", "Не удалось открыть окно с деталями товара.");
        }
    }
}
