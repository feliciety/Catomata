package project.autox;

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

public class NFAController {

    private int currentState = 0; // Start at q0
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
    private ImageView q0, q1, q2f;

    public void initialize() {
        // Set up button animations
        setupButton(ValidateBTN);
        setupButton(SimulateBTN);
        setupButton(ClearBTN);

        // Apply shadow effect to state indicators
        applyShadowToImageView(q0);
        applyShadowToImageView(q1);
        applyShadowToImageView(q2f);

        ValidateBTN.setOnAction(event -> {
            OutputTM.getChildren().clear();
            String input = inputTextField.getText(); // Get input from the text field
            if (isValid(input)) { // Check if the input is valid (contains only 'a' and 'b')
                StringBuilder transitions = new StringBuilder(); // Use StringBuilder to collect transitions

                for (int i = 0; i < input.length(); i++) {
                    char ch = input.charAt(i);
                    int previousState = currentState; // Store the current state before transition
                    boolean success = transitionForValidation(ch); // Perform the transition

                    // Append the current transition to the StringBuilder with newline
                    transitions.append("δ: q").append(previousState)
                            .append(" x ").append(ch)
                            .append(" → q").append(currentState)
                            .append("\n");
                }

                if (currentState == 2) { // Check if the final state is q2 (accepting state)
                    transitions.append("ACCEPTED"); // Add ACCEPTED to the output
                } else {
                    transitions.append("REJECTED"); // Add REJECTED to the output
                }

                // Finally, add the transitions to the FlowPane
                addTransitionToOutput(transitions.toString());
            } else {
                System.out.println("Invalid input. Only 'a' and 'b' are allowed."); // Invalid input error
            }
        });

        SimulateBTN.setOnAction(event -> {
            String input = inputTextField.getText(); // Get the input from the text field
            if (isValid(input)) { // Check if the input is valid
                animateInput(input); // Proceed with the animation if input is valid
            } else {
                System.out.println("Invalid input. Only 'a' and 'b' are allowed."); // Notify about invalid input
            }
        });

        ClearBTN.setOnAction(event -> {
            if (characterAnimationTimeline != null) {
                characterAnimationTimeline.stop(); // Stop the current animation
            }
            inputTextField.clear(); // Clear the input text field
            currentState = 0; // Reset current state when clearing input
            OutputTM.getChildren().clear();
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
        // Check if the input is empty or if the first character is 'a'
        if (input.isEmpty() || input.charAt(0) == 'a') {
            return false; // Return false if the first character is 'a'
        }

        // Continue checking the rest of the characters
        for (char c : input.toCharArray()) {
            if (c != 'a' && c != 'b') {
                return false; // Return false if any other character is not 'a' or 'b'
            }
        }

        return true; // Return true if the input is valid and does not start with 'a'
    }



    public void animateInput(String input) {
        currentState = 0; // Reset to the initial state
        Timeline characterAnimationTimeline = new Timeline(); // Initialize the timeline
        int[] index = {0}; // Using array to modify in lambda

        characterAnimationTimeline.setCycleCount(input.length());
        for (int i = 0; i < input.length(); i++) {
            final int charIndex = i; // Capture the current character index
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 1.5), event -> { // Change duration for visible transitions
                if (index[0] < input.length()) {
                    char ch = input.charAt(index[0]);

                    transition(ch); // Transition based on the character
                    index[0]++; // Move to the next character
                }
            });
            characterAnimationTimeline.getKeyFrames().add(keyFrame); // Add keyframe to the character animation timeline
        }
        characterAnimationTimeline.play(); // Play the character animation timeline
    }



    private void animateSequentially(ImageView first, ImageView second) {
        // Animate the first state (e.g., q0)
        animateImageView(first);

        // Create a sequential transition to animate the second state after the first
        PauseTransition pause = new PauseTransition(Duration.seconds(1.0)); // Add delay between animations
        pause.setOnFinished(event -> animateImageView(second)); // Animate the second state after the first
        pause.play();
    }


