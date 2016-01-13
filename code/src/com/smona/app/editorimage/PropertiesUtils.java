package com.smona.app.editorimage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

    public static Object getProperty(String filename, String key) {
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(
                    filename));
            Properties prop = new Properties();
            prop.load(in);
            Object value = prop.get(key);
            in.close();
            return value;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Object getProperty(InputStream in, String key) {
        try {
            Properties prop = new Properties();
            prop.load(in);
            Object value = prop.get(key);
            in.close();
            return value;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void save(String filename, String key, String value)
            throws IOException {
        Properties prop = new Properties();
        FileOutputStream outStream = new FileOutputStream(filename, false);
        prop.setProperty(key, value);
        prop.store(outStream, "Read Link");
        outStream.close();
    }
}
