package ru.geekbrains.java3.client.view;

import ru.geekbrains.java3.client.controller.ClientController;

import java.io.IOException;

public class NetworkClient {

    public static void main(String[] args) {
        try {
            ClientController clientController =
                    new ClientController("localhost",8189);
            clientController.runApplication();
        } catch (IOException e) {
            System.err.println("Failed to connect to the server! Please, check your network settings");
        }
    }
}
