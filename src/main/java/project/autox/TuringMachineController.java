package project.autox;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

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

    // Turing Machine Inner Class
    public static class TuringMachine {
        private enum State { Q0, Q1, Q2, Q3, Q4, Q5f }

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
            for (int i = tape.length - 1; i >= 0; i--) {
                if (tape[i] != 'B') {
                    this.head = i;
                    break;
                }
            }
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
                    case Q0:
                        if (currentSymbol == '1') {
                            tape[head] = '0'; head--; currentState = State.Q1;
                        } else if (currentSymbol == '0') {
                            tape[head] = '1'; head--; currentState = State.Q0;
                        } else if (currentSymbol == '+') {
                            tape[head] = 'B'; head++; currentState = State.Q4;
                        } else if (currentSymbol == 'B') {
                            head--; currentState = State.Q0;
                        }
                        break;
                    case Q1:
                        if (currentSymbol == '1' || currentSymbol == '0' || currentSymbol == 'B') {
                            head--; currentState = State.Q1;
                        } else if (currentSymbol == '+') {
                            tape[head] = '+'; head--; currentState = State.Q3;
                        }
                        break;
                    case Q2:
                        if (currentSymbol == '1' || currentSymbol == '0' || currentSymbol == '+') {
                            head++; currentState = State.Q2;
                        } else if (currentSymbol == 'B') {
                            tape[head] = 'B'; head--; currentState = State.Q0;
                        }
                        break;
                    case Q3:
                        if (currentSymbol == '0') {
                            tape[head] = '1'; head++; currentState = State.Q2;
                        } else if (currentSymbol == '1') {
                            tape[head] = '0'; head--; currentState = State.Q3;
                        } else if (currentSymbol == 'B') {
                            tape[head] = '1'; head++; currentState = State.Q2;
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
                String result = tm.run();

                // Create a Text node to display the result
                Text outputText = new Text("Binary1: " + binary1 + "\nBinary2: " + binary2 + "\n" + result);

                // Clear the previous output and add the new result to OutputTM
                OutputTM.getChildren().clear();
                OutputTM.getChildren().add(outputText);

                // Adjust the position and layout of the output text within the AnchorPane
                AnchorPane.setTopAnchor(outputText, 10.0);
                AnchorPane.setLeftAnchor(outputText, 10.0);
            }}

    @FXML
    public void handleClear() {
        Binput1.clear();
        Binput2.clear();
        Result.getChildren().clear();
    }
}
