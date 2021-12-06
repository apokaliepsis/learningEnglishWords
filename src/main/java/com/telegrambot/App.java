package com.telegrambot;

import com.telegrambot.bot.Bot;
import com.telegrambot.service.MessageReciever;
import com.telegrambot.service.MessageSender;
import org.apache.log4j.Logger;

import org.telegram.telegrambots.ApiContextInitializer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class App {
    private static final Logger log = Logger.getLogger(App.class);
    private static final int PRIORITY_FOR_SENDER = 1;
    private static final int PRIORITY_FOR_RECEIVER = 3;
    private static final String BOT_ADMIN = "873327794";
    public static ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    {
        replyKeyboardMarkup.setResizeKeyboard(true);
        //replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        //Bot englishWordsBot = new Bot("Travel777Bot", "2068756472:AAGLcPz1w1DoZ8b7HyO3vGg73qbHQVkE_HE");
        Bot englishWordsBot = new Bot();

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

/*        JDBI jdbi = new JDBI();
        jdbi.setDataBaseSettings("jdbc:h2:tcp://localhost/~/englishWordsH2","admin","123456");
        jdbi.createUpdate(Arrays.asList("hello", 873327794),
                "delete from words where word like concat('%',?,'%') and chatId=?",false);*/


/*        String line = "I guess she prefers roses - Мне кажется, она предпочитает розы";
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
        System.out.println(line.substring(0,Math.min(line.length(), 15)));*/


    }

    private static void sendStartReport(Bot bot) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(BOT_ADMIN);
        sendMessage.setText("Запустился");
        bot.sendQueue.add(sendMessage);
    }
}
