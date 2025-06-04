package com.blocketing.discord;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import com.blocketing.config.ConfigLoader;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * This class is responsible for managing the Discord bot using JDA.
 * It handles starting the bot, sending messages and embeds to Discord, and provides access to the JDA instance.
 */
public class JdaDiscordBot {
    private static JDA jda;

    /**
     * Starts the Discord bot using the token from the configuration.
     * Registers event listeners and slash commands.
     *
     * @throws Exception if the bot cannot be started.
     */
    public static void start() {
        String token = ConfigLoader.getProperty("BOT_TOKEN");
        if (token == null || token.isBlank()) {
            System.err.println("[Blocketing] BOT_TOKEN is not set! Discord integration will be disabled.");
            return;
        }
        try {
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new JdaMessageListener(), new JdaCommandListener())
                    .build().awaitReady();

            // Register slash command in the guild
            String guildId = ConfigLoader.getProperty("GUILD_ID");
            if (guildId != null) {
                Guild guild = jda.getGuildById(guildId);
                if (guild != null) {
                    guild.updateCommands().addCommands(
                            Commands.slash("command", "Execute a Minecraft command")
                                    .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.STRING, "command", "The command to execute", true)
                    ).queue();
                }
            }
            System.out.println("JDA Discord Bot started!");
        } catch (Exception e) {
            System.err.println("[Blocketing] Failed to start Discord bot: " + e.getMessage());
            System.err.println("[Blocketing] Discord integration is disabled. Please check your configuration.");
        }
    }

    /**
     * Sends a plain text message to the configured Discord channel.
     *
     * @param message The message to send.
     */
    public static void sendMessageToDiscord(String message) {
        String channelId = ConfigLoader.getProperty("CHANNEL_ID");
        if (jda != null && channelId != null) {
            jda.getTextChannelById(channelId).sendMessage(message).queue();
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
        String channelId = ConfigLoader.getProperty("CHANNEL_ID");
        if (jda != null && channelId != null) {
            net.dv8tion.jda.api.EmbedBuilder embed = new net.dv8tion.jda.api.EmbedBuilder()
                    .setTitle(title)
                    .setDescription(description)
                    .setColor(new java.awt.Color(color));
            if (avatarUrl != null) embed.setThumbnail(avatarUrl);
            embed.setTimestamp(java.time.Instant.now());
            jda.getTextChannelById(channelId).sendMessageEmbeds(embed.build()).queue();
        }
    }

    public static JDA getJda() {
        return jda;
    }
}