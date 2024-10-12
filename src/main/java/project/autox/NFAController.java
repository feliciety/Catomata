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

    private int currentState = 0; // State of the main automaton
    private int rejectedState = 0; // State of the rejected automaton
    private final int TRAP_STATE = 1; // Trap state
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

                int prevState = currentState;
                int prevRejectedState = rejectedState;

                acceptedTransition(ch);
                acceptedTransitions.append("δ: q")
                        .append(prevState)
                        .append(" x ").append(ch)
                        .append(" → q").append(currentState)
                        .append("\n");

                rejectedTransition(ch);
                rejectedTransitions.append("δ: q")
                        .append(prevRejectedState)
                        .append(" x ").append(ch)
                        .append(" → q").append(rejectedState)
                        .append("\n");

                addTransitionToOutput(acceptedTransitions.toString(), AcceptedOutputTM);
                addTransitionToOutput(rejectedTransitions.toString(), RejectedOutputTM);

                // If we reach the trap state in either accepted or rejected, show the trap state message.
                if (currentState == TRAP_STATE) {
                    addTransitionToOutput("TRAP STATE", AcceptedOutputTM);
                    break;
                }
                if (rejectedState == TRAP_STATE) {
                    addTransitionToOutput("TRAP STATE", RejectedOutputTM);
                    break;
                }

                acceptedTransitions.setLength(0);
                rejectedTransitions.setLength(0);
            }


            if (currentState == 2) {
                addTransitionToOutput("ACCEPTED", AcceptedOutputTM);
            } else if (currentState == TRAP_STATE) {
                addTransitionToOutput("TRAP STATE", AcceptedOutputTM);
            } else {
                addTransitionToOutput("REJECTED", AcceptedOutputTM);
            }

            if (rejectedState == 2) {
                addTransitionToOutput("ACCEPTED", RejectedOutputTM);
            } else if (rejectedState == TRAP_STATE) {
                addTransitionToOutput("TRAP STATE", RejectedOutputTM);
            } else {
                addTransitionToOutput("REJECTED", RejectedOutputTM);
            }
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

    private void addTransitionToOutput(String transition, FlowPane outputPane) {
        // Split the transition string by line breaks to handle each line separately
        String[] lines = transition.split("\n");

        for (String line : lines) {
            Label transitionLabel = new Label(line);

            // Set default text color for transition details
            transitionLabel.setTextFill(Color.BLACK);
            transitionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold");

            // Check if the line is "ACCEPTED" or "REJECTED" and set the color accordingly
            if (line.trim().equals("ACCEPTED")) {
                transitionLabel.setTextFill(Color.GREEN);
            } else if (line.trim().equals("REJECTED") || line.trim().equals("TRAP STATE")) {
                transitionLabel.setTextFill(Color.RED);
            }

            outputPane.getChildren().add(transitionLabel);
            FlowPane.setMargin(transitionLabel, new Insets(10.0, 10.0, 10.0, 10.0));
        }
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
            case 0: // Start state
                if (ch == 'b') {
                    currentState = 2; // Move to state 2
                    animateImageView(q2f);
                } else if (ch == 'a') {
                    currentState = 1; // Move to state 1
                    animateImageView(q1);
                }
                break;
            case 1: // State 1
                if (ch == 'a') {
                    currentState = TRAP_STATE; // Trap state
                    animateImageView(q1); // Keep showing state 1
                }
                break;
            case 2: // Accepting state
                animateImageView(q2f);
                break;
            default:
                currentState = TRAP_STATE; // Go to trap state for invalid input
        }
    }

    private void rejectedTransition(char ch) {
        switch (rejectedState) {
            case 0: // q0
                if (ch == 'b') {
                    rejectedState = 0; // Remain in q0
                    animateImageView(q0reject);
                } else if (ch == 'a') {
                    rejectedState = TRAP_STATE; // Transition to trap state
                    animateImageView(q1rejected);
                }
                break;
            case TRAP_STATE: // trap state for rejected input
                break; // No transitions possible from trap state
            default:
                rejectedState = TRAP_STATE; // Handle invalid input
        }
    }

    private void animateSequentially(ImageView first, ImageView second) {
        animateImageView(first);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.0));
        pause.setOnFinished(event -> animateImageView(second));
        pause.play();
    }

    private void animateImageView(ImageView imageView) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), imageView);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }
}
