package com.blocketing;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import io.github.cdimascio.dotenv.Dotenv;

public class DiscordBot {

    private static final Dotenv dotenv = Dotenv.configure().directory("../").load();                                                                                                                                                                                                                                // Thanks to euphoriys solving a bug with adding an additional "."
    private static final String BOT_TOKEN = dotenv.get("BOT_TOKEN");
    private static final String CHANNEL_ID = dotenv.get("CHANNEL_ID");

    /**
     * Sends a message to the Discord channel using Discord-Bot.
     *
     * @param message The message to send.
     */
    public static void sendMessage(String message) {
        try {
            // URL with URI creation
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