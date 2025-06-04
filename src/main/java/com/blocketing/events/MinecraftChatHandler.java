package com.blocketing.events;

import com.blocketing.config.ConfigLoader;
import com.blocketing.discord.JdaDiscordBot;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * This class is responsible for monitoring Minecraft chat messages and sending them to Discord.
 */
public class MinecraftChatHandler {

    private static boolean advancementsEnabled = ConfigLoader.getBooleanProperty("ADVANCEMENTS_ENABLED", true);
    private static boolean deathsEnabled = ConfigLoader.getBooleanProperty("DEATHS_ENABLED", true);

    /**
     * Registers the event handlers for chat messages, player join, and player disconnect events.
     */
    public static void register() {
        ServerMessageEvents.CHAT_MESSAGE.register(MinecraftChatHandler::onChatMessage);
        ServerMessageEvents.GAME_MESSAGE.register(MinecraftChatHandler::onGameMessage);
    }

    /**
     * This method is called when a chat message is sent.
     * @param message The chat message.
     * @param sender The player who sent the message.
     * @param parameters The parameters of the message.
     */
    public static void onChatMessage(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters parameters) {
        String playerName = sender.getGameProfile().getName();
        String chatMessage = message.getContent().getString();

        JdaDiscordBot.sendMessageToDiscord("**[" + playerName + "]** " + chatMessage);
    }

    /**
     * Handles game messages.
     * @param server The Minecraft server.
     * @param message The game message.
     * @param overlay Whether the message should overlay the previous message.
     */
    public static void onGameMessage(MinecraftServer server, Text message, boolean overlay) {
        String messageContent = message.getString();

        if (deathsEnabled && isDeathMessage(messageContent)) {
            handleDeathMessage(messageContent);
        } else if (advancementsEnabled && isAdvancementMessage(messageContent)) {
            handleAdvancementMessage(messageContent);
        }
    }

