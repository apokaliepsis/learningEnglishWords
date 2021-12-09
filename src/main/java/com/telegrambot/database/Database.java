package com.telegrambot.database;

import com.telegrambot.bot.Bot;
import org.apache.log4j.Logger;
import org.assertj.core.api.Assertions;
import ru.kamatech.qaaf.database.JDBI;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        for (String s : dictionary) {
            if (!data.contains(s)) {
                getJdbi().createUpdate(Arrays.asList(chatId, s), "insert into words (chatId, word) values (?,?)", false);

            }
        }

    }

    public void setStateToDB(int state, long chatId) {
        if (getJdbi().getFirstRowFromResponse(Collections.singletonList(chatId), "select* from configuration where chatId =?", false).size() == 0) {
            getJdbi().createUpdate(Arrays.asList(state, chatId), "insert into configuration (state, chatId) values (?,?)", false);
        } else {
            System.out.println("Запись найдена. Меняем state");
            getJdbi().createUpdate(Arrays.asList(state, chatId), "UPDATE configuration SET state = ? WHERE chatId = ?", false);
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


}
