package project.autox;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.List;

public class TuringMachineController {

    @FXML
    private TextField Binput1, Binput2;

    @FXML
    private ScrollPane InfiniteTape; // Changed to ScrollPane

    @FXML
    private Button ValidateBTN, clearBTN;

    @FXML
    private Pane TuringOutput; // Declare TuringOutput pane

    private List<Text> tape = new ArrayList<>();

    @FXML
    public void handleValidate() {
        String input1 = Binput1.getText();
        String input2 = Binput2.getText();

        if (isValidBinary(input1) && isValidBinary(input2)) {
            String result = addBinary(input1, input2);
            System.out.println("Adding: " + input1 + " + " + input2 + " = " + result); // Debugging line
            displayInitialTape(input1, input2); // Display initial tape with inputs
            displayOutput(result); // Display the result in TuringOutput pane
        } else {
            displayError("Invalid binary input!");
        }
    }

    @FXML
    public void handleClear() {
        Binput1.clear();
        Binput2.clear();
        InfiniteTape.setContent(null); // Clear previous tape
        TuringOutput.getChildren().clear(); // Clear previous output
        tape.clear();
    }

    private boolean isValidBinary(String input) {
        return input.matches("[01]+");
    }

    // Binary addition logic
    private String addBinary(String binary1, String binary2) {
        StringBuilder result = new StringBuilder();
        int carry = 0;
        int i = binary1.length() - 1;
        int j = binary2.length() - 1;

        // Iterate from the end to the beginning of both binary strings
        while (i >= 0 || j >= 0 || carry > 0) {
            int bit1 = (i >= 0) ? binary1.charAt(i--) - '0' : 0;
            int bit2 = (j >= 0) ? binary2.charAt(j--) - '0' : 0;

            int sum = bit1 + bit2 + carry;
            result.append(sum % 2); // Append result bit
            carry = sum / 2; // Update carry
        }

        return result.reverse().toString(); // Reverse result
    }

    // Display initial tape with inputs
    private void displayInitialTape(String input1, String input2) {
        // Format the initial tape string
        String initialTape = "Initial Tape: [_" + input1 + "B" + input2 + "_]";
        String ArrowHead = "Head ^";
        Text tapeText = new Text(initialTape + "\n          " +ArrowHead);

        tapeText.setStyle("-fx-font-size: 20px; -fx-padding: 10; -fx-border-color: black; -fx-border-width: 2;"); // Style for the tape text

        InfiniteTape.setContent(tapeText); // Set the initial tape text as content of the ScrollPane

        // Scroll to the left to show the initial tape
        InfiniteTape.setHvalue(0);
    }

    // Display output in TuringOutput pane
    private void displayOutput(String result) {
        TuringOutput.getChildren().clear(); // Clear previous output

        // Create a new Text object with the output result
        Text outputText = new Text(result);
        outputText.setStyle("-fx-font-size: 20px; -fx-padding: 10;"); // Style for the output

        TuringOutput.getChildren().add(outputText); // Add the output to the TuringOutput pane
    }

    // Error handling (could be shown in UI)
    private void displayError(String errorMsg) {
        System.out.println(errorMsg);
        // Optional: Show error message in the UI
    }
}
