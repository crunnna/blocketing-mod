package com.blocketing;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.networking.v1.PacketSender;

/**
 * This class would be responsible for monitoring Minecraft chat messages and sending them to Discord.
 */
public class ChatHandlerMinecraft {

    /**
     * Registers the event handlers for chat messages, player join, and player disconnect events.
     */
    public static void register() {
        ServerMessageEvents.CHAT_MESSAGE.register(ChatHandlerMinecraft::onChatMessage);
        ServerPlayConnectionEvents.JOIN.register(ChatHandlerMinecraft::onPlayerJoin);
        ServerPlayConnectionEvents.DISCONNECT.register(ChatHandlerMinecraft::onPlayerDisconnect);
    }

    /**
     * This method is called when a chat message is sent.
     *
     * @param message The chat message.
     * @param sender The player who sent the message.
     * @param parameters The parameters of the message.
     */
    public static void onChatMessage(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters parameters) {
        String playerName = sender.getGameProfile().getName();
        String chatMessage = message.getContent().getString();

        sendToDiscordBot(playerName, chatMessage);
    }

    /**
     * Sends the chat message to the Discord-Bot.
     * @param playerName The name of the player who sent the message.
     * @param chatMessage The chat message.
     */
    private static void sendToDiscordBot(String playerName, String chatMessage) {
        DiscordBot.sendMessage("**[" + playerName + "]** " + chatMessage);
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
     * Handles player join events.
     * @param handler The network handler for the player.
     * @param sender The packet sender.
     * @param server The Minecraft server.
     */
    private static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        String playerName = handler.getPlayer().getGameProfile().getName();
        String playerUUID = handler.getPlayer().getUuid().toString();
        String avatarUrl = "https://api.mineatar.io/face/" + playerUUID + "?scale=8"; // Get the player's avatar    (scale= (4=mini, 8=normal, 12=big))

        DiscordBot.sendEmbed("Player Joined", "**" + playerName + "** joined the server.", 0x00FF00, avatarUrl); // Green colored embed
    }

    /**
     * Handles player disconnect events.
     * @param handler The network handler for the player.
     * @param server The Minecraft server.
     */
    private static void onPlayerDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        String playerName = handler.getPlayer().getGameProfile().getName();
        String playerUUID = handler.getPlayer().getUuid().toString();
        String avatarUrl = "https://api.mineatar.io/face/" + playerUUID + "?scale=8"; // Get the player's avatar    (scale= (4=mini, 8=normal, 12=big))

        DiscordBot.sendEmbed("Player Left", "**" + playerName + "** left the server.", 0xFF0000, avatarUrl); // Red colored embed
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
}