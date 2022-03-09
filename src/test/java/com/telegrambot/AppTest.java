package com.telegrambot;

import com.offbytwo.jenkins.model.TestCase;
import com.telegrambot.audio.Audio;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Unit test for simple App.
 */
public class AppTest  {

    @Test
    public void testApp()
    {

        Date d1 = Calendar.getInstance().getTime();
        Date d2;

        long minutesForWait = 2;
        d2 = Calendar.getInstance().getTime();
        while (((d2.getTime() - d1.getTime()) / (60 * 1000) % 60)<minutesForWait){

            System.out.println("Выполняется...");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            d2 = Calendar.getInstance().getTime();
            System.out.println(TimeUnit.MILLISECONDS.toMinutes(d2.getTime() - d1.getTime()));
        }

    }
}
