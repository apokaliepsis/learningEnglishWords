package com.telegrambot.menu;



import com.telegrambot.App;
import com.telegrambot.bot.Bot;

import com.telegrambot.dictionary.TypeDictionary;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

import static com.telegrambot.App.replyKeyboardMarkup;

public class Menu extends Bot {


    public ReplyKeyboard getSetting(ReplyKeyboardMarkup replyKeyboardMarkup) {
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow11 = new KeyboardRow();
        KeyboardRow keyboardRow12 = new KeyboardRow();
        KeyboardRow keyboardRow13 = new KeyboardRow();
        KeyboardRow keyboardRow14 = new KeyboardRow();
        KeyboardRow keyboardRow15 = new KeyboardRow();

        keyboardRow11.add("\uD83D\uDCDA Выбрать словарь");
        keyboardRow11.add("\uD83D\uDCE5 Скачать текущий словарь");
        keyboardRow12.add("\uD83D\uDDD1 Очистить словарь");
        keyboardRow12.add("⏱ Установить временной интервал");
        keyboardRow13.add("<Назад");
        keyboardRow13.add("\uD83E\uDD78 Скрыть меню");


        keyboardRows.add(keyboardRow11);
        keyboardRows.add(keyboardRow12);
        keyboardRows.add(keyboardRow13);
//        keyboardRows.add(keyboardRow14);
//        keyboardRows.add(keyboardRow15);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboard getMainMenu(ReplyKeyboardMarkup replyKeyboardMarkup) {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardRow keyboardRow3 = new KeyboardRow();

        keyboardRow1.add("▶ Старт");
        keyboardRow2.add("◼ Стоп");
        keyboardRow3.add("⚙️ Настройка");


        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
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

        keyboardRow1.add("Топ 100 слов");
        keyboardRow1.add("Топ 500 слов");
        keyboardRow1.add("Топ 1000 слов");
        keyboardRow1.add("Топ 2000 слов");
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
/*        KeyboardRow keyboardRow4 = new KeyboardRow();
        KeyboardRow keyboardRow5 = new KeyboardRow();
        KeyboardRow keyboardRow6 = new KeyboardRow();*/

        keyboardRow1.add("2 минуты");
        keyboardRow1.add("5 минут");
        keyboardRow1.add("10 минут");
        keyboardRow1.add("15 минут");
        keyboardRow2.add("20 минут");
        keyboardRow2.add("30 минут");
        keyboardRow2.add("1 час");
        keyboardRow2.add("1 минута");
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
    public List<?> getGlobalMenu(Update update, List<?> dictionary, Menu menu, SendMessage sendMessage) {

        long chatId = update.getMessage().getChatId();
        switch (update.getMessage().getText()) {
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
            case "▶ Старт":
                Map<String, Object> dataConfig = getDatabase().getJdbi().getFirstRowFromResponse(Collections.singletonList(update.getMessage().getChatId()),
                        "select time from configuration where chatId=?", false);

                if (dataConfig.size() == 0 || dataConfig.get("TIME") == null) {
                    try {
                        sendMessage.setText("Не выбрано время! Установите в настройках интервал появления слов");
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                else if (threadClient.contains(update.getMessage().getChatId())) {
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
                } else {
                    getDatabase().setStateToDB(1, update);
                    runIterationWords(update, (List<String>) dictionary);
                }
                break;
            case "/stop":
            case "◼ Стоп":
                //threadClient.remove(chatId);
                stopThreadChatId(chatId);
                if (dictionary.size() == 0) {
                    try {
                        sendMessage.setText("Не выбран словарь! Зайдите в настройки и выберите словарь");
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
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
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup));
                sendMessage.setText("Выберите словарь и установите время появления слов \n"+"Количество слов в словаре: "+dictionary.size());
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
                sendMessage.setReplyMarkup(menu.getDictionaryMenu(App.replyKeyboardMarkup));
                sendMessage.setText("Выберите словарь");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            case "/sendpackwords":
            case "Отправить пакет слов":
/*                sendMessage.setText("Выберите словарь");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }*/
                sendPackWords(update);

                break;
            case "/downloaddictionary":
            case "\uD83D\uDCE5 Скачать текущий словарь":
                if(getDictionary().getDictionaryFromDB(chatId).size()>0){
                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setChatId(String.valueOf(chatId));
                    File file = new File(getDictionary().downloadCurrentDictionary(update));
                    sendDocument.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup));
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
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "<Назад":
            case "Главное меню":
                sendMessage.setReplyMarkup(menu.getMainMenu(App.replyKeyboardMarkup));
                sendMessage.setText("Главное меню");

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            case "Топ 100 слов":
                System.out.println("Выбрано топ 500 слов");
                dictionary = selectDictionary(update, dictionary, menu, sendMessage, TypeDictionary.Top100Words);
                break;
            case "Топ 500 слов":
                System.out.println("Выбрано топ 500 слов");
                dictionary = selectDictionary(update, dictionary, menu, sendMessage, TypeDictionary.Top500Words);
                break;
            case "Топ 1000 слов":
                System.out.println("Выбрано топ 1000 слов");
                dictionary = selectDictionary(update, dictionary, menu, sendMessage, TypeDictionary.Top1000Words);
                break;
            case "Топ 2000 слов":
                System.out.println("Выбрано топ 2000 слов");
                dictionary = selectDictionary(update, dictionary, menu, sendMessage, TypeDictionary.Top2000Words);
                break;
            case "\uD83D\uDCD2 Загрузить свой список слов":
                //dictionary = new ArrayList<>();
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
/*                if(getStateFromDB(chatId)==1){
                sendMessage.setText("В данный момент запущен процесс запоминания слов. Нажмите на кнопку \"Стоп\", чтобы остановить процесс");
                try {
                    execute(sendMessage);
                }
                catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }*/
                dictionary = getDictionary().clearDictionaryToDB(update);

                sendMessage.setText("Словарь очищен");
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            case "/settime":
            case "⏱ Установить временной интервал":
                sendMessage.setText("Выберите интервал");
                sendMessage.setReplyMarkup(menu.getTimeSetting(App.replyKeyboardMarkup));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "2 минуты":
                getDatabase().setTimeSettingToDB(2, update);
                sendMessage.setText("Время установлено");
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "5 минут":
                getDatabase().setTimeSettingToDB(5, update);
                sendMessage.setText("Время установлено");
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "10 минут":
                getDatabase().setTimeSettingToDB(10, update);
                sendMessage.setText("Время установлено");
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "15 минут":
                getDatabase().setTimeSettingToDB(15, update);
                sendMessage.setText("Время установлено");
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "20 минут":
                getDatabase().setTimeSettingToDB(20, update);
                sendMessage.setText("Время установлено");
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "30 минут":
                getDatabase().setTimeSettingToDB(30, update);
                sendMessage.setText("Время установлено");
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "1 час":
                getDatabase().setTimeSettingToDB(60, update);
                sendMessage.setText("Время установлено");
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "1 минута":
                getDatabase().setTimeSettingToDB(1, update);
                sendMessage.setText("Время установлено");
                sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "/menu":
                sendMessage.setText("Главное меню");
                sendMessage.setReplyMarkup(menu.getMainMenu(App.replyKeyboardMarkup));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "/help":
                String textHelp = "\uD83D\uDCDD Справка\n\n" +
                        "Данный бот помогает выучить самые популярные английские слова, путём появления сообщений с текстом слова и перевода, и прикрепления звукового файла с произношением. Для каждого пользователя ведётся индивидуальный словарь.\n\n"+

                    "Возможности \uD83D\uDCAA:\n"+
                "- \uD83E\uDD16 автоматическое появление слов;\n"+
                "- \uD83D\uDC49 ручной перебор слов;\n"+
                "- \uD83D\uDD57 установка временного интервала появления слов;\n"+
                "- \uD83D\uDD6A прикрепление звукового файла с произношением;\n"+
                "- \uD83D\uDDD1 возможность удаления слова из словаря;\n"+
                "- \uD83D\uDCDD добавление слов в словарь, посредством вставки списка слов, отправки файла со словами;\n"+
                "- \uD83D\uDCBE скачивание текущего словаря.\n\n"+

                        "Для запуска процесса необходимо:\n\n"+
                        "1) выбрать словарь или загрузить свой список - /setwords \n"+
                        "2) установить временной интервал - /settime \n"+
                        "3) нажать кнопку \"Старт\" - /run\n\n"+
/*                        "/start - запуск запоминания слов\n" +
                        "/setwords - загрузка словаря\n" +
                        "/settime - установка времени\n" +
                        "/stop - остановка запоминания слов\n" +*/
                        "/menu - переход в главное меню\n\n"+
                        "*Для связи по любым вопросам: @as_alekseev";
                sendMessage.setText(textHelp);
                //sendMessage.setReplyMarkup(menu.getMainMenu(App.replyKeyboardMarkup));
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;

        }
        return dictionary;
    }

    private List selectDictionary(Update update, List dictionary, Menu menu, SendMessage sendMessage, TypeDictionary typeDictionary) {
        //dictionary = setDictionary(TypeDictionary.Top500Words,null);
        dictionary.clear();
        getDictionary().clearDictionaryToDB(update);
        getDatabase().setWordsToDB(getDictionary().setDictionary(typeDictionary, update), update);
        dictionary = getDictionary().getDictionaryFromDB(update.getMessage().getChatId());
        //data.put("Dictionary",result);
        sendMessage.setReplyMarkup(menu.getSetting(App.replyKeyboardMarkup));
        sendMessage.setText("Словарь загружен");

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return dictionary;
    }

}
