package com.blocketing.discord.util;

import com.blocketing.config.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * This class is responsible for sending messages to a Discord webhook.
 */
public class WebhookSender {
    private static final Logger LOGGER = LoggerFactory.getLogger("Blocketing|Webhook");

    /**
     * Sends a message to a Discord webhook.
     *
     * @param username   The username to display in the webhook message.
     * @param avatarUrl  The URL of the avatar to display in the webhook message.
     * @param content    The content of the message to send.
     */
    public static void send(String username, String avatarUrl, String content) {
        // Retrieve webhook URL from config
        String webhookUrl = ConfigLoader.getProperty("WEBHOOK_URL");
        if (webhookUrl == null || webhookUrl.isBlank()) {
            LOGGER.warn("Webhook URL is not set. Skipping webhook send.");
            return;
        }

        try {
            // Build JSON payload for Discord webhook
            String json = String.format(
                    "{\"username\":\"%s\",\"avatar_url\":\"%s\",\"content\":\"%s\"}",
                    username,
                    avatarUrl,
                    content.replace("\"", "\\\"")
            );

            // Open HTTP connection to Discord webhook
            URL url = URI.create(webhookUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send JSON payload
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            // Check HTTP response code
            int responseCode = conn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                LOGGER.warn("Failed to send webhook message: HTTP {}", responseCode);
            }

            // Close the connection
            conn.getInputStream().close();
        } catch (Exception e) {
            LOGGER.error("Failed to send webhook message", e);
        }
    }
}
