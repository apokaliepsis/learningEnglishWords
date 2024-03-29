package com.telegrambot.menu;
import com.telegrambot.App;
import com.telegrambot.bot.Bot;
import com.telegrambot.database.Database;
import com.telegrambot.dictionary.TypeDictionary;
import com.telegrambot.util.UpdateWrapper;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.File;
import java.net.URISyntaxException;
import java.util.*;

public class Menu extends Bot {
    private final String onVoice = "\uD83D\uDD0A Включить добавление произношения";
    private final String offVoice = "\uD83D\uDD07 Отключить добавление произношения";
    private final String onExample = "✅ Включить примеры";
    private final String offExample = "✖ Отключить примеры";
    public ReplyKeyboard getSetting(ReplyKeyboardMarkup replyKeyboardMarkup, long chatId) {
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow11 = new KeyboardRow();
        KeyboardRow keyboardRow12 = new KeyboardRow();
        KeyboardRow keyboardRow13 = new KeyboardRow();
        KeyboardRow keyboardRow14 = new KeyboardRow();
        //KeyboardRow keyboardRow15 = new KeyboardRow();

        keyboardRow11.add("\uD83D\uDCDA Выбрать словарь");
        keyboardRow11.add("\uD83D\uDCE5 Скачать текущий словарь");
        keyboardRow12.add("\uD83D\uDDD1 Очистить словарь");
        keyboardRow12.add("⏱ Установить временной интервал");

        if(Database.getUserVoice(chatId)==1){
            keyboardRow13.add(offVoice);
        }
        else keyboardRow13.add(onVoice);

        if(Database.getUserExample(chatId)==1){
            keyboardRow13.add(offExample);
        }
        else keyboardRow13.add(onExample);
        keyboardRow14.add("<Назад");
        keyboardRow14.add("\uD83E\uDD78 Скрыть меню");


        keyboardRows.add(keyboardRow11);
        keyboardRows.add(keyboardRow12);
        keyboardRows.add(keyboardRow13);
        keyboardRows.add(keyboardRow14);
        //keyboardRows.add(keyboardRow15);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboard getMainMenu(ReplyKeyboardMarkup replyKeyboardMarkup) {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        //KeyboardRow keyboardRow3 = new KeyboardRow();

        keyboardRow1.add("▶ Старт");
        keyboardRow1.add("◼ Стоп");
        keyboardRow1.add("♻ Вручную");
        keyboardRow2.add("⚙️ Настройка");
        keyboardRow2.add("\uD83D\uDCC8 Статистика");


        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }
    private ReplyKeyboard getDictionaryMenu(ReplyKeyboardMarkup replyKeyboardMarkup) {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardRow keyboardRow3 = new KeyboardRow();
        //KeyboardRow keyboardRow4 = new KeyboardRow();
        //KeyboardRow keyboardRow5 = new KeyboardRow();

        keyboardRow1.add("Топ 100");
        keyboardRow1.add("Топ 500");
        keyboardRow1.add("Топ 1000");
        keyboardRow1.add("Топ 2000");
        keyboardRow2.add("Английский для IT");
        keyboardRow2.add("\uD83D\uDCD2 Загрузить свой список слов");
        keyboardRow3.add("<<Назад");


        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
//        keyboardRows.add(keyboardRow4);
//        keyboardRows.add(keyboardRow5);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
    private ReplyKeyboard getTimeSetting(ReplyKeyboardMarkup replyKeyboardMarkup){
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardRow keyboardRow3 = new KeyboardRow();
//        KeyboardRow keyboardRow4 = new KeyboardRow();
//        KeyboardRow keyboardRow5 = new KeyboardRow();
//        KeyboardRow keyboardRow6 = new KeyboardRow();

        keyboardRow1.add("2 минуты");
        keyboardRow1.add("5 минут");
        keyboardRow1.add("10 минут");
        keyboardRow1.add("15 минут");
        keyboardRow1.add("20 минут");
        keyboardRow2.add("30 минут");
        keyboardRow2.add("1 час");
        keyboardRow2.add("2 часа");
        keyboardRow2.add("3 часа");
        keyboardRow2.add("6 часов");
        keyboardRow3.add("12 часов");
        keyboardRow3.add("24 часа");
        keyboardRow3.add("1 минута");
        keyboardRow3.add("<<Назад");

        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
//        keyboardRows.add(keyboardRow4);
//        keyboardRows.add(keyboardRow5);
//        keyboardRows.add(keyboardRow6);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
    public List<String> getGlobalMenu(Update update, List<String> dictionary, Menu menu, SendMessage sendMessage) {

        long chatId = UpdateWrapper.getChatId(update);
        switch (update.getMessage().getText().replaceAll("@"+getBotUsername(),"")) {
            case "/start":
                try {
                    sendMessage.setText("Добро пожаловать в бот по изучению английских слов!\n\n" +
                            "Для запуска процесса необходимо:\n"+
                            "1) выбрать словарь или загрузить свой список - /setwords \n"+
                            "2) установить временной интервал - /settime \n"+
                            "3) нажать кнопку \"Старт\" - /run\n\n" +
                            "Открыть главное меню - /menu\n"+
                            "Перейти в справку - /help");
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "/run":
            case "/continue":
            case "▶ Старт":
                startThreadIterationWords(update, dictionary, sendMessage);
                break;
            case "/stop":
            case "◼ Стоп":
                stopThreadChatId(chatId);
                if (dictionary.size() == 0) {
                    try {
                        sendMessage.setText("Не выбран словарь! Зайдите в настройки и выберите словарь");
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    getDatabase().setStateToDB(0, update);
                    try {
                        sendMessage.setText("Остановлено");
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "<<Назад":
            case "⚙️ Настройка":
                deleteMessage(update.getMessage().getMessageId(),chatId);
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup, chatId));
                sendMessage.setText("Настройка");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                if (update.getMessage().getText().equals("Главное меню")) {
                    sendMessage.setReplyMarkup(menu.getMainMenu(App.replyKeyboardMarkup));
                    sendMessage.setText("Главное меню");
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "/setwords":
            case "\uD83D\uDCDA Выбрать словарь":
                deleteMessage(update.getMessage().getMessageId(),chatId);
                sendMessage.setReplyMarkup(menu.getDictionaryMenu(App.replyKeyboardMarkup));
                sendMessage.setText("Выберите словарь");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            case onVoice:
                getDatabase().setStateVoiceToDB(1,update);
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup,chatId));
                sendMessage.setText("Включено добавление произношения");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            case offVoice:
                getDatabase().setStateVoiceToDB(0,update);
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup,chatId));
                sendMessage.setText("Отключено добавление произношения");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            case onExample:
                getDatabase().setStateExampleToDB(1,update);
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup,chatId));
                sendMessage.setText("Включены примеры");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            case offExample:
                getDatabase().setStateExampleToDB(0,update);
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup,chatId));
                sendMessage.setText("Отключены примеры");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            case "/sendpackwords":
            case "♻ Вручную":
            case "Отправить пакет слов":
                deleteMessage(update.getMessage().getMessageId(),chatId);
                sendPackWords(update);
                break;
            case "/downloaddictionary":
            case "\uD83D\uDCE5 Скачать текущий словарь":
                deleteMessage(update.getMessage().getMessageId(),chatId);
                if(getDictionary().getDictionaryFromDB(chatId).size()>0){
                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setChatId(String.valueOf(chatId));
                    File file = new File(getDictionary().downloadCurrentDictionary(update));
                    sendDocument.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup, chatId));
                    sendDocument.setDocument(new InputFile(file));
                    try {
                        execute(sendDocument);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    try {
                        Arrays.stream(Objects.requireNonNull(new File(
                                new File(Menu.class.getProtectionDomain().getCodeSource().getLocation()
                                        .toURI()).getParent() + "/").listFiles((f, p) -> p.endsWith(chatId+".txt")))
                        ).forEach(File::delete);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    sendMessage.setText("Словарь сейчас пуст. Добавьте слова");
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "\uD83E\uDD78 Скрыть меню":
            case "/hidemenu":
                System.out.println("Hide menu");

                ReplyKeyboardRemove removeKeyboard = new ReplyKeyboardRemove();
                removeKeyboard.setRemoveKeyboard(true);
                sendMessage.setReplyMarkup(removeKeyboard);
                sendMessage.setText("Clear menu");
                try {
                    deleteMessage(update.getMessage().getMessageId(),chatId);
                    deleteMessage(execute(sendMessage).getMessageId(),chatId);

                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "<Назад":
            case "Главное меню":
                sendMessage.setReplyMarkup(menu.getMainMenu(App.replyKeyboardMarkup));
                sendMessage.setText("Главное меню");
                try {
                    deleteMessage(update.getMessage().getMessageId(),chatId);
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "Топ 100":
                deleteMessage(update.getMessage().getMessageId(),chatId);
                System.out.println("Выбрано топ 500 слов");
                dictionary = selectDictionary(update, dictionary, menu, sendMessage, TypeDictionary.Top100Words);
                break;
            case "Топ 500":
                deleteMessage(update.getMessage().getMessageId(),chatId);
                System.out.println("Выбрано топ 500 слов");
                dictionary = selectDictionary(update, dictionary, menu, sendMessage, TypeDictionary.Top500Words);
                break;
            case "Топ 1000":
                deleteMessage(update.getMessage().getMessageId(),chatId);
                System.out.println("Выбрано топ 1000 слов");
                dictionary = selectDictionary(update, dictionary, menu, sendMessage, TypeDictionary.Top1000Words);
                break;
            case "Топ 2000":
                deleteMessage(update.getMessage().getMessageId(),chatId);
                System.out.println("Выбрано топ 2000 слов");
                dictionary = selectDictionary(update, dictionary, menu, sendMessage, TypeDictionary.Top2000Words);
                break;
            case "Английский для IT":
                deleteMessage(update.getMessage().getMessageId(),chatId);
                System.out.println("Выбрано Английский для IT");
                dictionary = selectDictionary(update, dictionary, menu, sendMessage, TypeDictionary.EnglishForIT);
                break;
            case "\uD83D\uDCD2 Загрузить свой список слов":
                deleteMessage(update.getMessage().getMessageId(),chatId);
                sendMessage.setText("Отправьте список слов в формате:\n" +
                        "английское слово - русский перевод\n\n"+
                        "Либо загрузите и отправьте документ с необходимым списоком слов, используя указанный формат");
                sendMessage.setReplyMarkup(menu.getDictionaryMenu(App.replyKeyboardMarkup));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "\uD83D\uDDD1 Очистить словарь":
                deleteMessage(update.getMessage().getMessageId(),chatId);
                dictionary = getDictionary().clearDictionaryToDB(update);
                sendMessage.setText("Словарь очищен");
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup,chatId));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "/settime":
            case "⏱ Установить временной интервал":
                deleteMessage(update.getMessage().getMessageId(),chatId);
                sendMessage.setText("Выберите интервал");
                sendMessage.setReplyMarkup(menu.getTimeSetting(App.replyKeyboardMarkup));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "2 минуты":
                setTime(update, menu, sendMessage, 2);
                break;
            case "5 минут":
                setTime(update, menu, sendMessage, 5);
                break;
            case "10 минут":
                setTime(update, menu, sendMessage, 10);
                break;
            case "15 минут":
                setTime(update, menu, sendMessage, 15);
                break;
            case "20 минут":
                setTime(update, menu, sendMessage, 20);
                break;
            case "30 минут":
                setTime(update, menu, sendMessage, 30);
                break;
            case "1 час":
                setTime(update, menu, sendMessage, 60);
                break;
            case "2 часа":
                setTime(update, menu, sendMessage, 120);
                break;
            case "3 часа":
                setTime(update, menu, sendMessage, 180);
                break;
            case "6 часов":
                setTime(update, menu, sendMessage, 360);
                break;
            case "12 часов":
                setTime(update, menu, sendMessage, 720);
                break;
            case "24 часа":
                setTime(update, menu, sendMessage, 1440);
                break;
            case "1 минута":
                setTime(update, menu, sendMessage, 1);
                break;
            case "/menu":
                goToMenu(update, menu, sendMessage, chatId);
                break;
            case "/help":
                String textHelp = "\uD83D\uDCDD Справка\n\n" +
                "Данный бот помогает выучить самые популярные английские слова, путём появления сообщений с текстом слова и перевода, и прикрепления звукового файла с произношением. Для каждого пользователя ведётся индивидуальный словарь.\n\n"+
                "Возможности \uD83D\uDCAA:\n"+
                "- \uD83E\uDD16 автоматическое появление слов;\n"+
                "- \uD83D\uDC49 ручной перебор слов;\n"+
                "- \uD83D\uDD57 установка интервала появления слов;\n"+
                "- \uD83D\uDD09 прикрепление звукового файла с произношением;\n"+
                "- \uD83D\uDD0D примеры использования слов;\n"+
                "- \uD83D\uDDD1 возможность удаления слов из словаря;\n"+
                "- \uD83D\uDCDD добавление слов в словарь, посредством вставки списка слов, отправки файла со словами;\n"+
                "- \uD83D\uDCBE скачивание текущего словаря.\n\n"+
                        "Для запуска процесса необходимо:\n\n"+
                        "1) выбрать словарь или загрузить свой список - /setwords \n"+
                        "2) установить временной интервал - /settime \n"+
                        "3) нажать кнопку \"Старт\" - /run\n\n"+
                        "/menu - переход в главное меню\n\n" +
                        "Поддержать проект: https://yoomoney.ru/to/4100117612054619\n\n"+
                        "*Для связи по любым вопросам: @as_alekseev";
                sendMessage.setText(textHelp);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "\uD83D\uDCC8 Статистика":
                deleteMessage(update.getMessage().getMessageId(),chatId);
                sendMessage.setText(getStatisticData(chatId));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;

/*            case "Донаты":
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText("\uD83D\uDE4F Задонатить");
                inlineKeyboardButton.setCallbackData("Донат");
                inlineKeyboardButton.setUrl("https://yoomoney.ru/to/4100117612054619");
                rowInline.add(inlineKeyboardButton);
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setText("Для дальнейшего развития проекта, можно отправить любую сумму, которую Вы считаете нужной");
                sendMessage.setReplyMarkup(markupInline);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;*/
        }
        return dictionary;
    }

    public static String getStatisticData(long chatId) {
        String countWords = String.valueOf(Database.getCountWords(chatId));
        String time = String.valueOf(Database.getUserTime(chatId));
        String countUsers = String.valueOf(Database.getCountUsers());
        String countActiveUsers = String.valueOf(Database.getCountActiveUsers());

        if(countWords.isEmpty() || countWords.equals("null")||countWords.equals("0")){
            countWords = "0";
        }
        if(time.isEmpty() || time.equals("null") || time.equals("0")){
            time = "Не назначено";
        }
        return "\uD83D\uDCDA Количество слов в словаре: "+countWords+"\n"+
                "⏱ Временной интервал (в минутах): "+time+"\n"+
                "___________________________________________\n"+
                "Всего пользователей: "+ countUsers+"\n"+
                "Сейчас онлайн: "+countActiveUsers;

    }

    public void startThreadIterationWords(Update update, List<String> dictionary, SendMessage sendMessage) {
        //getDatabase();
        long chatId = UpdateWrapper.getChatId(update);
        Map<String, Object> dataConfig = Database.getJdbi().getFirstRowFromResponse(
                Collections.singletonList(chatId),
                "select time from configuration where chatId=?", false);
        if (dataConfig.size() == 0 || dataConfig.get("TIME") == null) {
            try {
                sendMessage.setText("Не выбрано время! Установите в настройках интервал появления слов");
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if (threadClient.contains(chatId)) {
            try {
                sendMessage.setText("Процесс запоминания слов уже запущен!");
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if (dictionary.size() == 0) {
            try {
                sendMessage.setText("Не выбран словарь! Зайдите в настройки и выберите словарь");
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else {
            runIterationWords(update, dictionary);
        }
    }

    public void goToMenu(Update update, Menu menu, SendMessage sendMessage, long chatId) {
        sendMessage.setText("Главное меню");
        sendMessage.setReplyMarkup(menu.getMainMenu(App.replyKeyboardMarkup));
        try {
            deleteMessage(update.getMessage().getMessageId(), chatId);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void deleteMessage(int messageId, long chatId){
        DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), messageId);
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    private void setTime(Update update, Menu menu, SendMessage sendMessage, int minutes) {
        getDatabase().setTimeSettingToDB(minutes, update);
        sendMessage.setText("Время установлено");
        sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup,UpdateWrapper.getChatId(update)));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private List<String> selectDictionary(Update update, List<String> dictionary, Menu menu, SendMessage sendMessage, TypeDictionary typeDictionary) {
        dictionary.clear();
        getDictionary().clearDictionaryToDB(update);
        getDatabase().setWordsToDB(getDictionary().setDictionary(typeDictionary, update), update);
        dictionary = getDictionary().getDictionaryFromDB(update.getMessage().getChatId());
        sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup,UpdateWrapper.getChatId(update)));
        sendMessage.setText("Словарь загружен");
        try {
            execute(sendMessage);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return dictionary;
    }

}
