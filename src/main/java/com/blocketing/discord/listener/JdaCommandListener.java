package com.blocketing.discord.listener;

import com.blocketing.discord.commands.ConsoleCommandHandler;
import com.blocketing.discord.commands.PlayersCommandHandler;
import com.blocketing.discord.commands.StatusCommandHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * This class listens for Discord slash commands and button interactions,
 * and delegates them to the appropriate command handlers.
 */
public class JdaCommandListener extends ListenerAdapter {
    private final ConsoleCommandHandler consoleHandler = new ConsoleCommandHandler();
    private final StatusCommandHandler statusHandler = new StatusCommandHandler();
    private final PlayersCommandHandler playersHandler = new PlayersCommandHandler();

    /**
     * Handles incoming slash command interactions from Discord.
     *
     * @param event The slash command interaction event.
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "console", "command" -> consoleHandler.handle(event);
            case "status" -> statusHandler.handle(event);
            case "players" -> playersHandler.handle(event, 0);
        }
    }

    /**
     * Handles button interactions for paginated player lists.
     *
     * @param event The button interaction event.
     */
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        playersHandler.handleButton(event);
    }
}