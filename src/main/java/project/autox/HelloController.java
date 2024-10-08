package project.autox;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.animation.FillTransition;
import javafx.scene.paint.Color;

import java.io.IOException;

public class HelloController {

    @FXML
    private Pane contentPane;

    @FXML
    private Button dfaButton, nfaButton, cfgButton, pdaButton, tmButton;

    private Button activeButton = null; // Keep track of the active button
    private final Color originalColor = Color.web("#F6FDFF"); // Original color
    private final Color hoverColor = Color.web("#F4FFDE"); // Hover fill color

    @FXML
    public void initialize() {
        // Set the initial colors for the buttons
        setButtonColor(dfaButton);
        setButtonColor(nfaButton);
        setButtonColor(cfgButton);
        setButtonColor(pdaButton);
        setButtonColor(tmButton);

        // Add click event handlers to each button
        dfaButton.setOnMouseClicked(e -> handleButtonClick(dfaButton));
        nfaButton.setOnMouseClicked(e -> handleButtonClick(nfaButton));
        cfgButton.setOnMouseClicked(e -> handleButtonClick(cfgButton));
        pdaButton.setOnMouseClicked(e -> handleButtonClick(pdaButton));
        tmButton.setOnMouseClicked(e -> handleButtonClick(tmButton));
    }

    private void setButtonColor(Button button) {
        button.setStyle("-fx-background-color: " + toRgb(originalColor) + ";"); // Set initial color
    }

    private void handleButtonClick(Button clickedButton) {
        // If a different button was already active, reset its color
        if (activeButton != null && activeButton != clickedButton) {
            resetButtonColor(activeButton);
        }

        // Change the clicked button's color to the hover color
        fillButtonColor(clickedButton, hoverColor);
        activeButton = clickedButton; // Set the newly active button
    }

    private void resetButtonColor(Button button) {
        fillButtonColor(button, originalColor); // Reset to original color
    }

    private void fillButtonColor(Button button, Color color) {
        FillTransition fillTransition = new FillTransition(Duration.millis(300), button.getShape());
        fillTransition.setFromValue((Color) button.getBackground().getFills().get(0).getFill());
        fillTransition.setToValue(color);
        fillTransition.play();
        button.setStyle("-fx-background-color: " + toRgb(color) + ";"); // Update button color
    }

    private String toRgb(Color color) {
        return String.format("rgba(%d, %d, %d, %.2f)",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255),
                color.getOpacity());
    }

    @FXML
    public void loadDFA() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/autox/FXML/DFA.fxml"));
        Pane dfaPane = loader.load();
        contentPane.getChildren().setAll(dfaPane);
    }

    @FXML
    public void loadNFA() throws IOException {
        Pane nfaPane = FXMLLoader.load(getClass().getResource("/project/autox/FXML/NFA.fxml"));
        contentPane.getChildren().setAll(nfaPane);
    }

    @FXML
    public void loadCFG() throws IOException {
        Pane cfgPane = FXMLLoader.load(getClass().getResource("/project/autox/FXML/CFG.fxml"));
        contentPane.getChildren().setAll(cfgPane);
    }

    @FXML
    public void loadPDA() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/autox/FXML/PDA.fxml"));
        Pane pdaPane = loader.load();
        contentPane.getChildren().setAll(pdaPane);
    }

    @FXML
      public void loadTM() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/autox/FXML/TuringMachine.fxml"));
        Pane tmPane = loader.load();
        contentPane.getChildren().setAll(tmPane);

    }

    public void loadTH() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/autox/FXML/TOH.fxml"));
        AnchorPane HpagePane = loader.load();
    }
}
