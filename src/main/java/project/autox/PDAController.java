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
            try {
                int num = Integer.parseInt(input); // Get the number input
                String generatedInput = generateInput(num); // Generate 'aaaabbbb' based on the number
                Input_PDA.setText(generatedInput); // Set generated input
                currentState = 0; // Reset state
                stack.clear();
                stack.push('Z'); // Initial stack symbol
            } catch (NumberFormatException e) {
                System.out.println("Input is invalid. Enter a number.");
            }
        });

        Simulate_PDA.setOnAction(event -> {
            animateInput(Input_PDA.getText()); // Simulate using the generated input
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

    public boolean transition(char ch) {
        switch (currentState) {
            case 0:
                if (ch == 'a') {
                    stack.push('A');
                    animateSequentially(q0, q1); // Animate transition from q0 to q1
                    currentState = 1; // Move to state q1
                } else {
                    currentState = -1; // Trap state
                    return false;
                }
                break;

            case 1:
                if (ch == 'a') {
                    stack.push('A');
                    animateImageView(q1); // Stay in q1, loop on 'a'
                } else if (ch == 'b') {
                    if (stack.isEmpty() || stack.peek() != 'A') {
                        currentState = -1; // Trap state
                        return false;
                    }
                    stack.pop(); // Pop 'A' for each 'b'
                    animateImageView(q2); // Animate q1 -> q2 transition
                    currentState = 2; // Move to state q2
                } else {
                    currentState = -1; // Trap state
                    return false;
                }
                break;

            case 2:
                if (ch == 'b') {
                    if (stack.isEmpty() || stack.peek() != 'A') {
                        currentState = -1; // Trap state
                        return false;
                    }
                    stack.pop(); // Pop 'A' for 'b'
                    animateImageView(q2); // Stay in q2 while processing 'b'
                } else {
                    currentState = -1; // Trap state
                    return false;
                }
                break;

            default:
                return false;
        }

        // Check if stack is empty and in final state
        if (stack.size() == 1 && stack.peek() == 'Z' && currentState == 2) {
            stack.pop();
            currentState = 3; // Final state q3f
            animateSequentially(q2, q3f); // Animate q2 -> q3f
            System.out.println("accepted");
            return true;
        }

        return false;
    }

    private void animateSequentially(ImageView first, ImageView second) {
        animateImageView(first);
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> animateImageView(second));
        pause.play();
    }

    private void animateImageView(ImageView imageView) {
        imageView.setVisible(true);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), imageView);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }
}
