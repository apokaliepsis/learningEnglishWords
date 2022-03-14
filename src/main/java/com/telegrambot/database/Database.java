package com.telegrambot.database;

import com.telegrambot.App;
import com.telegrambot.bot.Bot;
import org.apache.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kamatech.qaaf.database.JDBI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
        return (int) getJdbi().getFirstRowFromResponse(Collections.singletonList(chatId),
                "select state from configuration where chatId=?", false).get("STATE");
    }
//    public int getIdFromDB(long chatId) {
//        return (int) getJdbi().getAllRowsFromResponse(Collections.singletonList(chatId),
//                "select state from configuration where chatId=?", false).get("STATE");
//    }
    public void setWordsToDB(List<String> dictionary, Update update) {
        long chatId = update.getMessage().getChatId();
        List<String> data = getDictionary().getDictionaryFromDB(chatId);
        int count = 0;
        for (String s : dictionary) {
            if (!data.contains(s)) {
                count++;
                getJdbi().createUpdate(Arrays.asList(chatId, s),
                        "insert into words (chatId, word) values (?,?)", false);

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
    public static String getDateTime() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        return dateFormat.format(date);
    }
    public void setStateToDB(int state, Update update) {
        long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getFrom().getUserName();
        if (getJdbi().getFirstRowFromResponse(Collections.singletonList(chatId),
                "select* from configuration where chatId =?", false).size() == 0) {
            getJdbi().createUpdate(Arrays.asList(username, state, chatId),
                    "insert into configuration (username, state, chatId) values (?,?,?)", false);
        } else {
            System.out.println("Запись найдена. Меняем state");
            getJdbi().createUpdate(Arrays.asList(state, getDateTime(), chatId),
                    "UPDATE configuration SET state = ?, date = ? WHERE chatId = ?", false);
        }
    }

    public void setTimeSettingToDB(int minutes, Update update) {
        long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getFrom().getUserName();
        if (getJdbi().getFirstRowFromResponse(Collections.singletonList(chatId),
                "select* from configuration where chatId =?", false).size() == 0) {
            getJdbi().createUpdate(Arrays.asList(username, chatId, 0, minutes),
                    "insert into configuration (username, chatId, state, time) values (?,?,?,?)", false);
        } else {
            System.out.println("Запись найдена. Меняем time");
            getJdbi().createUpdate(Arrays.asList(minutes, chatId),
                    "UPDATE configuration SET time = ? WHERE chatId = ?", false);
        }

    }
    public static void checkConnection(){
        logger.info("Checking connection to base...");
        Assertions.assertThat(getJdbi().getAllRowsFromResponse(Collections.emptyList(),"show tables",
                false)).size().as("No database connection!").isNotZero();
        logger.info("Successfully");
    }
    public static void setDefaultClientStatus(){
        getJdbi().createUpdate(Collections.singletonList(0), "UPDATE configuration SET state = ?",
                false);
    }
    private String getNewsLetterDate(){
        return String.valueOf(getJdbi().getFirstRowFromResponse(Collections.emptyList(),
                "select DATE_NEWSLETTER from SETTINGS",false).get("DATE_NEWSLETTER"));
    }
    public void sendMessageUserLongTimeNoVisit(){
        List<Map<String, Object>> chatIdList;
        String dateNewsLetter = getNewsLetterDate();
        while (true){
            if(dateNewsLetter.isEmpty()|| dateNewsLetter.equals("null")){
                chatIdList = getJdbi().getAllRowsFromResponse(Collections.emptyList(),
                        "SELECT CHATID FROM CONFIGURATION WHERE DATE<=now() - 3",false);
            }
            else{
                chatIdList = getJdbi().getAllRowsFromResponse(Collections.emptyList(),
                        "SELECT CHATID FROM CONFIGURATION WHERE DATE<=now() - 3 and date <= (select DATE_NEWSLETTER  from SETTINGS)-3",
                        false);
            }

            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Пора приступить к изучению слов! Не забывайте, что ежедневные занятия - ключ к успеху.");
            for(Map map:chatIdList){
                sendMessage.setChatId(String.valueOf(map.get("CHATID")));
                try {
                    execute(sendMessage);
                    logger.info("Sent reminder");
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            if(getJdbi().getFirstRowFromResponse(Collections.emptyList(),"select* from settings",false).size()==0){
                getJdbi().createUpdate(Collections.emptyList(),"insert into settings (DATE_NEWSLETTER) values (now())",false);

            }
            else {
                getJdbi().createUpdate(Collections.emptyList(),"update settings set DATE_NEWSLETTER = now()",false);
            }
            try {
                TimeUnit.HOURS.sleep(24);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        }


}
