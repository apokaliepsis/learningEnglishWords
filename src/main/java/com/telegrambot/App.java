package com.telegrambot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegrambot.bot.Bot;
import com.telegrambot.database.Database;
import com.telegrambot.service.MessageReciever;
import com.telegrambot.service.MessageSender;
import org.apache.log4j.Logger;

//import org.telegram.telegrambots.ApiContextInitializer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.kamatech.qaaf.database.JDBI;
import ru.kamatech.qaaf.properties.Properties;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

public class App {
    private static final Logger logger = Logger.getLogger(App.class);
    private static final int PRIORITY_FOR_SENDER = 1;
    private static final int PRIORITY_FOR_RECEIVER = 3;
    private static final String BOT_ADMIN = "873327794";

    public static ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    static {
        replyKeyboardMarkup.setResizeKeyboard(true);
        //replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
    }

    public static void main(String[] args) {
        //ApiContextInitializer.init();
        Bot englishWordsBot = new Bot();
        Database.checkConnection();
        MessageReciever messageReciever = new MessageReciever(englishWordsBot);
        MessageSender messageSender = new MessageSender(englishWordsBot);

        englishWordsBot.botConnect();

        Thread receiver = new Thread(messageReciever);
        receiver.setDaemon(true);
        receiver.setName("MsgReciever");
        receiver.setPriority(PRIORITY_FOR_RECEIVER);
        receiver.start();

        Thread sender = new Thread(messageSender);
        sender.setDaemon(true);
        sender.setName("MsgSender");
        sender.setPriority(PRIORITY_FOR_SENDER);
        sender.start();

        sendStartReport(englishWordsBot);

    }

    private static void sendStartReport(Bot bot) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(BOT_ADMIN);
        sendMessage.setText("Запустился");
        bot.sendQueue.add(sendMessage);
    }
}
