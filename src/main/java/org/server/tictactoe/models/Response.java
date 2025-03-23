package org.server.tictactoe.models;

import java.io.Serializable;

public class Response<T> implements Serializable {
    private final T data;
    private final String description;

    public Response(T data, String description) {
        this.data = data;
        this.description = description;
    }

    public T getData() {
        return data;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "null";
    }
}
