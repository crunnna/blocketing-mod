package com.blocketing.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * This class would be responsible for loading and saving the configuration for the blocketing-mod.
 */
public class ConfigLoader {

    private static final Properties config = new Properties();
    private static final String CONFIG_PATH = "config/blocketing.properties";

    static { loadConfig(); }

    /**
     * Loads the configuration from the 'blocketing.properties' file.
     */
    private static void loadConfig() {
        try {
            // Checks whether the ‘config’ directory exists and creates it if necessary
            if (!Files.exists(Paths.get("config"))) {
                Files.createDirectories(Paths.get("config"));
                System.out.println("Configuration directory created at config");
            }
            // Checks whether the configuration file exists and creates it if necessary
            if (!Files.exists(Paths.get(CONFIG_PATH))) {
                Files.createFile(Paths.get(CONFIG_PATH));
                System.out.println("Configuration file created at " + CONFIG_PATH);
            }
            // Loads the configuration file
            try (InputStream input = Files.newInputStream(Paths.get(CONFIG_PATH))) {
                config.load(input);
            }
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

    /**
     * Gets the property value as a boolean for the given key.
     *
     * @param key The key to retrieve the value for.
     * @param defaultValue The default value to return if the key is not found.
     * @return The boolean value of the key, or the default value if not found.
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = config.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    /**
     * Sets the property value for the given key.
     *
     * @param key The key to set the value for.
     * @param value The value to set.
     */
    public static void setProperty(String key, String value) {
        config.setProperty(key, value);
        saveConfig();
    }

    /**
     * Saves the configuration to the 'blocketing.properties' file.
     */
    private static void saveConfig() {
        try (FileOutputStream output = new FileOutputStream(CONFIG_PATH)) {
            config.store(output, null);
        } catch (IOException e) {
            System.err.println("Error saving the configuration file: " + e.getMessage());
        }
    }
}