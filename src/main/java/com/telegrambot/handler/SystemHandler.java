package com.telegrambot.handler;

import com.telegrambot.bot.Bot;
import com.telegrambot.command.Command;
import com.telegrambot.command.ParsedCommand;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


public class SystemHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(SystemHandler.class);
    private final String END_LINE = "\n";

    public SystemHandler(Bot bot) {
        super(bot);
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Command command = parsedCommand.getCommand();

        switch (command) {
            case START:
                bot.sendQueue.add(getMessageStart(chatId));
                break;
/*            case HELP:
                bot.sendQueue.add(getMessageHelp(chatId));
                break;
            case ID:
                return "Your telegramID: " + update.getMessage().getFrom().getId();*/
        }
        return "";
    }

/*    private Object getMessageHelp(String chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);

        String text = "*Справка*" + END_LINE + END_LINE +
                "[/start](/start) - запуск запоминания слов" + END_LINE +
                "[/stop](/stop) - остановка запоминания слов" + END_LINE +
                "[/menu](/stop) - главное меню" + END_LINE +
                "[/help](/help) - справка бота" + END_LINE;
        sendMessage.setText(text);
        return sendMessage;
    }*/

    private Object getMessageStart(String chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);
        //text.append("All that I can do - you can see calling the command [/help](/help)");
        String text = "Привет!. Я  *" + bot.getBotUsername() + "*" + END_LINE +
                "Я помогу тебе выучить самые популярные английские слова, которые чаще всего встречаются в разговорной речи .\n" +
                "Процесс запоминания происходит незаметно - просто иногда поглядывай на уведомления, в которых будет появляться перевод слова и аудио с произношением.\n" +
                "Для запуска нужно просто выбрать словарь в настройках, указать время выполнения и нажать кнопку \"Старт\".";
        sendMessage.setText(text);
        return sendMessage;
    }
}
