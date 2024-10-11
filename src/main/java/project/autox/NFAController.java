package project.autox;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Set;

public class NFAController {

    private Set<Integer> currentStates = new HashSet<>(); // Track multiple states

    @FXML
    private TextField inputTextField;
    @FXML
    private Button ValidateBTN;
    @FXML
    private Button SimulateBTN;
    @FXML
    private Button ClearBTN;

    @FXML
    private ImageView q0, q1f, q2;

    public void initialize() {
        currentStates.add(0); // Start at q0

        setupButton(ValidateBTN);
        setupButton(SimulateBTN);
        setupButton(ClearBTN);

        applyShadowToImageView(q0);
        applyShadowToImageView(q1f);
        applyShadowToImageView(q2);

        ValidateBTN.setOnAction(event -> {
            String input = inputTextField.getText();
            if (isValid(input)) {
                boolean isAccepted = validateInput(input);
                System.out.println(isAccepted ? "Input is accepted by NFA." : "Input is rejected by NFA.");
            } else {
                System.out.println("Invalid input. Only 'a' and 'b' are allowed.");
            }
        });

        SimulateBTN.setOnAction(event -> {
            animateInput(inputTextField.getText());
        });

        ClearBTN.setOnAction(event -> {
            clearInput();
        });
    }

    private void clearInput() {
        inputTextField.clear();
        currentStates.clear();
        currentStates.add(0); // Reset to q0
    }

    private void applyShadowToImageView(ImageView imageView) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.50));
        shadow.setRadius(5);
        shadow.setOffsetX(0);
        shadow.setOffsetY(2);
        imageView.setEffect(shadow);
    }

    private void setupButton(Button button) {
        DropShadow shadow = createShadowEffect();
        addFloatingEffect(button, shadow);
        addClickAnimation(button, shadow);
    }

    private DropShadow createShadowEffect() {
        DropShadow newShadow = new DropShadow();
        newShadow.setColor(Color.rgb(0, 0, 0, 0.25));
        newShadow.setRadius(4);
        newShadow.setOffsetX(0);
        newShadow.setOffsetY(1);
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

            shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        });

        button.setOnMouseReleased(event -> {
            ScaleTransition releaseTransition = new ScaleTransition(Duration.millis(150), button);
            releaseTransition.setFromX(0.95);
            releaseTransition.setFromY(0.95);
            releaseTransition.setToX(1.0);
            releaseTransition.setToY(1.0);
            releaseTransition.setOnFinished(e -> {
                shadow.setColor(Color.rgb(0, 0, 0, 0.25));
            });
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
        currentStates.clear(); // Reset to initial states
        currentStates.add(0);
        int[] index = {0}; // Using array to modify in lambda

        // Animate the initial state q0
        animateImageView(q0, () -> {
            // After animating q0, start processing the input
            animateNextCharacter(input, index);
        });
    }

    private void animateNextCharacter(String input, int[] index) {
        if (index[0] < input.length()) {
            char ch = input.charAt(index[0]);
            boolean transitionSuccessful = transition(ch);

            if (transitionSuccessful) {
                // Animate the corresponding states
                ImageView currentImageView = null;
                if (currentStates.contains(0)) {
                    currentImageView = q0;
                }
                if (currentStates.contains(1)) {
                    currentImageView = q1f;
                }
                if (currentStates.contains(2)) {
                    currentImageView = q2;
                }

                if (currentImageView != null) {
                    // Animate the current state
                    animateImageView(currentImageView, () -> {
                        // After the animation, wait briefly before moving to the next character
                        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                        pause.setOnFinished(event -> {
                            index[0]++; // Move to the next character
                            animateNextCharacter(input, index); // Call again for the next character
                        });
                        pause.play();
                    });
                }
            } else {
                System.out.println("Transition failed for character: " + ch);
            }
        }
    }

    public boolean transition(char ch) {
        Set<Integer> nextStates = new HashSet<>();

        for (Integer state : currentStates) {
            switch (state) {
                case 0: // q0
                    if (ch == 'a') {
                        nextStates.add(1); // Transition to q1
                        nextStates.add(2); // Transition to q2
                    } else if (ch == 'b') {
                        nextStates.add(0); // Stay in q0
                    }
                    break;
                case 1: // q1
                    if (ch == 'a') {
                        nextStates.add(1); // Stay in q1 (loop on 'a')
                    }
                    break;
                case 2: // q2
                    // q2 is a terminating state after 'a', no further transitions
                    break;
                default:
                    return false; // Invalid state
            }
        }
        currentStates = nextStates; // Update to the new set of active states
        return !currentStates.isEmpty(); // Return true if there are active states
    }

    private boolean validateInput(String input) {
        currentStates.clear(); // Reset for validation
        currentStates.add(0); // Start from q0

        for (char ch : input.toCharArray()) {
            if (!transition(ch)) {
                return false; // Input is rejected if we reach an invalid transition
            }
        }
        return currentStates.contains(2); // Accept only if we end in q2 (final state)
    }

    private void animateImageView(ImageView imageView, Runnable onFinished) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), imageView);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2); // Scale up
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);

        scaleTransition.setOnFinished(event -> {
            if (onFinished != null) {
                onFinished.run(); // Call the callback if provided
            }
        });

        scaleTransition.play(); // Start the animation
    }
}
