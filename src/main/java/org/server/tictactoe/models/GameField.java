package org.server.tictactoe.models;

public class GameField {
    private final int[][] field;

    public GameField(int size) {
        this.field = new int[size][size];
    }

    public int[][] getField() {
        return field;
    }

    public int getSize() {
        return field.length;
    }
}
