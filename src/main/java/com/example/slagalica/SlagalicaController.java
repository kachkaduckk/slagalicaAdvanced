
package com.example.slagalica;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SlagalicaController {

    private int missesCount = 0;
    private int movesCount = 0;

    private int size = 4;

    @FXML
    private Label moves;

    @FXML
    private Label misses;

    @FXML
    private GridPane puzzleGrid;

    @FXML
    private TextField sizeInput;

    private final List<Button> buttons = new ArrayList<>();

    @FXML
    public void initialize() {
        sizeInput.setText("4");
        createBoard();
        restartPuzzle();
    }

    private void createBoard() {
        puzzleGrid.getChildren().clear();
        puzzleGrid.getColumnConstraints().clear();
        puzzleGrid.getRowConstraints().clear();
        buttons.clear();

        double cellSize = 300.0 / size;

        for (int i = 0; i < size; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setMinWidth(cellSize);
            column.setPrefWidth(cellSize);
            column.setMaxWidth(cellSize);
            puzzleGrid.getColumnConstraints().add(column);

            RowConstraints row = new RowConstraints();
            row.setMinHeight(cellSize);
            row.setPrefHeight(cellSize);
            row.setMaxHeight(cellSize);
            puzzleGrid.getRowConstraints().add(row);
        }

        for (int i = 0; i < size * size; i++) {
            Button button = new Button();

            button.setId("bt" + i);
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            button.setOnAction(this::numButton);

            int row = i / size;
            int column = i % size;

            puzzleGrid.add(button, column, row);
            buttons.add(button);
        }
    }

    @FXML
    private void restartButton(ActionEvent e) {

        try {
            int newSize = Integer.parseInt(sizeInput.getText());

            if (newSize < 2) {
                throw new NumberFormatException();
            }

            // size = newSize;

        } catch (NumberFormatException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Pogrešan unos");
            alert.setHeaderText("Neispravna veličina slagalice");
            alert.setContentText("Unesi cijeli broj veći ili jednak 2.");
            alert.showAndWait();

            return;
        }

        createBoard();
        restartPuzzle();
    }


    private void restartPuzzle() {

        for (int i = 0; i < buttons.size(); i++) {
            if (i == buttons.size() - 1) {
                buttons.get(i).setText("");
            } else {
                buttons.get(i).setText(String.valueOf(i + 1));
            }
        }

        Random random = new Random();

        int emptyPosition = buttons.size() - 1;

        int shuffleMoves = size * size * 30;

        for (int i = 0; i < shuffleMoves; i++) {

            List<Integer> possibleMoves = getNeighbours(emptyPosition);

            int randomPosition = possibleMoves.get(
                    random.nextInt(possibleMoves.size())
            );

            String temp = buttons.get(emptyPosition).getText();

            buttons.get(emptyPosition).setText(
                    buttons.get(randomPosition).getText()
            );

            buttons.get(randomPosition).setText(temp);

            emptyPosition = randomPosition;
        }

        missesCount = 0;
        movesCount = 0;

        moves.setText("0");
        misses.setText("0");
    }


    private List<Integer> getNeighbours(int position) {

        List<Integer> neighbours = new ArrayList<>();

        int row = position / size;
        int column = position % size;

        if (row > 0) {
            neighbours.add(position - size);
        }

        if (row < size - 1) {
            neighbours.add(position + size);
        }

        if (column > 0) {
            neighbours.add(position - 1);
        }

        if (column < size - 1) {
            neighbours.add(position + 1);
        }

        return neighbours;
    }

    private int findPosition(Button button) {
        return buttons.indexOf(button);
    }

    @FXML
    private void numButton(ActionEvent e) {

        Button clickedButton = (Button) e.getSource();

        if (clickedButton.getText().isEmpty()) {
            return;
        }

        int idClicked = findPosition(clickedButton);

        Button emptyButton = null;

        for (Button b : buttons) {

            if (b.getText().isEmpty()) {
                emptyButton = b;
                break;
            }
        }

        int idEmpty = findPosition(emptyButton);

        int rowClicked = idClicked / size;
        int rowEmpty = idEmpty / size;

        int columnClicked = idClicked % size;
        int columnEmpty = idEmpty % size;

        if ((Math.abs(rowClicked - rowEmpty) <= 1 && (Math.abs(columnClicked - columnEmpty) <= 1))
                || rowClicked == 0 && (Math.abs(rowClicked - rowEmpty) == size-1) && (Math.abs(columnClicked - columnEmpty) <= 1)
                || rowClicked == size-1 && (Math.abs(rowClicked - rowEmpty) == size-1) && (Math.abs(columnClicked - columnEmpty) <= 1)
                || columnClicked == 0 && (Math.abs(columnClicked - columnEmpty) == size-1) && (Math.abs(rowClicked - rowEmpty) <= 1)
                || columnClicked == size-1 && (Math.abs(columnClicked - columnEmpty) == size-1) && (Math.abs(rowClicked - rowEmpty) <= 1)
                || rowClicked == size-1 && columnClicked==size-1 && rowEmpty==0 && columnEmpty==0
                || rowClicked == 0 && columnClicked == 0 && rowEmpty == size-1 && columnEmpty == size-1
                || rowClicked == size-1 && columnClicked == 0 && rowEmpty == 0 && columnEmpty == size-1
                || rowClicked == 0 && columnClicked == size-1 && rowEmpty == size-1 && columnEmpty == 0){

            emptyButton.setText(clickedButton.getText());
            clickedButton.setText("");
        }
        else {
            missesCount++;
        }

        movesCount++;

        moves.setText(String.valueOf(movesCount));
        misses.setText(String.valueOf(missesCount));

        checkWin();
    }
    private void checkWin() {

        for (int i = 0; i < buttons.size(); i++) {

            String expected;

            if (i == buttons.size() - 1) {
                expected = "";
            } else {
                expected = String.valueOf(i + 1);
            }

            if (!buttons.get(i).getText().equals(expected)) {
                return;
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pobjeda!");
        alert.setHeaderText("Čestitamo!");
        alert.setContentText(
                "Uspješno si složio slagalicu!\n" +
                        "Broj poteza: " + movesCount + "\n" +
                        "Broj pogreški: " + missesCount
        );

        alert.showAndWait();
    }

    @FXML
    private void aboutUs (ActionEvent e){
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("info");
        about.setHeaderText("General info about program");
        about.setContentText("This is a customizable 15-puzzle game!");
        about.showAndWait();

    }

    @FXML
    private void close (ActionEvent e){
        System.exit(0);
    }

    @FXML
    private void easy (ActionEvent e){
        size = 3;
        createBoard();
        restartPuzzle();
    }

    @FXML
    private void medium (ActionEvent e){
        size = 4;
        createBoard();
        restartPuzzle();
    }

    @FXML
    private void hard (ActionEvent e){
        size = 5;
        createBoard();
        restartPuzzle();
    }
}