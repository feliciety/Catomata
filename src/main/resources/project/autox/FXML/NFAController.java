package project.autox.FXML;

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
import javafx.geometry.Insets;

public class NFAController {

    private int currentState = 0; // Start at q0 (accepted state)
    private int rejectedState = 0; // Add a rejectedState tracker
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
    private FlowPane OutputTM;
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

        ValidateBTN.setOnAction(event -> {
            OutputTM.getChildren().clear();
            RejectedOutputTM.getChildren().clear();

            String input = inputTextField.getText();

            if (isValid(input)) {
                currentState = 0;
                rejectedState = 0;

                StringBuilder acceptedTransitions = new StringBuilder();
                StringBuilder rejectedTransitions = new StringBuilder();

                for (int i = 0; i < input.length(); i++) {
                    char ch = input.charAt(i);

                    // Non-deterministic transitions for 'b' input at q0
                    if (currentState == 0 && ch == 'b') {
                        // Accepted transitions for 'b' in q0
                        acceptedTransitions.append("δ: q0 x b → q0 (Accepted Path)\n");
                        addTransitionToOutput(acceptedTransitions.toString(), OutputTM);
                        acceptedTransitions.setLength(0);

                        acceptedTransitions.append("δ: q0 x b → q2 (Accepted Path)\n");
                        addTransitionToOutput(acceptedTransitions.toString(), OutputTM);
                        acceptedTransitions.setLength(0);
                        currentState = 2;

                        // Rejected transitions for 'b' in q0
                        rejectedTransitions.append("δ: q0 x b → q0 (Rejected Path)\n");
                        addTransitionToOutput(rejectedTransitions.toString(), RejectedOutputTM);
                        rejectedTransitions.setLength(0);

                        rejectedTransitions.append("δ: q0 x b → q2 (Rejected Path)\n");
                        addTransitionToOutput(rejectedTransitions.toString(), RejectedOutputTM);
                        rejectedTransitions.setLength(0);
                        rejectedState = 2;
                    } else {
                        // Handle normal transitions for both accepted and rejected paths
                        int prevState = currentState;
                        int prevRejectedState = rejectedState;
                        boolean success = transition(ch);
                        boolean rejectedSuccess = rejectedTransition(ch);

                        // Add transitions to respective outputs
                        acceptedTransitions.append("δ: q")
                                .append(prevState)
                                .append(" x ").append(ch)
                                .append(" → q").append(currentState)
                                .append("\n");
                        addTransitionToOutput(acceptedTransitions.toString(), OutputTM);
                        acceptedTransitions.setLength(0);

                        rejectedTransitions.append("δ: q")
                                .append(prevRejectedState)
                                .append(" x ").append(ch)
                                .append(" → q").append(rejectedState)
                                .append("\n");
                        addTransitionToOutput(rejectedTransitions.toString(), RejectedOutputTM);
                        rejectedTransitions.setLength(0);
                    }
                }

                // Add ACCEPTED or REJECTED to the end of the transitions for both paths
                acceptedTransitions.append(currentState == 2 ? "ACCEPTED" : "REJECTED");
                addTransitionToOutput(acceptedTransitions.toString(), OutputTM);

                rejectedTransitions.append(rejectedState == 2 ? "ACCEPTED" : "REJECTED");
                addTransitionToOutput(rejectedTransitions.toString(), RejectedOutputTM);
            } else {
                System.out.println("Invalid input. Only 'a' and 'b' are allowed.");
            }
        });

        SimulateBTN.setOnAction(event -> animateInput(inputTextField.getText()));
        ClearBTN.setOnAction(event -> {
            if (characterAnimationTimeline != null) {
                characterAnimationTimeline.stop();
            }
            inputTextField.clear();
            currentState = 0;
            rejectedState = 0;
            OutputTM.getChildren().clear();
            RejectedOutputTM.getChildren().clear();
        });
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

    public boolean transition(char ch) {
        switch (currentState) {
            case 0:
                if (ch == 'a') {
                    currentState = 1;
                } else if (ch == 'b') {
                    currentState = 0; // Stay at q0 for one path
                }
                break;
            case 1:
                if (ch == 'a' || ch == 'b') {
                    currentState = 1;
                }
                break;
            case 2:
                break;
            default:
                return false;
        }
        return true;
    }

    public boolean rejectedTransition(char ch) {
        switch (rejectedState) {
            case 0:
                if (ch == 'a') {
                    rejectedState = 1;
                } else if (ch == 'b') {
                    rejectedState = 0; // Stay at q0 for one path
                }
                break;
            case 1:
                if (ch == 'a' || ch == 'b') {
                    rejectedState = 1;
                }
                break;
            case 2:
                break;
            default:
                return false;
        }
        return true;
    }
}
