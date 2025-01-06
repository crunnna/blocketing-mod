package com.blocketing;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// This class would be responsible for loading the configuration file 'config.properties'.
public class ConfigLoader {

    private static final Properties config = new Properties();
    static { loadConfig(); }

    /**
     * Loads the configuration from the 'config.properties' file.
     */
    private static void loadConfig() {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("The configuration file 'config.properties' was not found!");
                return;
            }
            config.load(input);
        } catch (IOException e) {
            System.err.println("Error loading the configuration file: " + e.getMessage());
        }
    }

    /**
     * Gets the property value for the given key.
     *
     * @param key The key to retrieve the value for.
     * @return The value of the key, or null if not found.
     */
    public static String getProperty(String key) {
        return config.getProperty(key);
    }

    /**
     * Gets the property value as an integer for the given key.
     *
     * @param key The key to retrieve the value for.
     * @return The integer value of the key, or -1 if not found or invalid.
     */
    public static int getIntProperty(String key) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (NumberFormatException e) {
            System.err.println("Invalid integer for key: " + key);
            return -1;
        }
    }
}