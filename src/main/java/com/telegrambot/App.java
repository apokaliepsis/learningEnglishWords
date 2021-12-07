package com.telegrambot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegrambot.bot.Bot;
import com.telegrambot.service.MessageReciever;
import com.telegrambot.service.MessageSender;
import org.apache.log4j.Logger;

//import org.telegram.telegrambots.ApiContextInitializer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;

public class App {
    private static final Logger log = Logger.getLogger(App.class);
    private static final int PRIORITY_FOR_SENDER = 1;
    private static final int PRIORITY_FOR_RECEIVER = 3;
    private static final String BOT_ADMIN = "873327794";
    @JsonIgnore
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    {
        replyKeyboardMarkup.setResizeKeyboard(true);
        //replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
    }

    public static void main(String[] args) {
        //ApiContextInitializer.init();
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
                "delete from words where word like concat('%',?,'%') and chatId=?",false);


        String line = "I guess she prefers roses - Мне кажется, она предпочитает розы";
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

/*        String answer = "{\"ok\":true,\"result\":{\"message_id\":270,\"from\":{\"id\":5064738249,\"is_bot\":true,\"first_name\":\"\\u0410\\u043d\\u0433\\u043b\\u0438\\u0439\\u0441\\u043a\\u0438\\u0435 \\u0441\\u043b\\u043e\\u0432\\u0430\",\"username\":\"LearningTopWords_bot\"},\"chat\":{\"id\":873327794,\"first_name\":\"\\u0410\\u043d\\u0442\\u043e\\u043d\",\"last_name\":\"\\u0410\\u043b\\u0435\\u043a\\u0441\\u0435\\u0435\\u0432\",\"username\":\"as_alekseev\",\"type\":\"private\"},\"date\":1638843787,\"text\":\"\\u0417\\u0430\\u043f\\u0443\\u0441\\u0442\\u0438\\u043b\\u0441\\u044f\"}}";
        try {
            System.out.println(deserializeResponse(answer));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }*/

    }
    public static Message deserializeResponse(String answer) throws TelegramApiRequestException {
        try {
            ApiResponse<Message> result = OBJECT_MAPPER.readValue(answer,
                    new TypeReference<ApiResponse<Message>>(){});
            if (result.getOk()) {
                return result.getResult();
            } else {
                throw new TelegramApiRequestException("Error sending message", result);
            }
        } catch (IOException e) {
            throw new TelegramApiRequestException("Unable to deserialize response", e);
        }
    }
    private static void sendStartReport(Bot bot) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(BOT_ADMIN);
        sendMessage.setText("Запустился");
        bot.sendQueue.add(sendMessage);
    }
}
