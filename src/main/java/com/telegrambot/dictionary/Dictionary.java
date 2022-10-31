package com.telegrambot.dictionary;



import com.telegrambot.App;
import com.telegrambot.bot.Bot;
import com.telegrambot.database.Database;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.kamatech.qaaf.properties.Properties.getPathFromResources;

public class Dictionary extends Bot {
    private static final Logger logger = Logger.getLogger(Dictionary.class);
    public List<?> clearDictionaryToDB(Update update) {

        logger.info("Clearing the dictionary");
        List<?> dictionary;
        getDatabase().setStateToDB(0, update);
        dictionary = new ArrayList<>();
        getDatabase().getJdbi().createUpdate(Collections.singletonList(update.getMessage().getChatId()),
                "delete from words where chatId=?", false);
        return dictionary;
    }
    public List<String> getDictionaryFromDB(long chatId) {
        logger.info("Getting a list of words from the DB");
        getDatabase();
        List<Map<String, Object>> list = Database.getJdbi().getAllRowsFromResponse(Collections.singletonList(chatId),
                "select word from words where chatid=?", false);
        List<String> rows = new ArrayList<>();
        for (Map<?,?> map : list) {
            rows.add(String.valueOf(map.get("WORD")));
        }
        //System.out.println("DB="+rows);
        return rows;
    }
    public List<String> setDictionary(TypeDictionary typeDictionary, Update update) {
        logger.info("Loading words from the base dictionary");
        List<String> result = null;
        String dictonaryDefault;

        switch (typeDictionary) {
            case Top100Words:
                dictonaryDefault = getPathFromResources("100words.txt");
                break;
            case Top500Words:
                dictonaryDefault = getPathFromResources("500words.txt");
                break;
            case Top1000Words:
                dictonaryDefault = getPathFromResources("1000words.txt");
                break;
            case Top2000Words:
                dictonaryDefault = getPathFromResources("2000words.txt");
                break;
            case CompilationWords:
                List<String> listTrim = Arrays.stream(
                                update.getMessage().getText().split("\n"))
                        .map(String::trim)
                        .collect(Collectors.toList());
                Set<String> uniqueWords = new HashSet<>(listTrim);
                result = new ArrayList<>(uniqueWords);
                return result;

            default:
                dictonaryDefault = getPathFromResources("dictionary");

        }
        try (Stream<String> lines = java.nio.file.Files.lines(Paths.get(dictonaryDefault))) {
            result = lines.collect(Collectors.toList());
        } catch (IOException e) {
            logger.error(e.getMessage());
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
    public void getFileDictionaryFomDb(Update update){
        List dictionary = getDictionaryFromDB(update.getMessage().getChatId());
        FileWriter writer = null;
        try {
            writer = new FileWriter("output.txt");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        for(Object str: dictionary) {
            try {
                writer.write(str + System.lineSeparator());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }
    public String downloadCurrentDictionary(Update update){
        List<String> list = new Dictionary().getDictionaryFromDB(update.getMessage().getChatId());
        System.out.println(list);
        String pathSoundWordFile = null;
        try {
            pathSoundWordFile = new File(App.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getParent() + "/dictionary"+update.getMessage().getChatId()+".txt";
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }
//        FileWriter writer = null;
//        try {
//            writer = new FileWriter(pathSoundWordFile);
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
        Writer writer = null;
        try {
            assert pathSoundWordFile != null;
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(pathSoundWordFile), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        for(String str: list) {
            try {
                writer.write(str + System.lineSeparator());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return pathSoundWordFile;
    }
    public static String[] getExampleUseWord(String word){
        logger.info("Search example use word...");
        String html = null;
        String exampleUseWord = null;
        String exampleUseWordTranslate = null;
        String [] dataWord = new String[2];
        try {
            html = Jsoup.connect("https://www.translate.ru/перевод/английский-русский/" + word).get().html();
            Document doc = Jsoup.parse(html);
            Element link = doc.select(".samSource").first();
            exampleUseWord = link.text(); // "<b>example</b>"
            System.out.println(exampleUseWord);
            Element link2 = doc.select(".samTranslation").first();
            exampleUseWordTranslate = link2.text();
            System.out.println(exampleUseWordTranslate);
            dataWord[0] = exampleUseWord;
            dataWord[1] = exampleUseWordTranslate;
        } catch (Exception e) {
            dataWord = null;
        }

        logger.info("Example use word: "+dataWord);
        return dataWord;
    }
}
