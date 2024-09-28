package project.autox;


import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;


public class DFAController{

    private static int currentState = 1; //initial state
    @FXML
    private TextField inputTextField;
    @FXML
    private Button ValidateBTN;
    @FXML
    private Button SimulateBTN;


    public void initialize() {

        ValidateBTN.setOnAction(event -> {
            String input = inputTextField.getText();
            if (isValid(input)) {
                currentState = 1;
                boolean isAccepted = transition(input);
                if (isAccepted) {
                    System.out.println("The input is accepted by the DFA.");
                } else if (currentState == -1) {
                    System.out.println("In Trap state.");
                } else {
                    System.out.println("The input is rejected by the DFA.");
                }
            } else {
                System.out.println("Input is invalid. Only 'a' and 'b' are allowed.");
            }
        });

        SimulateBTN.setOnAction(event -> {
            simulateInput(inputTextField.getText());
            //code simulate dri
        });



    }


    private static boolean isValid(String input) {
        for (char c : input.toCharArray()) {
            if (c != 'a' && c != 'b') {
                return false;
            }
        }
        return true;
    }

    private static void simulateInput(String input) {
        //simulate code dri
    }

    public static boolean transition(String input) {
        for (char ch : input.toCharArray()) {
            if (currentState == -1) {
                return false;
            }

            switch (currentState) {
                case 1:
                    if (ch == 'a') currentState = 2;
                    else if (ch == 'b') currentState = 4;
                    break;
                case 2:
                    if (ch == 'a') currentState = -1;
                    else if (ch == 'b') currentState = 3;
                    break;
                case 3:
                    if (ch == 'a') currentState = 6;
                    else if (ch == 'b') currentState = -1;
                    break;
                case 4:
                    if (ch == 'a') currentState = -1;
                    else if (ch == 'b') currentState = 5;
                    break;
                case 5:
                    if (ch == 'a') currentState = -1;
                    else if (ch == 'b') currentState = 6;
                    break;
                case 6:
                    if (ch == 'b') currentState = 7;
                    break;
                case 7:
                    if (ch == 'a') currentState = 8;
                    break;
                case 8:
                    if (ch == 'a') currentState = 6;
                    else if (ch == 'b') currentState = 9;
                    break;
                case 9:
                    if (ch == 'a' || ch == 'b') currentState = 10;
                    break;
                case 10:
                    //accepted
                    break;
                default:
                    System.out.println("Invalid input or state.");
                    return false;
            }
        }
        return isAcceptingState(); //return if the final state accepted


    }


    public static boolean isAcceptingState() {
        return currentState == 10;
    }


}


