package com.telegrambot;

import com.offbytwo.jenkins.model.TestCase;
import com.telegrambot.audio.Audio;
import com.telegrambot.database.Database;
import org.apache.commons.lang.math.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.*;

import static com.telegrambot.database.Database.getJdbi;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Unit test for simple App.
 */
public class AppTest  {

    @org.junit.jupiter.api.Test
    void fastTest(){

        Document doc;
        try {
            doc = Jsoup.connect("https://translate.google.com/?hl=ru&sl=en&tl=ru&text=she&op=translate").get();
            //System.out.println(doc);
            for (Element result : doc.select("html-blob")) {
                final String text = ((TextNode) result.childNode(0)).getWholeText();
                System.out.println(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


}
