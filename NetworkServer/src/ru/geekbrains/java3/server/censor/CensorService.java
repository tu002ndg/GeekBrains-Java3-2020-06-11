package ru.geekbrains.java3.server.censor;

public interface CensorService {
    String TEXT_CENSORED = "[***цензура***]";
    String apply(String message);

}
