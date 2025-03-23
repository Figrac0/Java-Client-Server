package org.server.tictactoe;

import org.server.tictactoe.models.Response;
import org.server.tictactoe.models.UserModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TicTacToeDbImp implements TicTacToe {

    private Connection conn;
    private Statement stmt;

    public TicTacToeDbImp() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:tictactoe.db");
            stmt = conn.createStatement();
            createTableIfNotExists();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Response<Boolean> clearAllUsersAndResetIds() {
        try {
            stmt.executeUpdate("DELETE FROM users;");
            stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='users';");
            return new Response<>(true, "Все пользователи удалены, ID сброшены.");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(false, e.getMessage());
        }
    }

    // public static void main(String[] args) {
    // TicTacToeDbImp db = new TicTacToeDbImp();
    // var response = db.clearAllUsersAndResetIds();

    // }

    private void createTableIfNotExists() {
        try {
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            nickname TEXT UNIQUE NOT NULL,
                            password TEXT NOT NULL,
                            played_games INTEGER DEFAULT 0,
                            won_games INTEGER DEFAULT 0
                        );
                    """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response<Boolean> register(String nickname, String password) {
        try {
            var res = stmt.executeUpdate(
                    "INSERT INTO users(nickname, password) VALUES('" + nickname + "', '" + password + "');");
            return new Response<>(res > 0, "Пользователь зарегистрирован.");
        } catch (Exception e) {
            return new Response<>(false, "Пользователь уже существует.");
        }
    }

    @Override
    public Response<Boolean> login(String nickname, String password) {
        try {
            var rs = stmt.executeQuery(
                    "SELECT password FROM users WHERE nickname = '" + nickname + "'");
            if (rs.next()) {
                boolean success = rs.getString("password").equals(password);
                return new Response<>(success, success ? "Вход выполнен." : "Неверный пароль.");
            } else {
                return new Response<>(false, "Пользователь не найден.");
            }
        } catch (Exception e) {
            return new Response<>(false, e.getMessage());
        }
    }

    @Override
    public Response<Boolean> addVictoryForUser(String nickname) {
        try {
            var res = stmt.executeUpdate(
                    "UPDATE users SET won_games = won_games + 1, played_games = played_games + 1 WHERE nickname = '"
                            + nickname + "'");
            return new Response<>(res > 0, "");
        } catch (Exception e) {
            return new Response<>(false, e.getMessage());
        }
    }

    @Override
    public Response<Boolean> addDefeatForUser(String nickname) {
        try {
            var res = stmt.executeUpdate(
                    "UPDATE users SET played_games = played_games + 1 WHERE nickname = '" + nickname + "'");
            return new Response<>(res > 0, "");
        } catch (Exception e) {
            return new Response<>(false, e.getMessage());
        }
    }

    @Override
    public Response<List<UserModel>> getRatingWithCurrentUser(String nickname) {
        try {
            var rs = stmt.executeQuery("""
                        SELECT id, nickname, played_games, won_games
                        FROM users
                        ORDER BY id ASC;
                    """);

            List<UserModel> list = new ArrayList<>();
            while (rs.next()) {
                int played = rs.getInt("played_games");
                int won = rs.getInt("won_games");
                double ratio = (played > 0) ? (double) won / played : 0.0;

                list.add(new UserModel(
                        rs.getInt("id"),
                        rs.getString("nickname"),
                        played,
                        won,
                        ratio));
            }
            rs.close();
            return new Response<>(list, "");
        } catch (Exception e) {
            return new Response<>(new ArrayList<>(), e.getMessage());
        }
    }

}
