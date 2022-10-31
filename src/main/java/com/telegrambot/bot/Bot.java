package com.telegrambot.bot;

import com.google.common.collect.ImmutableMap;
import com.telegrambot.App;
import com.telegrambot.service.Settings;
import com.telegrambot.audio.Audio;
import com.telegrambot.database.Database;
import com.telegrambot.dictionary.TypeDictionary;
import com.telegrambot.menu.Menu;
import com.telegrambot.dictionary.Dictionary;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import static com.telegrambot.App.replyKeyboardMarkup;
import static com.telegrambot.database.Database.getJdbi;


public class Bot extends TelegramLongPollingBot {

    private static final String BOT_ADMIN = "873327794";
    private static final Logger logger = Logger.getLogger(Bot.class);
    protected final Queue<Object> threadClient = new ConcurrentLinkedQueue<>();
    private static Queue<Map<Long, String>> clearWordClientList = new ConcurrentLinkedQueue<>();
    private Database database;
    private Menu menu;
    private Dictionary dictionary;
    private Audio audio;
    private static Queue<Map<Long, List>> tempWords = new ConcurrentLinkedQueue<>();

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
    protected Menu getMenu() {
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
//        GetUpdates getUpdates = new GetUpdates();
//        try {
//            updatesList = execute(getUpdates);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
        //System.out.println("getUpdates="+updatesList);
        System.out.println("UPDATE="+update);
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            System.out.println(t.getName());
        }
        if(update.hasMessage() && update.getMessage().hasDocument()){
            downloadFile(update);
        }
        else if (update.hasMessage()) {
            logger.debug("Receive new Update. updateID: " + update.getUpdateId());
            long chatId = update.getMessage().getChatId();
            List<?> dictionary = getDictionary().getDictionaryFromDB(chatId);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
            //sendMessage.setReplyMarkup(getMenu().getMainMenu(App.replyKeyboardMarkup));
            sendMessage.disableNotification();
            dictionary = getMenu().getGlobalMenu(update, dictionary, getMenu(), sendMessage);

            if (update.getMessage().getText().contains("\n") && update.getMessage().getText().contains(" - ")) {
                setListWordsToDictionaryUser(update, chatId, dictionary, sendMessage);
            }

        }
        else if (update.hasCallbackQuery()) {
            logger.info("Button is pressed");
            String data = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if(data.equals("/sendpackwords")){
                sendPackWords(update);
            }
            else{
                clearWordClientList.add(ImmutableMap.of(chatId, data));

                String word = update.getCallbackQuery().getData();
                System.out.println("Слово для удаления="+word);

                getJdbi().createUpdate(Arrays.asList(word, chatId),
                        "delete from words where word like concat('%',?,'%') and chatId=?", false);
                logger.info("Added word to delete");
                logger.info("clearWordClientList="+clearWordClientList);
            }

        }

    }

    private void setListWordsToDictionaryUser(Update update, long chatId, List<?> dictionary, SendMessage sendMessage) {
        System.out.println("Определена загрузка слов");
        logger.info("Start downloading words");
        System.out.println("Размер словаря до=" + dictionary.size());
        List<String> rows = getDictionary().setDictionary(TypeDictionary.CompilationWords, update);
        getDatabase().setWordsToDB(rows, update);
        dictionary = getDictionary().getDictionaryFromDB(chatId);

        System.out.println("Размер словаря после=" + dictionary.size());

        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setReplyMarkup(getMenu().getSetting(replyKeyboardMarkup));
        sendMessage.setText("Слова загружены \n"+ dictionary.size());
        System.out.println("dictionary=" + dictionary.size());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
        logger.info("Words loaded");
    }

    public void sendStartReport() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(BOT_ADMIN);
        sendMessage.setText("Запустился");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }

    }
    private void downloadFile(Update update) {
        logger.info("Received file");
        String fileName = update.getMessage().getDocument().getFileName();
        if(fileName.contains("dictionary")){
            if (update.getMessage().getDocument().getFileSize()<1000000){
                getDatabase().setWordsToDB(getDataFileFromMessage(update),update);
            }
            else{
                logger.info("File size exceeded");
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
                sendMessage.setReplyMarkup(getMenu().getMainMenu(replyKeyboardMarkup));
                sendMessage.disableNotification();
                sendMessage.setText("Превышен размер файла. Попробуйте загрузить другой файл");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    logger.error(e.getMessage());
                }
            }
            try {
                logger.info("Delete file \""+fileName+"\"");
                Arrays.stream(Objects.requireNonNull(new File(
                        new File(Bot.class.getProtectionDomain().getCodeSource().getLocation()
                                .toURI()).getParent() + "/").listFiles((f, p) ->
                        p.contains(fileName)))
                ).forEach(File::delete);
            } catch (URISyntaxException e) {
                logger.error(e.getMessage());
            }
        }

    }

    private List<String> getDataFileFromMessage(Update update) {
        logger.info("Download words");
        List<String> dataFile = new ArrayList<>();

        String doc_id = update.getMessage().getDocument().getFileId();
        String doc_name = update.getMessage().getDocument().getFileName();
        String doc_mine = update.getMessage().getDocument().getMimeType();
        int doc_size = update.getMessage().getDocument().getFileSize();
        String getID = String.valueOf(update.getMessage().getFrom().getId());

        Document document = new Document();
        document.setMimeType(doc_mine);
        document.setFileName(doc_name);
        document.setFileSize(doc_size);
        document.setFileId(doc_id);

        GetFile getFile = new GetFile();
        getFile.setFileId(document.getFileId());
        try {
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            File docFile = downloadFile(file, new File(new File(Bot.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getParent() + "/" + getID + "_" + doc_name));
            dataFile = java.nio.file.Files.readAllLines(docFile.toPath());
        }
        catch (TelegramApiException | IOException | URISyntaxException e) {
            logger.error(e.getMessage());
        }

        return dataFile;
    }

    protected void runIterationWords(Update update, List<String> dictionaryList) {

        long chatId = update.getMessage().getChatId();
        Thread thread = new Thread(String.valueOf(chatId)) {
            public void run() {
                logger.info("Started a new user thread");
                setUserInfoCompletionDB(update);
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
                Date d1 = Calendar.getInstance().getTime();
                Date d2;
                long maxTimeWaitMinutes = 360;
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
//                                    Database.getJdbi().createUpdate(Arrays.asList(wordForRemove, chatId),
//                                            "delete from words where word like concat('%',?,'%') and chatId=?", false);
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
                        //inlineKeyboardButton.setCallbackData(word.substring(0,Math.min(word.length(), 30)));
                        inlineKeyboardButton.setCallbackData(line.substring(0,Math.min(line.length(), 30)));
                        rowInline.add(inlineKeyboardButton);
                        rowsInline.add(rowInline);
                        markupInline.setKeyboard(rowsInline);
                        String urlAudio = getAudio().getUrlAudio(word);
                        String pathAudioFile = getAudio().getSoundWordFile(urlAudio, word);
                        audio.setAudio(new InputFile(new File(pathAudioFile)));

                        //message.setReplyMarkup(markupInline);

                        audio.setReplyMarkup(markupInline);
                        String[] exampleUseWord = Dictionary.getExampleUseWord(word);
                        if(exampleUseWord!=null){
                            line = line+"\n\nExample:\n"+exampleUseWord[0]+"\n"+exampleUseWord[1];
                        }
                        //message.setText(line);
                        audio.setCaption(line);
                        logger.info(word);
                        execute(audio);
                        //execute(message);

                        if(dictionaryList.size()==0){
                            message.setText("Словарь пуст! Загрузите слова");
                            execute(message);
                            break;
                        }
                        Arrays.stream(Objects.requireNonNull(new File(
                                new File(Audio.class.getProtectionDomain().getCodeSource().getLocation()
                                        .toURI()).getParent() + "/").listFiles((f, p) -> p.endsWith(".ogg")))
                        ).forEach(File::delete);
                        d2 = Calendar.getInstance().getTime();
                        long currentTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(d2.getTime() - d1.getTime());
                        logger.info("Work duration: "+currentTimeMinutes+" minutes");
                        if(currentTimeMinutes>=maxTimeWaitMinutes && chatId>0){//ограничение на время работы потока для пользователя
                            logger.info("Run time exceeded. Stopping...");
                            message.setText("◼ Стоп");
                            execute(message);
                            getDatabase().setStateToDB(0, update);
                            stopThreadChatId(chatId);
                            break;
                        }
                        TimeUnit.MINUTES.sleep((int) getJdbi()
                                .getFirstRowFromResponse(Collections.singletonList(chatId),
                                        "select time from configuration where chatId=?",
                                        false).get("TIME"));


                    } catch (InterruptedException | TelegramApiException e) {
                        logger.error(e.getMessage());
                        if(e instanceof InterruptedException){
                            break;
                        }
                        /*else if (e instanceof TelegramApiRequestException) {
                            System.out.println("Пользователь покинул чат");
                            stopThreadChatId(chatId);
                            break;
                        }*/
                        else if(e.getMessage().contains("bot was blocked")||e instanceof TelegramApiRequestException){
                            logger.info("User left the chat");
                            getDatabase().setStateToDB(0, update);
                            stopThreadChatId(chatId);
                            break;
                        }
                    } catch (URISyntaxException e) {
                        logger.error(e.getMessage());
                    }

                }
            }
        };
        thread.start();

    }

    protected void stopThreadChatId(long chatId) {
        logger.info("Stop thread");
        threadClient.remove(chatId);
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().contains(Long.toString(chatId))) {

                t.interrupt();
                break;
            }
        }
    }
    protected void sendPackWords(Update update){
        logger.info("Send pack of words");
        long chatId;
        try{
            chatId = update.getMessage().getChatId();
        } catch (Exception e) {
            logger.error(e.getMessage());
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        long finalChatId = chatId;
        Thread thread = new Thread("sendPackWordsThread"+ chatId){
            public void run(){
                setUserInfoCompletionDB(update);

                List<?> dictionaryList = getDictionary().getDictionaryFromDB(finalChatId);
                String word;

                SendAudio audio = new SendAudio();
                audio.setChatId(String.valueOf(finalChatId));
                audio.disableNotification();
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(finalChatId));
                if(dictionaryList.size()==0){
                    message.setText("Словарь пуст! Загрузите слова");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                Random rand;
                String line;

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline2 = new ArrayList<>();


                int count = 5;
                if(dictionaryList.size()<count){
                    count = dictionaryList.size();
                }

                while(count>0){
                    rowInline.clear();
                    rowsInline.clear();
                    try {
                        System.out.println("Next word: ");

                        rand = new Random();
                        line = (String) dictionaryList.get(rand.nextInt(dictionaryList.size()));
                        //word = line.substring(0, line.indexOf(' '));
                        word = getDictionary().getWordFromLine(line);
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        inlineKeyboardButton.setText("Удалить из словаря");

                        //inlineKeyboardButton.setCallbackData(word.substring(0,Math.min(word.length(), 30)));
                        inlineKeyboardButton.setCallbackData(line.substring(0,Math.min(line.length(), 30)));
                        rowInline.add(inlineKeyboardButton);

                        rowsInline.add(rowInline);

                        String urlAudio = getAudio().getUrlAudio(word);
                        String pathAudioFile = getAudio().getSoundWordFile(urlAudio, word);
                        audio.setAudio(new InputFile(new File(pathAudioFile)));

                        //message.setReplyMarkup(markupInline);

                        if(count==1){
                            InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
                            inlineKeyboardButton2.setText("Ещё слова");
                            inlineKeyboardButton2.setCallbackData("/sendpackwords");
                            rowInline2.add(inlineKeyboardButton2);
                            rowsInline.add(rowInline2);

                        }
                        markupInline.setKeyboard(rowsInline);
                        audio.setReplyMarkup(markupInline);

                        //message.setText(line);
                        String[] exampleUseWord = Dictionary.getExampleUseWord(word);
                        if(exampleUseWord!=null){
                            line = line+"\n\nExample:\n"+exampleUseWord[0]+"\n"+exampleUseWord[1];
                        }
                        audio.setCaption(line);
                        System.out.println(word);
                        execute(audio);

                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (TelegramApiException | InterruptedException e) {
                        logger.error(e.getMessage());
                    }
                    count--;
                }

                try {
                    Arrays.stream(Objects.requireNonNull(new File(
                            new File(Audio.class.getProtectionDomain().getCodeSource().getLocation()
                                    .toURI()).getParent() + "/").listFiles((f, p) -> p.endsWith(".ogg")))
                    ).forEach(File::delete);
                } catch (URISyntaxException e) {
                    logger.error(e.getMessage());

                }
            }
        };
        thread.start();
        //thread.interrupt();

    }

    @Override
    public String getBotUsername() {
        //return Settings.environment.getProperty("bot.user.name");
        if(App.IS_TEST){
            return "TestAASBot";
        }
        else{
            return "LearningTopWords_bot";
        }

    }

    @Override
    public String getBotToken() {
        String parameter = "token";
        String token = null;
        String command = System.getProperty("sun.java.command");
        if(command.contains(parameter)){
            token = command.substring(command.indexOf(parameter) + parameter.length()).trim().split(" ")[0];
        }
        if(token==null||token.isEmpty()){
            if(App.IS_TEST){
                token = Settings.environment.getProperty("bot.user.token.test");
            }
            else{
                token = Settings.environment.getProperty("bot.user.token");
            }
        }
        return token;
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
            logger.info("[STARTED] TelegramAPI. Bot Connected. Bot class: " + this);
        } catch (TelegramApiRequestException e) {
            int RECONNECT_PAUSE = 10000;
            logger.error("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    private void sendDocUploadingAFile(Long chatId, java.io.File save) {

        SendDocument sendDocumentRequest = new SendDocument();
        sendDocumentRequest.setChatId(String.valueOf(chatId));

        try {
            execute(sendDocumentRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void setUserInfoCompletionDB(Update update){
        long chatId;
        String username;
        String languageCode;
        String fio;
        String dateTime;
        try{
            chatId = update.getMessage().getChatId();
            username = update.getMessage().getFrom().getUserName();
            languageCode  = update.getMessage().getFrom().getLanguageCode();
            fio = update.getMessage().getFrom().getFirstName()+" "+update.getMessage().getFrom().getLastName();
        } catch (Exception e) {
            logger.error(e.getMessage());
            chatId = update.getCallbackQuery().getMessage().getChatId();
            username = update.getCallbackQuery().getFrom().getUserName();
            languageCode  = update.getCallbackQuery().getFrom().getLanguageCode();
            fio = update.getCallbackQuery().getFrom().getFirstName()+" "+update.getCallbackQuery().getFrom().getLastName();
        }
        dateTime  = Database.getDateTime();
        if(fio.contains("null")){
            fio = fio.replaceAll("null","").trim();
        }
        int count_word = ((Long) getJdbi().getFirstRowFromResponse(Collections.emptyList(), "select count(*) from words where chatid=" + chatId, false).get("COUNT(*)")).intValue();
        getJdbi().createUpdate(Arrays.asList(username, fio, languageCode, dateTime, count_word, chatId),
                "UPDATE configuration SET username = ?, fio = ?, language_code = ?, date = ?, words = ? WHERE chatId = ?", false);
    }

}