    public void simulateTransition(String input) {
        // Reset the current state to the initial state (q0)
        currentState = 0;

        // Validate the input first
        if (!validateInput(input)) {
            System.out.println("Invalid input: Input can only contain 'a' and 'b'");
            return; // Stop the simulation if input is invalid
        }

        // Process each character in the input string
        for (char ch : input.toCharArray()) {
            if (!transition(ch)) {
                System.out.println("Transition unsuccessful for character: " + ch);
                return; // Stop the simulation if transition fails
            }
        }

        // Check if the final state is accepting or not
        if (currentState == 2) {
            System.out.println("Input accepted");
        } else {
            System.out.println("Input not accepted");
        }
    }

    public boolean transition(char ch) {
        switch (currentState) {
            case 0: // q0
                switch (ch) {
                    case 'a':
                        currentState = 2; // Transition to q2
                        animateSequentially(q0, q2f); // Animate q0 first, then q2
                        break;
                    case 'b':
                        animateImageView(q0); // Stay in q0
                        break;
                    default:
                        return false; // Invalid character (shouldn't reach here due to validation)
                }
                break;
            case 1: // q1 (non-accepting state)
                switch (ch) {
                    case 'a':
                        currentState = 1; // Stay in q1
                        animateImageView(q1);
                        break;
                    case 'b':
                        currentState = 0; // Transition back to q0
                        animateImageView(q0);
                        break;
                    default:
                        return false; // Invalid character (shouldn't reach here due to validation)
                }
                break;
            case 2: // q2 (accepting state)
                switch (ch) {
                    case 'a':
                        currentState = 2; // Stay in q2
                        animateImageView(q2f);
                        break;
                    case 'b':
                        currentState = 2; // Stay in q2
                        animateImageView(q2f);
                        break;
                    default:
                        return false; // Invalid character (shouldn't reach here due to validation)
                }
                break;
            default:
                return false; // Invalid state
        }

        // Stop animation if still in q0
        if (currentState == 0) {
            return false;
        }

        return true; // Transition was successful
    }



    private void addTransitionToOutput(String transition) {
        // Split the transition string by line breaks to handle each line separately
        String[] lines = transition.split("\n");

        for (String line : lines) {
            Label transitionLabel = new Label(line);

            // Set default text color for transition details
            transitionLabel.setTextFill(Color.BLACK);
            transitionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold");

            // Check if the line is "ACCEPTED" or "REJECTED" and set the color accordingly
            if (line.trim().equals("ACCEPTED")) {
                transitionLabel.setTextFill(Color.GREEN); // Set color to green for ACCEPTED
            } else if (line.trim().equals("REJECTED")) {
                transitionLabel.setTextFill(Color.RED); // Set color to red for REJECTED
            }

            OutputTM.getChildren().add(transitionLabel); // Add the label to the FlowPane
            FlowPane.setMargin(transitionLabel, new javafx.geometry.Insets(10.0, 10.0, 10.0, 10.0)); // Set margin for each label
        }
    }





    public boolean transitionForValidation(char ch) {
        switch (currentState) {
            case 0: // q0
               if (ch == 'b') {
                    currentState = 1; // Transition to q1
                }
                break;
            case 1: // q1
                if (ch == 'a') {
                    currentState = 2; // Transition to trap state (q2)
                } else if (ch == 'b') {
                    currentState = 1; // Stay in q1
                }
                break;
            case 2: // q2 (Trap state)
                if (ch == 'a') {
                    currentState = 2; // Trap state
                    animateImageView(q2f);
                } else if (ch == 'b') {
                    animateImageView(q1);
                }
                break;
            default:
                return false; // Transition was unsuccessful
        }

        // Reject immediately if we are still in q0 after processing
        return false;
    }



    private boolean validateInput(String input) {
        if (input.isEmpty() || input.charAt(0) == 'a') {
            return false; // Return false if the first character is 'a'
        }

        // Continue checking the rest of the characters
        for (char c : input.toCharArray()) {
            if (c != 'a' && c != 'b') {
                return false; // Return false if any other character is not 'a' or 'b'
            }
        }

        return true; // Return true if the input is valid and does not start with 'a'
    }



    private void animateImageView(ImageView imageView) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), imageView);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2); // Scale up
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }


}
