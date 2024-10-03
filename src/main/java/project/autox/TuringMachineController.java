package project.autox;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.ScrollPane;

import java.util.Arrays;

public class TuringMachineController {

    @FXML
    private TextField Binput1;
    @FXML
    private TextField Binput2;
    @FXML
    private AnchorPane OutputTM;
    @FXML
    private Pane Result;
    @FXML
    private ScrollPane scrollPane;  // Add a reference to the ScrollPane

    // Turing Machine Inner Class
    public static class TuringMachine {
        private enum State {Q1, Q2, Q0, Q3, Q4, Q5f}

        private char[] tape;
        private int head;
        private State currentState;
        private int stepCount;
        private StringBuilder output = new StringBuilder();  // To store simulation output

        public TuringMachine(String input) {
            int tapeSize = input.length() + 6;
            this.tape = new char[tapeSize];
            Arrays.fill(this.tape, 'B');

            int inputStart = 3;
            for (int i = 0; i < input.length(); i++) {
                this.tape[i + inputStart] = input.charAt(i);
            }
            this.head = inputStart;
            this.currentState = State.Q0;
            this.stepCount = 0;
            output.append("Initial tape: ").append(Arrays.toString(tape)).append("\n");
        }

        public String run() {
            while (currentState != State.Q5f) {
                if (head < 0 || head >= tape.length) {
                    output.append("Head moved out of bounds. Halting.\n");
                    break;
                }
                char currentSymbol = tape[head];
                stepCount++;
                output.append("Step ").append(stepCount).append(": Head at index ").append(head).append(", symbol: ").append(currentSymbol).append(", state: ").append(currentState).append("\n");

                switch (currentState) {
                    case Q1:
                        if (currentSymbol == '1') {
                            tape[head] = '0'; head--; currentState = State.Q2;
                        } else if (currentSymbol == '0') {
                            tape[head] = '1'; head--; currentState = State.Q1;
                        } else if (currentSymbol == '+') {
                            tape[head] = 'B'; head++; currentState = State.Q4;
                        } else if (currentSymbol == 'B') {
                            head--; currentState = State.Q1;
                        }
                        break;
                    case Q2:
                        if (currentSymbol == '1' || currentSymbol == '0' || currentSymbol == 'B') {
                            head--; currentState = State.Q2;
                        } else if (currentSymbol == '+') {
                            tape[head] = '+'; head--; currentState = State.Q3;
                        }
                        break;
                    case Q0:
                        if (currentSymbol == '1' || currentSymbol == '0' || currentSymbol == '+') {
                            head++; currentState = State.Q0;
                        } else if (currentSymbol == 'B') {
                            tape[head] = 'B'; head--; currentState = State.Q1;
                        }
                        break;
                    case Q3:
                        if (currentSymbol == '0') {
                            tape[head] = '1'; head++; currentState = State.Q0;
                        } else if (currentSymbol == '1') {
                            tape[head] = '0'; head--; currentState = State.Q3;
                        } else if (currentSymbol == 'B') {
                            tape[head] = '1'; head++; currentState = State.Q0;
                        } else if (currentSymbol == '+') {
                            head--; currentState = State.Q3;
                        }
                        break;
                    case Q4:
                        if (currentSymbol == '1') {
                            tape[head] = 'B'; head++; currentState = State.Q4;
                        } else if (currentSymbol == 'B') {
                            tape[head] = 'B'; currentState = State.Q5f;
                        }
                        break;
                    default:
                        currentState = State.Q5f;
                        break;
                }
                output.append("Tape after state ").append(currentState).append(": ").append(Arrays.toString(tape)).append("\n");
            }
            output.append("Final tape: ").append(Arrays.toString(tape)).append("\n");
            output.append("Total steps taken: ").append(stepCount).append("\n");
            return output.toString();
        }
    }

    // Action handler for the 'Simulate' button
    @FXML
    public void handleValidate() {
        String binary1 = Binput1.getText();
        String binary2 = Binput2.getText();

        if (!binary1.isEmpty() && !binary2.isEmpty()) {
            // Prepare the Turing Machine input
            String input = binary1 + "+" + binary2;
            TuringMachine tm = new TuringMachine(input);

            // Run the simulation and get the result
            String turingResult = tm.run();

            // Convert binary inputs to integers, perform addition, and convert back to binary
            int bin1Int = Integer.parseInt(binary1, 2);
            int bin2Int = Integer.parseInt(binary2, 2);
            int sum = bin1Int + bin2Int;
            String binarySum = Integer.toBinaryString(sum);

            // Clear previous results in the Pane Result
            Result.getChildren().clear();

            // Display binary addition result in Pane Result with styling
            Text additionResult = new Text(binarySum);
            additionResult.setFont(Font.font("Arial", FontWeight.BOLD, 16));  // Set font size to 16 and bold
            Result.getChildren().add(additionResult);

            // Create a Text node to display the Turing Machine result with styling
            Text outputText = new Text("Binary1: " + binary1 + "\nBinary2: " + binary2 + "\n" + turingResult);
            outputText.setFont(Font.font("Arial", 14));  // Set font size to 14 for Turing machine result
            OutputTM.getChildren().clear();
            OutputTM.getChildren().add(outputText);

            // Adjust the position and layout of the output text within the AnchorPane
            AnchorPane.setTopAnchor(outputText, 10.0);
            AnchorPane.setLeftAnchor(outputText, 10.0);

            // Dynamically adjust the height of the OutputTM based on content
            double lineHeight = 20;  // Adjust as necessary
            int numberOfLines = turingResult.split("\n").length + 3; // +3 for the input binary lines
            double newHeight = numberOfLines * lineHeight;

            OutputTM.setPrefHeight(newHeight); // Update the preferred height
            scrollPane.setPrefHeight(newHeight + 20); // Add some padding if needed

            // Scroll to the bottom of the ScrollPane
            Platform.runLater(() -> {
                scrollPane.setVvalue(1.0);  // Scroll to the bottom
            });
        }
    }


    @FXML
    public void handleClear() {
        Binput1.clear();
        Binput2.clear();
        OutputTM.getChildren().clear();
        Result.getChildren().clear();  // Clear the binary addition result as well
    }
}
