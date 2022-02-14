package com.telegrambot.database;

import com.telegrambot.ApTest;
import com.telegrambot.App;
import com.telegrambot.bot.Bot;
import com.telegrambot.dictionary.Dictionary;
import org.apache.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kamatech.qaaf.database.JDBI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Database extends Bot {
    private static final Logger logger = Logger.getLogger(Database.class);
    private static JDBI jdbi;
    private static final String HOST = "jdbc:h2:tcp://localhost/~/englishWordsH2";


    public static JDBI getJdbi() {
        if (jdbi == null) {
            jdbi = new JDBI();
            /*jdbi.setDataBaseSettings("jdbc:sqlite:"+ *//*Properties.getPathFromResources("englishWords.db")*//*
                    "/home/anton/englishWords.db",null,null);*/
            //jdbi.setDataBaseSettings("jdbc:h2:/home/anton/englishWordsH2.mv.db",null,null);
            //jdbi.setDataBaseSettings("jdbc:h2:tcp://localhost/~/englishWordsH2", "admin", "123456");

            String urlConnection = System.getProperty("urlConnection");
            //logger.info("Connect to database...");
            if(urlConnection==null||urlConnection.isEmpty()){
                //logger.info("Get contour from config: "+url);
                logger.info("URL connection = "+ HOST);
                jdbi.setDataBaseSettings(HOST, "admin", "123456");
            }
            else{
                logger.info("Database host received from the passed argument");
                logger.info("URL connection = "+urlConnection);
                jdbi.setDataBaseSettings(urlConnection, "admin", "123456");
            }

            jdbi.setDataBaseSettings(HOST, "admin", "123456");

            Assertions.assertThat(jdbi.getAllRowsFromResponse(Collections.emptyList(),"show tables",
                    false)).size().as("No database connection!").isNotZero();
        }
        return jdbi;
    }


    public int getStateFromDB(long chatId) {
        return (int) getJdbi().getFirstRowFromResponse(Collections.singletonList(chatId), "select state from configuration where chatId=?", false).get("STATE");
    }
    public void setWordsToDB(List<String> dictionary, long chatId) {
        List<String> data = getDictionary().getDictionaryFromDB(chatId);
        int count = 0;
        for (String s : dictionary) {
            if (!data.contains(s)) {
                count++;
                getJdbi().createUpdate(Arrays.asList(chatId, s), "insert into words (chatId, word) values (?,?)", false);

            }
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setReplyMarkup(getMenu().getMainMenu(App.replyKeyboardMarkup));
        sendMessage.disableNotification();
        sendMessage.setText("Загружено слов: "+count);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
    private static String getDateTime() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        return dateFormat.format(date);
    }
    public void setStateToDB(int state, long chatId) {
        if (getJdbi().getFirstRowFromResponse(Collections.singletonList(chatId), "select* from configuration where chatId =?", false).size() == 0) {
            getJdbi().createUpdate(Arrays.asList(state, chatId), "insert into configuration (state, chatId) values (?,?)", false);
        } else {
            System.out.println("Запись найдена. Меняем state");
            getJdbi().createUpdate(Arrays.asList(state, getDateTime(), chatId), "UPDATE configuration SET state = ?, date = ? WHERE chatId = ?", false);
        }
    }

    public void setTimeSettingToDB(int minutes, long chatId) {
        if (getJdbi().getFirstRowFromResponse(Collections.singletonList(chatId), "select* from configuration where chatId =?", false).size() == 0) {
            getJdbi().createUpdate(Arrays.asList(chatId, 0, minutes),
                    "insert into configuration (chatId, state, time) values (?,?,?)", false);
        } else {
            System.out.println("Запись найдена. Меняем time");
            getJdbi().createUpdate(Arrays.asList(minutes, chatId), "UPDATE configuration SET time = ? WHERE chatId = ?", false);
        }

    }
    public static void checkConnection(){
        logger.info("Checking connection to base...");
        Assertions.assertThat(getJdbi().getAllRowsFromResponse(Collections.emptyList(),"show tables",
                false)).size().as("No database connection!").isNotZero();
        logger.info("Successfully");
    }
    public static void setDefaultClientStatus(){
        getJdbi().createUpdate(Collections.singletonList(0), "UPDATE configuration SET state = ?", false);
    }


}
