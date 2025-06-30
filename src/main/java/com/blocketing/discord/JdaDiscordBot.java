package com.blocketing.discord;

import com.blocketing.discord.listener.JdaCommandListener;
import com.blocketing.discord.listener.JdaMessageListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import com.blocketing.config.ConfigLoader;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;

/**
 * This class is responsible for managing the Discord bot using JDA.
 * It handles starting, stopping and restarting the bot, as well as sending messages and embeds to Discord.
 */
public class JdaDiscordBot {
    private static final Logger LOGGER = LoggerFactory.getLogger("Blocketing|Discord"); // Logger for Discord bot events and errors.

    private static JDA jda; // The JDA instance for interacting with Discord.

    /**
     * Starts the Discord bot using the token from the configuration.
     * Registers listeners and slash commands.
     */
    public static void start() {
        if (!ConfigLoader.isDiscordConfigValid(LOGGER)) return;

        final String token = ConfigLoader.getProperty("BOT_TOKEN");
        final String guildId = ConfigLoader.getProperty("GUILD_ID");

        try {
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new JdaMessageListener(), new JdaCommandListener())
                    .build().awaitReady();

            registerSlashCommands(guildId);
            LOGGER.info("JDA Discord Bot started!");
        } catch (Exception e) {
            LOGGER.error("Failed to start Discord bot: {}", e.getMessage());
            LOGGER.error("Discord integration is disabled. Please check your configuration.", e);
        }
    }

    /**
     * Registers slash commands for the Discord bot in the specified guild.
     *
     * @param guildId The ID of the guild where the commands should be registered.
     */
    private static void registerSlashCommands(String guildId) {
        if (jda == null) {
            LOGGER.warn("JDA is not initialized. Cannot register slash commands.");
            return;
        }
        final Guild guild = jda.getGuildById(guildId);
        if (guild != null) {
            guild.updateCommands().addCommands(
                    Commands.slash("console", "Execute a Minecraft command")
                            .addOption(OptionType.STRING, "command", "The command to execute", true),
                    Commands.slash("status", "Show server status (TPS, MSPT, CPU, RAM)"),
                    Commands.slash("players", "Show online players and total player count")
            ).queue();
        } else {
            LOGGER.warn("Discord guild not found for ID: {}", guildId);
        }
    }

    /**
     * Sends a plain text message to the configured Discord channel.
     *
     * @param message The message to send.
     */
    public static void sendMessageToDiscord(String message) {
        final String channelId = ConfigLoader.getProperty("CHANNEL_ID");
        if (jda == null) {
            LOGGER.warn("JDA is not initialized. Cannot send message to Discord.");
            return;
        }
        if (isValidSnowflake(channelId)) {
            final var channel = jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else {
                LOGGER.warn("Discord channel not found for ID: {}", channelId);
            }
        } else {
            LOGGER.warn("Invalid Discord channel ID: {}", channelId);
        }
    }

    /**
     * Sends an embed message to the configured Discord channel.
     *
     * @param title       The title of the embed.
     * @param description The description of the embed.
     * @param color       The color of the embed in RGB format.
     * @param avatarUrl   The URL of the avatar image to display in the embed (optional).
     */
    public static void sendEmbedToDiscord(String title, String description, int color, String avatarUrl) {
        final String channelId = ConfigLoader.getProperty("CHANNEL_ID");
        if (jda == null) {
            LOGGER.warn("JDA is not initialized. Cannot send embed to Discord.");
            return;
        }
        if (!isValidSnowflake(channelId)) {
            LOGGER.warn("Invalid Discord channel ID: {}", channelId);
            return;
        }
        final MessageChannel channel = jda.getChannelById(MessageChannel.class, channelId);
        if (channel == null) {
            LOGGER.warn("Discord channel not found for ID: {}", channelId);
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(new Color(color))
                .setTimestamp(Instant.now());
        if (avatarUrl != null && !avatarUrl.isBlank()) {
            embed.setThumbnail(avatarUrl);
        }
        channel.sendMessageEmbeds(embed.build()).queue();
    }

    /**
     * Gets the JDA instance for interacting with Discord.
     */
    public static void stop() {
        if (jda != null) {
            jda.shutdownNow();
            jda = null;
            LOGGER.info("JDA Discord Bot stopped.");
        }
    }

    /**
     * Restarts the JDA Discord Bot.
     * This will stop the current instance and start a new one.
     */
    public static void restart() {
        stop();
        start();
    }

    /**
     * Checks if the given string is a valid Discord snowflake ID.
     *
     * @param id The string to check.
     * @return True if the string is a valid snowflake ID, false otherwise.
     */
    private static boolean isValidSnowflake(String id) {
        return id != null && id.matches("\\d{17,20}");
    }
}