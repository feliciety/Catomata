package project.autox;
import javafx.animation.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Stack;


public class PDAController {

    private Stack<Character> stack = new Stack<>();
    private static int currentState = 0; // Initial state q0

    @FXML
    private TextField Input_PDA;
    @FXML
    private Button Validate_PDA;
    @FXML
    private Button Simulate_PDA;
    @FXML
    private Button Clear_PDA;
    @FXML
    private ImageView q0, q1, q2, q3f;


    public void initialize() {
        // Set up button animations
        setupButton(Clear_PDA);
        setupButton(Validate_PDA);
        setupButton(Simulate_PDA);

        // Apply shadow effect to state indicators
        applyShadowToImageView(q0);
        applyShadowToImageView(q1);
        applyShadowToImageView(q2);
        applyShadowToImageView(q3f);

        Validate_PDA.setOnAction(event -> {
            String input = Input_PDA.getText();
            if (isValid(input)) {
                currentState = 0; // Reset state
                stack.clear();
                //stack.push('Z');

            } else {
                System.out.println("Input is invalid. Only 'a' and 'b' are allowed.");
            }


        });

        Simulate_PDA.setOnAction(event -> {
            animateInput(Input_PDA.getText());
        });

        Clear_PDA.setOnAction(event -> {
            Input_PDA.clear();
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

    public boolean transition(char ch) {
        switch (currentState) {
            case 0:
                if (ch == 'a') {
                    // Push 'A' onto the stack for each 'a'
                    stack.push('A');
                    animateSequentially(q0, q1); // Animate transition from q0 to q1
                    currentState = 1; // Move to state q1
                } else if (ch == 'b') {
                    // If 'b' is encountered in q0 without an 'A' on the stack, reject
                    if (stack.isEmpty() || stack.peek() != 'A') {
                        currentState = -1; // Enter trap state if invalid
                        return false;
                    }

                } else {
                    // Invalid input
                    currentState = -1; // Trap state
                    return false;
                }
                break;

            case 1:
                // In state q1, expect either 'a' (push) or 'b' (pop)
                if (ch == 'a') {
                    stack.push('A');
                    animateImageView(q1); // Stay in q1, loop on 'a'
                } else if (ch == 'b') {
                    if (stack.isEmpty() || stack.peek() != 'A') {
                        currentState = -1; // Enter trap state if invalid
                        return false;
                    }
                    stack.pop(); // Pop 'A' for each 'b'
                    animateImageView(q2); // Animate q1 -> q2 transition
                    currentState = 2; // Move to state q2
                } else {
                    currentState = -1; // Trap state for invalid input
                    return false;
                }
                break;

            case 2:
                // In state q2, expect only 'b' (keep popping from stack)
                if (ch == 'b') {
                    if (stack.isEmpty() || stack.peek() != 'A') {
                        currentState = -1; // Enter trap state if invalid
                        return false;
                    }
                    stack.pop(); // Pop 'A' for 'b'
                    animateImageView(q2); // Stay in q2 while processing 'b'

                } else {
                    currentState = -1; // Invalid transition to trap state
                    return false;
                }
                break;


            default:
                // If in trap state (-1), reject the input
                return false;
        }

        // If we have processed all characters, check if the stack is empty and we're in state q2
        if (stack.isEmpty() && currentState == 2) {
            currentState = 3; // Move to final state q3f (accepted)
            animateSequentially(q2, q3f);
            System.out.print("accepted");

            return true; // Input accepted
        }

        return false; // Input not yet accepted (continue processing)
    }
}