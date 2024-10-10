module project.autox {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens project.autox to javafx.fxml;
    exports project.autox;
}