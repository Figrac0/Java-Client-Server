package org.server.tictactoe.models;

import java.io.Serializable;

public record UserModel(
                int id,
                String name,
                int playedGames,
                int wonGames,
                double ratio) implements Serializable {

        @Override
        public String toString() {
                return "UserModel(" +
                                "id=" + id +
                                ", name=" + name +
                                ", playedGames=" + playedGames +
                                ", wonGames=" + wonGames +
                                ", ratio=" + String.format(java.util.Locale.US, "%.5f", ratio) +
                                ")";
        }
}
