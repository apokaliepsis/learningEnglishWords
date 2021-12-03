package com.telegrambot.database;

import com.telegrambot.bot.Bot;
import ru.kamatech.qaaf.database.JDBI;

import java.util.Arrays;
import java.util.List;

public class Database extends Bot {
    private JDBI jdbi;



    public JDBI getJdbi() {
        if (jdbi == null) {
            jdbi = new JDBI();
            /*jdbi.setDataBaseSettings("jdbc:sqlite:"+ *//*Properties.getPathFromResources("englishWords.db")*//*
                    "/home/anton/englishWords.db",null,null);*/
            //jdbi.setDataBaseSettings("jdbc:h2:/home/anton/englishWordsH2.mv.db",null,null);
            jdbi.setDataBaseSettings("jdbc:h2:tcp://localhost/~/englishWordsH2", "admin", "123456");
        }
        return jdbi;
    }
    public int getStateFromDB(long chatId) {
        return (int) getJdbi().getFirstRowFromResponse(Arrays.asList(chatId), "select state from configuration where chatId=?", false).get("STATE");
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
        if (getJdbi().getFirstRowFromResponse(Arrays.asList(chatId), "select* from configuration where chatId =?", false).size() == 0) {
            getJdbi().createUpdate(Arrays.asList(state, chatId), "insert into configuration (state, chatId) values (?,?)", false);
        } else {
            System.out.println("Запись найдена. Меняем state");
            getJdbi().createUpdate(Arrays.asList(state, chatId), "UPDATE configuration SET state = ? WHERE chatId = ?", false);
        }
    }

    public void setTimeSettingToDB(int minutes, long chatId) {
        if (getJdbi().getFirstRowFromResponse(Arrays.asList(chatId), "select* from configuration where chatId =?", false).size() == 0) {
            getJdbi().createUpdate(Arrays.asList(chatId, 0, minutes),
                    "insert into configuration (chatId, state, time) values (?,?,?)", false);
        } else {
            System.out.println("Запись найдена. Меняем time");
            getJdbi().createUpdate(Arrays.asList(minutes, chatId), "UPDATE configuration SET time = ? WHERE chatId = ?", false);
        }

    }

}
