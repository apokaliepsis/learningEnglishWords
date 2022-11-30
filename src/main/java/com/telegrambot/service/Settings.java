package com.telegrambot.service;
import java.io.InputStream;
import java.util.Properties;

public class Settings {
        public static Properties environment = new Properties();
        static {
            init();
        }
        private static void init(){
            String environmentPath = "security.properties";
            try{
                initFromFile(environment, environmentPath);
            }
            catch (Exception e){
                System.out.println("Error initializing props: "+e.getMessage());
            }

        }

        private static void initFromFile(Properties props, String filepath) {
            try{
                InputStream fio = Settings.class.getClassLoader().getResourceAsStream(filepath);
                props.load(fio);
                assert fio != null;
                fio.close();
            }catch (Exception e) {
                System.out.println("Error initializing props: "+e.getMessage());
            }
        }
    }

