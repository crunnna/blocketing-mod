package com.blocketing.discord.listener;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.blocketing.config.ConfigLoader;
import com.blocketing.Blocketing;
import net.minecraft.server.MinecraftServer;
import org.slf4j.LoggerFactory;

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
        if (event.getAuthor().isBot()) return; // Ignore messages from bots
        String channelId = ConfigLoader.getProperty("CHANNEL_ID");
        if (!event.getChannel().getId().equals(channelId)) return; // Check if the message is in the configured channel
        if (!event.getMessage().getAttachments().isEmpty() || !event.getMessage().getEmbeds().isEmpty()) return; // Ignore messages with attachments or embeds

        // Log Discord-Message to console if enabled
        if (ConfigLoader.getBooleanProperty("DISCORD_CHAT_LOG_ENABLED", false)) {
            LoggerFactory.getLogger("Blocketing|Discord|Chat").info("[Discord] <{}>: {}", event.getAuthor().getName(), event.getMessage().getContentDisplay());
        }

        // Get the Minecraft server instance
        MinecraftServer server = Blocketing.getMinecraftServer();
        if (server != null) {
            String username = event.getAuthor().getName();
            String content = event.getMessage().getContentDisplay();

            // Get top role color
            java.awt.Color roleColor = null;
            if (event.getMember() != null && event.getMember().getColor() != null) {
                roleColor = event.getMember().getColor();
            }
            net.minecraft.util.Formatting mcRoleColor = com.blocketing.utils.ColorUtil.getNearestFormatting(roleColor);

            // Build formatted message
            net.minecraft.text.Text prefix = net.minecraft.text.Text.literal("[")
                    .styled(style -> style.withColor(net.minecraft.util.Formatting.BLUE))
                    .append(net.minecraft.text.Text.literal("Discord")
                            .styled(style -> style.withColor(net.minecraft.util.Formatting.BLUE).withBold(true)))
                    .append(net.minecraft.text.Text.literal("] ")
                            .styled(style -> style.withColor(net.minecraft.util.Formatting.BLUE)));

            // Create user text with color
            net.minecraft.text.Text user = net.minecraft.text.Text.literal("<" + username + ">")
                    .styled(style -> style.withColor(mcRoleColor));

            // Create message text with white color
            net.minecraft.text.Text msg = net.minecraft.text.Text.literal(" " + content)
                    .styled(style -> style.withColor(net.minecraft.util.Formatting.WHITE));

            // Combine all parts into a full message
            net.minecraft.text.Text full = net.minecraft.text.Text.empty()
                    .append(prefix)
                    .append(user)
                    .append(msg);

            // Send the message to the Minecraft server's chat
            for (net.minecraft.server.network.ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                player.sendMessage(full, false);
            }
        }
    }
}