package ru.geekbrains.java3.server.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.java3.client.Command;
import ru.geekbrains.java3.client.CommandType;
import ru.geekbrains.java3.client.command.AuthCommand;
import ru.geekbrains.java3.client.command.BroadcastMessageCommand;
import ru.geekbrains.java3.client.command.PrivateMessageCommand;
import ru.geekbrains.java3.client.command.UpdateUserNameCommand;
import ru.geekbrains.java3.server.NetworkServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler {
    static final Logger clientLogger = LogManager.getLogger(ClientHandler.class);

    private static final long AUTH_TIME_DURATION = 120000;
    private final NetworkServer networkServer;
    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String nickname;



    public String getNickname() {
        return nickname;
    }

    public ClientHandler(NetworkServer networkServer, Socket socket) {
        this.networkServer = networkServer;
        this.clientSocket = socket;

    }

    public void run(Socket socket) {
        doHandle(socket);
    }


//    private void doHandle(Socket socket) {
//        try {
//            out = new ObjectOutputStream(socket.getOutputStream());
//            in = new ObjectInputStream(socket.getInputStream());
//            new Thread(()->{
//                try {
//                    authTimer();
//                    authentication();
//                    readMessage();
//                } catch (IOException e) {
//                    System.out.printf("Соединение с клиентом %s было закрыто%n",
//                            nickname);
//                }
//                finally {
//                    closeConnection();
//                }
//            }).start();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void doHandle(Socket socket) {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            networkServer.executorService.execute(()-> {
                try {
                    authTimer();
                    authentication();
                    readMessage();
                } catch (IOException e) {
                    clientLogger.info(String.format("Соединение с клиентом %s было закрыто%n",
                            nickname));
                }
                finally {
                    closeConnection();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            networkServer.unsubscribe(this);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessage() throws IOException {
        while (true) {
            Command command = readCommand();
            if (command == null) {
                continue;
            }
            clientLogger.info(String.format("Клиент {%s} прислал сообщение/команду",nickname));
            switch (command.getType()) {
                case END:
                    clientLogger.info("Received 'END' command");
                    return;
                case UPDATE_USERNAME: {
                    UpdateUserNameCommand commandData =
                            (UpdateUserNameCommand) command.getData();
                    String username = commandData.getUsername();
                    String newUsername = commandData.getNewUsername();
                    //Обновить nickname в источнике данных
                    int isUpdated =
                            networkServer.getAuthService()
                                    .updateUsername(username,newUsername);
                    if (isUpdated==0) {
                        //Если успешно обновлено, то
                        // 1. обновить имя в текущем списке на сервере
                        nickname = newUsername;
                        // 2. отправить всем обновленный список
                        networkServer.updateUsername(username, nickname);
                    } else if (isUpdated==-1) {
                        networkServer.sendMessage(nickname,
                                Command.errorCommand(
                                        String.format("Имя %s уже используется!",
                                                newUsername)));
                    }
                    break;
                }
                case PRIVATE_MESSAGE: {
                    PrivateMessageCommand commandData = (PrivateMessageCommand) command.getData();
                    String receiver = commandData.getReceiver();
                    //Применить цензуру к сообщению
                    String message =
                            networkServer.getCensorService().apply(commandData.getMessage());
                    networkServer.sendMessage(receiver,
                            Command.messageCommand(nickname, message));
                    break;
                }
                case BROADCAST_MESSAGE: {
                    BroadcastMessageCommand commandData = (BroadcastMessageCommand) command.getData();
                    // Применить цензуру к сообщению
                    String message =
                            networkServer.getCensorService().apply(commandData.getMessage());
                    networkServer.broadcastMessage(Command.messageCommand(nickname, message), this);
                    break;
                }
                default:
                    System.err.println("Unknown type of command : " + command.getType());
            }
        }
    }

    private Command readCommand() throws IOException {
        try {
            return (Command) in.readObject();
        } catch (ClassNotFoundException e) {
            String errorMessage = "Unknown type of object from client!";
            clientLogger.error(errorMessage);
            e.printStackTrace();
            sendMessage(Command.errorCommand(errorMessage));
            return null;
        }
    }

    private void authTimer()  {
        Thread th = new Thread(()->{
            try {
                Thread.sleep(AUTH_TIME_DURATION);
                if (nickname==null) {
                    Command authErrorCommand =
                            Command.authErrorCommand(
                                    "Время аутентификации истекло...");
                    sendMessage(authErrorCommand);
                    closeConnection();
                  }
            }
            catch (InterruptedException e) {
                e.printStackTrace();}
            catch (IOException e) {
                e.printStackTrace();}
            });
        th.setDaemon(true);
        th.start();
    }

    private void authentication() throws IOException {
        while (true) {
            Command command = readCommand();
            if (command == null) {
                continue;
            }
            if (command.getType() == CommandType.AUTH) {
                boolean successfulAuth = processAuthCommand(command);
                if (successfulAuth){
                    return;
                }
            } else {
                System.err.printf("Unknown type of command for auth process: %s%n",
                        command.getType());
            }
        }
    }


    private boolean processAuthCommand(Command command) throws IOException {
        AuthCommand commandData = (AuthCommand) command.getData();
        String login = commandData.getLogin();
        String password = commandData.getPassword();
        String username = networkServer.getAuthService().getUsernameByLoginAndPassword(login, password);
        if (username == null) {
            Command authErrorCommand = Command.authErrorCommand("Отсутствует учетная запись по данному логину и паролю!");
            sendMessage(authErrorCommand);
            return false;
        }
        else if (networkServer.isNicknameBusy(username)) {
            Command authErrorCommand = Command.authErrorCommand("Данный пользователь уже авторизован!");
            sendMessage(authErrorCommand);
            return false;
        }
        else {
            nickname = username;

            String message = nickname + " зашел в чат!";
            networkServer.broadcastMessage(Command.messageCommand(null, message), this);
            commandData.setUsername(nickname);
            sendMessage(command);
            networkServer.subscribe(this);
            return true;
        }
    }


    public void sendMessage(Command message) throws IOException {
        out.writeObject(message);
    }
}
