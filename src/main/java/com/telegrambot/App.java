package com.telegrambot;

import com.telegrambot.bot.Bot;
import com.telegrambot.database.Database;
import org.apache.log4j.Logger;

//import org.telegram.telegrambots.ApiContextInitializer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.concurrent.TimeUnit;

public class App {

    public static final boolean IS_TEST = false;

    public static ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    static {
        replyKeyboardMarkup.setResizeKeyboard(true);
        //replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
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
