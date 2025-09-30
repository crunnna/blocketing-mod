package com.blocketing.events;

import com.blocketing.discord.JdaDiscordBot;
import com.blocketing.utils.UpdateChecker;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

/**
 * Handles player join and disconnect events.
 * Sends notifications to Discord and checks for mod updates on join.
 */
public class PlayerEventHandler {

    /**
     * Registers event listeners for player join and disconnect.
     * Should be called during server initialization.
     */
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register(PlayerEventHandler::onPlayerJoin);
        ServerPlayConnectionEvents.DISCONNECT.register(PlayerEventHandler::onPlayerDisconnect);
    }

    /**
     * Handles player join events.
     * @param handler The network handler for the player.
     * @param sender The packet sender.
     * @param server The Minecraft server.
     */
    private static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        if (handler.getPlayer() == null) return; // Safety check

        final String playerName = handler.getPlayer().getGameProfile().name();
        final String playerUUID = handler.getPlayer().getGameProfile().id().toString();
        // Construct the player's avatar URL (scale=8 for normal size)
        final String avatarUrl = "https://api.mineatar.io/face/" + playerUUID + "?scale=8";

        // Send a green embed to Discord for player join
        JdaDiscordBot.sendEmbedToDiscord("Player Joined", "**" + playerName + "** joined the server.", 0x00FF00, avatarUrl);

        // Check for mod updates and notify the player if needed
        UpdateChecker.checkForUpdateAndNotifyOperator(handler.getPlayer());
    }

    /**
     * Handles player disconnect events.
     * @param handler The network handler for the player.
     * @param server The Minecraft server.
     */
    private static void onPlayerDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        if (handler.getPlayer() == null) return; // Safety check

        final String playerName = handler.getPlayer().getGameProfile().name();
        final String playerUUID = handler.getPlayer().getGameProfile().id().toString();
        // Construct the player's avatar URL (scale=8 for normal size)
        final String avatarUrl = "https://api.mineatar.io/face/" + playerUUID + "?scale=8";

        // Send a red embed to Discord for player leave
        JdaDiscordBot.sendEmbedToDiscord("Player Left", "**" + playerName + "** left the server.", 0xFF0000, avatarUrl);
    }
}
