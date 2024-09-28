module project.autox {
    requires javafx.controls;
    requires javafx.fxml;


    opens project.autox to javafx.fxml;
    exports project.autox;
}