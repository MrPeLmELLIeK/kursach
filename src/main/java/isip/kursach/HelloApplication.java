package isip.kursach;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private DatabaseManager primaryDatabaseManager;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        // Сначала загружаем корневой элемент (это загрузит FXML файл)
        Scene scene = new Scene(fxmlLoader.load(), 500, 400);

        // Получаем контроллер после загрузки FXML
        HelloController controller = fxmlLoader.getController();
        controller.setPrimaryStage(stage);

        primaryDatabaseManager = new DatabaseManager();
        primaryDatabaseManager.connect();

        try {
            // Проверка таблицы
            primaryDatabaseManager.ensureTableExists();
        } catch (Exception e) {
            System.out.println("Ошибка при проверке/создании таблицы: " + e.getMessage());
            // Показываем модальное окно с ошибкой
            ErrorDialog.showError("Ошибка","Ошибка при проверке/создании таблицы: " + e.getMessage());
            primaryDatabaseManager.disconnect(); // Закрываем соединение при ошибке
            Platform.exit(); // Завершаем работу приложения
            return; // Прерываем выполнение метода start
        }

        stage.setTitle("Вход");
        stage.setResizable(false);
        stage.setScene(scene);
        WindowUtil.setCloseHandler(stage, primaryDatabaseManager);
        stage.show();


    }



    public static void main(String[] args) {
        launch();
    }
}
