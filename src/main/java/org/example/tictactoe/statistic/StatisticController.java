package org.example.tictactoe.statistic;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.tictactoe.ClientApplication;
import org.example.tictactoe.UserModelParser;
import org.server.tictactoe.models.UserModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StatisticController implements Initializable {

    @FXML
    private TableView<UserModelUi> userTableView;

    @FXML
    private TableColumn<UserModelUi, String> nameColumn;

    @FXML
    private TableColumn<UserModelUi, Integer> playedColumn;

    @FXML
    private TableColumn<UserModelUi, Integer> wonColumn;

    @FXML
    private TableColumn<UserModelUi, String> ratioColumn;

    @FXML
    private TableColumn<UserModelUi, Integer> ratingColumn;

    private final DataOutputStream outputStream = ClientApplication.outputStream;
    private final DataInputStream inputStream = ClientApplication.inputStream;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(cell -> cell.getValue().name());
        playedColumn.setCellValueFactory(cell -> cell.getValue().playedGames().asObject());
        wonColumn.setCellValueFactory(cell -> cell.getValue().wonGames().asObject());
        ratioColumn.setCellValueFactory(cell -> cell.getValue().ratio());
        ratingColumn.setCellValueFactory(cell -> cell.getValue().rating().asObject());

        try {
            outputStream.writeUTF("GET_RATING " + ClientApplication.login);
            outputStream.flush();

            String response = inputStream.readUTF();
            List<UserModel> list = UserModelParser.parse(response);

            for (UserModel user : list) {
                userTableView.getItems().add(new UserModelUi(user));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
