package com.telegrambot.audio;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;

public class Audio {
    public String getUrlAudio(String text) {
        text = text.replaceAll(" ", "%20");
        return "https://translate.google.com.vn/translate_tts?ie=UTF-8&q=" + text + "&tl=en&client=tw-ob";
    }

    public String getSoundWordFile(String urlPath, String word) {
        String s;
        Process p;
        String pathSoundWordFile = FileSystems.getDefault().getPath("target").normalize().toAbsolutePath().toString() + "/" + word.replaceAll(" ", "_") + ".ogg";
        try {
            p = Runtime.getRuntime().exec("ffmpeg -i " + urlPath + " -ac 1 -map 0:a -codec:a opus -b:a 128k -vbr off -ar 24000 " + pathSoundWordFile);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File f = new File(pathSoundWordFile);
        if (f.exists() && !f.isDirectory()) {
            System.out.println("pathSoundWordFile=" + pathSoundWordFile);
            return pathSoundWordFile;
        } else return null;
    }
}
