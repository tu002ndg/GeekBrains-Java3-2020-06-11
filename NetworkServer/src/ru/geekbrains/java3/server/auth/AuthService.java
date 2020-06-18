package ru.geekbrains.java3.server.auth;

public interface AuthService {
    String getUsernameByLoginAndPassword(String login, String password);
    int updateUsername(String username, String newUsername);

    void start();
    void stop();
}
