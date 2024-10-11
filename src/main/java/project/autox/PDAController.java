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

                // Output should always match the number of 'a's and 'b's
                System.out.println("Generated Input: " + generatedInput); // Debugging output
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
            Label stackLabel = new Label(c.toString()); // Create a label for each stack element

            // Apply styling to make the label look like a square
            stackLabel.setMinWidth(40); // Set a fixed width
            stackLabel.setMinHeight(40); // Set a fixed height (same as width for a square)
            stackLabel.setStyle("-fx-border-color: black; -fx-border-width: 2px; " +
                    "-fx-alignment: center; -fx-font-size: 18px; " +
                    "-fx-font-family: 'Monospaced'; -fx-background-color: lightgray;");

            // Add individual labels to the FlowPane
            OutputPDA.getChildren().add(stackLabel);
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
            input.append("a"); // Add 'a' n times
        }
        for (int i = 0; i < n; i++) {
            input.append("b"); // Add 'b' n times
        }
        return input.toString(); // Correctly returns 'aaaabbbb' for n = 4
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
            case 0: // Initial state q0
                System.out.println("Current State: " + currentState + ", Stack Size: " + stack.size());
                if (ch == 'a') { // Handle first 'a' in q0
                    stack.push('A'); // Push 'A' for the first 'a'
                    animateImageView(q1);
                    currentState = 1; // Move to q1 after pushing 'A'
                    return true;
                }
                return false; // If anything else is encountered in state 0, reject it

            case 1: // In state q1
                if (ch == 'a') { // Expecting 'a'
                    System.out.println("Current State: " + currentState + ", Stack Size: " + stack.size());
                    stack.push('A'); // Push 'A' onto the stack
                    animateImageView(q1); // Animate q1
                    return true; // Continue processing
                } else if (ch == 'b') { // Transition to state q2 on 'b'
                    if (stack.isEmpty()) {
                        return false; // Transition not accepted
                    }
                    // Only transition to q2 if there is something in the stack
                    currentState = 2; // Move to state q2
                    return transition(ch); // Call transition again for 'b'
                }
                break;

            case 2: // In state q2
                if (ch == 'b') {
                    if (stack.isEmpty()) {
                        return false; // Transition not accepted
                    }
                    stack.pop(); // Pop 'A' for each 'b'
                    animateImageView(q2); // Animate q2
                    return true; // Continue processing
                }
                break;
        }

        // Handle epsilon transition and other conditions as required
        if (currentState == 2 && ch == 'ε' && !stack.isEmpty() && stack.peek() == 'Z') {
            stack.pop(); // Pop 'Z'
            stack.push('ε'); // Push empty symbol (you can represent it as 'ε')
            updateStackVisualization(); // Update the stack visualization after pop and push
            animateImageView(q3f); // Animate q3f
            currentState = 3; // Final state q3f
            return true; // Accepted
        }

        return false; // Return false if no valid transition is found
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
