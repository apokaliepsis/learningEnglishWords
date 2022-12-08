package com.telegrambot.database;

import com.telegrambot.App;
import com.telegrambot.bot.Bot;
import com.telegrambot.service.Settings;
import com.telegrambot.util.UpdateWrapper;
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
    private static JDBI jdbi;
    private static final String HOST = "jdbc:h2:tcp://localhost/~/englishWordsH2";
    private static final Logger logger = Logger.getLogger(Settings.class);


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
        logger.info("Get state user from DB");
        return (int) getJdbi().getFirstRowFromResponse(Collections.singletonList(chatId),
                "select state from configuration where chatId=?", false).get("STATE");
    }
    public static long getCountWords(long chatId) {
        logger.info("Get count words to DB");
        Object countWords;
        try{
            countWords = getJdbi().getFirstRowFromResponse(Collections.singletonList(chatId),
                    "select count(*) from words where chatId=?", false).get("COUNT(*)");
        }
        catch (Exception e){
            e.printStackTrace();
            countWords = 0;

        }
        logger.info(chatId+": countWords="+countWords);
        return (long) countWords;
    }
    public static long getCountUsers() {
        logger.info("Get count users to DB");
        return (long) getJdbi().getFirstRowFromResponse(Collections.emptyList(),
                "select count(*) from configuration", false).get("COUNT(*)");
    }
    public static long getCountActiveUsers() {
        logger.info("Get count active users to DB");
        return (long) getJdbi().getFirstRowFromResponse(Collections.emptyList(),
                "select count(*) from configuration where state=1", false).get("COUNT(*)");
    }
    public static List<Object> getActiveUser() {
        logger.info("Get active users");
        List<Map<String, Object>> chatIdMap = getJdbi().getAllRowsFromResponse(Collections.emptyList(),
                "select chatId from configuration where state=1", false);
        List<Object> chatIdList = new ArrayList<>();
        for(Map<String, Object> map:chatIdMap){
            Object chatid = map.get("CHATID");
            chatIdList.add(chatid);
        }
        return chatIdList;
    }
    public void setWordsToDB(List<String> dictionary, Update update) {
        logger.info("Loading words into DB");
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
            logger.error(e.getMessage());
        }

    }
    public static String getDateTime() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        return dateFormat.format(date);
    }
    public void setStateToDB(int state, Update update) {
        logger.info("Setting user state in DB");

        long chatId = UpdateWrapper.getChatId(update);
        //int count_word = ((Long) getJdbi().getFirstRowFromResponse(Collections.emptyList(), "select count(*) from words where chatid=873327794", false).get("COUNT(*)")).intValue();
        String username = UpdateWrapper.getUserName(update);
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
        logger.info("Setting interval time in DB");
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
    public static int getUserTime(long chatId) {
        int time;
        try{
            time = (int) getJdbi()
                    .getFirstRowFromResponse(Collections.singletonList(chatId),
                            "select time from configuration where chatId=?",
                            false).get("TIME");
        }
        catch (Exception e){
            e.printStackTrace();
            time = 0;
        }

        logger.info(chatId+": time="+time);
        return time;
    }
    public static void checkConnection(){
        logger.info("Checking connection to base...");
        Assertions.assertThat(getJdbi().getAllRowsFromResponse(Collections.emptyList(),"show tables",
                false)).size().as("No database connection!").isNotZero();
        logger.info("Successfully");
    }
    public static void setDefaultClientStatus(){
        logger.info("Setting status 0 for all users");
        getJdbi().createUpdate(Collections.singletonList(0), "UPDATE configuration SET state = ?",
                false);
    }
    private String getNewsLetterDate(){
        logger.info("Getting the time of the last mailing");
        return String.valueOf(getJdbi().getFirstRowFromResponse(Collections.emptyList(),
                "select DATE_NEWSLETTER from SETTINGS",false).get("DATE_NEWSLETTER"));
    }
    public void sendMessageUserLongTimeNoVisit(){
        logger.info("Send notification to users");
        List<Map<String, Object>> chatIdList;
        String dateNewsLetter = getNewsLetterDate();
        while(true){
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
                    logger.error(e.getMessage());
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
                logger.error(e.getMessage());
            }
        }
        }
        public static void updateWordsCount(){
            new Thread(() -> {
                List<Map<String, Object>> x = getJdbi().getAllRowsFromResponse(Collections.emptyList(),
                        "select chatid from configuration",
                        false);
                List<Object> chatIdList = new ArrayList<>();
                for(Map<String, Object> data : x){
                    chatIdList.add(data.get("CHATID"));

                }
                System.out.println(chatIdList);
                for(Object chatId: chatIdList){
                    int count_word = ((Long) getJdbi().getFirstRowFromResponse(Collections.emptyList(), "select count(*) from words where chatid=" + chatId, false).get("COUNT(*)")).intValue();

                    getJdbi().createUpdate(Arrays.asList(count_word, chatId), "UPDATE configuration SET words = ? where chatid = ?",
                            false);
                }
            }).start();
        }

/*        public static void recoveryActiveSession(){
        List<?> activeUserList = getActiveUser();
        for(Object id: activeUserList){

        }

        }*/

}
