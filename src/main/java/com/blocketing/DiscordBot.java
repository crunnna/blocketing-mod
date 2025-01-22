package com.blocketing;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class DiscordBot {

    /**
     * Sends a plain text message to the Discord channel.
     * @param message The message to send.
     */
    public static void sendMessage(String message) {
        sendPayload("{ \"content\": \"" + message + "\" }");
    }

    /**
     * Sends an embed message to the Discord channel.
     * @param title The title of the embed.
     * @param description The description of the embed.
     * @param color The color of the embed.
     */
    public static void sendEmbed(String title, String description, int color, String avatarUrl) {
        String jsonPayload = "{ \"embeds\": [{ " +
                "\"title\": \"" + title + "\"," +
                "\"description\": \"" + description + "\"," +
                "\"color\": " + color + "," +
                "\"thumbnail\": { \"url\": \"" + avatarUrl + "\" }" +
                "}] }";
        sendPayload(jsonPayload);
    }

    /**
     * Sends a JSON payload to the Discord channel using Discord-Bot.
     * @param jsonPayload The JSON payload to send.
     */
    private static void sendPayload(String jsonPayload) {
        try {
            String botToken = ConfigLoader.getProperty("BOT_TOKEN");
            String channelId = ConfigLoader.getProperty("CHANNEL_ID");

            if (botToken == null || channelId == null) {
                System.err.println("Bot token or channel ID is not set. Message cannot be sent.");
                return;
            }

            URI uri = new URI("https", "discord.com", "/api/v10/channels/" + channelId + "/messages", null);
            URL url = uri.toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bot " + botToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(jsonPayload.getBytes());
                outputStream.flush();
            }

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