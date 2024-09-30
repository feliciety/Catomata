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


    private static boolean isValid(String input) {
        return input.matches("[ab]+");
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
                case 0:
                    if (ch == 'a') {
                        stack.push('A');
                    } else if (ch == 'b') {

                        if (stack.isEmpty()) {
                            return false;
                        }
                        currentState = 1;
                        stack.pop();
                    } else {
                        return false; // Invalid input
                    }
                    break;

                case 1:
                    if (ch == 'b') {
                        if (!stack.isEmpty()) {
                            stack.pop();
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                    break;

                default:
                    return false;
            }
        }


        return stack.isEmpty();
    }
}