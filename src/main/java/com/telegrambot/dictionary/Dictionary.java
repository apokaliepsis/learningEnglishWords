package com.telegrambot.dictionary;


import com.telegrambot.ApTest;
import com.telegrambot.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Dictionary extends Bot {
    public List clearDictionaryToDB(long chatId) {
        List dictionary;
        getDatabase().setStateToDB(0, chatId);
        dictionary = new ArrayList<>();
        getDatabase().getJdbi().createUpdate(Collections.singletonList(chatId), "delete from words where chatId=?", false);
        return dictionary;
    }
    public List<String> getDictionaryFromDB(long chatId) {
        List<Map<String, Object>> list = getDatabase().getJdbi().getAllRowsFromResponse(Collections.singletonList(chatId), "select word from words where chatid=?", false);
        List<String> rows = new ArrayList<>();
        for (Map map : list) {
            rows.add(String.valueOf(map.get("WORD")));
        }
        //System.out.println("DB="+rows);
        return rows;
    }
    public List<String> setDictionary(TypeDictionary typeDictionary, Update update) {
        List<String> result = null;
        String dictonaryDefault;

        switch (typeDictionary) {
            case Top100Words:
                dictonaryDefault = getPathFromResources("100words");
                break;
            case Top500Words:
                dictonaryDefault = getPathFromResources("500words");
                break;
            case Top1000Words:
                dictonaryDefault = getPathFromResources("1000words");
                break;
            case CompilationWords:
                result = Arrays.asList(update.getMessage().getText().split("\n"));

                return result;

            default:
                dictonaryDefault = getPathFromResources("dictionary");

        }
        try (Stream<String> lines = java.nio.file.Files.lines(Paths.get(dictonaryDefault))) {
            result = lines.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public String getWordFromLine(String line) {
        int indexDelimiter = 0;
        String c;
        for (int i = 0; i < line.length(); i++) {
            c = String.valueOf(line.charAt(i));
            if (c.equals("[") || c.equals("(") || c.equals("-")) {
                indexDelimiter = i;
                break;
            }
        }
        line = line.substring(0, indexDelimiter).trim();
        return line;
    }
    public void getFileDictionaryFomDb(long chatId){
        List dictionary = getDictionaryFromDB(chatId);
        FileWriter writer = null;
        try {
            writer = new FileWriter("output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Object str: dictionary) {
            try {
                writer.write(str + System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public String downloadCurrentDictionary(long chatId){
        List<String> list = new Dictionary().getDictionaryFromDB(chatId);
        System.out.println(list);
        String pathSoundWordFile = null;
        try {
            pathSoundWordFile = new File(ApTest.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getParent() + "/dictionary"+chatId+".txt";
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(pathSoundWordFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String str: list) {
            try {
                writer.write(str + System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathSoundWordFile;
    }
}
