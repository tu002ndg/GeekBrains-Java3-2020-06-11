package ru.geekbrains.java3.server.censor;

public class BaseCensorService extends AbstractCensorService implements CensorService {


    // загрузить список нецензурных слов из массива
    @Override
    void uploadObsceneWords() {
        // load list of obscene words
        String[] test = {"офигеть", "кукиш", "пьянь", "урод", "болван", "идиот",
                "алкоголик", "балбес",
                "болтун", "дебил", "дармоед", "глупый", "дурак", "дура", "олух",
                "бестолочь", "лох", "кретин"};
        for (String s : test) {
            obsceneWords.add(s);
        }
    }




}
