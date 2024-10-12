package project.autox;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.scene.layout.FlowPane;

public class NFARController {

    private int currentState = 0; // Start at q0
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
    private FlowPane RejectedOutputTM;

    @FXML
    private ImageView q0reject, q1rejected, q2frejected;

    public void initialize() {
        // Set up button animations
        setupButton(ValidateBTN);
        setupButton(SimulateBTN);
        setupButton(ClearBTN);

        // Apply shadow effect to state indicators
        applyShadowToImageView(q0reject);
        applyShadowToImageView(q1rejected);
        applyShadowToImageView(q2frejected);

        ValidateBTN.setOnAction(event -> {
            RejectedOutputTM.getChildren().clear(); // Clear previous output
            String input = inputTextField.getText(); // Get input from the text field
            if (isValid(input)) { // Check if the input is valid (contains only 'a' and 'b')
                StringBuilder transitions = new StringBuilder(); // Use StringBuilder to collect transitions

                for (int i = 0; i < input.length(); i++) {
                    char ch = input.charAt(i);
                    int previousState = currentState; // Store the current state before transition
                    boolean success = transition(ch); // Perform the transition

                    // Append the current transition to the StringBuilder with newline
                    transitions.append("δ: q").append(previousState)
                            .append(" x ").append(ch)
                            .append(" → q").append(currentState)
                            .append("\n");
                }

                if (currentState == 2) { // Check if the final state is q2 (accepting state)
                    transitions.append("ACCEPTED"); // Add ACCEPTED to the output
                } else {
                    transitions.append("REJECTED"); // Add REJECTED to the output
                }

                // Finally, add the transitions to the FlowPane
                addTransitionToOutput(transitions.toString());
            } else {
                System.out.println("Invalid input. Only 'a' and 'b' are allowed."); // Invalid input error
            }
        });

        SimulateBTN.setOnAction(event -> {
            animateInput(inputTextField.getText());
        });

        ClearBTN.setOnAction(event -> {
            if (characterAnimationTimeline != null) {
                characterAnimationTimeline.stop(); // Stop the current animation
            }
            inputTextField.clear(); // Clear the input text field
            currentState = 0; // Reset current state when clearing input
            RejectedOutputTM.getChildren().clear(); // Clear rejected output
        });
    }


    private void setupButton(Button button) {
        button.setOnMouseEntered(event -> {
            button.setEffect(new DropShadow(5, Color.GRAY)); // Add shadow on hover
        });
        button.setOnMouseExited(event -> {
            button.setEffect(null); // Remove shadow
        });
    }

    private void applyShadowToImageView(ImageView imageView) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.GRAY);
        dropShadow.setRadius(10);
        imageView.setEffect(dropShadow);
    }

    private void addTransitionToOutput(String transition) {
        Label transitionLabel = new Label(transition);
        transitionLabel.setTextFill(Color.BLACK);
        transitionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: normal");
        RejectedOutputTM.getChildren().add(transitionLabel);
        FlowPane.setMargin(transitionLabel, new javafx.geometry.Insets(10.0, 10.0, 10.0, 10.0)); // Set margin for each label
    }

    private boolean isValid(String input) {
        return input.matches("[ab]*"); // Check if input contains only 'a' and 'b'
    }

    private void animateInput(String input) {
        if (characterAnimationTimeline != null) {
            characterAnimationTimeline.stop(); // Stop any ongoing animation
        }

        Timeline timeline = new Timeline();
        for (int i = 0; i < input.length(); i++) {
            final char ch = input.charAt(i);
            final int index = i; // final variable for use in the lambda expression

            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i), event -> {
                boolean transitionSuccess = transition(ch);
                if (transitionSuccess) {
                    animateImageView(getCurrentStateImageView());
                    moveStateImage(getCurrentStateImageView()); // Move the state image
                } else {
                    animateImageView(q2frejected); // If transition fails, animate the trap state
                    moveStateImage(q2frejected); // Move to the trap state
                }
            });
            timeline.getKeyFrames().add(keyFrame);
        }

        characterAnimationTimeline = timeline;
        timeline.play();
    }

    private void moveStateImage(ImageView imageView) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), imageView);
        translateTransition.setFromX(imageView.getLayoutX());
        translateTransition.setToX(imageView.getLayoutX() + 50); // Move 50 pixels to the right
        translateTransition.setFromY(imageView.getLayoutY());
        translateTransition.setToY(imageView.getLayoutY()); // Keep Y position constant
        translateTransition.play();
    }

    private ImageView getCurrentStateImageView() {
        switch (currentState) {
            case 0: return q0reject; // State q0
            case 1: return q1rejected; // State q1
            case 2: return q2frejected; // State q2
            default: return null;
        }
    }

    public boolean transition(char ch) {
        switch (currentState) {
            case 0: // q0
                if (ch == 'b') {
                    currentState = 0; // Stay in q0
                    animateImageView(q0reject); // Animate q0
                } else if (ch == 'a') {
                    currentState = 2; // Transition to trap state
                    animateImageView(q2frejected);
                }
                break;
            case 1:
                if (ch == 'a') {
                    currentState = 2; // Transition to trap state
                    animateImageView(q2frejected);
                }
                break;
            case 2: // Trap state
                // Stay in trap state for any input
                animateImageView(q2frejected);
                break;
            default:
                return false; // Transition was unsuccessful
        }

        // Stop animation if still in q0
        if (currentState == 0) {
            return false;
        }

        return true; // Transition was successful
    }

    private void animateImageView(ImageView imageView) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), imageView);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2); // Scale up
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }
}
