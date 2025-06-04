package com.blocketing.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.blocketing.config.ConfigLoader;
import net.dv8tion.jda.api.EmbedBuilder;
import com.blocketing.Blocketing;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.awt.*;

/**
 * This class is responsible for handling Discord slash command interactions,
 * specifically the /command command for executing Minecraft commands from Discord.
 */
public class JdaCommandListener extends ListenerAdapter {

    /**
     * Handles slash command interactions from Discord.
     * Checks for operator role permissions and executes the given Minecraft command on the server.
     *
     * @param event The slash command interaction event.
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("command")) return;

        String opRoleId = ConfigLoader.getProperty("OP_ROLE_ID");
        if (opRoleId != null && event.getMember() != null && !event.getMember().getRoles().stream().anyMatch(r -> r.getId().equals(opRoleId))) {
            event.reply("You do not have permission to use this command.").setEphemeral(true).queue();
            return;
        }

        String command = event.getOption("command").getAsString();
        String username = event.getUser().getName();
        String avatarUrl = event.getUser().getEffectiveAvatarUrl();

        MinecraftServer server = Blocketing.getMinecraftServer();
        if (server == null) {
            event.reply("Minecraft server is not available.").setEphemeral(true).queue();
            return;
        }

        // Run the command on the main server thread
        server.execute(() -> {
            boolean success = false;
            try {
                ServerCommandSource source = server.getCommandSource();
                int result = server.getCommandManager().getDispatcher().execute(command, source);
                success = result > 0;
            } catch (Exception e) {
                success = false;
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(new Color(0xf1c40f))
                    .setTitle("Command Executed")
                    .setDescription("**Command:** " + command + "\n**Executed by:** " + username)
                    .setThumbnail(avatarUrl)
                    .setTimestamp(java.time.Instant.now());
            if (success) {
                event.replyEmbeds(embed.build()).queue();
            } else {
                event.reply("Failed to execute command: `" + command + "`").setEphemeral(true).queue();
            }
        });
    }
}