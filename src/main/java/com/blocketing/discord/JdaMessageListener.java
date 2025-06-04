package com.blocketing.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.blocketing.config.ConfigLoader;
import com.blocketing.Blocketing;
import com.blocketing.events.MinecraftChatHandler;
import net.minecraft.server.MinecraftServer;

/**
 * This class listens for messages received in a specific Discord channel
 * and forwards them to the Minecraft server's chat.
 */
public class JdaMessageListener extends ListenerAdapter {

    /**
     * Sends a plain text message to the configured Discord channel.
     *
     * @param event The message to send.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String channelId = ConfigLoader.getProperty("CHANNEL_ID");
        if (!event.getChannel().getId().equals(channelId)) return;
        if (!event.getMessage().getAttachments().isEmpty() || !event.getMessage().getEmbeds().isEmpty()) return;

        MinecraftServer server = Blocketing.getMinecraftServer();
        if (server != null) {
            String username = event.getAuthor().getName();
            String content = event.getMessage().getContentDisplay();
            MinecraftChatHandler.sendMessageToAllPlayers(server, username, content);
        }
    }
}