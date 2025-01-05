package com.blocketing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

public class DiscordBot {

    private static String BOT_TOKEN;
    private static String CHANNEL_ID;
    static { loadConfig();} // Load the configuration when the class is loaded                                                                                                                                                                                                                                                                                                                                                  thx sultan

    /**
     * Loads the configuration from the 'config.properties' file.
     */
    private static void loadConfig() {
        Properties config = new Properties();

        try (InputStream input = DiscordBot.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("The configuration file 'config.properties' was not found!");
                return;
            }
            config.load(input);

            // Werte aus der Datei laden
            BOT_TOKEN = config.getProperty("BOT_TOKEN");
            CHANNEL_ID = config.getProperty("CHANNEL_ID");

            // Überprüfen, ob alle Werte vorhanden sind
            if (BOT_TOKEN == null || CHANNEL_ID == null) {
                System.err.println("Missing values in the configuration file. Please check the 'config.properties' file.");
            }
        } catch (IOException e) {
            System.err.println("Error loading the configuration file: " + e.getMessage());
        }
    }

    /**
     * Sends a message to the Discord channel using Discord-Bot.
     *
     * @param message The message to send.
     */
    public static void sendMessage(String message) {
        try {
            if (BOT_TOKEN == null || CHANNEL_ID == null) {
                System.err.println("Bot token or channel ID is not set. Message cannot be sent.");
                return;
            }

            // URL mit URI-Erstellung
            URI uri = new URI("https", "discord.com", "/api/v10/channels/" + CHANNEL_ID + "/messages", null);
            URL url = uri.toURL();

            // Establish connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Configure HTTP POST
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bot " + BOT_TOKEN);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create JSON payload
            String jsonPayload = "{ \"content\": \"" + message + "\" }";

            // Send data
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(jsonPayload.getBytes());
                outputStream.flush();
            }

            // Check response status
            int responseCode = connection.getResponseCode();
            if (responseCode == 200 || responseCode == 204) {
                System.out.println("Message sent successfully!");
            } else {
                System.out.println("Error sending message: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}