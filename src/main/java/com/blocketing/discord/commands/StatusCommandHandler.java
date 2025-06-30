package com.blocketing.discord.commands;

import com.blocketing.Blocketing;
import com.blocketing.utils.ServerMetrics;
import com.blocketing.utils.UpdateChecker;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;

/**
 * This class handles the `/status` slash command from Discord,
 * providing server status information such as TPS, MSPT, CPU, RAM, version and uptime.
 */
public class StatusCommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("Blocketing|Discord|StatusCommandHandler");

    /**
     * Handles the `/status` command interaction.
     *
     * @param event The slash command interaction event.
     */
    public void handle(SlashCommandInteractionEvent event) {
        // Get the Minecraft server instance
        MinecraftServer server = Blocketing.getMinecraftServer();
        if (server == null) {
            LOGGER.error("Minecraft server is not running for /status command.");
            event.reply("The Minecraft server is not running.").setEphemeral(true).queue();
            return;
        }

        // Gather server metrics
        double mspt = ServerMetrics.getAverageMspt(server); // Average milliseconds per tick
        double tps = ServerMetrics.getTps(); // Current ticks per second
        String mem = ServerMetrics.getMemoryUsage(); // RAM usage formatted as "X MB / Y MB"
        double cpuLoad = ServerMetrics.getProcessCpuLoad(); // CPU load as a percentage
        String cpu = cpuLoad >= 0 ? String.format("%.1f%%", cpuLoad * 100) : "N/A";
        String serverName = server.getServerMotd();
        String modVersion = UpdateChecker.getCurrentModVersion();
        String serverVersion = server.getVersion();
        String uptime = ServerMetrics.getFormattedUptime();

        // Build the embed description with formatted metrics
        final String description = """
**Version:** %s
**```
🕒 Uptime: %s
⚡ TPS: %s
⏱️ MSPT: %s
🧮 CPU: %s
💾 RAM: %s
```**
""".formatted(
                serverVersion,
                uptime,
                String.format("%.2f", tps),
                String.format("%.2f", mspt),
                cpu,
                mem
        );

        // Create and send the embed to Discord
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🖥️ Server Status")
                .setColor(new Color(0x2c3e50))
                .setDescription(description)
                .setThumbnail("https://raw.githubusercontent.com/crunnna/blocketing-fabric-mod/main/src/main/resources/assets/blocketing/icon.png")
                .setFooter(serverName + " - using Blocketing v" + modVersion, null)
                .setTimestamp(Instant.now());

        event.replyEmbeds(embed.build()).queue();
        LOGGER.info("Sent server status to Discord: TPS={}, MSPT={}, CPU={}, RAM={}", tps, mspt, cpu, mem);
    }
}