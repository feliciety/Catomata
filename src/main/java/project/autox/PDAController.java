package project.autox;

import javafx.animation.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Stack;

public class PDAController {

    private Stack<Character> stack = new Stack<>();
    private static int currentState = 0; // Initial state q0
    private Timeline animationTimeline; // Declare the timeline variable

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
    @FXML
    private Label PDAresult; // Declare the Label for displaying the result
    @FXML
    private FlowPane OutputPDA; // FlowPane for visualizing the stack

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
            try {
                int num = Integer.parseInt(input); // Get the number input
                String generatedInput = generateInput(num); // Generate 'aaaabbbb' based on the number
                PDAresult.setText(generatedInput); // Update PDAresult with generated input
                currentState = 0; // Reset state
                stack.clear();
                stack.push('Z'); // Initial stack symbol
                updateStackVisualization(); // Update FlowPane to show initial stack state
            } catch (NumberFormatException e) {
                System.out.println("Input is invalid. Enter a number.");
            }
        });

        Simulate_PDA.setOnAction(event -> {
            String input = PDAresult.getText(); // Get the generated input
            if (!input.isEmpty()) { // Check if input is not empty
                animateInput(input); // Simulate using the generated input
            } else {
                System.out.println("Input is empty. Please enter a value to simulate.");
            }
        });

        Clear_PDA.setOnAction(event -> {
            Input_PDA.clear();
            PDAresult.setText(""); // Clear the result label
            stack.clear(); // Clear stack
            updateStackVisualization(); // Update stack visualization after clearing
        });
    }

    private void updateStackVisualization() {
        OutputPDA.getChildren().clear(); // Clear previous stack visualization
        for (Character c : stack) {
            Label label = new Label(String.valueOf(c)); // Create label for each stack element
            label.setStyle("-fx-border-color: black; -fx-padding: 5;"); // Add some styling
            OutputPDA.getChildren().add(label); // Add label to the FlowPane
        }
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

    private void addClickAnimation(Button button, DropShadow shadow) {
        button.setOnMousePressed(event -> {
            ScaleTransition pressTransition = new ScaleTransition(Duration.millis(150), button);
            pressTransition.setFromX(1.0);
            pressTransition.setFromY(1.0);
            pressTransition.setToX(0.95);
            pressTransition.setToY(0.95);
            pressTransition.play();
        });

        button.setOnMouseReleased(event -> {
            ScaleTransition releaseTransition = new ScaleTransition(Duration.millis(150), button);
            releaseTransition.setFromX(0.95);
            releaseTransition.setFromY(0.95);
            releaseTransition.setToX(1.0);
            releaseTransition.setToY(1.0);
            releaseTransition.play();
        });
    }

    private String generateInput(int n) {
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < n; i++) {
            input.append("a");
        }
        for (int i = 0; i < n; i++) {
            input.append("b");
        }
        return input.toString();
    }

    public void animateInput(String input) {
        animationTimeline = new Timeline(); // Initialize the timeline variable
        int[] index = {0}; // Using array to modify in lambda

        animationTimeline.setCycleCount(input.length() + 1); // Adjusted for ε transition
        for (int i = 0; i < input.length(); i++) { // Loop through the input
            final int charIndex = i;
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 1), event -> { // Adjust duration for visibility
                char ch = input.charAt(charIndex);
                System.out.println("Current Character: " + ch); // Debug statement
                if (!transition(ch)) { // Call transition without storing the result
                    animationTimeline.stop(); // Stop animation if transition fails
                }
                updateStackVisualization(); // Update stack visualization after each step
            });
            animationTimeline.getKeyFrames().add(keyFrame);
        }

        // Add epsilon transition immediately after the last character
        KeyFrame epsilonFrame = new KeyFrame(Duration.seconds(input.length()), event -> {
            if (!transition('ε')) { // Add epsilon transition at the end
                animationTimeline.stop(); // Stop if transition fails
            }
            updateStackVisualization(); // Update stack visualization for ε transition
        });
        animationTimeline.getKeyFrames().add(epsilonFrame);

        animationTimeline.play();
    }

    public boolean transition(char ch) {
        switch (currentState) {
            case 0: // From q0
                // Do not read 'a' in q0, just animate and move to state q1
                animateImageView(q0); // Animate q0 (without fade)
                currentState = 1; // Move to state q1
                return true;

            case 1: // In q1
                if (ch == 'a') { // Expecting 'a' in state q1
                    stack.push('A'); // Push A onto the stack for each 'a'
                    animateImageView(q1); // Animate q1 (without fade)
                    return true;
                } else if (ch == 'b') { // Move to state q2 on 'b'
                    if (stack.isEmpty()) {
                        return false; // Transition not accepted
                    }
                    stack.pop(); // Pop A for each 'b'
                    animateImageView(q1); // Animate q1 (without fade)
                    currentState = 2; // Move to state q2
                    return true;
                }
                break;

            case 2: // In q2
                if (ch == 'b') {
                    if (stack.isEmpty()) {
                        return false; // Transition not accepted
                    }
                    stack.pop(); // Pop A for each 'b'
                    animateImageView(q2); // Animate q2 (without fade)
                    return true;
                }
                break;
        }

        // Check for epsilon transition
        if (currentState == 2 && ch == 'ε') {
            if (stack.isEmpty()) {
                animateImageView(q3f); // Animate q3f (without fade)
                currentState = 3; // Move to state q3f
                System.out.println("Accepted");
                return true; // Accepted
            }
        }

        // Check if stack is empty and in final state
        if (stack.size() == 1 && stack.peek() == 'Z' && currentState == 2) {
            stack.pop();
            currentState = 3; // Final state q3f
            animateImageView(q3f); // Animate q3f (without fade)
            System.out.println("Accepted");
            return true;
        }

        return false; // If no valid transition is found, reject the input
    }

    // Simple image view animation (for staying in the same state)
    private void animateImageView(ImageView imageView) {
        imageView.setVisible(true);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), imageView);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2); // Scale up and down once
        scaleTransition.play();
    }
}
