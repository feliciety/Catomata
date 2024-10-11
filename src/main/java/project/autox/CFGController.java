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

    @FXML
    public void initialize() {
    }

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

        // Base case: If there's no more 'S', output the final derivation
        if (index == -1) {
            OutputCFG.appendText(current.toString() + "\n"); // Output the final string
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
            current.replace(index, index + 1, "aSa");
            OutputCFG.appendText("S -> " + current.toString() + "\n"); // Show derivation step
            derive(input, current); // Continue with new derivation
        } else if (nextChar == 'b') {
            current.replace(index, index + 1, "bSb");
            OutputCFG.appendText("S -> " + current.toString() + "\n"); // Show derivation step
            derive(input, current); // Continue with new derivation
        }

        // After processing, if 'S' still exists, replace it with 'C'
        if (current.indexOf("S") != -1) {
            current.replace(current.indexOf("S"), current.indexOf("S") + 1, "C"); // Replace 'S' with 'C'
            OutputCFG.appendText("S -> " + current.toString() + "\n"); // Show intermediate string

            // Generate output after replacing 'S' with 'C'
            // We replace 'C' with an empty string, simulating the derivation process
            current.replace(current.indexOf("C"), current.indexOf("C") + 1, ""); // Remove 'C'
            OutputCFG.appendText("S -> " + current.toString() + "\n"); // Output the result after C is removed
        }
    }
}

