package org.example.tictactoe.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.tictactoe.ClientApplication;
import org.example.tictactoe.menu.MyMenuController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public void setStreams(DataOutputStream outputStream, DataInputStream inputStream) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // По желанию: инициализация
    }

    @FXML
    private void login() {
        try {
            String nickname = usernameField.getText();
            outputStream.writeUTF("LOGIN " + nickname);
            outputStream.flush();

            String response = inputStream.readUTF();

            if ("LOGIN_SUCCESS".equals(response)) {
                ClientApplication.login = nickname;
                openMenu();
            } else {
                String errorMessage = inputStream.readUTF(); // читаем вторую строку с описанием
                showAlert("Ошибка входа", errorMessage);
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Ошибка подключения к серверу.");
            e.printStackTrace();
        }
    }

    private void openMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tictactoe/menu-choice.fxml"));
        Parent root = loader.load();

        // Получаем текущий Stage
        Stage stage = (Stage) usernameField.getScene().getWindow();

        // Передаем stage в контроллер меню
        MyMenuController controller = loader.getController();
        controller.setMainStage(stage);

        // Меняем сцену
        stage.setScene(new Scene(root));
        stage.setTitle("Главное меню");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
