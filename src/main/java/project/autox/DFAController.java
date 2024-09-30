package project.autox;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;

public class DFAController {

    private int currentState = 0; // Start at q0

    @FXML
    private TextField inputTextField;
    @FXML
    private Button ValidateBTN;
    @FXML
    private Button SimulateBTN;

    @FXML
    private ImageView q0, q1, q2, q3, q4, q5, q6, q7, q8, q9, q10f;

    public void initialize() {
        ValidateBTN.setOnAction(event -> {
            String input = inputTextField.getText();
            if (isValid(input)) {
                currentState = 0; // Reset for new input to start at q0
                resetImageViews(); // Reset image views before processing
                animateInput(input); // Animate the input character by character
            } else {
                System.out.println("Input is invalid. Only 'a' and 'b' are allowed.");
            }
        });

        SimulateBTN.setOnAction(event -> {
            simulateInput(inputTextField.getText());
        });
    }

    private boolean isValid(String input) {
        for (char c : input.toCharArray()) {
            if (c != 'a' && c != 'b') {
                return false;
            }
        }
        return true;
    }

    private void simulateInput(String input) {
        // Add any additional simulation logic if needed
    }

    public void animateInput(String input) {
        Timeline timeline = new Timeline();
        int[] index = {0}; // Using array to modify in lambda

        timeline.setCycleCount(input.length());
        for (int i = 0; i < input.length(); i++) {
            final int charIndex = i;
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 1.5), event -> { // Reduced time for smoother animation
                if (index[0] < input.length()) {
                    char ch = input.charAt(index[0]);
                    if (currentState == -1) {
                        System.out.println("In Trap state.");
                        return; // Exit if in trap state
                    }
                    transition(ch);
                    index[0]++;
                }
            });
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.play();
    }

    public boolean transition(char ch) {
        switch (currentState) {
            case 0: // q0
                if (ch == 'a') {
                    currentState = 1; // Transition to q1
                    animateImageView(q0);
                } else if (ch == 'b') {
                    currentState = 4; // Transition to q4
                    animateImageView(q0); // Assuming you animate q0 here as well
                }
                break;
            case 1: // q1
                if (ch == 'a') {
                    currentState = 2; // Trap state
                    animateImageView(q2);
                } else if (ch == 'b') {
                    currentState = 3; // Transition to q3
                    animateImageView(q3);
                }
                break;
            case 2: // q2
                if (ch == 'a') {
                    currentState = 1; // Transition back to q1
                    animateImageView(q2);
                } else if (ch == 'b') {
                    currentState = 3; // Transition to q3
                    animateImageView(q2);
                }
                break;
            case 3: // q3
                if (ch == 'a') {
                    currentState = 4; // Transition to q4
                    animateImageView(q3);
                } else if (ch == 'b') {
                    currentState = -1; // Trap state
                }
                break;
            case 4: // q4
                if (ch == 'a') {
                    currentState = 5; // Transition to q5
                    animateImageView(q4);
                } else if (ch == 'b') {
                    currentState = -1; // Trap state
                }
                break;
            case 5: // q5
                if (ch == 'a') {
                    currentState = -1; // Trap state
                } else if (ch == 'b') {
                    currentState = 6; // Transition to q6
                    animateImageView(q5);
                }
                break;
            case 6: // q6
                if (ch == 'b') {
                    currentState = 7; // Transition to q7
                    animateImageView(q6);
                }
                break;
            case 7: // q7
                if (ch == 'a') {
                    currentState = 8; // Transition to q8
                    animateImageView(q7);
                }
                break;
            case 8: // q8
                if (ch == 'a') {
                    currentState = 6; // Transition back to q6
                    animateImageView(q5);
                } else if (ch == 'b') {
                    currentState = 9; // Transition to q9
                    animateImageView(q8);
                }
                break;
            case 9: // q9
                if (ch == 'a' || ch == 'b') {
                    currentState = 10; // Transition to final state q10
                    animateImageView(q9);
                }
                break;
            case 10: // q10 (accepting state)
                animateImageView(q10f); // Final state
                break;
            default:
                return false; // Invalid state
        }
        return isAcceptingState();
    }

    public boolean isAcceptingState() {
        return currentState == 10; // Final accepting state
    }

    private void resetImageViews() {
        ImageView[] imageViews = {q0, q1, q2, q3, q4, q5, q6, q7, q8, q9, q10f};
        for (ImageView imageView : imageViews) {
            imageView.setScaleX(1.0);
            imageView.setScaleY(1.0);
            imageView.setEffect(null); // Reset shadow effect
        }
        animateImageView(q0); // Start at q0
    }

    private void animateImageView(ImageView imageView) {
        if (imageView != null) {
            // Create a DropShadow effect with a lighter color
            DropShadow shadow = new DropShadow();
            shadow.setOffsetX(5);
            shadow.setOffsetY(5);
            shadow.setRadius(10);
            shadow.setSpread(0.5);
            shadow.setColor(javafx.scene.paint.Color.LIGHTGRAY); // Lighter shadow color

            // Apply the shadow effect
            imageView.setEffect(shadow);

            // Scale transition to enlarge the imageView with a gooey effect
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), imageView);
            scaleTransition.setFromX(1.0);
            scaleTransition.setFromY(1.0);
            scaleTransition.setToX(1.3); // More exaggerated scale for gooey effect
            scaleTransition.setToY(1.3); // More exaggerated scale for gooey effect
            scaleTransition.setCycleCount(1);

            // Return transition to shrink the imageView back
            ScaleTransition returnTransition = new ScaleTransition(Duration.seconds(0.5), imageView);
            returnTransition.setFromX(1.3);
            returnTransition.setFromY(1.3);
            returnTransition.setToX(1.0);
            returnTransition.setToY(1.0);
            returnTransition.setCycleCount(1);

            // Add elasticity to the transitions
            scaleTransition.setOnFinished(event -> {
                imageView.setEffect(null);
                // Create a second scale transition to add more gooey effect
                ScaleTransition extraBounce = new ScaleTransition(Duration.seconds(0.2), imageView);
                extraBounce.setFromX(1.0);
                extraBounce.setFromY(1.0);
                extraBounce.setToX(1.1);
                extraBounce.setToY(1.1);
                extraBounce.setCycleCount(1);
                extraBounce.setOnFinished(evt -> {
                    // Scale down back to original
                    ScaleTransition finalBounce = new ScaleTransition(Duration.seconds(0.2), imageView);
                    finalBounce.setFromX(1.1);
                    finalBounce.setFromY(1.1);
                    finalBounce.setToX(1.0);
                    finalBounce.setToY(1.0);
                    finalBounce.play();
                });
                extraBounce.play();
            });

            // Play transitions
            scaleTransition.play();
            scaleTransition.setOnFinished(event -> returnTransition.play());
        }
    }
}
