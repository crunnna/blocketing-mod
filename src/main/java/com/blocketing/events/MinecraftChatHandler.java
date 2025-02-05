package com.blocketing.events;

import com.blocketing.config.ConfigLoader;
import com.blocketing.discord.DiscordBot;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * This class would be responsible for monitoring Minecraft chat messages and sending them to Discord.
 */
public class MinecraftChatHandler {

    private static boolean advancementsEnabled = ConfigLoader.getBooleanProperty("ADVANCEMENTS_ENABLED", true);

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

        DiscordBot.sendMessage("**[" + playerName + "]** " + chatMessage);
    }

    /**
     * Handles game messages.
     * @param server The Minecraft server.
     * @param message The game message.
     * @param overlay Whether the message should overlay the previous message.
     */
    public static void onGameMessage(MinecraftServer server, Text message, boolean overlay) {
        sendAdvancementMessage(message, overlay);
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
        DiscordBot.sendEmbed("Server Started", "The Minecraft server **" + serverName + "** has started.", 0x800080, placeholderUrl); // Purple colored embed
    }

    /**
     * Sends a message to Discord-Bot when the server stops.
     */
    public static void sendServerStopMessage() {
        String placeholderUrl = "https://example.com/placeholder.png";
        DiscordBot.sendEmbed("Server Stopped", "The Minecraft server has stopped.", 0x40E0D0, placeholderUrl); // Turquoise colored embed
    }

    /**
     * Sends a message to Discord-Bot when an advancement is made.
     * @param message The message to send.
     * @param overlay Whether the message should overlay the previous message.
     */
    public static void sendAdvancementMessage(Text message, boolean overlay) {
        if (advancementsEnabled && message.getString().contains("has made the advancement")) {
            String advancementMessage = message.getString();
            int playerNameEndIndex = advancementMessage.indexOf(" has made the advancement");
            String playerName = advancementMessage.substring(0, playerNameEndIndex).trim();
            String advancement = advancementMessage.substring(playerNameEndIndex + " has made the advancement ".length()).trim();
            String formattedMessage = "**" + playerName + "** has made the advancement **" + advancement + "**";
            DiscordBot.sendEmbed("Advancement Made", formattedMessage, 0x77DD77, null);
        }
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
}