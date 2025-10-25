package com.blocketing.discord.listener;

import com.blocketing.discord.commands.ConsoleCommandHandler;
import com.blocketing.discord.commands.PlayersCommandHandler;
import com.blocketing.discord.commands.StatusCommandHandler;
import com.blocketing.config.ConfigLoader;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class listens for Discord slash commands and button interactions,
 * and delegates them to the appropriate command handlers.
 */
public class JdaCommandListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger("Blocketing|Discord|JdaCommandListener");

    private final ConsoleCommandHandler consoleHandler = new ConsoleCommandHandler();
    private final StatusCommandHandler statusHandler = new StatusCommandHandler();
    private final PlayersCommandHandler playersHandler = new PlayersCommandHandler();

    /**
     * Handles incoming slash command interactions from Discord.
     * Commands are only delegated if the interaction happened in the configured channel.
     *
     * @param event The slash command interaction event.
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        final MessageChannelUnion eventChannel = event.getChannel();
        if (isNotInConfiguredChannel(eventChannel)) {
            LOGGER.debug("Ignored slash command '{}' from non-configured channel", event.getName());
            return;
        }

        switch (event.getName()) {
            case "console", "command" -> consoleHandler.handle(event);
            case "status" -> statusHandler.handle(event);
            case "players" -> playersHandler.handle(event, 0);
        }
    }

    /**
     * Handles button interactions for paginated player lists.
     * Button events are also restricted to the configured channel to avoid cross-server handling.
     *
     * @param event The button interaction event.
     */
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        final MessageChannelUnion eventChannel = event.getChannel();
        if (isNotInConfiguredChannel(eventChannel)) {
            LOGGER.debug("Ignored button interaction '{}' from non-configured channel", event.getComponentId());
            return;
        }
        playersHandler.handleButton(event);
    }

    /**
     * Checks if the given channel is not the one configured for bot interactions.
     *
     * @param channel The channel to check.
     * @return true if the channel is not the configured one, false otherwise.
     */
    private boolean isNotInConfiguredChannel(MessageChannelUnion channel) {
        final String configured = ConfigLoader.getProperty("CHANNEL_ID");
        if (channel == null) return true; // treat null channel as 'not configured'
        if (configured == null || configured.isBlank()) return true; // no configured channel -> treat as not configured
        try {
            return !channel.getId().equals(configured);
        } catch (Exception e) {
            LOGGER.debug("Failed to compare channel IDs", e);
            return true;
        }
    }
}