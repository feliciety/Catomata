package project.autox;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class NFAController {

    private int currentStates = 0; // Track multiple states

    @FXML
    private TextField inputTextField;
    @FXML
    private Button ValidateBTN;
    @FXML
    private Button SimulateBTN;
    @FXML
    private Button ClearBTN;

    @FXML
    private ImageView q0, q1, q2f;

    public void initialize() {
        currentStates = 0; // Start at q0

        setupButton(ValidateBTN);
        setupButton(SimulateBTN);
        setupButton(ClearBTN);

        applyShadowToImageView(q0);
        applyShadowToImageView(q1);
        applyShadowToImageView(q2f);

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
        currentStates = 0;
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
        currentStates = 0;
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
                if (currentStates == 0) {
                    currentImageView = q0;
                } else if (currentStates == 1) {
                    currentImageView = q1;
                } else if (currentStates == 2) {
                    currentImageView = q2f;
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
        } else {
            // Handle when the input has been fully processed
            if (currentStates == 2) {
                System.out.println("Input accepted. Ended in final state q2.");
            } else {
                System.out.println("Input rejected. Did not end in final state.");
            }
        }
    }

    public boolean transition(char ch) {
        switch (currentStates) {
            case 0: // q0
                if (ch == 'b') {
                    currentStates = 1; // Transition to q1
                    return true;
                }
                break;
            case 1: // q1
                if (ch == 'a') {
                    currentStates = 2; // Transition to q2 (final state)
                    return true;
                }
                else if (ch == 'b'){
                    currentStates = 1;
                    return true;
                }
                break;
            case 2: // q2 (Trap state)
                if (ch == 'b') {
                    currentStates = 1; // Transition back to q1 (looping in trap state)
                    return true;
                }
                else if (ch == 'a'){
                    currentStates = 2;
                }
                break;
        }
        return false; // Transition was unsuccessful
    }

    private boolean validateInput(String input) {
        currentStates = 0; // Reset for validation

        for (char ch : input.toCharArray()) {
            if (!transition(ch)) {
                return false; // Input is rejected if we reach an invalid transition
            }
        }
        return currentStates == 2; // Accept only if we end in q2 (final state)
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