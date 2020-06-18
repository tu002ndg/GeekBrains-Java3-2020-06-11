package ru.geekbrains.java3.client.command;

import java.io.Serializable;
import java.util.List;

public class UpdateUserNameCommand implements Serializable {
    private final String username;
    private final String newUsername;
    private final List<String> users;

    public UpdateUserNameCommand(String username, String newUsername, List<String> users) {
        this.username = username;
        this.newUsername = newUsername;
        this.users = users;
    }

    public String getUsername() {
        return username;
    }
    public String getNewUsername() {
        return newUsername;
    }
    public List<String> getUsers() {
        return users;
    }
}
