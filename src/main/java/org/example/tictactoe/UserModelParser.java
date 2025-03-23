package org.example.tictactoe;

import org.server.tictactoe.models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserModelParser {

    public static List<UserModel> parse(String input) {
        List<UserModel> list = new ArrayList<>();

        input = input.replace("[", "").replace("]", "");
        String[] entries = input.split("UserModel");

        for (String e : entries) {
            if (!e.contains("="))
                continue;

            e = e.replace("(", "").replace(")", "").trim();
            String[] pairs = e.split(", ");

            int id = 0, played = 0, won = 0;
            String name = "";
            double ratio = 0;

            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length != 2)
                    continue;

                String key = kv[0].trim();
                String value = kv[1].trim();

                switch (key) {
                    case "id" -> id = Integer.parseInt(value);
                    case "name" -> name = value;
                    case "playedGames" -> played = Integer.parseInt(value);
                    case "wonGames" -> won = Integer.parseInt(value);
                    case "ratio" -> {
                        try {
                            ratio = Double.parseDouble(kv[1].replace(",", "."));
                        } catch (NumberFormatException ex) {
                            ratio = 0.0;
                        }
                    }

                }
            }

            list.add(new UserModel(id, name, played, won, ratio));
        }

        return list;
    }
}
