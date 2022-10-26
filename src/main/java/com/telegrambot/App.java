package com.telegrambot;

import com.telegrambot.bot.Bot;
import com.telegrambot.database.Database;


import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;


public class App {

    public static final boolean IS_TEST = true;

    public static ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    static {
        replyKeyboardMarkup.setResizeKeyboard(true);
        //replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
    }

    public static void main(String[] args) {
        Bot englishWordsBot = new Bot();
        Database.checkConnection();
        Database.setDefaultClientStatus();
        englishWordsBot.botConnect();
        englishWordsBot.sendStartReport();
//        new Thread(() -> { Database database = new Database();
//        database.sendMessageUserLongTimeNoVisit();
//        }).start();

    }
}
