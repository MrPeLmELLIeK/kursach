package isip.kursach;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.io.IOException;

import java.util.List;


public class ProductsControl {

    @FXML
    private TableView<Products> ProductsTable;

    @FXML
    private Button CategotyTable;

    @FXML
    private Button tableManufacturers;

    @FXML
    private TableColumn<Products, Integer> id;

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
    private ComboBox<Category> categoryComboBox;

    @FXML
    private TextField idFields;

    @FXML
    private TextField name_products;

    @FXML
    private TextField columnPrice;

    @FXML
    private TextField columnRating;

    @FXML
    private TextArea columnDescription;

    @FXML
    private Button usersTable;

    @FXML
    private Button ordersTab;



    @FXML
    private ComboBox<Manufacturers> manufacturerComboBox;
    private DatabaseManager primaryDatabaseManager;
    private Connection connection;

    @FXML
    private Button AdminsTab;

    @FXML
    private Button ProductsTab;

    private static final String SCHEMA_NAME = "user01";

    @FXML
    public void initialize() throws SQLException {

        primaryDatabaseManager = new DatabaseManager(); // Инициализация DatabaseManager
        connection = DriverManager.getConnection("jdbc:postgresql://89.109.54.20:6543/user01", "user01", "83328");

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        column_name_products.setCellValueFactory(new PropertyValueFactory<>("name_products"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        rating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        category.setCellValueFactory(new PropertyValueFactory<>("category"));
        manufacturers.setCellValueFactory(new PropertyValueFactory<>("manufacturers"));

        loadCategoryComboBox();
        loadManufacturersComboBox();
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

    public void TableProducts(ActionEvent event) {
        ErrorDialog.showError("Предупреждение!", "Вы и так находитесь в данном окне.");
    }

    private void loadCategoryComboBox() {
        // Получение данных из базы данных
        ObservableList<Category> categories = primaryDatabaseManager.fetchCategoryData();

        // Установка данных в ComboBox
        categoryComboBox.setItems(categories);

        // Настройка отображения имен категорий в ComboBox
        categoryComboBox.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getName_category() : ""; // Отображаем имя категории
            }

            @Override
            public Category fromString(String string) {
                return categoryComboBox.getItems().stream()
                        .filter(c -> c.getName_category().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    private void loadManufacturersComboBox() {
        // Получение данных из базы данных
        ObservableList<Manufacturers> manufacturers = primaryDatabaseManager.fetchManufacturersData();

        // Установка данных в ComboBox
        manufacturerComboBox.setItems(manufacturers);

        // Настройка отображения имен категорий в ComboBox
        manufacturerComboBox.setConverter(new StringConverter<Manufacturers>() {
            @Override
            public String toString(Manufacturers manufacturers) {
                return manufacturers != null ? manufacturers.getName_manufacturers() : ""; // Отображаем имя категории
            }

            @Override
            public Manufacturers fromString(String string) {
                return manufacturerComboBox.getItems().stream()
                        .filter(c -> c.getName_manufacturers().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    public void UpdateTable(ActionEvent event) {
        try {
            // Получение данных из базы данных
            ObservableList<Products> data = primaryDatabaseManager.fetchProductsData();

            // Установка данных в таблицу
            ProductsTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Ошибка", "Не удалось загрузить данные из базы данных.");
        }
    }

    public void insertData(ActionEvent event) {
        // Получаем данные из текстовых полей
        String nameProducts = name_products.getText();
        String priceText = columnPrice.getText().replace(",", "."); // Заменяем запятую на точку
        double price = Double.parseDouble(priceText); // Преобразуем в double
        int rating = Integer.parseInt(columnRating.getText());
        String description = columnDescription.getText();
        int category = categoryComboBox.getValue().getId();
        int manufacturer = manufacturerComboBox.getValue().getId();

        // Формируем SQL-запрос (без id, так как он генерируется автоматически)
        String query = "INSERT INTO " + SCHEMA_NAME + ".products (name_products, price, rating, description, category, manufacturer) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nameProducts);
            preparedStatement.setDouble(2, price); // Передаём double напрямую
            preparedStatement.setInt(3, rating);
            preparedStatement.setString(4, description);
            preparedStatement.setInt(5, category);
            preparedStatement.setInt(6, manufacturer);

            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println("Добавлено строк: " + rowsInserted);

            // Оповещение об успешном добавлении
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Успех");
                alert.setHeaderText(null);
                alert.setContentText("Данные успешно добавлены!");
                alert.showAndWait();
            });

            UpdateTable(event);
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении данных: " + e.getMessage());

            // Оповещение об ошибке
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при добавлении данных: ", e.getMessage()));
        }
    }

    // Метод для обновления данных в таблице
    public void updateData(ActionEvent event) {
        // Получаем данные из текстовых полей
        int id = Integer.parseInt(idFields.getText());
        String nameProducts = name_products.getText();
        String priceText = columnPrice.getText().replace(",", "."); // Заменяем запятую на точку
        double price = Double.parseDouble(priceText); // Преобразуем в double
        int rating = Integer.parseInt(columnRating.getText());
        String description = columnDescription.getText();
        int category = categoryComboBox.getValue().getId();
        int manufacturer = manufacturerComboBox.getValue().getId();

        // Формируем SQL-запрос
        String query = "UPDATE " + SCHEMA_NAME + ".products SET name_products=?, price=?, rating=?, description=?, category=?, manufacturer=? WHERE id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nameProducts);
            preparedStatement.setDouble(2, price); // Передаём double напрямую
            preparedStatement.setInt(3, rating);
            preparedStatement.setString(4, description);
            preparedStatement.setInt(5, category);
            preparedStatement.setInt(6, manufacturer);
            preparedStatement.setInt(7, id);

            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println("Обновлено строк: " + rowsUpdated);

            // Оповещение об успешном обновлении
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Успех");
                alert.setHeaderText(null);
                alert.setContentText("Данные успешно обновлены!");
                alert.showAndWait();
            });

            UpdateTable(event);
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении данных: " + e.getMessage());

            // Оповещение об ошибке
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при обновлении данных: ", e.getMessage()));
        }
    }

    // Метод для удаления данных из таблицы
    public void deleteData(ActionEvent event) {
        // Получаем ID и название продукта из текстовых полей
        String idText = idFields.getText();
        String nameProducts = name_products.getText();

        // Проверка на пустые значения
        if (idText.isEmpty() && nameProducts.isEmpty()) {
            Platform.runLater(() -> ErrorDialog.showError("Ошибка ввода", "Введите ID или название продукта."));
            return;
        }

        // Формируем SQL-запрос
        String query;
        if (!idText.isEmpty()) {
            // Удаление по ID
            query = "DELETE FROM " + SCHEMA_NAME + ".products WHERE id=?";
        } else {
            // Удаление по названию
            query = "DELETE FROM " + SCHEMA_NAME + ".products WHERE name_products=?";
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (!idText.isEmpty()) {
                int id = Integer.parseInt(idText);
                preparedStatement.setInt(1, id);
            } else {
                preparedStatement.setString(1, nameProducts);
            }

            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println("Удалено строк: " + rowsDeleted);

            // Оповещение об успешном удалении
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Успех");
                alert.setHeaderText(null);
                alert.setContentText("Данные успешно удалены!");
                alert.showAndWait();
            });

            // Очистка полей после удаления
            idFields.clear();
            name_products.clear();

            UpdateTable(event);
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении данных: " + e.getMessage());

            // Оповещение об ошибке
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при удалении данных: ", e.getMessage()));
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат ID: " + idText);

            // Оповещение об ошибке
            Platform.runLater(() -> ErrorDialog.showError("Ошибка ввода", "Некорректный формат ID."));
        }
    }

}
