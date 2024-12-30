package com.blocketing;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * This class would be responsible for monitoring Minecraft chat messages and sending them to Discord.
 */
public class ChatHandlerMinecraft {

    /**
     * Registers the *onChatMessage* method with the *ServerMessageEvents.CHAT_MESSAGE* event to process chat messages
     */
    public static void register() {
        ServerMessageEvents.CHAT_MESSAGE.register(ChatHandlerMinecraft::onChatMessage);
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
     *
     * @param playerName The name of the player who sent the message.
     * @param chatMessage The chat message.
     */
    private static void sendToDiscordBot(String playerName, String chatMessage) {
        DiscordBot.sendMessage("**[" + playerName + "]** " + chatMessage);
    }

    /**
     * Sends a message to all players in the Minecraft chat.
     *
     * @param minecraftServer The Minecraft server.
     * @param username The username of the player who sent the message.
     * @param content The content of the message.
     */
    public static void sendMessageToAllPlayers(MinecraftServer minecraftServer, String username, String content) {
        for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
            player.sendMessage(Text.of("<@DC_" + username + "> " + content), false);
        }
    }
}