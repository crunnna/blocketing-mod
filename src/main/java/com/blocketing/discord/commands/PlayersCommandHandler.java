package com.blocketing.discord.commands;

import com.blocketing.Blocketing;
import com.blocketing.utils.UpdateChecker;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class handles the `/players` slash command and related button interactions from Discord,
 * providing a paginated list of online players on the Minecraft server.
 */
public class PlayersCommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("Blocketing|Discord|PlayersCommandHandler"); // Logger for player command events and errors.

    private final int MAX_ACTIVE = 2; // Maximum number of active paginated player list messages tracked for cleanup.
    private final LinkedList<String> recentPlayerMessages = new LinkedList<>(); // Stores the IDs of recent player list messages for cleanup.
    private final ConcurrentHashMap<String, Integer> playerPageMap = new ConcurrentHashMap<>(); // Maps Discord message IDs to their current page number.
    private record PlayerInfo(List<ServerPlayerEntity> players, int online, int max) {} // Simple record to hold player info snapshot.

    /**
     * Handles the `/players` command interaction and sends a paginated embed of online players.
     *
     * @param event The slash command interaction event.
     * @param page  The page number to display.
     */
    public void handle(SlashCommandInteractionEvent event, int page) {
        MinecraftServer server = Blocketing.getMinecraftServer();
        if (server == null) {
            LOGGER.error("Minecraft server is not available for /players command.");
            event.reply("Minecraft server is not available.").setEphemeral(true).queue();
            return;
        }

        PlayerInfo info = getPlayerInfo(server);
        int pageSize = 10;
        int totalPages = (int) Math.ceil(info.online() / (double) pageSize);

        EmbedBuilder embed = buildPlayerEmbed(info.players(), info.online(), info.max(), page, pageSize, totalPages, server);
        Button prev = Button.primary("players_prev", "⬅ Previous").withDisabled(page == 0);
        Button next = Button.primary("players_next", "Next ➡").withDisabled(page + 1 >= totalPages);

        // If there are more players than fit on one page, add navigation buttons
        if (info.online() > pageSize) {
            event.replyEmbeds(embed.build())
                    .addActionRow(prev, next)
                    .queue(response -> {
                        response.retrieveOriginal().queue(original -> {
                            String messageId = original.getId();
                            playerPageMap.put(messageId, page);

                            // Track recent messages for cleanup to avoid clutter
                            recentPlayerMessages.addLast(messageId);
                            while (recentPlayerMessages.size() > MAX_ACTIVE) {
                                String oldId = recentPlayerMessages.removeFirst();
                                event.getChannel().editMessageComponentsById(oldId).setComponents().queue();
                                playerPageMap.remove(oldId);
                            }
                            LOGGER.info("Sent paginated player list (page {}/{}) to Discord.", page + 1, totalPages);
                        });
                    });
        } else {
            event.replyEmbeds(embed.build()).queue();
            LOGGER.info("Sent player list (no pagination, {} players) to Discord.", info.online());
        }
    }

    /**
     * Handles button interactions for navigating between player list pages.
     *
     * @param event The button interaction event.
     */
    public void handleButton(ButtonInteractionEvent event) {
        String messageId = event.getMessageId();
        Integer page = playerPageMap.getOrDefault(messageId, 0);

        // Determine which button was pressed and update the page number
        if (event.getComponentId().equals("players_prev")) {
            if (page > 0) page--;
        } else if (event.getComponentId().equals("players_next")) {
            page++;
        } else {
            return;
        }

        event.deferEdit().queue();
        updatePlayersEmbed(event, page, messageId);
        LOGGER.debug("Handled player list pagination button: {}, new page: {}", event.getComponentId(), page + 1);
    }

    /**
     * Updates the player list embed for the given page and message.
     *
     * @param event     The button interaction event.
     * @param page      The page number to display.
     * @param messageId The Discord message ID to update.
     */
    private void updatePlayersEmbed(ButtonInteractionEvent event, int page, String messageId) {
        MinecraftServer server = Blocketing.getMinecraftServer();
        if (server == null) {
            LOGGER.warn("Minecraft server is not available for player list pagination.");
            event.getHook().editOriginal("Minecraft server is not available.").queue();
            return;
        }
        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        int online = players.size();
        int max = server.getPlayerManager().getMaxPlayerCount();

        int pageSize = 10;
        int totalPages = (int) Math.ceil(online / (double) pageSize);

        EmbedBuilder embed = buildPlayerEmbed(players, online, max, page, pageSize, totalPages, server);

        Button prev = Button.primary("players_prev", "⬅ Previous").withDisabled(page == 0);
        Button next = Button.primary("players_next", "Next ➡").withDisabled(page + 1 >= totalPages);

        event.getHook().editOriginalEmbeds(embed.build())
                .setActionRow(prev, next)
                .queue();

        playerPageMap.put(messageId, page);
        LOGGER.debug("Updated player list embed for message {} to page {}/{}", messageId, page + 1, Math.max(totalPages, 1));
    }

    /**
     * Builds the embed for displaying online players.
     *
     * @param players      The list of online players.
     * @param online       The number of online players.
     * @param max          The maximum player count.
     * @param page         The current page number.
     * @param pageSize     The number of players per page.
     * @param totalPages   The total number of pages.
     * @param server       The Minecraft server instance.
     * @return An EmbedBuilder containing the player list embed.
     */
    private EmbedBuilder buildPlayerEmbed(List<ServerPlayerEntity> players, int online, int max, int page, int pageSize, int totalPages, MinecraftServer server) {
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, online);

        StringBuilder playerList = new StringBuilder();
        if (online == 0) {
            playerList.append("No players online.");
        } else {
            // List only the players for the current page
            List<ServerPlayerEntity> pagePlayers = players.subList(fromIndex, toIndex);
            for (ServerPlayerEntity p : pagePlayers) {
                playerList.append("- ").append(p.getGameProfile().name()).append("\n");
            }
        }

        String modVersion = UpdateChecker.getCurrentModVersion();
        String serverName = server.getServerMotd();

        String description = """
**
🟢 Online: %d/%d
```
%s
```**
**📖 Page %d/%d**
""".formatted(online, max, playerList, page + 1, Math.max(totalPages, 1));

        return new EmbedBuilder()
                .setTitle("👥 Online Players")
                .setColor(new Color(0x77DD77))
                .setDescription(description)
                .setThumbnail("https://raw.githubusercontent.com/crunnna/blocketing-mod/main/src/main/resources/assets/blocketing/icon.png")
                .setFooter(serverName + " - using Blocketing v" + modVersion, null)
                .setTimestamp(Instant.now());
    }

    /**
     * Retrieves the current list of online players and server stats.
     *
     * @param server The Minecraft server instance.
     * @return PlayerInfo containing the player list, online count, and max count.
     */
    private PlayerInfo getPlayerInfo(MinecraftServer server) {
        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        int online = players.size();
        int max = server.getPlayerManager().getMaxPlayerCount();
        return new PlayerInfo(players, online, max);
    }
}