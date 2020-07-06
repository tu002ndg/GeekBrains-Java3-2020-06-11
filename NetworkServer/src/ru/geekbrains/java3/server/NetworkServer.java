package ru.geekbrains.java3.server;

import ru.geekbrains.java3.client.Command;
import ru.geekbrains.java3.server.auth.AuthService;
import ru.geekbrains.java3.server.auth.DbSqlLiteAuthService;
import ru.geekbrains.java3.server.censor.CensorService;
import ru.geekbrains.java3.server.censor.FileCensorService;
import ru.geekbrains.java3.server.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import ru.geekbrains.java3.server.auth.BaseAuthService;
//import ru.geekbrains.java3.server.censor.BaseCensorService;


public class NetworkServer {
    static final Logger serverLogger = LogManager.getLogger(NetworkServer.class);

    private final int port;
    private final List<ClientHandler> clients
            = new CopyOnWriteArrayList<>();
    private final AuthService authService;
    private final CensorService censorService;
    public ExecutorService executorService;


    public NetworkServer(int port) {
        this.port = port;
        this.authService = new DbSqlLiteAuthService();
//        this.authService = new BaseAuthService();
//        this.censorService = new BaseCensorService();
        this.censorService = new FileCensorService();
    }


    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(port)){
//            System.out.printf(
//                    "Сервер был успешно запущен на порту %s%n",
//                    port);
            serverLogger.info(String.format("Сервер был успешно запущен на порту %s%n",
                    port));
            executorService = Executors.newFixedThreadPool(2);
            authService.start();
            while (true) {
                serverLogger.info("Ожидание подключения клиента...");
                Socket clientSocket = serverSocket.accept();
                serverLogger.info("Клиент подключился");
                createClientHandler(clientSocket);
            }
        } catch (IOException e) {
            serverLogger.fatal("Ошибка при работе сервера");
            e.printStackTrace();
        } finally {
            authService.stop();
            executorService.shutdown();
        }
    }

    private void createClientHandler(Socket clientSocket) {
        ClientHandler clientHandler =
                new ClientHandler(this,clientSocket);
        clientHandler.run(clientSocket);

    }

    public AuthService getAuthService() {

        return authService;
    }


    public /*synchronized */ void broadcastMessage(Command message, ClientHandler owner)
            throws IOException {
        for (ClientHandler client: clients
             ) {
            if (client!=owner)
                client.sendMessage(message);
        }
    }


    public /* synchronized */ void sendMessage(String receiver, Command commandMessage)
            throws IOException {
        for (ClientHandler client: clients
                ) {
            if (client.getNickname().equals(receiver)) {
                client.sendMessage(commandMessage);
            break;
            }
        }

    }

    public /* synchronized */  void updateUsername(String username, String NewUsername)
            throws IOException {
        List<String> users = getAllUsernames();
        broadcastMessage(Command.updateUserNameCommand(username, NewUsername, users),
                null);
    }


    public /* synchronized */ void subscribe(ClientHandler clientHandler)
            throws IOException {
        clients.add(clientHandler);
        List<String> users = getAllUsernames();
        broadcastMessage(Command.updateUsersListCommand(users), null);
    }


    public /* synchronized */ void unsubscribe(ClientHandler clientHandler)
            throws IOException {
        clients.remove(clientHandler);
        List<String> users = getAllUsernames();
        broadcastMessage(Command.updateUsersListCommand(users), null);
    }

    private List<String> getAllUsernames() {
/*        return clients.stream()
                .map(client -> client.getNickname())
                .collect(Collectors.toList());
*/
        List<String> usernames = new LinkedList<>();
        for (ClientHandler clientHandler : clients) {
            usernames.add(clientHandler.getNickname());
        }
        return usernames;
    }

    public boolean isNicknameBusy(String username) {
        for (ClientHandler client : clients) {
            if (client.getNickname().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public CensorService getCensorService() {
        return censorService;
    }
}
