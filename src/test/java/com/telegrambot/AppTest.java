package com.telegrambot;

import java.util.*;

import static com.telegrambot.database.Database.getJdbi;


/**
 * Unit test for simple App.
 */
public class AppTest  {

    @org.junit.jupiter.api.Test
    void fastTest(){

//        Document doc;
//        try {
//            doc = Jsoup.connect("https://translate.google.com/?hl=ru&sl=en&tl=ru&text=she&op=translate").get();
//            String textContents = Objects.requireNonNull(doc.select("#yDmH0d > c-wiz > div > div.WFnNle > c-wiz > nav > a.mqNsCe.jq8G6c > div.TcXXXb").first()).text();
//            System.out.println(textContents);
//            //System.out.println(doc);
////            for (Element result : doc.select("/html/head")) {
////                final String text = ((TextNode) result.childNode(0)).getWholeText();
////                System.out.println(text);
////            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String html = null;
//        try {
//            html = Jsoup.connect("https://www.translate.ru/перевод/английский-русский/it").get().html();
//            Document doc = Jsoup.parse(html);
//            Element link = doc.select(".samSource").first();
//            String linkInnerH = link.text(); // "<b>example</b>"
//            System.out.println(linkInnerH);
//            Element link2 = doc.select(".samTranslation").first();
//            String linkInnerH2 = link2.text();
//            System.out.println(linkInnerH2);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }


        for (int i = 0; i < 1000; i++) {
            try {
                System.out.println("Выполняется цикл...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }





    }


}
