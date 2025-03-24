package org.server.tictactoe.models;

import java.io.Serializable;

public record UserModel(
                int id,
                String name,
                int playedGames,
                int wonGames,
                double ratio,
                int rating) implements Serializable {

        @Override
        public String toString() {
                return "UserModel(" +
                                "id=" + id +
                                ", name=" + name +
                                ", playedGames=" + playedGames +
                                ", wonGames=" + wonGames +
                                ", ratio=" + ratio +
                                ", rating=" + rating +
                                ")";
        }
}
