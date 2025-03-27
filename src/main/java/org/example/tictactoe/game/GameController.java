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
    // Генерация поля заданного размера (через GridPane)
    private GridPane ticTacToeTable;

    private final DataOutputStream outputStream = ClientApplication.outputStream;
    private final DataInputStream inputStream = ClientApplication.inputStream;

    private int[][] field;
    private int fieldSize;
    private int currentTurn = 0;

    private int winLength;

    private boolean gameOver = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setCategory(int size) {
        this.fieldSize = size;
        this.field = new int[size][size];

        // Проверка победы (динамическая: 3/4/5 клеток в зависимости от поля);
        if (size == 3 || size == 5) {
            winLength = 3;
        } else if (size == 7) {
            winLength = 4;
        } else if (size == 10) {
            winLength = 5;
        } else {
            winLength = 3;
        }

        ticTacToeTable.setHgap(5);
        ticTacToeTable.setVgap(5);

        double buttonSize = 500.0 / size;
        double fontSize = switch (size) {
            case 3 -> 36;
            case 5 -> 30;
            case 7 -> 26;
            case 10 -> 18;
            default -> 24;
        };

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Button button = new Button();
                button.setMinSize(buttonSize, buttonSize);
                button.setFont(new Font(fontSize));
                button.setFocusTraversable(false);

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

        // передача результата на сервер (ADD_VICTORY, ADD_DEFEAT) через сокет;

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
        // Проверка всех направлений от каждой ячейки
        int[][] directions = {
                { 1, 0 }, // вниз
                { 0, 1 }, // вправо
                { 1, 1 }, // по диагонали ↘
                { 1, -1 } // по диагонали ↙
        };

        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (field[i][j] != player)
                    continue;

                for (int[] dir : directions) {
                    int count = 1;
                    int dx = dir[0], dy = dir[1];

                    int x = i + dx;
                    int y = j + dy;

                    while (x >= 0 && x < fieldSize && y >= 0 && y < fieldSize && field[x][y] == player) {
                        count++;
                        if (count >= winLength)
                            return true;
                        x += dx;
                        y += dy;
                    }
                }
            }
        }

        return false;
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

        String resultMessage = "";
        if (result[0] == 1 && result[1] == 0) {
            resultMessage = "Поздравляем! Вы выиграли!";
        } else if (result[0] == 0 && result[1] == 1) {
            resultMessage = "Вы проиграли. Попробуйте снова!";
        } else if (result[0] == 0 && result[1] == 0) {
            resultMessage = "Ничья! Хорошая попытка!";
        }

        alert.setContentText(resultMessage);

        if (result[0] == 1 && result[1] == 0) {
            alert.getDialogPane().setStyle("-fx-background-color: lightgreen; -fx-font-size: 16px;");
        } else if (result[0] == 0 && result[1] == 1) {
            alert.getDialogPane().setStyle("-fx-background-color: lightcoral; -fx-font-size: 16px;");
        } else {
            alert.getDialogPane().setStyle("-fx-background-color: lightyellow; -fx-font-size: 16px;");
        }

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

    // сначала выигрыш, затем блокировка, затем центр, углы, оценки позиций
    // (evaluatePosition). Также реализована победа по длине линии
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

// private int[] findComputerMove() {
// int bestScore = Integer.MIN_VALUE;
// int[] bestMove = new int[] { -1, -1 };

// for (int i = 0; i < fieldSize; i++) {
// for (int j = 0; j < fieldSize; j++) {
// if (field[i][j] == 0) {
// field[i][j] = 2;
// int score = minimax(0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
// field[i][j] = 0;

// if (score > bestScore) {
// bestScore = score;
// bestMove = new int[] { i, j };
// }
// }
// }
// }

// return bestMove;
// }

// private int minimax(int depth, boolean isMaximizing, int alpha, int beta) {
// if (checkWin(2))
// return 10 - depth; // победа ИИ
// if (checkWin(1))
// return depth - 10; // победа игрока
// if (isBoardFull())
// return 0; // ничья

// if (isMaximizing) {
// int maxEval = Integer.MIN_VALUE;
// for (int i = 0; i < fieldSize; i++) {
// for (int j = 0; j < fieldSize; j++) {
// if (field[i][j] == 0) {
// field[i][j] = 2;
// int eval = minimax(depth + 1, false, alpha, beta);
// field[i][j] = 0;
// maxEval = Math.max(maxEval, eval);
// alpha = Math.max(alpha, eval);
// if (beta <= alpha)
// return maxEval;
// }
// }
// }
// return maxEval;
// } else {
// int minEval = Integer.MAX_VALUE;
// for (int i = 0; i < fieldSize; i++) {
// for (int j = 0; j < fieldSize; j++) {
// if (field[i][j] == 0) {
// field[i][j] = 1;
// int eval = minimax(depth + 1, true, alpha, beta);
// field[i][j] = 0;
// minEval = Math.min(minEval, eval);
// beta = Math.min(beta, eval);
// if (beta <= alpha)
// return minEval;
// }
// }
// }
// return minEval;
// }
// }