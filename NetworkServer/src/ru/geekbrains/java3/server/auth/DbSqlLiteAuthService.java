package ru.geekbrains.java3.server.auth;

import java.sql.*;

public class DbSqlLiteAuthService implements AuthService{

    private  Connection conn;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------

    public DbSqlLiteAuthService()
    {
        conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:CHAT.db");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();}
          catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int updateUsername(String username, String newUsername) {
        try (PreparedStatement stm = conn.prepareStatement(
                "UPDATE users SET name=? WHERE name=?")) {
            stm.setString(1,newUsername);
            stm.setString(2,username);
            stm.executeUpdate();
        } catch (SQLException e) {
            return -1;
        }
        return  0;
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        String username = null;
        try (PreparedStatement stm = conn.prepareStatement(
                    "SELECT name FROM users WHERE login=? AND password=?")) {
            stm.setString(1, login);
            stm.setString(2, password);
            ResultSet resSet = stm.executeQuery();
            if (resSet.next()) {
                username = resSet.getString("name");
            }
            resSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }

    @Override
    public void start() {
        createUserTable();
        writeTestUsersToDB();
    }

    @Override
    public void stop() {
       closeDB();
    }

    private void createUserTable() {
        // --------Создание таблицы user --------
        try (
                Statement stm = conn.createStatement()) {
            stm.execute(
                    "CREATE TABLE " +
                            "if not exists 'users' " +
                            "('id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "'name' text UNIQUE, 'login' text UNIQUE, 'password' text);");
            System.out.println("Таблица создана или уже существует.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --------Заполнение таблицы users --------
    private void writeTestUsersToDB() {
        try (
        PreparedStatement stm = conn.prepareStatement(
                "INSERT INTO users (name,login,password) VALUES (?,?,?)"))
        {
        for (int i = 1; i <= 5; i++) {
            stm.setString(1,"user"+i);
            stm.setString(2,"login"+i);
            stm.setString(3,"pass"+i);
            stm.addBatch();
        }
            stm.executeBatch();
            System.out.println("Таблица 'users' заполнена.");
        } catch (SQLException e) {
            System.out.println("Таблица 'users' уже заполнена ранее.");
        }
    }

    // --------Закрытие--------
    private void closeDB ()
    {
        try {
            conn.close();
            System.out.println("Соединения закрыты");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
