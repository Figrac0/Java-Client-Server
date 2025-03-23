package org.server.tictactoe;

import org.server.tictactoe.models.Response;
import org.server.tictactoe.models.UserModel;

import java.util.List;

public interface TicTacToe {
    Response<Boolean> register(String nickname);

    Response<Boolean> login(String nickname);

    Response<Boolean> addVictoryForUser(String nickname);

    Response<Boolean> addDefeatForUser(String nickname);

    Response<List<UserModel>> getRatingWithCurrentUser(String nickname);
}
