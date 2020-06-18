package ru.geekbrains.java3.client.controller;

@FunctionalInterface
public interface MessageHandler {
    void handle(String message);
}
