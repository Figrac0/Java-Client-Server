package org.example.tictactoe.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
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

    @FXML
    private PasswordField passwordField;

    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public void setStreams(DataOutputStream outputStream, DataInputStream inputStream) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Необязательно
    }

    @FXML
    private void onLogin() {
        String nickname = usernameField.getText().trim();
        String password = passwordField.getText();

        if (nickname.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Введите логин и пароль.");
            return;
        }

        try {
            outputStream.writeUTF("LOGIN " + nickname + " " + password);
            outputStream.flush();

            String response = inputStream.readUTF();
            if ("LOGIN_SUCCESS".equals(response)) {
                ClientApplication.login = nickname;
                openMenu();
            } else {
                String message = inputStream.readUTF();
                showAlert("Ошибка входа", message);
            }
        } catch (IOException e) {
            showAlert("Ошибка", "Ошибка соединения с сервером.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onRegister() {
        String nickname = usernameField.getText().trim();
        String password = passwordField.getText();

        if (nickname.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Введите логин и пароль.");
            return;
        }

        try {
            outputStream.writeUTF("REGISTER " + nickname + " " + password);
            outputStream.flush();

            String response = inputStream.readUTF();
            if ("REGISTER_SUCCESS".equals(response)) {
                ClientApplication.login = nickname;
                openMenu();
            } else {
                String message = inputStream.readUTF();
                showAlert("Ошибка регистрации", message);
            }
        } catch (IOException e) {
            showAlert("Ошибка", "Ошибка соединения с сервером.");
            e.printStackTrace();
        }
    }

    private void openMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tictactoe/menu-choice.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) usernameField.getScene().getWindow();

        MyMenuController controller = loader.getController();
        controller.setMainStage(stage);

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
