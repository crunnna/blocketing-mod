package com.blocketing.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Utility class responsible for loading, saving, and managing the configuration for the blocketing-mod.
 * <p>
 * This class should not be instantiated.
 */
public final class ConfigLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger("Blocketing|Config");

    // Holds the loaded configuration properties
    private static final Properties config = new Properties();
    private static final String CONFIG_PATH = "config/blocketing.properties";

    // Static initializer to load configuration at class load time
    static { loadConfig(); }

    /**
     * Loads the configuration from the 'blocketing.properties' file.
     * Creates the config directory and file if they do not exist.
     */
    private static void loadConfig() {
        try {
            // Ensure the 'config' directory exists
            if (!Files.exists(Paths.get("config"))) {
                Files.createDirectories(Paths.get("config"));
                LOGGER.info("Configuration directory created at config");
            }
            // Ensure the configuration file exists
            if (!Files.exists(Paths.get(CONFIG_PATH))) {
                Files.createFile(Paths.get(CONFIG_PATH));
                LOGGER.info("Configuration file created at {}", CONFIG_PATH);
            }
            // Load properties from the configuration file
            try (InputStream input = Files.newInputStream(Paths.get(CONFIG_PATH))) {
                config.load(input);
            }
            // Set default values for missing properties
            if (!config.containsKey("DISCORD_CHAT_LOG_ENABLED")) {
                config.setProperty("DISCORD_CHAT_LOG_ENABLED", "true");
                saveConfig();
            }
        } catch (IOException e) {
            LOGGER.error("Error loading the configuration file", e);
        }
    }

    /**
     * Validates the Discord configuration.
     *
     * @param logger The logger to log warnings if the configuration is invalid.
     * @return true if the configuration is valid, false otherwise.
     */
    public static boolean isDiscordConfigValid(final Logger logger) {
        final String token = getProperty("BOT_TOKEN");
        final String guildId = getProperty("GUILD_ID");
        final String channelId = getProperty("CHANNEL_ID");

        if (token == null || token.isBlank()) {
            logger.warn("BOT_TOKEN is not set! Discord integration will be disabled.");
            return false;
        }
        if (guildId == null || !guildId.matches("\\d{17,20}")) {
            logger.warn("GUILD_ID is invalid or not set! Discord integration will be disabled.");
            return false;
        }
        if (channelId == null || !channelId.matches("\\d{17,20}")) {
            logger.warn("CHANNEL_ID is invalid or not set! Discord integration will be disabled.");
            return false;
        }
        return true;
    }

    /**
     * Reloads the configuration from the file, replacing all current properties.
     */
    public static void reloadConfig() {
        try (InputStream input = Files.newInputStream(Paths.get(CONFIG_PATH))) {
            config.clear();
            config.load(input);
            LOGGER.info("Configuration reloaded.");
        } catch (IOException e) {
            LOGGER.error("Error reloading the configuration file", e);
        }
    }

    /**
     * Gets the property value for the given key.
     *
     * @param key The key to retrieve the value for.
     * @return The value of the key, or null if not found.
     */
    public static String getProperty(final String key) {
        return config.getProperty(key);
    }

    /**
     * Gets the property value as a boolean for the given key.
     *
     * @param key The key to retrieve the value for.
     * @param defaultValue The default value to return if the key is not found.
     * @return The boolean value of the key, or the default value if not found.
     */
    public static boolean getBooleanProperty(final String key, final boolean defaultValue) {
        final String value = config.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    /**
     * Sets the property value for the given key and saves the configuration.
     *
     * @param key The key to set the value for.
     * @param value The value to set.
     */
    public static void setProperty(final String key, final String value) {
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
            LOGGER.error("Error saving the configuration file", e);
        }
    }
}