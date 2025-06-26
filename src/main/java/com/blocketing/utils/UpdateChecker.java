package com.blocketing.utils;

import com.blocketing.config.ConfigLoader;
import com.blocketing.discord.JdaDiscordBot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * This class is responsible for checking for updates of the Blocketing mod
 * and notifying operators and Discord about available updates.
 */
public class UpdateChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger("Blocketing|UpdateChecker");
    private static final String GITHUB_API_URL = "https://api.github.com/repos/crunnna/blocketing-fabric-mod/releases/latest";
    private static final String CHANGELOG_URL = "https://github.com/crunnna/blocketing-fabric-mod/releases/latest";


    /**
     * Checks for the latest version of the Blocketing mod and notifies Discord if an update is available.
     *
     * @param server The Minecraft server instance.
     */
    public static void checkForUpdateAndNotifyDiscord(MinecraftServer server) {
        if (!ConfigLoader.getBooleanProperty("UPDATE_INFO_ENABLED", true)) return;
        String latest = fetchLatestVersion();
        String current = ConfigLoader.getProperty("mod_version");
        if (latest != null && !latest.equals(current)) {
            String msg = "A new Blocketing version (" + latest + ") is available! See the changelog: " + CHANGELOG_URL;
            JdaDiscordBot.sendEmbedToDiscord("Update Available", msg, 0xFF0000, null);
        }
    }

    /**
     * Checks for the latest version of the Blocketing mod and notifies operators if an update is available.
     *
     * @param player The player who has permission to check for updates.
     */
    public static void checkForUpdateAndNotifyOperator(ServerPlayerEntity player) {
        if (player.hasPermissionLevel(4) && com.blocketing.config.ConfigLoader.getBooleanProperty("UPDATE_INFO_ENABLED", true)) {
            String latest = fetchLatestVersion();
            String current = ConfigLoader.getProperty("mod_version");
            if (latest != null && !latest.equals(current)) {
                String msg = "A new Blocketing version (" + latest + ") is available! See the changelog: " + CHANGELOG_URL;
                player.sendMessage(Text.of(msg), false);
                player.sendMessage(Text.of("Tip: Use /blocketing toggle update-info to enable/disable this notification."), false);
            }
        }
    }

    /**
     * Checks for the latest version of the Blocketing mod on GitHub.
     *
     * @return The latest version as a String, or null if an error occurs.
     */
    private static String fetchLatestVersion() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(GITHUB_API_URL).openConnection();
            conn.setRequestProperty("Accept", "application/vnd.github+json");
            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (scanner.hasNext()) json.append(scanner.nextLine());
            scanner.close();
            String tag = json.toString().split("\"tag_name\":\"")[1].split("\"")[0];
            return tag.replaceFirst("^v", "");
        } catch (Exception e) {
            LOGGER.warn("Could not check for updates", e);
            return null;
        }
    }
}
