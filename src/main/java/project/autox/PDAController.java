package project.autox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
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

    // Validation to ensure the input contains only 'a' and 'b'
    private static boolean isValid(String input) {
        return input.matches("[ab]+"); // Check if input contains only 'a' and 'b'
    }

    public void initialize() {
        Validate_PDA.setOnAction(event -> {
            String input = Input_PDA.getText();
            if (isValid(input)) {
                currentState = 0; // Reset state
                stack.clear();
                boolean isAccepted = transition(input);
                if (isAccepted) {
                    System.out.println("The input is accepted by the PDA.");
                } else {
                    System.out.println("The input is rejected by the PDA.");
                }
            } else {
                System.out.println("Input is invalid. Only 'a' and 'b' are allowed.");
            }
        });
    }

    public boolean transition(String input) {
        for (char ch : input.toCharArray()) {
            switch (currentState) {
                case 0: // q0 (initial state)
                    if (ch == 'a') {
                        stack.push('A'); // Push 'A' for each 'a'
                    } else if (ch == 'b') {
                        // Transition to q1 only if we see 'b' and the stack is not empty
                        if (stack.isEmpty()) {
                            return false; // Reject if there's no 'A' to match
                        }
                        currentState = 1; // Move to state q1
                        stack.pop(); // Pop 'A' for the first 'b'
                    } else {
                        return false; // Invalid input
                    }
                    break;

                case 1: // q1 (after reading 'b's)
                    if (ch == 'b') {
                        if (!stack.isEmpty()) {
                            stack.pop(); // Pop 'A' for each 'b'
                        } else {
                            return false; // Reject if there's no matching 'A'
                        }
                    } else {
                        return false; // Invalid input, must be 'b'
                    }
                    break;

                default:
                    return false; // Should not reach here
            }
        }

        // Accept only if the stack is empty at the end (indicating equal number of a's and b's)
        return stack.isEmpty();
    }
}