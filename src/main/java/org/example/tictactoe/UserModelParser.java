package org.example.tictactoe;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.server.tictactoe.models.UserModel;

import java.lang.reflect.Type;
import java.util.List;

public class UserModelParser {

    private static final Gson gson = new Gson();

    public static List<UserModel> parse(String json) {
        Type listType = new TypeToken<List<UserModel>>() {
        }.getType();
        return gson.fromJson(json, listType);
    }
}
