package isip.kursach;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.application.Platform;

public class WindowUtil {
    private DatabaseManager primaryDatabaseManager;

    public static void setCloseHandler(Stage stage, DatabaseManager primaryDatabaseManager) {
        stage.setOnCloseRequest(event -> {
            // Отменяем стандартное закрытие окна
            event.consume();

            // Показываем диалоговое окно с подтверждением выхода
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение выхода");
            alert.setHeaderText("Вы уверены, что хотите выйти?");
            alert.setContentText("Все несохраненные данные будут потеряны.");

            // Ожидаем ответа пользователя
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Закрываем соединение с базой данных
                    primaryDatabaseManager.close();

                    // Закрываем приложение
                    Platform.exit();
                }
            });
        });
    }
}
