package com.telegrambot.audio;

import org.apache.log4j.Logger;
import ru.kamatech.qaaf.properties.Properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;

public class Audio {
    private static final Logger logger = Logger.getLogger(Audio.class);
    public String getUrlAudio(String text) {
        logger.info("Get url-sound from google-translate for text: " + text);
        text = text.replaceAll(" ", "%20");
        return "https://translate.google.com.vn/translate_tts?ie=UTF-8&q=" + text + "&tl=en&client=tw-ob";
    }
    public String getSoundWordFile(String urlPath, String word) {
        logger.info("Get sound-file "+urlPath);
        String s;
        Process p;
        //String pathSoundWordFile = FileSystems.getDefault().getPath("target").normalize().toAbsolutePath().toString() + "/" + word.replaceAll(" ", "_") + ".ogg";
        String pathSoundWordFile = null;
        try {
            pathSoundWordFile = new File(Audio.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getParent() + "/" + word.replaceAll(" ", "_") + ".ogg";
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }
        try {
            //ffmpeg -i "https://translate.google.com.vn/translate_tts?ie=UTF-8&q=hot&tl=en&client=tw-ob" -ac 1 -map 0:a -codec:a libopus  -b:a 128k -vbr off -ar 24000 "hot.ogg"
            String command = "ffmpeg -i " +urlPath + " -ac 1 -map 0:a -codec:a libopus -b:a 128k -vbr off -ar 24000 " +pathSoundWordFile;
            logger.info(command);
            p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        File f = new File(pathSoundWordFile);
        if (f.exists() && !f.isDirectory()) {
            System.out.println("pathSoundWordFile=" + pathSoundWordFile);
            return pathSoundWordFile;
        } else return null;
    }
}
