package project.autox;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.layout.FlowPane;
import javafx.geometry.Insets;
import javafx.util.Duration;

public class NFAController {

    private int currentState = 0;
    private int rejectedState = 0;
    private Timeline characterAnimationTimeline;

    @FXML
    private TextField inputTextField;
    @FXML
    private Button ValidateBTN;
    @FXML
    private Button SimulateBTN;
    @FXML
    private Button ClearBTN;

    @FXML
    private FlowPane AcceptedOutputTM;
    @FXML
    private FlowPane RejectedOutputTM;

    @FXML
    private ImageView q0, q1, q2f, q0reject, q1rejected, q2frejected;

    public void initialize() {
        setupButton(ValidateBTN);
        setupButton(SimulateBTN);
        setupButton(ClearBTN);

        applyShadowToImageView(q0);
        applyShadowToImageView(q1);
        applyShadowToImageView(q2f);
        applyShadowToImageView(q0reject);
        applyShadowToImageView(q1rejected);
        applyShadowToImageView(q2frejected);

        ValidateBTN.setOnAction(event -> validateInput());
        SimulateBTN.setOnAction(event -> animateInput(inputTextField.getText()));
        ClearBTN.setOnAction(event -> clearFields());
    }

    private void validateInput() {
        AcceptedOutputTM.getChildren().clear();
        RejectedOutputTM.getChildren().clear();

        String input = inputTextField.getText();

        if (isValid(input)) {
            currentState = 0;
            rejectedState = 0;

            StringBuilder acceptedTransitions = new StringBuilder();
            StringBuilder rejectedTransitions = new StringBuilder();

            for (int i = 0; i < input.length(); i++) {
                char ch = input.charAt(i);

                if (currentState == 0 && ch == 'b') {
                    acceptedTransitions.append("δ: q0 x b → q2 (Accepted Path)\n");
                    addTransitionToOutput(acceptedTransitions.toString(), AcceptedOutputTM);
                    acceptedTransitions.setLength(0);
                    currentState = 2;

                    rejectedTransitions.append("δ: q0 x b → q0 (Rejected Path)\n");
                    addTransitionToOutput(rejectedTransitions.toString(), RejectedOutputTM);
                    rejectedTransitions.setLength(0);
                    rejectedState = 0;
                } else {
                    int prevState = currentState;
                    int prevRejectedState = rejectedState;

                    acceptedTransition(ch);
                    acceptedTransitions.append("δ: q")
                            .append(prevState)
                            .append(" x ").append(ch)
                            .append(" → q").append(currentState)
                            .append("\n");
                    addTransitionToOutput(acceptedTransitions.toString(), AcceptedOutputTM);
                    acceptedTransitions.setLength(0);

                    rejectedTransition(ch);
                    rejectedTransitions.append("δ: q")
                            .append(prevRejectedState)
                            .append(" x ").append(ch)
                            .append(" → q").append(rejectedState)
                            .append("\n");
                    addTransitionToOutput(rejectedTransitions.toString(), RejectedOutputTM);
                    rejectedTransitions.setLength(0);
                }
            }

            addTransitionToOutput(currentState == 2 ? "ACCEPTED" : "REJECTED", AcceptedOutputTM);
            addTransitionToOutput(rejectedState == 2 ? "ACCEPTED" : "REJECTED", RejectedOutputTM);
        } else {
            System.out.println("Invalid input. Only 'a' and 'b' are allowed.");
        }
    }

    private void clearFields() {
        if (characterAnimationTimeline != null) {
            characterAnimationTimeline.stop();
        }
        inputTextField.clear();
        currentState = 0;
        rejectedState = 0;
        AcceptedOutputTM.getChildren().clear();
        RejectedOutputTM.getChildren().clear();
    }

    private void setupButton(Button button) {
        button.setOnMouseEntered(event -> button.setEffect(new DropShadow(5, Color.GRAY)));
        button.setOnMouseExited(event -> button.setEffect(null));
    }

    private void applyShadowToImageView(ImageView imageView) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.GRAY);
        dropShadow.setRadius(10);
        imageView.setEffect(dropShadow);
    }

    private void addTransitionToOutput(String transition, FlowPane pane) {
        Label transitionLabel = new Label(transition);
        transitionLabel.setTextFill(Color.BLACK);
        transitionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: normal");
        pane.getChildren().add(transitionLabel);
        FlowPane.setMargin(transitionLabel, new Insets(10.0, 10.0, 10.0, 10.0));
    }

    private boolean isValid(String input) {
        return input.matches("[ab]*");
    }

    private void animateInput(String input) {
        currentState = 0;
        if (characterAnimationTimeline != null) {
            characterAnimationTimeline.stop();
        }

        characterAnimationTimeline = new Timeline();
        for (int i = 0; i < input.length(); i++) {
            final int charIndex = i;
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(charIndex * 1.5), event -> {
                char ch = input.charAt(charIndex);
                validateInput();
            });
            characterAnimationTimeline.getKeyFrames().add(keyFrame);
        }
        characterAnimationTimeline.play();
    }

    private void acceptedTransition(char ch) {
        switch (currentState) {
            case 0:
                currentState = (ch == 'a') ? 1 : (ch == 'b') ? 2 : 0;
                break;
            case 1:
                currentState = 1;
                break;
            case 2:
                currentState = 2;
                break;
        }
    }

    private void rejectedTransition(char ch) {
        switch (rejectedState) {
            case 0:
                rejectedState = (ch == 'a') ? 1 : 0;
                break;
            case 1:
                rejectedState = 1;
                break;
        }
    }
}
