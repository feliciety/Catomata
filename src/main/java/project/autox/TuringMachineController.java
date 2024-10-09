package project.autox;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Arrays;

public class TuringMachineController {

    @FXML
    private TextField Binput1;
    @FXML
    private TextField Binput2;
    @FXML
    private FlowPane OutputTM;
    @FXML
    private TextField Lresult; // Changed to TextField

    // Turing Machine Inner Class
    public static class TuringMachine {
        private enum State {Q1, Q2, Q0, Q3, Q4, Q5f }

        private final char[] tape;
        private int head;
        private State currentState;
        private int stepCount;
        private final StringBuilder output = new StringBuilder();  // To store simulation output

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
            output.append("\nInitial tape: ").append(Arrays.toString(tape)).append("\n");
        }

        public String run() {
            while (currentState != State.Q5f) {
                if (head < 0 || head >= tape.length) {
                    output.append("Head moved out of bounds. Halting.\n");
                    break;
                }
                char currentSymbol = tape[head];
                stepCount++;
                output.append("\nStep ").append(stepCount).append(": \nHead at index ").append(head).append(", symbol: ").append(currentSymbol).append(", \nstate: ").append(currentState).append("\n");

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
            output.append("\nFinal tape: ").append(Arrays.toString(tape)).append("\n");
            output.append("\nTotal steps taken: ").append(stepCount).append("\n");
            return output.toString();
        }
    }

    // Action handler for the 'Simulate' button
    @FXML
    public void handleValidate() {
        String binary1 = Binput1.getText();
        String binary2 = Binput2.getText();

        if (!binary1.isEmpty() && !binary2.isEmpty()) {
            // Convert binary inputs to integers
            int bin1Int = Integer.parseInt(binary1, 2);
            int bin2Int = Integer.parseInt(binary2, 2);

            // Perform binary addition
            int sum = bin1Int + bin2Int;

            // Convert the sum back to binary string
            String binarySum = Integer.toBinaryString(sum);

            // Display binary addition result in Lresult (TextField now)
            Lresult.setText(binarySum); // Set the result text to Lresult

            // Set font style for the Lresult TextField
            Lresult.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            Lresult.setAlignment(javafx.geometry.Pos.CENTER);


            // Prepare the Turing Machine input
            String input = binary1 + "+" + binary2;
            TuringMachine tm = new TuringMachine(input);

            // Run the simulation and get the result
            String turingResult = tm.run();

            // Create a Text node to display the Turing Machine result
            Text outputText = new Text("Binary1: " + binary1 + "\nBinary2: " + binary2 + "\n" + turingResult);

            // Clear the previous output and add the new result to OutputTM
            OutputTM.getChildren().clear();
            OutputTM.getChildren().add(outputText);

            // Adjust the position and layout of the output text within the FlowPane
            FlowPane.setMargin(outputText, new javafx.geometry.Insets(10.0, 10.0, 10.0, 10.0));
        }
    }

    @FXML
    public void handleClear() {
        Binput1.clear();
        Binput2.clear();
        Lresult.clear(); // Clear the TextField
        OutputTM.getChildren().clear();
    }
}
