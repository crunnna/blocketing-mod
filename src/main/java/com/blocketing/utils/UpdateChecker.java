package com.blocketing.utils;

import com.blocketing.config.ConfigLoader;
import com.blocketing.discord.JdaDiscordBot;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;
import java.util.Scanner;

/**
 * This class is responsible for checking for updates of the Blocketing mod
 * and notifying operators and Discord about available updates.
 */
public class UpdateChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger("Blocketing|UpdateChecker");
    private static final String GITHUB_API_URL = "https://api.github.com/repos/crunnna/blocketing-mod/releases/latest";
    private static final String CHANGELOG_URL = "https://github.com/crunnna/blocketing-mod/releases/latest";

    /**
     * Gets the current mod version from Fabric mod metadata.
     */
    public static String getCurrentModVersion() {
        Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer("blocketing");
        return mod.map(m -> m.getMetadata().getVersion().getFriendlyString()).orElse("unknown");
    }

    /**
     * Checks for the latest version of the Blocketing mod and notifies Discord if an update is available.
     *
     * @param server The Minecraft server instance.
     */
    public static void checkForUpdateAndNotifyDiscord(MinecraftServer server) {
        if (!ConfigLoader.getBooleanProperty("UPDATE_INFO_ENABLED", true)) return;
        new Thread(() -> {
            String latest = normalizeVersion(fetchLatestVersion());
            String current = normalizeVersion(getCurrentModVersion());

            if (latest != null && current != null && !latest.equals(current)) {
                String msg = "**A new Blocketing version (`"+ latest +"`) is available!**\n"
                        + "[View Changelog](https://github.com/crunnna/blocketing-mod/releases/latest)";
                String logoUrl = "https://raw.githubusercontent.com/crunnna/blocketing-mod/main/src/main/resources/assets/blocketing/icon.png";
                JdaDiscordBot.sendEmbedToDiscord(
                        "\uD83C\uDD95 Update Available",
                        msg,
                        0xFF0000,
                        logoUrl
                );
            }
        }).start();
    }

    /**
     * Checks for the latest version of the Blocketing mod and notifies operators if an update is available.
     *
     * @param player The player who has permission to check for updates.
     */
    public static void checkForUpdateAndNotifyOperator(ServerPlayerEntity player) {
        // Only notify players with permission level 4 (OP) and if update notifications are enabled
        if (!player.hasPermissionLevel(4) ||
                !ConfigLoader.getBooleanProperty("UPDATE_INFO_ENABLED", true)) {
            return;
        }
        new Thread(() -> {
            String latest = normalizeVersion(fetchLatestVersion());
            String current = normalizeVersion(getCurrentModVersion());
            if (latest != null && current != null && !latest.equals(current)) {

                Text prefix = Text.literal("[Blocketing] ")
                        .setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(true));

                Text newVersion = Text.literal("New version ")
                        .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(false));

                Text version = Text.literal(latest)
                        .setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true).withUnderline(true));

                Text available = Text.literal(" available!  ")
                        .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(false));

                Text changelogLink = Text.literal("→ Changelog")
                        .setStyle(Style.EMPTY
                                .withColor(Formatting.WHITE)
                                .withUnderline(true)
                                .withClickEvent(new ClickEvent.OpenUrl(URI.create(CHANGELOG_URL)))
                        );

                // Append the version information and changelog link
                Text fullMessage = prefix
                        .copy()
                        .append(newVersion)
                        .append(version)
                        .append(available)
                        .append(changelogLink);

                player.sendMessage(fullMessage, false);

                // Additional tip for toggling update notifications
                player.sendMessage(
                        Text.literal("Tip: Use /blocketing toggle update-info to enable/disable this notification.")
                                .styled(s -> s.withColor(Formatting.GRAY).withItalic(true)),
                        false
                );
            }
        }).start();
    }

    /**
     * Checks for the latest version of the Blocketing mod on GitHub.
     *
     * @return The latest version as a String, or null if an error occurs.
     */
    private static String fetchLatestVersion() {
        // Fetches the latest release information from GitHub API
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create(GITHUB_API_URL).toURL().openConnection();
            conn.setRequestProperty("Accept", "application/vnd.github+json");

            // try-with-resources for automatic closing of the scanner
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                StringBuilder json = new StringBuilder();
                while (scanner.hasNext()) json.append(scanner.nextLine());

                // Extracts the tag_name field from the JSON
                String tag = json.toString().split("\"tag_name\":\"")[1].split("\"")[0];
                return tag.trim();
            }
        } catch (Exception e) {
            LOGGER.warn("Could not check for updates", e);
            return null;
        }
    }

    /**
     * Normalizes the version string by removing leading 'v' and trimming whitespace.
     *
     * @param version The version string to normalize.
     * @return The normalized version string, or null if the input is null.
     */
    private static String normalizeVersion(String version) {
        if (version == null) return null;
        // Removes leading 'v' and trims whitespace
        return version.trim().replaceFirst("^v", "");
    }
}