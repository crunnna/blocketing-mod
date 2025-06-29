package com.blocketing.discord.commands;

import com.blocketing.Blocketing;
import com.blocketing.config.ConfigLoader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.awt.*;
import java.time.Instant;

/**
 * This class handles the execution of console commands from Discord.
 * It checks for permissions and executes the command on the Minecraft server.
 */
public class ConsoleCommandHandler {

    /**
     * Handles a slash command interaction event from Discord to execute a console command on the Minecraft server.
     *
     * @param event The SlashCommandInteractionEvent from JDA.
     */
    public void handle(SlashCommandInteractionEvent event) {
        // Get the Discord role ID that is allowed to execute commands
        final String opRoleId = ConfigLoader.getProperty("OP_ROLE_ID");

        // Check if the user has the required role
        if (opRoleId != null && event.getMember() != null &&
                event.getMember().getRoles().stream().noneMatch(r -> r.getId().equals(opRoleId))) {
            event.reply("You do not have permission to use this command.").setEphemeral(true).queue();
            return;
        }

        // Get the command and user info from the event
        final String command = event.getOption("command").getAsString();
        final String username = event.getUser().getName();
        final String avatarUrl = event.getUser().getEffectiveAvatarUrl();

        // Get the Minecraft server instance
        final MinecraftServer server = Blocketing.getMinecraftServer();
        if (server == null) {
            event.reply("Minecraft server is not available.").setEphemeral(true).queue();
            return;
        }

        // Execute the command on the server thread
        server.execute(() -> {
            boolean success = false;
            try {
                ServerCommandSource source = server.getCommandSource();
                int result = server.getCommandManager().getDispatcher().execute(command, source);
                success = result > 0;
            } catch (Exception e) {
                // Log the exception for debugging
                e.printStackTrace();
                success = false;
            }

            // Build the response embed
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(new Color(0xf1c40f))
                    .setTitle("Command Executed")
                    .setDescription("**Command:** " + command + "\n**Executed by:** " + username)
                    .setThumbnail(avatarUrl)
                    .setFooter(server.getServerMotd() + " - using Blocketing v" + com.blocketing.utils.UpdateChecker.getCurrentModVersion(), null)
                    .setTimestamp(Instant.now());

            // Send the result back to Discord
            if (success) {
                event.replyEmbeds(embed.build()).queue();
            } else {
                event.reply("Failed to execute command: `" + command + "`").setEphemeral(true).queue();
            }
        });
    }
}