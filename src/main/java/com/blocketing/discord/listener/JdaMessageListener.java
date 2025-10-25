package com.blocketing.discord.listener;

import com.blocketing.utils.ColorUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.blocketing.config.ConfigLoader;
import com.blocketing.Blocketing;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.LoggerFactory;
import java.awt.Color;

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
        String channelCfg = ConfigLoader.getProperty("CHANNEL_ID");
        if (channelCfg == null || channelCfg.isBlank()) return;
        final String eventChannelId = event.getChannel().getId();
        if (!eventChannelId.equals(channelCfg)) return; // Check if the message is in the configured channel
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
            Color roleColor = null;
            if (event.getMember() != null && event.getMember().getColor() != null) {
                roleColor = event.getMember().getColor();
            }
            Formatting mcRoleColor = ColorUtil.getNearestFormatting(roleColor);

            // Build formatted message
            Text prefix = Text.literal("[")
                    .styled(style -> style.withColor(Formatting.BLUE))
                    .append(Text.literal("Discord")
                            .styled(style -> style.withColor(Formatting.BLUE).withBold(true)))
                    .append(Text.literal("] ")
                            .styled(style -> style.withColor(Formatting.BLUE)));

            // Create user text with color
            Text user = Text.literal("<" + username + ">")
                    .styled(style -> style.withColor(mcRoleColor));

            // Create message text with white color
            Text msg = Text.literal(" " + content)
                    .styled(style -> style.withColor(Formatting.WHITE));

            // Combine all parts into a full message
            Text full = Text.empty()
                    .append(prefix)
                    .append(user)
                    .append(msg);

            // Send the message to the Minecraft server's chat
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                player.sendMessage(full, false);
            }
        }
    }
}