package org.example.tictactoe.menu;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.tictactoe.game.GameController;

public class MyMenuController {

    @FXML
    private StackPane rootPane;

    private Stage mainStage;

    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    @FXML
    protected void onSizeThreeClick() {
        openGameFieldWithSize(3);
    }

    @FXML
    protected void onSizeFiveClick() {
        openGameFieldWithSize(5);
    }

    @FXML
    protected void onSizeSevenClick() {
        openGameFieldWithSize(7);
    }

    @FXML
    protected void onSizeTenClick() {
        openGameFieldWithSize(10);
    }

    @FXML
    protected void onStatisticClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tictactoe/statistic-view.fxml"));
            Parent root = loader.load();

            Stage statStage = new Stage();
            statStage.setScene(new Scene(root));
            statStage.setTitle("Статистика");
            statStage.initModality(Modality.WINDOW_MODAL);
            statStage.initOwner(mainStage);

            statStage.setOnHidden(e -> mainStage.show());
            mainStage.hide();
            statStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openGameFieldWithSize(int size) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tictactoe/game-view.fxml"));
            Parent root = loader.load();

            GameController controller = loader.getController();
            controller.setCategory(size);

            Stage gameStage = new Stage();
            gameStage.setScene(new Scene(root));
            gameStage.setTitle("Игровое поле " + size + "x" + size);
            gameStage.initModality(Modality.WINDOW_MODAL);
            gameStage.initOwner(mainStage);

            gameStage.setOnHidden(e -> mainStage.show());
            mainStage.hide();
            gameStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
