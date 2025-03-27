package org.example.tictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.tictactoe.auth.LoginController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

//Сокеты создаются и используются в ClientApplication, LoginController, GameController, StatisticController.

//Интерфейс: JavaFX с FXML (login-view, menu-choice, game-view, statistic-view);

//Пользователь управляет игрой, данные отправляются на сервер;

//Стиль в style.css, подключается в ClientApplication.

public class ClientApplication extends Application {

    public static Socket socket;
    public static DataOutputStream outputStream;
    public static DataInputStream inputStream;
    public static String login;

    @Override
    public void start(Stage stage) {
        try {
            socket = new Socket("localhost", 8080);
            // Инициализация потоков
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            // Клиент подключается к серверу и далее отправляет команды (вход, регистрация,
            // игра, статистика) по сокетам.

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tictactoe/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 700, 500);

            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

            stage.setTitle("Крестики-нолики — Вход в игру");
            stage.setResizable(false);
            stage.setScene(scene);

            // Загрузка первого окна (login-view.fxml) и передача потоков в контроллер
            LoginController controller = loader.getController();
            controller.setStreams(outputStream, inputStream);

            stage.show();

        } catch (Exception e) {
            System.err.println("Ошибка запуска клиента: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
