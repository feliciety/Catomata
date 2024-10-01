package project.autox;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
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
    private Button ClearBTN;

    @FXML
    private ImageView q0, q1, q2, q3, q4, q5, q6, q7, q8, q9, q10f;

    public void initialize() {
        // Set up button animations
        setupButton(ValidateBTN);
        setupButton(SimulateBTN);
        setupButton(ClearBTN);

        // Apply shadow effect to state indicators
        applyShadowToImageView(q0);
        applyShadowToImageView(q1);
        applyShadowToImageView(q2);
        applyShadowToImageView(q3);
        applyShadowToImageView(q4);
        applyShadowToImageView(q5);
        applyShadowToImageView(q6);
        applyShadowToImageView(q7);
        applyShadowToImageView(q8);
        applyShadowToImageView(q9);
        applyShadowToImageView(q10f);

        ValidateBTN.setOnAction(event -> {
            String input = inputTextField.getText();
            if (isValid(input)) {
                currentState = 0; // Reset for new input to start at q0
                animateInput(input); // Animate the input character by character
            } else {
                System.out.println("Input is invalid. Only 'a' and 'b' are allowed.");
            }
        });

        SimulateBTN.setOnAction(event -> {
            simulateInput(inputTextField.getText());
        });
        ClearBTN.setOnAction(event -> {
            inputTextField.clear();
        });

    }

    private void applyShadowToImageView(ImageView imageView) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.50)); // RGB color with alpha
        shadow.setRadius(5); // Gaussian blur radius
        shadow.setOffsetX(0); // Horizontal offset
        shadow.setOffsetY(2); // Vertical offset
        imageView.setEffect(shadow); // Set the shadow effect to the ImageView
    }


    private void setupButton(Button button) {
        DropShadow shadow = createShadowEffect();
        addFloatingEffect(button, shadow); // Add shadow to the button
        addClickAnimation(button, shadow); // Add click animation to the button
    }

    private DropShadow createShadowEffect() {
        DropShadow newShadow = new DropShadow();
        newShadow.setColor(Color.rgb(0, 0, 0, 0.25)); // RGB color with alpha
        newShadow.setRadius(4); // Gaussian blur radius
        newShadow.setOffsetX(0); // Horizontal offset
        newShadow.setOffsetY(1); // Vertical offset
        return newShadow;
    }

    private void addFloatingEffect(Button button, DropShadow shadow) {
        button.setEffect(shadow);
    }

    private void removeShadowEffect(DropShadow shadow) {
        animateShadowSize(shadow, 10, 3, 0.2, 0.0, 150);
    }

    private void restoreShadowEffect(DropShadow shadow) {
        animateShadowSize(shadow, 3, 10, 0.0, 0.2, 150);
    }

    private void animateShadowSize(DropShadow shadow, double fromRadius, double toRadius, double fromOpacity, double toOpacity, int durationMillis) {
        Transition transition = new Transition() {
            {
                setCycleDuration(Duration.millis(durationMillis));
            }

            @Override
            protected void interpolate(double frac) {
                double currentRadius = fromRadius + (toRadius - fromRadius) * frac;
                double currentOpacity = fromOpacity + (toOpacity - fromOpacity) * frac;

                shadow.setRadius(currentRadius);
                shadow.setColor(Color.rgb(0, 0, 0, currentOpacity)); // Update shadow color with new opacity
            }
        };
        transition.play();
    }

    private void addClickAnimation(Button button, DropShadow shadow) {
        button.setOnMousePressed(event -> {
            // Shrink the button and gradually reduce the shadow when pressed
            ScaleTransition pressTransition = new ScaleTransition(Duration.millis(150), button);
            pressTransition.setFromX(1.0);
            pressTransition.setFromY(1.0);
            pressTransition.setToX(0.95);  // Shrink button slightly when pressed
            pressTransition.setToY(0.95);
            pressTransition.play();

            removeShadowEffect(shadow);  // Reduce the shadow smoothly
        });

        button.setOnMouseReleased(event -> {
            // Restore the button size and bring back the shadow when released
            ScaleTransition releaseTransition = new ScaleTransition(Duration.millis(150), button);
            releaseTransition.setFromX(0.95);
            releaseTransition.setFromY(0.95);
            releaseTransition.setToX(1.0);  // Restore button size
            releaseTransition.setToY(1.0);
            releaseTransition.setOnFinished(e -> restoreShadowEffect(shadow));  // Restore the shadow smoothly
            releaseTransition.play();
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
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 2), event -> {
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

    private void animateSequentially(ImageView first, ImageView second) {
        // Animate the first state (e.g., q0)
        animateImageView(first);

        // Create a sequential transition to animate the second state after the first
        PauseTransition pause = new PauseTransition(Duration.seconds(1)); // Add delay between animations
        pause.setOnFinished(event -> animateImageView(second)); // Animate the second state after the first
        pause.play();
    }

    public boolean transition(char ch) {
        switch (currentState) {
            case 0: // q0
                if (ch == 'a') {
                    currentState = 1; // Transition to q1
                    animateSequentially(q0, q1); // Animate q0 first, then q1
                } else if (ch == 'b') {
                    currentState = 4; // Transition to q4
                    animateSequentially(q0, q4); // Animate q0 first, then q4
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
                if (ch == 'a' || ch == 'b') {
                    currentState = -1; // Transition to trap state
                    animateImageView(q2); // Animate q2 in both cases
                }
                break;
            case 3: // q3
                if (ch == 'a') {
                    currentState = 6; // Transition to q6
                    animateImageView(q6);
                } else if (ch == 'b') {
                    currentState = -1; // Trap state
                    animateImageView(q2);
                }
                break;
            case 4: // q4
                if (ch == 'a') {
                    currentState = 5; // Transition to q5
                    animateImageView(q5);
                } else if (ch == 'b') {
                    currentState = -1; // Trap state
                    animateImageView(q2);
                }
                break;
            case 5: // q5
                if (ch == 'a') {
                    currentState = -1; // Trap state
                    animateImageView(q2);
                } else if (ch == 'b') {
                    currentState = 6; // Transition to q6
                    animateImageView(q6);
                }
                break;
            case 6: // q6
                if (ch == 'a') {
                    currentState = 6;
                    animateImageView(q6);
                }
                else if (ch == 'b') {
                    currentState = 7; // Transition to q7
                    animateImageView(q7);
                }
                break;
            case 7: // q7
                if (ch == 'a') {
                    currentState = 8; // Transition to q8
                    animateImageView(q8);
                }
                else if (ch == 'b') {
                    currentState = 7; // Transition to q7
                    animateImageView(q7);
                }
                break;
            case 8: // q8
                if (ch == 'a') {
                    currentState = 6; // Transition back to q6
                    animateImageView(q6);
                } else if (ch == 'b') {
                    currentState = 9; // Transition to q9
                    animateImageView(q9);
                }
                break;
            case 9: // q9
                if (ch == 'a' || ch == 'b') {
                    currentState = 10; // Transition to final state q10
                    animateImageView(q10f);
                }
                break;
            default:
                return false; // Invalid state
        }
        return true; // Valid transition
    }

    private void animateImageView(ImageView imageView) {
        imageView.setVisible(true);

        // Create a shadow effect for the ImageView
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.25)); // Initial shadow color with 25% opacity
        shadow.setRadius(4); // Shadow blur radius
        shadow.setOffsetX(0); // Horizontal offset
        shadow.setOffsetY(2); // Vertical offset

        imageView.setEffect(shadow); // Apply the shadow to the imageView

        // Scale transition for the ImageView
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), imageView);
        scaleTransition.setFromX(1);    // Original size
        scaleTransition.setFromY(1);    // Original size
        scaleTransition.setToX(1.2);    // Scale up to 120%
        scaleTransition.setToY(1.2);    // Scale up to 120%
        scaleTransition.setCycleCount(2); // Scale up and down
        scaleTransition.setAutoReverse(true); // Reverse scaling back to original size

        // Shadow fade in/out transition synchronized with scaling
        Timeline shadowFadeEffect = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(shadow.colorProperty(), Color.rgb(0, 0, 0, 0.50))), // Initial shadow
                new KeyFrame(Duration.millis(500), new KeyValue(shadow.colorProperty(), Color.rgb(0, 0, 0, 0))), // Fade out shadow
                new KeyFrame(Duration.millis(1000), new KeyValue(shadow.colorProperty(), Color.rgb(0, 0, 0, 0.50))) // Fade back in
        );

        // Ensure shadow resets to full opacity at the end of animation
        scaleTransition.setOnFinished(event -> {
            shadow.setColor(Color.rgb(0, 0, 0, 0.50)); // Reset shadow to full opacity after animation
        });

        // Play both the scale transition and shadow fade effect
        scaleTransition.play();
        shadowFadeEffect.play();
    }
}
