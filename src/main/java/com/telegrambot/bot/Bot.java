package com.telegrambot.bot;

import com.telegrambot.App;
import com.telegrambot.audio.Audio;
import com.telegrambot.database.Database;
import com.telegrambot.dictionary.Dictionary;
import com.telegrambot.dictionary.TypeDictionary;
import com.telegrambot.menu.Menu;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.telegrambot.service.Settings;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
public class Bot extends TelegramLongPollingBot {

    private static final Logger log = Logger.getLogger(Bot.class);
    public final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();
    public final Queue<Object> receiveQueue = new ConcurrentLinkedQueue<>();
    protected final Queue<Object> threadClient = new ConcurrentLinkedQueue<>();
    private static Queue<Map<Long, String>> clearWordClientList = new ConcurrentLinkedQueue<>();
    private Database database;
    private Menu menu;
    private Dictionary dictionary;
    private Audio audio;



    protected Database getDatabase() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }
    protected Dictionary getDictionary() {
        if (dictionary == null) {
            dictionary = new Dictionary();
        }
        return dictionary;
    }
    private Menu getMenu() {
        if (menu == null) {
            menu = new Menu();
        }
        return menu;
    }
    private Audio getAudio() {
        if (audio == null) {
            audio = new Audio();
        }
        return audio;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            log.debug("Receive new Update. updateID: " + update.getUpdateId());
            receiveQueue.add(update);
            long chatId = update.getMessage().getChatId();
            List dictionary = getDictionary().getDictionaryFromDB(chatId);

            /*SendMessage sendMessage = new SendMessage()
                    .setChatId(String.valueOf(chatId))
                    .setReplyMarkup(getMenu().getMainMenu(App.replyKeyboardMarkup))
                    .disableNotification();*/
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setReplyMarkup(getMenu().getMainMenu(App.replyKeyboardMarkup));
            sendMessage.disableNotification();

            dictionary = getMenu().getGlobalMenu(update, chatId, dictionary, getMenu(), sendMessage);


            //dictionary = getWordsFromMessages(update, dictionary);
            if (update.getMessage().getText().contains("\n") && update.getMessage().getText().contains(" - ")) {
                System.out.println("Определена загрузка слов");

                System.out.println("Размер словаря до=" + dictionary.size());
                List<String> rows = getDictionary().setDictionary(TypeDictionary.CompilationWords, update);
                getDatabase().setWordsToDB(rows, chatId);
                dictionary = getDictionary().getDictionaryFromDB(chatId);

                System.out.println("Размер словаря после=" + dictionary.size());

                sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
                sendMessage.setReplyMarkup(getMenu().getSetting(App.replyKeyboardMarkup));
                sendMessage.setText("Слова загружены \n"+dictionary.size());
                System.out.println("dictionary=" + dictionary.size());
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

        }
        else if (update.hasCallbackQuery()) {
            System.out.println("Нажата кнопка");
            String line = update.getCallbackQuery().getData();
            Long id = Long.valueOf(update.getCallbackQuery().getFrom().getId());
            clearWordClientList.add(ImmutableMap.of(id, line));
//            Map<Long,String> map = new HashMap<>();
//            map.put(update.getMessage().getChatId(),line);
//            clearWordClientList.add(map);
            System.out.println("Добавлено слово для удаления");
            System.out.println("clearWordClientList="+clearWordClientList);

        }

    }
    protected void runIterationWords(long chatId, List<String> dictionaryList) {
        Thread thread = new Thread(String.valueOf(chatId)) {
            public void run() {
                String word;

                SendAudio audio = new SendAudio();
                audio.setChatId(String.valueOf(chatId));
                        audio.disableNotification();
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(chatId));

                Random rand;
                String line;
                threadClient.add(chatId);

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();


                while (getDatabase().getStateFromDB(chatId) == 1) {

                    rowInline.clear();
                    rowsInline.clear();
                    try {
                        //(Object object = bot.sendQueue.poll(); object != null; object = bot.sendQueue.poll())
                        System.out.println("clearWordClientList="+clearWordClientList);
                        for (Map<Long, String> hashMap = clearWordClientList.poll(); hashMap != null; hashMap = clearWordClientList.poll()) {
                            // For each hashmap, iterate over it
                            for (Map.Entry<Long, String> entry : hashMap.entrySet()) {
                                // Do something with your entrySet, for example get the key.
                                if (entry.getKey() == chatId) {
                                    System.out.println("Найдена мэпа. Удаляем слово");
                                    System.out.println(entry);
                                    String wordForRemove = entry.getValue();
                                    getDatabase().getJdbi().createUpdate(Arrays.asList(wordForRemove, chatId),
                                            "delete from words where word like concat('%',?,'%') and chatId=?", false);
                                    for(String str : dictionaryList)
                                    {
                                        if (str.contains(wordForRemove)){
                                            dictionaryList.remove(str);
                                            break;
                                        }
                                    }
                                    System.out.println("dictionaryList="+dictionaryList);

                                }
                            }
                        }

                        System.out.println("Next word: ");

                        rand = new Random();

                        line = dictionaryList.get(rand.nextInt(dictionaryList.size()));
                        //word = line.substring(0, line.indexOf(' '));
                        word = getDictionary().getWordFromLine(line);
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        inlineKeyboardButton.setText("Удалить из словаря");
                        inlineKeyboardButton.setCallbackData(word.substring(0,Math.min(word.length(), 30)));
                        rowInline.add(inlineKeyboardButton);
                        rowsInline.add(rowInline);
                        markupInline.setKeyboard(rowsInline);

                        String urlAudio = getAudio().getUrlAudio(word);
                        String pathAudioFile = getAudio().getSoundWordFile(urlAudio, word);
                        audio.setAudio(new InputFile(new File(pathAudioFile)));

                        message.setReplyMarkup(markupInline);


                        message.setText(line);
                        System.out.println(word);
                        execute(audio);
                        execute(message);

                        if(dictionaryList.size()==0){
                            message.setText("Словарь пуст! Загрузите слова");
                            execute(message);
                            break;
                        }
                        TimeUnit.MINUTES.sleep((int) getDatabase().getJdbi()
                                .getFirstRowFromResponse(Collections.singletonList(chatId),
                                        "select time from configuration where chatId=?",
                                        false).get("TIME"));

                        Arrays.stream(Objects.requireNonNull(new File(
                                new File(Audio.class.getProtectionDomain().getCodeSource().getLocation()
                                        .toURI()).getParent() + "/").listFiles((f, p) -> p.endsWith(".ogg")))
                        ).forEach(File::delete);

                    } catch (InterruptedException | TelegramApiException e) {
                        e.printStackTrace();
                        if (e instanceof TelegramApiRequestException) {
                            System.out.println("Пользователь покинул чат");
                            threadClient.remove(chatId);
                            for (Thread t : Thread.getAllStackTraces().keySet()) {
                                if (t.getName().equals(chatId)) {
                                    t.interrupt();
                                    break;
                                }
                            }
                            break;
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        thread.start();

    }

    @Override
    public String getBotUsername() {
        return Settings.environment.getProperty("bot.user.name");
    }

    @Override
    public String getBotToken() {
        return Settings.environment.getProperty("bot.user.token");
    }

    public void botConnect() {
        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        try {
            telegramBotsApi.registerBot(this);
            log.info("[STARTED] TelegramAPI. Bot Connected. Bot class: " + this);
        } catch (TelegramApiRequestException e) {
            int RECONNECT_PAUSE = 10000;
            log.error("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод получает абсолютный путь файла, который находится в каталоге resources
     *
     * @param path относительный путь
     * @return абсолютный путь файла
     */
    protected String getPathFromResources(String path) {

        File file = null;
        String resource = "/" + path;
        URL res = getClass().getResource(resource);
        if (res.getProtocol().equals("jar")) {

            try {
                InputStream input = getClass().getResourceAsStream(resource);
                String fileName = FilenameUtils.getName(path);
                //String extensionFile=FilenameUtils.getExtension(path);

                //file = File.createTempFile(fileName,"."+extensionFile);
                file = new File(Files.createTempDir(), fileName);

                OutputStream out = new FileOutputStream(file);
                int read;
                byte[] bytes = new byte[1024];

                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                out.close();
                file.deleteOnExit();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } else {
            file = new File(res.getFile());
        }

        if (!file.exists()) {
            throw new RuntimeException("Error: File " + file + " not found!");
        }
        return String.valueOf(file.toPath());

    }


}
