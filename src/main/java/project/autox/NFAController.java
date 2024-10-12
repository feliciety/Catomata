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

                    // Handle non-deterministic transitions explicitly for 'b' at q0
                    if (currentState == 0 && ch == 'b') {
                        // For accepted path: transition from q0 to q2
                        acceptedTransitions.append("δ: q0 x b → q2 (Accepted Path)\n");
                        addTransitionToOutput(acceptedTransitions.toString(), OutputTM);
                        acceptedTransitions.setLength(0); // Clear the acceptedTransitions buffer
                        currentState = 2;

                        // For rejected path: transition from q0 to q0 (self-loop)
                        rejectedTransitions.append("δ: q0 x b → q0 (Rejected Path)\n");
                        addTransitionToOutput(rejectedTransitions.toString(), RejectedOutputTM);
                        rejectedTransitions.setLength(0); // Clear the rejectedTransitions buffer
                        rejectedState = 0; // Remains in q0 for the rejected path
                    } else {
                        // Handle normal transitions for both paths
                        int prevState = currentState;
                        int prevRejectedState = rejectedState;

                        // Process accepted path transition
                        boolean success = Acceptedtransition(ch);
                        acceptedTransitions.append("δ: q")
                                .append(prevState)
                                .append(" x ").append(ch)
                                .append(" → q").append(currentState)
                                .append("\n");
                        addTransitionToOutput(acceptedTransitions.toString(), OutputTM);
                        acceptedTransitions.setLength(0);

                        // Process rejected path transition
                        boolean rejectedSuccess = rejectedTransition(ch);
                        rejectedTransitions.append("δ: q")
                                .append(prevRejectedState)
                                .append(" x ").append(ch)
                                .append(" → q").append(rejectedState)
                                .append("\n");
                        addTransitionToOutput(rejectedTransitions.toString(), RejectedOutputTM);
                        rejectedTransitions.setLength(0);
                    }
                }

                // Add ACCEPTED or REJECTED status at the end of each path's output
                if (currentState == 2) {
                    acceptedTransitions.append("ACCEPTED");
                } else {
                    acceptedTransitions.append("REJECTED");
                }
                addTransitionToOutput(acceptedTransitions.toString(), OutputTM);

                if (rejectedState == 2) {
                    rejectedTransitions.append("ACCEPTED");
                } else {
                    rejectedTransitions.append("REJECTED");
                }
                addTransitionToOutput(rejectedTransitions.toString(), RejectedOutputTM);
            } else {
                System.out.println("Invalid input. Only 'a' and 'b' are allowed.");
            }
        });




        //SimulateBTN.setOnAction(event -> animateInput(inputTextField.getText()));
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

    public boolean Acceptedtransition(char ch) {
        switch (currentState) {
            case 0:
                if (ch == 'a') {
                    currentState = 1;
                } else if (ch == 'b') {
                    currentState = 2; // Transition to q2 for accepted path
                }
                break;
            case 1:
                // q1 is a trap state for accepted path
                if (ch == 'a' || ch == 'b') {
                    currentState = 1;
                }
                break;
            case 2:
                // q2 self-loops for both inputs
                if (ch == 'a' || ch == 'b') {
                    currentState = 2;
                }
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
                    rejectedState = 0; // Loop in q0 for rejected path with 'b'
                }
                break;
            case 1:
                // q1 is a trap state for rejected path
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