    /**
     * Sends a message to all players in the Minecraft chat.
     * @param minecraftServer The Minecraft server.
     * @param username The username to mention in the message.
     * @param content The content of the message.
     */
    public static void sendMessageToAllPlayers(MinecraftServer minecraftServer, String username, String content) {
        for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
            player.sendMessage(Text.of("<@DC_" + username + "> " + content), false);
        }
    }

    /**
     * Sends a message to Discord-Bot when the server starts.
     */
    public static void sendServerStartMessage(String serverName) {
        String placeholderUrl = "https://example.com/placeholder.png";
        JdaDiscordBot.sendEmbedToDiscord("Server Started", "The Minecraft server **" + serverName + "** has started.", 0x800080, placeholderUrl); // Purple colored embed
    }

    /**
     * Sends a message to Discord-Bot when the server stops.
     */
    public static void sendServerStopMessage() {
        String placeholderUrl = "https://example.com/placeholder.png";
        JdaDiscordBot.sendEmbedToDiscord("Server Stopped", "The Minecraft server has stopped.", 0x40E0D0, placeholderUrl); // Turquoise colored embed
    }

    /**
     * Checks if a message is a death message.
     * @param message The message to check.
     * @return True if the message is a death message, false otherwise.
     */
    private static boolean isDeathMessage(String message) {
        String[] deathKeywords = {
                "died", "was slain", "fell", "was shot", "tried to swim",
                "was blown up", "was killed", "was burnt", "hit the ground",
                "was impaled", "was squashed", "was poked", "drowned",
                "was pricked", "blew up", "was fireballed", "was doomed",
                "was pricked to death", "walked into a cactus",
                "drowned", "drowned while trying to escape",
                "died from dehydration", "died from dehydration while trying to escape",
                "experienced kinetic energy", "experienced kinetic energy while trying to escape",
                "was killed by [Intentional Game Design]",
                "hit the ground too hard", "fell from a high place", "fell off a ladder",
                "fell off some vines", "fell off some weeping vines",
                "fell off some twisting vines", "fell off scaffolding",
                "fell while climbing", "fell out of the water", "was doomed to fall",
                "was doomed to fall by", "was impaled on a stalagmite",
                "was squashed by a falling anvil", "was squashed by a falling block",
                "was skewered by a falling stalactite",
                "went up in flames", "walked into fire", "burned to death",
                "was burned to a crisp", "tried to swim in lava",
                "tried to swim in lava to escape",
                "was struck by lightning", "discovered the floor was lava",
                "walked into the danger zone", "was killed by magic",
                "was frozen to death", "was frozen to death by",
                "was slain by", "was stung to death",
                "was obliterated by a sonically-charged shriek",
                "was smashed by", "was shot by", "was pummeled by",
                "was fireballed by", "was shot by a skull from",
                "starved to death", "suffocated in a wall",
                "was squished too much", "was squashed by",
                "left the confines of this world",
                "was poked to death by a sweet berry bush",
                "was killed while trying to hurt",
                "was impaled by", "was skewered"
        };

        for (String keyword : deathKeywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles a death message.
     * @param message The death message.
     */
    private static void handleDeathMessage(String message) {
        int playerNameEndIndex = message.indexOf(" ");
        String playerName = message.substring(0, playerNameEndIndex).trim();
        String deathMessage = message.substring(playerNameEndIndex).trim();
        String formattedMessage = "**" + playerName + "** " + deathMessage;
        JdaDiscordBot.sendEmbedToDiscord("💀 Player Death", formattedMessage, 0x000000, null);
    }

    /**
     * Checks if a message is an advancement message.
     * @param message The message to check.
     * @return True if the message is an advancement message, false otherwise.
     */
    private static boolean isAdvancementMessage(String message) {
        return message.contains(" has made the advancement ")
                || message.contains(" has reached the goal ")
                || message.contains(" has completed the challenge ");
    }

    /**
     * Sends a message to Discord-Bot when an advancement is made.
     * @param message The message to send.
     */
    private static void handleAdvancementMessage(String message) {
        String playerName;
        String advancement;
        String title;
        int playerNameEndIndex;

        if (message.contains(" has made the advancement ")) {
            playerNameEndIndex = message.indexOf(" has made the advancement");
            playerName = message.substring(0, playerNameEndIndex).trim();
            advancement = message.substring(playerNameEndIndex + " has made the advancement ".length()).trim();
            title = "✨ Advancement Unlocked";
        } else if (message.contains(" has reached the goal ")) {
            playerNameEndIndex = message.indexOf(" has reached the goal");
            playerName = message.substring(0, playerNameEndIndex).trim();
            advancement = message.substring(playerNameEndIndex + " has reached the goal ".length()).trim();
            title = "🏆 Goal Achieved";
        } else if (message.contains(" has completed the challenge ")) {
            playerNameEndIndex = message.indexOf(" has completed the challenge");
            playerName = message.substring(0, playerNameEndIndex).trim();
            advancement = message.substring(playerNameEndIndex + " has completed the challenge ".length()).trim();
            title = "🔥 Challenge Completed";
        } else {
            return;
        }

        String formattedMessage = "**" + playerName + "** has unlocked **" + advancement + "**!";
        JdaDiscordBot.sendEmbedToDiscord(title, formattedMessage, 0x77DD77, null);
    }

    /**
     * Toggles the advancementsEnabled flag.
     */
    public static void toggleAdvancementsEnabled() {
        advancementsEnabled = !advancementsEnabled;
        ConfigLoader.setProperty("ADVANCEMENTS_ENABLED", String.valueOf(advancementsEnabled));
    }

    /**
     * Gets the status of the advancementsEnabled flag.
     * @return The status of the advancementsEnabled flag.
     */
    public static boolean isAdvancementsEnabled() {
        return advancementsEnabled;
    }

    /**
     * Toggles the deathsEnabled flag.
     */
    public static void toggleDeathsEnabled() {
        deathsEnabled = !deathsEnabled;
        ConfigLoader.setProperty("DEATHS_ENABLED", String.valueOf(deathsEnabled));
    }

    /**
     * Gets the status of the deathsEnabled flag.
     * @return The status of the deathsEnabled flag.
     */
    public static boolean isDeathsEnabled() {
        return deathsEnabled;
    }
}