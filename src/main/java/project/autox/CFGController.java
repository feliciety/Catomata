package project.autox;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CFGController {

    @FXML
    private TextField InputCFG;  // fx:id="InputCFG"

    @FXML
    private TextArea OutputCFG;  // fx:id="OutputCFG"

    @FXML
    private Button ValidateCFG;  // fx:id="ValidateCFG"

    @FXML
    private Button clearBTN;  // fx:id="clearBTN"

    // Handle the validation button click
    @FXML
    private void handleValidateCFG() {
        String inputString = InputCFG.getText();  // Get input from InputCFG TextField

        // Validate input: only allows 'a' and 'b'
        if (!inputString.matches("[ab]+")) {
            OutputCFG.setText("Invalid input! Only 'a' and 'b' are allowed.");
            return; // Stop execution if invalid input
        }

        OutputCFG.clear(); // Clear previous derivations
        StringBuilder derivation = new StringBuilder("S"); // Start with S

        // Start the derivation process
        derive(inputString, derivation);
    }

    // Clear button handler
    @FXML
    private void handleClear() {
        InputCFG.clear();  // Clear the input field
        OutputCFG.clear(); // Clear the output text area
    }

    private void derive(String input, StringBuilder current) {
        int index = current.indexOf("S");

        // Base case: If there's no more 'S', stop recursion
        if (index == -1) {
            OutputCFG.appendText("S -> " + current.toString() + "\n");
            return;
        }

        // Ensure index is within bounds of input string
        if (index >= input.length()) {
            return;
        }

        // Derive based on the current input
        char nextChar = input.charAt(index);

        // Apply rules based on the next character
        if (nextChar == 'a') {
            // Display the production rule and update in one step
            System.out.println("S -> " + current.toString().replace("S", "aSa"));
            // Replace 'S' with 'aSa'
            current.replace(index, index + 1, "aSa");
            OutputCFG.appendText("S -> " + current.toString() + "\n"); // Show derivation step
            derive(input, current); // Continue with new derivation

        } else if (nextChar == 'b') {
            // Display the production rule and update in one step
            System.out.println("S -> " + current.toString().replace("S", "bSb"));
            // Replace 'S' with 'bSb'
            current.replace(index, index + 1, "bSb");
            OutputCFG.appendText("S -> " + current.toString() + "\n"); // Show derivation step
            derive(input, current); // Continue with new derivation
        }

        // If S still exists, replace with 'c' to show a completed derivation
        if (current.indexOf("S") != -1) {
            current.replace(current.indexOf("S"), current.indexOf("S") + 1, "c");
            System.out.println("S -> " + current.toString());
            OutputCFG.appendText("S -> " + current.toString() + "\n"); // Show final replacement
        }
    }
}
