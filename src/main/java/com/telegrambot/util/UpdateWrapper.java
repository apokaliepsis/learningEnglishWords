package com.telegrambot.util;

import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateWrapper {
    public static long getChatId(Update update){
        if(update.hasMessage()){
            return update.getMessage().getChatId();
        }
        else return update.getCallbackQuery().getMessage().getChatId();
    }
    public static String getUserName(Update update){
        if(update.hasMessage()){
            return update.getMessage().getFrom().getUserName();
        }
        else return update.getCallbackQuery().getFrom().getUserName();
    }
    public static String getLanguageCode(Update update){
        if(update.hasMessage()){
            return update.getMessage().getFrom().getLanguageCode();
        }
        else return update.getCallbackQuery().getFrom().getLanguageCode();
    }
    public static String getFirstName(Update update){
        if(update.hasMessage()){
            return update.getMessage().getFrom().getFirstName();
        }
        else return update.getCallbackQuery().getFrom().getFirstName();
    }
    //update.getMessage().getFrom().getLastName()
    public static String getLastName(Update update){
        if(update.hasMessage()){
            return update.getMessage().getFrom().getLastName();
        }
        else return update.getCallbackQuery().getFrom().getLastName();
    }

}