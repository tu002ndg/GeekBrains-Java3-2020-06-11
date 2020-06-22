package ru.geekbrains.java3.server.censor;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractCensorService implements CensorService {

    static final Set obsceneWords  =
            new LinkedHashSet<String>();

    abstract void uploadObsceneWords();

    AbstractCensorService() {
        uploadObsceneWords();
    }

    //Применить цензуру к сообщению
    public String apply(String message) {
        StringBuilder sb = new StringBuilder();
        for (String word : message.split(" ")) {
            if (isAllowedWord(word)) {
                sb.append(word);
            } else {
                sb.append(TEXT_CENSORED);
            }
            sb.append(" ");
        }
        return sb.toString();
    }


    //check if the word needs to be censored
    private boolean isAllowedWord(String word) {
        Iterator<String> iterator = obsceneWords.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            if (word.toLowerCase().contains(str))
                return false;
        }
        return true;
    }
}
