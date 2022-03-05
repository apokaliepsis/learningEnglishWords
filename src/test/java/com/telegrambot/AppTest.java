package com.telegrambot;

import com.offbytwo.jenkins.model.TestCase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Unit test for simple App.
 */
public class AppTest  {

    @Test
    public void testApp()
    {

        try {
            Runtime.getRuntime().exec("ffmpeg -i https://translate.google.com.vn/translate_tts?ie=UTF-8&q=grace&tl=en&client=tw-ob /home/anton/Загрузки/bumer2.ogg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
