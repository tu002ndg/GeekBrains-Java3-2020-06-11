package ru.geekbrains.java3.server.auth;

import java.util.List;
import java.util.Objects;

public class BaseAuthService implements AuthService {
    private static class UserData {
        private String login;
        private String password;
        private String username;

        public UserData(String login, String password, String username) {
            this.login = login;
            this.password = password;
            this.username = username;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserData userData = (UserData) o;
            return Objects.equals(login, userData.login) &&
                    Objects.equals(password, userData.password) &&
                    Objects.equals(username, userData.username);
        }

        @Override
        public int hashCode() {

            return Objects.hash(login, password, username);
        }
    }

    private final static List<UserData> USER_DATA = List.of(
            new UserData("login1", "pass1", "username1"),
            new UserData("login2", "pass2", "username2"),
            new UserData("login3", "pass3", "username3")
    );

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (UserData user: USER_DATA) {
            if (user.login.equals(login)&&user.password.equals(password)) {
                return user.username;
            }
        }
        return null;
    }

    @Override
    public int updateUsername(String username, String newUsername) {

        // check if the new username exists
        for (UserData user: USER_DATA
             ) {
            if(user.username.equals(newUsername)) {
                return -1;
            }
        }

        //replace the old value of username by the new one
        for (UserData user : USER_DATA) {
            if(user.username.equals(username)) {
                user.username = newUsername;
                break;
            }
        }
        return 0;
    }

    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
    }
}
