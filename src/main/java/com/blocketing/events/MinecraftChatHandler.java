package com.blocketing.events;

import com.blocketing.config.ConfigLoader;
import com.blocketing.discord.JdaDiscordBot;
import com.blocketing.discord.util.WebhookSender;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Handles Minecraft chat, advancement, and death messages,
 * and relays them to Discord using the configured integration.
 */
public class MinecraftChatHandler {

    private static boolean advancementsEnabled = ConfigLoader.getBooleanProperty("ADVANCEMENTS_ENABLED", true); // Whether advancement messages should be sent to Discord.
    private static boolean deathsEnabled = ConfigLoader.getBooleanProperty("DEATHS_ENABLED", true); // Whether death messages should be sent to Discord.

    /**
     * Registers event handlers for chat and game messages.
     * Should be called during server initialization.
     */
    public static void register() {
        // Register chat and game message event listeners
        ServerMessageEvents.CHAT_MESSAGE.register(MinecraftChatHandler::onChatMessage);
        ServerMessageEvents.GAME_MESSAGE.register(MinecraftChatHandler::onGameMessage);
    }

    /**
     * Called when a player sends a chat message.
     * Relays the message to Discord, optionally using a webhook.
     *
     * @param message    The signed chat message.
     * @param sender     The player who sent the message.
     * @param parameters Message parameters (unused).
     */
    public static void onChatMessage(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters parameters) {
        final String playerName = sender.getGameProfile().name();
        final String playerUUID = sender.getGameProfile().id().toString();
        final String chatMessage = message.getContent().getString();
        final boolean webhookMode = ConfigLoader.getBooleanProperty("WEBHOOK_CHAT_ENABLED", false);

        if (webhookMode) {
            // Use a player avatar for the webhook
            String avatarUrl = "https://api.mineatar.io/face/" + playerUUID + "?scale=8";
            WebhookSender.send(playerName, avatarUrl, chatMessage);
        } else {
            // Send as a plain message via the bot
            JdaDiscordBot.sendMessageToDiscord("**[" + playerName + "]** " + chatMessage);
        }
    }

    /**
     * Handles game messages such as deaths and advancements.
     *
     * @param server  The Minecraft server instance.
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
     * Sends a Discord embed when the server starts.
     *
     * @param serverName The name of the server.
     */
    public static void sendServerStartMessage(String serverName) {
        String placeholderUrl = "https://example.com/placeholder.png";
        JdaDiscordBot.sendEmbedToDiscord("Server Started", "The Minecraft server **" + serverName + "** has started.", 0x800080, placeholderUrl); // Purple-colored embed
    }

    /**
     * Sends a Discord embed when the server stops.
     */
    public static void sendServerStopMessage() {
        String placeholderUrl = "https://example.com/placeholder.png";
        JdaDiscordBot.sendEmbedToDiscord("Server Stopped", "The Minecraft server has stopped.", 0x40E0D0, placeholderUrl); // Turquoise-colored embed
    }

    /**
     * Checks if a message is a death message by matching known keywords.
     *
     * @param message The message to check.
     * @return True if the message is a death message.
     */
    private static boolean isDeathMessage(String message) {
        final String[] deathKeywords = {
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
     * Handles formatting and sending a death message to Discord.
     *
     * @param message The death message.
     */
    private static void handleDeathMessage(String message) {
        final int playerNameEndIndex = message.indexOf(" ");
        if (playerNameEndIndex <= 0) return; // Edge case: malformed message
        final String playerName = message.substring(0, playerNameEndIndex).trim();
        final String deathMessage = message.substring(playerNameEndIndex).trim();
        final String formattedMessage = "**" + playerName + "** " + deathMessage;
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
     * Handles formatting and sending an advancement message to Discord.
     *
     * @param message The advancement message.
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
        JdaDiscordBot.sendEmbedToDiscord(title, formattedMessage, 0x77DD77, null); // Pastel-green-colored embed
    }

    /**
     * Toggles whether advancement messages are sent to Discord.
     */
    public static void toggleAdvancementsEnabled() {
        advancementsEnabled = !advancementsEnabled;
        ConfigLoader.setProperty("ADVANCEMENTS_ENABLED", String.valueOf(advancementsEnabled));
    }

    /**
     * @return True if advancement messages are enabled.
     */
    public static boolean isAdvancementsEnabled() {
        return advancementsEnabled;
    }

    /**
     * Toggles whether death messages are sent to Discord.
     */
    public static void toggleDeathsEnabled() {
        deathsEnabled = !deathsEnabled;
        ConfigLoader.setProperty("DEATHS_ENABLED", String.valueOf(deathsEnabled));
    }

    /**
     * @return True if death messages are enabled.
     */
    public static boolean isDeathsEnabled() {
        return deathsEnabled;
    }
}