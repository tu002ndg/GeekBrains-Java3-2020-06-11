package ru.geekbrains.java3.server.censor;

import java.io.*;

public class FileCensorService extends AbstractCensorService implements CensorService{
    private static String FILE_CENSORED = "censor.txt";

    // загрузить список нецензурных слов из файла
    @Override
    void uploadObsceneWords() {
        // read from the file into the obsceneWords list
        try (FileInputStream fis = new FileInputStream(FILE_CENSORED)){
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(fis));
            String  word;
            while ((word = br.readLine())!=null) {
                obsceneWords.add(word);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
