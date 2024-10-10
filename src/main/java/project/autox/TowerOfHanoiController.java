package project.autox;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import static java.awt.Color.black;

public class TowerOfHanoiController {

    @FXML
    private VBox firstTower, secondTower, thirdTower;

    @FXML
    private Button tower1Btn, restartGame;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private TextField numberOfMoves;

    private Stack<Integer>[] towers;
    private int totalMoves = 0;
    private int totalDisks = 3;  // Default number of disks, adjustable
    private Queue<int[]> movesQueue; // Queue to store the moves for animation
    private Timeline timeline; // Timeline for animations

    @FXML
    public void initialize() {
        comboBox.getItems().addAll("3", "4", "5", "6");  // Limit to 3-6 disks
        comboBox.setValue("3");  // Default to 3 disks

        // Add listener to ComboBox
        comboBox.setOnAction(e -> setupGame());

        setupGame();
        tower1Btn.setOnAction(e -> startSimulation());
        restartGame.setOnAction(e -> restartGame());
    }

    // Set up the initial game state
    private void setupGame() {
        towers = new Stack[3];
        towers[0] = new Stack<>();
        towers[1] = new Stack<>();
        towers[2] = new Stack<>();

        // Get the number of disks from ComboBox
        totalDisks = Integer.parseInt(comboBox.getValue());

        // Initialize the disks on the first tower (Box 1), smallest at the bottom
        for (int i = 1; i <= totalDisks; i++) {
            towers[0].push(i);  // Push Disk 1 at the bottom, Disk 3 at the top
        }

        clearTowers();
        numberOfMoves.setText("0");
        updateTowerDisplay(); // Update the visual display of the towers
    }

    // Clear the visual towers
    private void clearTowers() {
        firstTower.getChildren().clear();
        secondTower.getChildren().clear();
        thirdTower.getChildren().clear();
    }

    // Update the visual representation of the towers
    private void updateTowerDisplay() {
        clearTowers();

        // Add disks to the VBox for each tower
        for (int i = towers[0].size() - 1; i >= 0; i--) {  // Reverse order to display correctly
            firstTower.getChildren().add(createDisk(towers[0].get(i)));
        }
        for (int i = towers[1].size() - 1; i >= 0; i--) {
            secondTower.getChildren().add(createDisk(towers[1].get(i)));
        }
        for (int i = towers[2].size() - 1; i >= 0; i--) {
            thirdTower.getChildren().add(createDisk(towers[2].get(i)));
        }
    }

    // Create a visual representation of a disk with a variable width
    private Label createDisk(int size) {
        Label disk = new Label("Disk " + size);

        int diskWidth = 300 - (size - 1) * 40; // Width decreases for larger disks
        disk.setPrefWidth(diskWidth);  // Set width based on disk size
        disk.setPrefHeight(40);  // Fixed height for all disks

        // Define an array of pastel colors
        String[] pastelColors = {
                "#FFB3BA",  // Light pink
                "#FFDFBA",  // Light peach
                "#FFFFBA",  // Light yellow
                "#BFFCC6",  // Light mint green
                "#BAE1FF",  // Light blue
                "#D5BAFF"   // Light lavender
        };

        // Cycle through colors based on disk size
        String color = pastelColors[(size - 1) % pastelColors.length]; // Ensure it wraps around if more disks

        // Set background color to a pastel color based on the disk size
        disk.setStyle("-fx-background-color: " + color + "; -fx-border-color: black; "
                + "-fx-border-width: 0.5px; -fx-alignment: center; " +  "-fx-border-radius: 10px; " +"-fx-background-radius: 10px; " );

        return disk;
    }

    // Start the simulation
    private void startSimulation() {
        totalMoves = 0;
        movesQueue = new LinkedList<>(); // Initialize the moves queue
        generateMoves(totalDisks, 1, 3, 2); // Generate the moves
        totalMoves = movesQueue.size(); // Total moves is the size of the queue
        numberOfMoves.setText(String.valueOf(totalMoves));

        animateMoves(); // Start the animation
    }

    // Animate the moves
    private void animateMoves() {
        if (timeline != null) {
            timeline.stop(); // Stop any existing animation
        }

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE); // Allow for indefinite cycle count

        // Animation for each move
        for (int i = 0; i < totalMoves; i++) {
            int[] move = movesQueue.poll(); // Get the next move
            if (move != null) {
                // Add the move to the timeline
                int finalI = i;
                KeyFrame keyFrame = new KeyFrame(Duration.seconds(i + 1), event -> {
                    moveOneDisk(move[0], move[1]); // Move the disk
                    // Stop the timeline after the last move
                    if (finalI == totalMoves - 1) {
                        timeline.stop(); // Stop the animation after the last move
                    }
                });
                timeline.getKeyFrames().add(keyFrame);
            }
        }

        timeline.play(); // Start the animation
    }

    // Recursive function to generate disk moves
    private void generateMoves(int numDisks, int fromTower, int toTower, int auxTower) {
        if (numDisks == 1) {
            // Move the smallest disk
            movesQueue.add(new int[]{fromTower - 1, toTower - 1});
            return;
        }

        // Move top n-1 disks from source to auxiliary, using destination as a buffer
        generateMoves(numDisks - 1, fromTower, auxTower, toTower);

        // Move the nth disk (largest) from source to destination
        movesQueue.add(new int[]{fromTower - 1, toTower - 1});

        // Move the n-1 disks from auxiliary to destination, using source as a buffer
        generateMoves(numDisks - 1, auxTower, toTower, fromTower);
    }

    // Move one disk from one tower to another visually
    private void moveOneDisk(int fromTower, int toTower) {
        if (!towers[fromTower].isEmpty()) {
            int disk = towers[fromTower].pop(); // Remove disk from the source tower
            towers[toTower].push(disk); // Add disk to the destination tower
            updateTowerDisplay(); // Update the visual representation
        }
    }

    // Restart the game
    private void restartGame() {
        if (timeline != null) {
            timeline.stop(); // Stop the animation if it's running
        }
        setupGame(); // Reset the game state
    }
}
