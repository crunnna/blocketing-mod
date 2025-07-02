package com.blocketing.discord.commands;

import com.blocketing.Blocketing;
import com.blocketing.config.ConfigLoader;
import com.blocketing.utils.UpdateChecker;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;

/**
 * This class handles the execution of console commands from Discord.
 * It checks for permissions and executes the command on the Minecraft server.
 */
public class ConsoleCommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("Blocketing|Discord|ConsoleCommandHandler");

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
            LOGGER.warn("User {} tried to execute a console command without permission.", event.getUser().getAsTag());
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
            LOGGER.error("Minecraft server is not available for command execution.");
            event.reply("Minecraft server is not available.").setEphemeral(true).queue();
            return;
        }

        // Execute the command on the server thread
        server.execute(() -> {
            boolean success;
            try {
                ServerCommandSource source = server.getCommandSource();
                int result = server.getCommandManager().getDispatcher().execute(command, source);
                success = result > 0;
                LOGGER.info("Executed command from Discord user {}: '{}', result: {}", username, command, result);
            } catch (Exception e) {
                LOGGER.error("Exception while executing command from Discord user {}: '{}'", username, command, e);
                success = false;
            }

            String titleEmoji = success ? "✅" : "❌";
            String description = """
**Command:**
```
%s
```
**→ Executed by: %s**
     """.formatted(command, username);

             EmbedBuilder embed = new EmbedBuilder()
                     .setTitle(titleEmoji + " Command Execution")
                     .setColor(new Color(0xf1c40f))
                     .setDescription(description)
                     .setThumbnail(avatarUrl)
                     .setFooter(server.getServerMotd() + " - using Blocketing v" + UpdateChecker.getCurrentModVersion(), null)
                     .setTimestamp(Instant.now());

             if (success) {
                 event.replyEmbeds(embed.build()).queue();
             } else {
                 event.replyEmbeds(embed.build()).setEphemeral(true).queue();
             }
         });
     }
}