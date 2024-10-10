package project.autox;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;

import java.io.IOException;

public class Intro2Controller {
    @FXML
    private Button continueBtn;

    @FXML
    private void initialize() {
        continueBtn.setOnAction(event -> {
            try {
                // Load the next scene (Intro1.fxml)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/autox/FXML/Hpage.fxml"));
                Parent intro1Root = loader.load();

                // Get the current stage
                Stage stage = (Stage) continueBtn.getScene().getWindow();

                // Create a fade-out transition
                FadeTransition fadeOut = new FadeTransition(Duration.millis(500), continueBtn.getScene().getRoot());
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> {
                    // When fade-out is done, set the new scene
                    Scene scene = new Scene(intro1Root, 1500, 700);

                    // Create a fade-in transition for the new scene
                    stage.setScene(scene);
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(500), scene.getRoot());
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                });
                fadeOut.play(); // Start fade-out transition
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
