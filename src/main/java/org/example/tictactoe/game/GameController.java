package org.example.tictactoe.game;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.tictactoe.ClientApplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML
    private GridPane ticTacToeTable;

    private final DataOutputStream outputStream = ClientApplication.outputStream;
    private final DataInputStream inputStream = ClientApplication.inputStream;

    private int[][] field;
    private int fieldSize;
    private int currentTurn = 0;

    private boolean gameOver = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setCategory(int size) {
        this.fieldSize = size;
        this.field = new int[size][size];

        ticTacToeTable.setHgap(5);
        ticTacToeTable.setVgap(5);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Button button = new Button();
                button.setFont(new Font(24));
                button.setMinSize(500.0 / size, 500.0 / size);
                int row = i, col = j;
                button.setOnAction(e -> onUserMove(button, row, col));
                ticTacToeTable.add(button, j, i);
            }
        }
    }

    private void onUserMove(Button button, int row, int col) {
        if (field[row][col] != 0 || gameOver)
            return;

        button.setText("X");
        button.setDisable(true);
        field[row][col] = 1;
        currentTurn++;

        if (checkWin(1)) {
            gameOver = true;
            sendResult("ADD_VICTORY");
            showResult("Победа!", new int[] { 1, 0 });
            return;
        }

        if (isBoardFull()) {
            gameOver = true;
            sendResult("ADD_DEFEAT");
            showResult("Ничья", new int[] { 0, 0 });
            return;
        }

        int[] computerMove = findComputerMove();
        field[computerMove[0]][computerMove[1]] = 2;

        Button compBtn = (Button) getNodeFromGridPane(ticTacToeTable, computerMove[1], computerMove[0]);
        if (compBtn != null) {
            compBtn.setText("O");
            compBtn.setDisable(true);
        }

        currentTurn++;

        if (checkWin(2)) {
            gameOver = true;
            sendResult("ADD_DEFEAT");
            showResult("Поражение!", new int[] { 0, 1 });
            return;
        }

        if (isBoardFull()) {
            gameOver = true;
            sendResult("ADD_DEFEAT");
            showResult("Ничья", new int[] { 0, 0 });
        }
    }

    private boolean checkWin(int player) {
        for (int i = 0; i < fieldSize; i++) {
            boolean rowWin = true;
            boolean colWin = true;
            for (int j = 0; j < fieldSize; j++) {
                if (field[i][j] != player)
                    rowWin = false;
                if (field[j][i] != player)
                    colWin = false;
            }
            if (rowWin || colWin)
                return true;
        }

        boolean mainDiag = true;
        boolean antiDiag = true;
        for (int i = 0; i < fieldSize; i++) {
            if (field[i][i] != player)
                mainDiag = false;
            if (field[i][fieldSize - i - 1] != player)
                antiDiag = false;
        }
        return mainDiag || antiDiag;
    }

    private void sendResult(String command) {
        try {
            outputStream.writeUTF(command + " " + ClientApplication.login);
            outputStream.flush();
            inputStream.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showResult(String title, int[] result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText("X: " + result[0] + "\nO: " + result[1]);
        alert.showAndWait();

        Stage stage = (Stage) ticTacToeTable.getScene().getWindow();
        stage.close();
    }

    private boolean isBoardFull() {
        for (int[] row : field) {
            for (int cell : row) {
                if (cell == 0)
                    return false;
            }
        }
        return true;
    }

    private int[] findComputerMove() {
        // 1. Попытаться выиграть
        int[] win = findBestMoveFor(2);
        if (win != null)
            return win;

        // 2. Попытаться заблокировать игрока
        int[] block = findBestMoveFor(1);
        if (block != null)
            return block;

        // 3. Ход в центр
        int mid = fieldSize / 2;
        if (field[mid][mid] == 0)
            return new int[] { mid, mid };

        // 4. Ход в углы
        int[][] corners = { { 0, 0 }, { 0, fieldSize - 1 }, { fieldSize - 1, 0 }, { fieldSize - 1, fieldSize - 1 } };
        for (int[] corner : corners) {
            if (field[corner[0]][corner[1]] == 0)
                return corner;
        }

        // 5. Ход туда, где максимальный потенциал победы
        int maxScore = -1;
        int[] bestMove = new int[] { 0, 0 };

        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (field[i][j] == 0) {
                    int score = evaluatePosition(i, j, 2);
                    if (score > maxScore) {
                        maxScore = score;
                        bestMove = new int[] { i, j };
                    }
                }
            }
        }

        return bestMove;
    }

    private int evaluatePosition(int row, int col, int player) {
        int score = 0;

        int[][] directions = {
                { -1, 0 }, { 1, 0 }, // вертикаль
                { 0, -1 }, { 0, 1 }, // горизонталь
                { -1, -1 }, { 1, 1 }, // диагональ ↘
                { -1, 1 }, { 1, -1 } // диагональ ↙
        };

        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];
            if (r >= 0 && r < fieldSize && c >= 0 && c < fieldSize) {
                if (field[r][c] == player)
                    score += 2; // свой рядом — круто
                else if (field[r][c] == 0)
                    score += 1; // пустая рядом — шанс
            }
        }

        return score;
    }

    private int[] findBestMoveFor(int player) {
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (field[i][j] == 0) {
                    field[i][j] = player;
                    boolean canWin = checkWin(player);
                    field[i][j] = 0;
                    if (canWin)
                        return new int[] { i, j };
                }
            }
        }
        return null;
    }

    private Node getNodeFromGridPane(GridPane grid, int col, int row) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row)
                return node;
        }
        return null;
    }
}