package org.server.tictactoe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//запуск сервера и прослушивание сокетных подключений
public class Server {

    public static void main(String[] args) {
        // Создаём серверный сокет, слушающий порт 8080
        final int PORT = 8080;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту " + PORT);

            while (true) {
                // Ожидает подключения клиента. Как только клиент подключается — создаётся
                // отдельный поток
                Socket clientSocket = serverSocket.accept();
                System.out.println("Новый клиент подключен: " + clientSocket.getInetAddress());

                // Каждому клиенту назначается обработчик соединения в отдельном потоке.
                TicTacToeHandler handler = new TicTacToeHandler(clientSocket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// Сервер (Server.java) запускает ServerSocket и слушает порт 8080.
// Каждый подключившийся клиент обслуживается в отдельном потоке
// (TicTacToeHandler).
// Через Socket создаются input/output потоки, и идёт обмен строками команд.
// Все команды обрабатываются, преобразуются в SQL-запросы в TicTacToeDbImp.
// Ответ формируется и возвращается клиенту по сокету.
