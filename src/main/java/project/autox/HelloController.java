package project.autox;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import java.io.IOException;

public class HelloController {

    @FXML
    private Pane contentPane;

    @FXML
    public void loadDFA() throws IOException {
        Pane dfaPane = FXMLLoader.load(getClass().getResource("/project/autox/FXML/DFA.fxml"));
        contentPane.getChildren().setAll(dfaPane);
    }

    @FXML
    public void loadNFA() throws IOException {
        Pane nfaPane = FXMLLoader.load(getClass().getResource("/project/autox/FXML/NFA.fxml"));
        contentPane.getChildren().setAll(nfaPane);
    }

    @FXML
    public void loadCFG() throws IOException {
        Pane cfgPane = FXMLLoader.load(getClass().getResource("/project/autox/FXML/CFG.fxml"));
        contentPane.getChildren().setAll(cfgPane);
    }

    @FXML
    public void loadPDA() throws IOException {
        Pane pdaPane = FXMLLoader.load(getClass().getResource("/project/autox/FXML/PDA.fxml"));
        contentPane.getChildren().setAll(pdaPane);
    }

      @FXML
    public void loadTM() throws IOException {
        Pane tmPane = FXMLLoader.load(getClass().getResource("/project/autox/FXML/TuringMachine.fxml"));
        contentPane.getChildren().setAll(tmPane);
    }
}
