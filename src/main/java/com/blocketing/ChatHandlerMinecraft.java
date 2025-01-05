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

public class ChatHandlerMinecraft {

    public static void register() {
        ServerMessageEvents.CHAT_MESSAGE.register(ChatHandlerMinecraft::onChatMessage);
        ServerPlayConnectionEvents.JOIN.register(ChatHandlerMinecraft::onPlayerJoin);
        ServerPlayConnectionEvents.DISCONNECT.register(ChatHandlerMinecraft::onPlayerDisconnect);
    }

    public static void onChatMessage(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters parameters) {
        String playerName = sender.getGameProfile().getName();
        String chatMessage = message.getContent().getString();

        sendToDiscordBot(playerName, chatMessage);
    }

    private static void sendToDiscordBot(String playerName, String chatMessage) {
        DiscordBot.sendMessage("**[" + playerName + "]** " + chatMessage);
    }

    public static void sendMessageToAllPlayers(MinecraftServer minecraftServer, String username, String content) {
        for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
            player.sendMessage(Text.of("<@DC_" + username + "> " + content), false);
        }
    }

    private static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        String playerName = handler.getPlayer().getGameProfile().getName();
        DiscordBot.sendEmbed("Player Joined", "**" + playerName + "** joined the server.", 0x00FF00); // Green color
    }

    private static void onPlayerDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        String playerName = handler.getPlayer().getGameProfile().getName();
        DiscordBot.sendEmbed("Player Left", "**" + playerName + "** left the server.", 0xFF0000); // Red color
    }
}