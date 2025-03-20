package isip.kursach;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

public class DatabaseManager {
    private static final String URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:postgresql://89.109.54.20:6543/user01";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "user01";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "83328";
    private static final String SCHEMA_NAME = System.getenv("DB_SCHEMA") != null ? System.getenv("DB_SCHEMA") : "user01";

    private Connection connection;

    public DatabaseManager() {
        connect();
        ensureTableExists(); // Проверяем/создаем таблицу
    }

    // Метод для подключения к базе данных
    public void connect() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Успешно подключено к базе данных.");
        } catch (SQLException e) {
            System.out.println("Ошибка при подключении к базе данных: " + e.getMessage());
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при подключении к базе данных: ", e.getMessage()));
            throw new RuntimeException("Не удалось подключиться к базе данных", e);
        }
    }

    // Метод для отключения от базы данных
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Соединение с базой данных закрыто.");
            } catch (SQLException e) {
                System.out.println("Ошибка при закрытии соединения: " + e.getMessage());
                Platform.runLater(() -> ErrorDialog.showError("Ошибка при закрытии соединения: ", e.getMessage()));
            }
        }
    }


    public void ensureTableExists() {
        String createTableQuery = String.format("""
                    CREATE TABLE IF NOT EXISTS %s.category (
                                    id SERIAL PRIMARY KEY,
                                    name_category VARCHAR(100) NOT NULL
                                );
                                CREATE TABLE IF NOT EXISTS %s.manufacturers (
                                    id SERIAL PRIMARY KEY,
                                    name_manufacturers VARCHAR(100) NOT NULL,
                                    organization_name VARCHAR(100) NOT NULL
                                );
                                CREATE TABLE IF NOT EXISTS %s.admins (
                                    id SERIAL PRIMARY KEY,
                                    login VARCHAR(30) NOT NULL UNIQUE,
                                    code INTEGER NOT NULL,
                                    password VARCHAR(100) NOT NULL
                                );
                                CREATE TABLE IF NOT EXISTS %s.users (
                                    id SERIAL PRIMARY KEY,
                                    login VARCHAR(30) NOT NULL UNIQUE,
                                    email VARCHAR(100) NOT NULL UNIQUE,
                                    number VARCHAR(20) NOT NULL,
                                    password VARCHAR(100) NOT NULL,
                                    address VARCHAR(100) NOT NULL,
                                    full_name VARCHAR(50) NOT NULL,
                                    birthday DATE
                                );
                                CREATE TABLE IF NOT EXISTS %s.products (
                                    id SERIAL,
                                    name_products VARCHAR(50) NOT NULL,
                                    price DOUBLE PRECISION,
                                    rating INTEGER,
                                    description VARCHAR(150),
                                    category INTEGER,
                                    manufacturer INTEGER,
                                    CONSTRAINT products_pk PRIMARY KEY (id),
                                    CONSTRAINT products_category_fk FOREIGN KEY (category) REFERENCES %s.category(login) ON DELETE SET NULL,
                                    CONSTRAINT products_manufacturers_fk FOREIGN KEY (manufacturer) REFERENCES %s.manufacturers(id) ON DELETE SET NULL
                                );
                                CREATE TABLE IF NOT EXISTS %s.orders (
                                    id SERIAL PRIMARY KEY,
                                    user_id INT NOT NULL,
                                    product_id INT NOT NULL,
                                    order_time TIMESTAMP,
                                    CONSTRAINT orders_user_fk FOREIGN KEY (user_id) REFERENCES %s.users(id) ON DELETE CASCADE,
                                    CONSTRAINT orders_product_fk FOREIGN KEY (product_id) REFERENCES %s.products(id) ON DELETE CASCADE
                                );
                                CREATE TABLE IF NOT EXISTS %s.recommended_products (
                                    id SERIAL PRIMARY KEY,
                                    name_products VARCHAR(50) NOT NULL,
                                    description VARCHAR(150),
                                    rating INTEGER,
                                    category INTEGER,
                                    price DOUBLE PRECISION
                                );
                """, SCHEMA_NAME, SCHEMA_NAME, SCHEMA_NAME, SCHEMA_NAME, SCHEMA_NAME, SCHEMA_NAME, SCHEMA_NAME, SCHEMA_NAME, SCHEMA_NAME, SCHEMA_NAME, SCHEMA_NAME);


        String createTriggerQuery = """
    CREATE OR REPLACE FUNCTION set_order_time()
    RETURNS TRIGGER AS $$
    BEGIN
        NEW.order_time = NOW(); -- Устанавливаем текущее время
        RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;

    DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM pg_trigger
            WHERE tgname = 'set_order_time_trigger'
        ) THEN
            CREATE TRIGGER set_order_time_trigger
            BEFORE INSERT ON user01.orders
            FOR EACH ROW
            EXECUTE FUNCTION set_order_time();
        END IF;
    END $$;
    """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTriggerQuery);
            System.out.println("Триггер и функция успешно созданы.");
        } catch (SQLException e) {
            System.out.println("Ошибка при создании триггера: " + e.getMessage());
            e.printStackTrace();
        }

        try (Statement statement = connection.createStatement()) {
            // Создаем таблицы, если они не существуют
            statement.execute(createTableQuery);
            System.out.println("Проверка таблиц завершена. Таблицы в схеме '" + SCHEMA_NAME + "' готовы к использованию.");

            // Проверяем, пуста ли таблица category
            String checkCategoryTableQuery = String.format("SELECT COUNT(*) FROM %s.category", SCHEMA_NAME);
            try (ResultSet resultSet = statement.executeQuery(checkCategoryTableQuery)) {
                resultSet.next();
                int rowCount = resultSet.getInt(1);

                // Если таблица category пуста, добавляем начальные данные
                if (rowCount == 0) {
                    String insertCategoryDataQuery = String.format("""
                                INSERT INTO %s.category (name_category) VALUES 
                                ('Холодильники'),
                                ('Стиральные машины'),
                                ('Микроволновые печи'),
                                ('Пылесосы'),
                                ('Телевизоры'),
                                ('Посудомоечные машины'),
                                ('Кофемашины'),
                                ('Увлажнители воздуха');
                            """, SCHEMA_NAME);

                    statement.executeUpdate(insertCategoryDataQuery);
                }
            }

            // Проверяем, пуста ли таблица manufacturers
            String checkManufacturersTableQuery = String.format("SELECT COUNT(*) FROM %s.manufacturers", SCHEMA_NAME);
            try (ResultSet resultSet = statement.executeQuery(checkManufacturersTableQuery)) {
                resultSet.next();
                int rowCount = resultSet.getInt(1);

                // Если таблица manufacturers пуста, добавляем начальные данные
                if (rowCount == 0) {
                    String insertManufacturersDataQuery = String.format("""
                                INSERT INTO %s.manufacturers (name_manufacturers, organization_name) VALUES 
                                ('Samsung', 'Samsung Inc.'), 
                                ('LG', 'LG Corp.'), 
                                ('Bosch', 'Bosch GmbH'), 
                                ('Philips', 'Philips International'),
                                ('Indesit', 'Indesit Company'),
                                ('Haier', 'Haier Group'),
                                ('Beko', 'Beko PLC'),
                                ('Sharp', 'Sharp Corporation');
                            """, SCHEMA_NAME);

                    statement.executeUpdate(insertManufacturersDataQuery);
                }
            }

            String checkAdminsTableQuery = String.format("SELECT COUNT(*) FROM %s.admins", SCHEMA_NAME);
            try (ResultSet resultSet = statement.executeQuery(checkAdminsTableQuery)) {
                resultSet.next();
                int rowCount = resultSet.getInt(1);

                // Если таблица manufacturers пуста, добавляем начальные данные
                if (rowCount == 0) {
                    String insertAdminsDataQuery = String.format("""
                                INSERT INTO %s.admins (login, code, password) VALUES
                                    ('test1', 11111, 'test1'),
                                    ('test2', 22222, 'test2'),
                                    ('test3', 33333, 'test3'),
                                    ('test4', 44444, 'test4');
                            """, SCHEMA_NAME);

                    statement.executeUpdate(insertAdminsDataQuery);
                }
            }


            String checkProductsTableQuery = String.format("SELECT COUNT(*) FROM %s.products", SCHEMA_NAME);
            try (ResultSet resultSet = statement.executeQuery(checkProductsTableQuery)) {
                resultSet.next();
                int rowCount = resultSet.getInt(1);

                // Если таблица manufacturers пуста, добавляем начальные данные
                if (rowCount == 0) {
                    String insertProductsDataQuery = String.format("""
                    INSERT INTO %s.products (name_products, price, rating, description, category, manufacturer) VALUES
                         ('Холодильник Samsung RB33J3420SA', 599.99, 4, 'Двухкамерный холодильник с No Frost', 1, 1),
                         ('Холодильник LG GA-B459SLGL', 799.99, 5, 'Холодильник с линейным компрессором', 1, 2),
                         ('Холодильник Bosch KGN39VL35', 899.99, 4, 'Холодильник с зоной свежести', 1, 3),
                         ('Стиральная машина Samsung WW80T554DAW', 499.99, 5, 'Стиральная машина с загрузкой 8 кг', 2, 1),
                         ('Стиральная машина LG F4J6TY1W', 549.99, 4, 'Стиральная машина с технологией Steam', 2, 2),
                         ('Стиральная машина Bosch WAT28441', 599.99, 5, 'Стиральная машина с EcoSilence Drive', 2, 3),
                         ('Микроволновка Samsung MG23T5018AK', 99.99, 4, 'Микроволновая печь с грилем', 3, 1),
                         ('Микроволновка LG MH6535GIB', 129.99, 5, 'Микроволновая печь с инверторным нагревателем', 3, 2),
                         ('Микроволновка Bosch BFL554MS0', 149.99, 4, 'Микроволновая печь с функцией разморозки', 3, 3),
                         ('Пылесос Samsung VC07R3520HD', 199.99, 4, 'Пылесос с мешком для пыли', 4, 1),
                         ('Пылесос LG VK89443N', 249.99, 5, 'Пылесос с циклонным фильтром', 4, 2),
                         ('Пылесос Bosch BGS7PET', 299.99, 4, 'Пылесос для уборки шерсти животных', 4, 3),
                         ('Телевизор Samsung QE55Q60AAU', 799.99, 5, 'Телевизор QLED 4K', 5, 1),
                         ('Телевизор LG OLED55C1', 1299.99, 5, 'Телевизор OLED 4K', 5, 2),
                         ('Телевизор Bosch 55OLED706', 1499.99, 4, 'Телевизор OLED с Dolby Vision', 5, 3),
                         ('Холодильник Philips HRF400', 699.99, 4, 'Холодильник с технологией No Frost', 1, 4),
                         ('Стиральная машина Philips W1234', 499.99, 5, 'Стиральная машина с загрузкой 7 кг', 2, 4),
                         ('Микроволновка Philips HD1234', 119.99, 4, 'Микроволновая печь с грилем', 3, 4),
                         ('Пылесос Philips FC1234', 179.99, 5, 'Пылесос с мешком для пыли', 4, 4),
                         ('Телевизор Philips 55PUS8505', 899.99, 5, 'Телевизор 4K UHD с Ambilight', 5, 4),
                         ('Кофемашина Indesit CI 50 T', 199.99, 4, 'Кофемашина с капучинатором', 7, 5),
                         ('Кофемашина Haier CM-101', 249.99, 5, 'Кофемашина с автоматическим приготовлением', 7, 6),
                         ('Кофемашина Beko CEG 5301 X', 299.99, 4, 'Кофемашина с функцией помола кофе', 7, 7),
                         ('Кофемашина Sharp SM-50', 349.99, 5, 'Кофемашина с сенсорным экраном', 7, 8),
                         ('Увлажнитель воздуха Indesit IHU 200', 99.99, 4, 'Увлажнитель воздуха с ароматизацией', 8, 5),
                         ('Увлажнитель воздуха Haier HUE-301', 129.99, 5, 'Увлажнитель воздуха с ультразвуковым распылением', 8, 6),
                         ('Увлажнитель воздуха Beko BHU-200', 149.99, 4, 'Увлажнитель воздуха с функцией ионизации', 8, 7),
                         ('Увлажнитель воздуха Sharp KC-850', 199.99, 5, 'Увлажнитель воздуха с очисткой воздуха', 8, 8),
                         ('Холодильник Indesit ITF 118 W', 399.99, 4, 'Однокамерный холодильник с морозильной камерой', 1, 5),
                         ('Стиральная машина Haier HW70-BP12729S', 449.99, 5, 'Стиральная машина с загрузкой 7 кг', 2, 6),
                         ('Микроволновка Beko MCM 20100 E', 99.99, 4, 'Микроволновая печь с функцией разморозки', 3, 7),
                         ('Пылесос Sharp EC-830', 199.99, 4, 'Пылесос с моющим фильтром', 4, 8),
                         ('Телевизор Haier LE32B9000', 349.99, 5, 'Телевизор с Smart TV', 5, 6);
                """, SCHEMA_NAME);

                    statement.executeUpdate(insertProductsDataQuery);
                }
            }

            String checkRecomendedProductsTableQuery = String.format("SELECT COUNT(*) FROM %s.recommended_products", SCHEMA_NAME);
            try (ResultSet resultSet = statement.executeQuery(checkRecomendedProductsTableQuery)) {
                resultSet.next();
                int rowCount = resultSet.getInt(1);

                if (rowCount == 0) {
                    String insertRecommendedProductsQuery = String.format("""
                        INSERT INTO %s.recommended_products (name_products, description, rating, category, price)
                        SELECT name_products, description, rating, category, price
                        FROM %s.products
                        ORDER BY rating DESC
                        LIMIT 20
                """, SCHEMA_NAME, SCHEMA_NAME);

                    statement.executeUpdate(insertRecommendedProductsQuery);
                    System.out.println("Данные успешно вставлены в таблицу recommended_products.");
                }
            }

            String checkUsersTableQuery = String.format("SELECT COUNT(*) FROM %s.users", SCHEMA_NAME);
            try (ResultSet resultSet = statement.executeQuery(checkUsersTableQuery)) {
                resultSet.next();
                int rowCount = resultSet.getInt(1);

                // Если таблица category пуста, добавляем начальные данные
                if (rowCount == 0) {
                    String insertUsersDataQuery = String.format("""
                    INSERT INTO %s.users (login, email, number, password, address, full_name, birthday) VALUES
                    ('john_doe', 'john.doe@example.com', '+1234567890', 'securepassword123', '123 Main St, Springfield', 'John Doe', '1990-05-15'),
                    ('jane_smith', 'jane.smith@example.com', '+0987654321', 'anotherpassword456', '456 Elm St, Shelbyville', 'Jane Smith', '1985-10-22'),
                    ('alice_wonder', 'alice.wonder@example.com', '+1122334455', 'alicepassword789', '789 Oak St, Capital City', 'Alice Wonder', '1995-03-30');
                """, SCHEMA_NAME);

                    statement.executeUpdate(insertUsersDataQuery);
                }
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при проверке/создании таблиц: " + e.getMessage());
            Platform.runLater(() -> ErrorDialog.showError("Ошибка", "Не удалось создать таблицу или добавить данные: " + e.getMessage()));
            throw new RuntimeException("Не удалось создать таблицу или добавить данные", e);
        }
    }

    // Метод для получения данных из таблицы
    public ObservableList<Category> fetchCategoryData() {
        ObservableList<Category> Categorydata = FXCollections.observableArrayList();
        String query = String.format("SELECT * FROM %s.category", SCHEMA_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name_category = resultSet.getString("name_category");
                Categorydata.add(new Category(id, name_category));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
            e.printStackTrace();
        }

        return Categorydata;
    }


    public ObservableList<Manufacturers> fetchManufacturersData() {
        ObservableList<Manufacturers> ManufacturersData = FXCollections.observableArrayList();
        String query = String.format("SELECT * FROM %s.manufacturers", SCHEMA_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String Name_manufacturers = resultSet.getString("name_manufacturers");
                String Organization_name = resultSet.getString("organization_name");
                ManufacturersData.add(new Manufacturers(id, Name_manufacturers, Organization_name));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
            e.printStackTrace();
        }

        return ManufacturersData;
    }

   public ObservableList<RecommendedProducts> fetchRecommendedProductsData() {
        ObservableList<RecommendedProducts> RecommendedProductsData = FXCollections.observableArrayList();
        String query = String.format("SELECT * FROM %s.recommended_products", SCHEMA_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name_products = resultSet.getString("name_products");
                double price = resultSet.getDouble("price");
                String rating = resultSet.getString("rating");
                String description = resultSet.getString("description");
                int category = resultSet.getInt("category");
                RecommendedProductsData.add(new RecommendedProducts(id, name_products, description, rating, category, price));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
            e.printStackTrace();
        }

        return RecommendedProductsData;
    }

    public ObservableList<Admins> fetchAdminsData() {
        ObservableList<Admins> adminsData = FXCollections.observableArrayList();
        String query = String.format("SELECT * FROM %s.admins", SCHEMA_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String login = resultSet.getString("login");
                int code = resultSet.getInt("code");
                String password = resultSet.getString("password");
                adminsData.add(new Admins(id, login, code, password));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
            e.printStackTrace();
        }

        return adminsData; // Возвращаем список типа ObservableList<Admins>
    }

    public ObservableList<Products> fetchProductsData() {
        ObservableList<Products> productsData = FXCollections.observableArrayList();
        String query = String.format("SELECT * FROM %s.products", SCHEMA_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name_products = resultSet.getString("name_products");
                double price = resultSet.getDouble("price");
                String rating = resultSet.getString("rating");
                String description = resultSet.getString("description");
                int category = resultSet.getInt("category");
                int manufacturers = resultSet.getInt("manufacturer");

                // Создаем объект Products и добавляем его в список
                productsData.add(new Products(id, name_products, price, rating, description, category, manufacturers));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
            e.printStackTrace();
        }

        return productsData; // Возвращаем список типа ObservableList<Admins>
    }

    public Products getProductById(int id) {
        String query = String.format("SELECT * FROM %s.products WHERE id = ?", SCHEMA_NAME);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name_products = resultSet.getString("name_products");
                double price = resultSet.getDouble("price");
                String rating = resultSet.getString("rating");
                String description = resultSet.getString("description");
                int category = resultSet.getInt("category");
                int manufacturers = resultSet.getInt("manufacturer");

                return new Products(id, name_products, price, rating, description, category, manufacturers);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
/*
    // Метод для вставки данных в таблицу
    public void insertData(int id, String name_category) {
        String query = String.format("INSERT INTO %s (id, name_category) VALUES (%d, '%s')", SCHEMA_NAME, id, name_category);

        try (var preparedStatement = connection.prepareStatement(query)) {
            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println("Добавлено строк: " + rowsInserted);
        } catch (SQLException e) {
            System.out.println("Ошибка при вставке данных: " + e.getMessage());
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при вставке данных: ", e.getMessage()));
        }
    }

    // Метод для обновления данных в таблице
    public void updateData(int id, String name_category) {
        String query = String.format("UPDATE %s SET name_category='%s' WHERE id=%d", SCHEMA_NAME, name_category, id);

        try (var preparedStatement = connection.prepareStatement(query)) {
            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println("Обновлено строк: " + rowsUpdated);
        } catch (SQLException e) {
            System.out.println("Ошибка при изменении данных: " + e.getMessage());
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при изменении данных: ", e.getMessage()));
        }
    }

    // Метод для удаления данных из таблицы
    public void deleteData(int id) {
        String query = String.format("DELETE FROM %s WHERE id=%d", SCHEMA_NAME, id);

        try (var preparedStatement = connection.prepareStatement(query)) {
            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println("Удалено строк: " + rowsDeleted);
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении данных: " + e.getMessage());
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при удалении данных: ", e.getMessage()));
        }
    }
*/
    // Метод для закрытия соединения
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Соединение с базой данных закрыто.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public ObservableList<Users> fetchUsersData() {
        ObservableList<Users> usersData = FXCollections.observableArrayList(); // Исправлено название списка
        String query = String.format("SELECT * FROM %s.users", SCHEMA_NAME); // Исправлено на таблицу users

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String login = resultSet.getString("login"); // Исправлено имя столбца
                String email = resultSet.getString("email"); // Исправлено имя столбца
                String password = resultSet.getString("password"); // Исправлено имя столбца
                String numberPhone = resultSet.getString("number"); // Исправлено имя столбца
                String address = resultSet.getString("address"); // Исправлено имя столбца
                String fullName = resultSet.getString("full_name"); // Исправлено имя столбца
                LocalDate birthday = resultSet.getDate("birthday").toLocalDate(); // Исправлено имя столбца и преобразование в LocalDate

                // Создаем объект Users и добавляем его в список
                usersData.add(new Users(id, login, email, numberPhone, password, address, fullName, birthday));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
            e.printStackTrace();
        }

        return usersData; // Возвращаем список типа ObservableList<Users>
    }

    public ObservableList<Orders> fetchOrdersData() {
        ObservableList<Orders> ordersData = FXCollections.observableArrayList();
        String query = String.format(
                "SELECT o.id, u.login AS user_login, p.name_products AS product_name, o.order_time " +
                        "FROM %s.orders o " +
                        "JOIN %s.users u ON o.user_id = u.id " +
                        "JOIN %s.products p ON o.product_id = p.id",
                SCHEMA_NAME, SCHEMA_NAME, SCHEMA_NAME
        );

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String userLogin = resultSet.getString("user_login");
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


}




