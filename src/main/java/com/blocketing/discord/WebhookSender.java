package com.blocketing.discord;

import com.blocketing.config.ConfigLoader;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebhookSender {

    /**
     * Sends a message to a Discord webhook.
     *
     * @param username   The username to display in the webhook message.
     * @param avatarUrl  The URL of the avatar to display in the webhook message.
     * @param content    The content of the message to send.
     */
    public static void send(String username, String avatarUrl, String content) {
        String webhookUrl = ConfigLoader.getProperty("WEBHOOK_URL");
        if (webhookUrl == null || webhookUrl.isBlank()) return;
        try {
            String json = String.format("{\"username\":\"%s\",\"avatar_url\":\"%s\",\"content\":\"%s\"}",
                    username, avatarUrl, content.replace("\"", "\\\""));
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            conn.getInputStream().close();
        } catch (Exception e) {
            System.err.println("Failed to send webhook message: " + e.getMessage());
        }
    }
}
