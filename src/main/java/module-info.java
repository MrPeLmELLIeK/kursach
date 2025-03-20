module isip.kursach {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.prefs;
    requires java.desktop;


    opens isip.kursach to javafx.fxml;
    exports isip.kursach;
}