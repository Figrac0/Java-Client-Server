package org.server.tictactoe;

import org.server.tictactoe.models.Response;
import org.server.tictactoe.models.UserModel;

import java.io.*;
import java.net.Socket;
import java.util.List;

import com.google.gson.Gson;

public class TicTacToeHandler implements Runnable {

    private final Gson gson = new Gson();

    private final Socket clientSocket;
    private final DataInputStream input;
    private final DataOutputStream output;
    private final TicTacToe db;

    public TicTacToeHandler(Socket socket) throws IOException {
        this.clientSocket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.db = new TicTacToeDbImp();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String request = input.readUTF();
                String[] parts = request.split(" ");
                String command = parts[0];

                switch (command) {
                    case "LOGIN" -> handleLogin(parts);
                    case "ADD_VICTORY" -> handleAddVictory(parts);
                    case "ADD_DEFEAT" -> handleAddDefeat(parts);
                    case "GET_RATING" -> handleGetRating(parts);
                    case "REGISTER" -> handleRegister(parts);
                    default -> output.writeUTF("UNKNOWN_COMMAND");
                }
            }
        } catch (IOException e) {
            System.err.println("Клиент отключился: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Ошибка закрытия сокета: " + e.getMessage());
            }
        }
    }

    private void handleLogin(String[] parts) throws IOException {
        if (parts.length != 3) {
            output.writeUTF("INVALID_ARGUMENTS");
            return;
        }
        String username = parts[1];
        String password = parts[2];

        Response<Boolean> response = db.login(username, password);
        output.writeUTF(response.getData() ? "LOGIN_SUCCESS" : "LOGIN_FAILURE");
        if (!response.getData())
            output.writeUTF(response.getDescription());
    }

    private void handleRegister(String[] parts) throws IOException {
        if (parts.length != 3) {
            output.writeUTF("INVALID_ARGUMENTS");
            return;
        }
        String username = parts[1];
        String password = parts[2];

        Response<Boolean> response = db.register(username, password);
        output.writeUTF(response.getData() ? "REGISTER_SUCCESS" : "REGISTER_FAILURE");
        if (!response.getData())
            output.writeUTF(response.getDescription());
    }

    private void handleAddVictory(String[] parts) throws IOException {
        if (parts.length != 2) {
            output.writeUTF("INVALID_ARGUMENTS");
            return;
        }
        String username = parts[1];
        Response<Boolean> response = db.addVictoryForUser(username);
        output.writeUTF(response.getData() ? "VICTORY_ADDED" : "VICTORY_FAILED");
    }

    private void handleAddDefeat(String[] parts) throws IOException {
        if (parts.length != 2) {
            output.writeUTF("INVALID_ARGUMENTS");
            return;
        }
        String username = parts[1];
        Response<Boolean> response = db.addDefeatForUser(username);
        output.writeUTF(response.getData() ? "DEFEAT_ADDED" : "DEFEAT_FAILED");
    }

    private void handleGetRating(String[] parts) throws IOException {
        if (parts.length != 2) {
            output.writeUTF("INVALID_ARGUMENTS");
            return;
        }
        String username = parts[1];
        Response<List<UserModel>> response = db.getRatingWithCurrentUser(username);
        String json = gson.toJson(response.getData()); // сериализуем список в JSON
        output.writeUTF(json);
    }

}
