package org.example.tictactoe.statistic;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.server.tictactoe.models.UserModel;

import java.io.Serializable;

public record UserModelUi(
        SimpleStringProperty name,
        SimpleIntegerProperty playedGames,
        SimpleIntegerProperty wonGames,
        SimpleStringProperty ratio,
        SimpleIntegerProperty rating) implements Serializable {

    public UserModelUi(UserModel user) {
        this(
                new SimpleStringProperty(user.name()),
                new SimpleIntegerProperty(user.playedGames()),
                new SimpleIntegerProperty(user.wonGames()),
                new SimpleStringProperty(String.format("%.2f%%", user.ratio() * 100)),
                new SimpleIntegerProperty(user.rating()));
    }
}
