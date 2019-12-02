package com.star.app.screen.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.io.IOException;
import java.util.Properties;

public class OptionsUtils {
    public static Properties loadProperties() {
        try {
            Properties properties = new Properties();
            properties.load(Gdx.files.local("options.properties").read());
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Unable to read options.properties");
    }

    public static boolean isOptionsExists() {
        return Gdx.files.local("options.properties").exists();
    }

    public static void createDefaultProperties() {
        try {
            Properties properties = new Properties();
            properties.put("PLAYER1_FORWARD", String.valueOf(Input.Keys.W));
            properties.put("PLAYER1_LEFT", String.valueOf(Input.Keys.A));
            properties.put("PLAYER1_RIGHT", String.valueOf(Input.Keys.D));
            properties.put("PLAYER1_BACKWARD", String.valueOf(Input.Keys.S));
            properties.put("PLAYER1_FIRE", String.valueOf(Input.Keys.P));
            properties.store(Gdx.files.local("options.properties").write(false), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
