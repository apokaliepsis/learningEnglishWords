package com.telegrambot;

import com.offbytwo.jenkins.model.TestCase;
import com.telegrambot.audio.Audio;
import com.telegrambot.database.Database;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.telegrambot.database.Database.getJdbi;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Unit test for simple App.
 */
public class AppTest  {

    @Test
    public void testApp()
    {
        Database.checkConnection();
        new Database().sendMessageUserLongTimeNoVisit();


    }
}
