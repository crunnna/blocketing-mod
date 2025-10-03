package com.blocketing.commands;

import com.blocketing.config.ConfigLoader;
import com.blocketing.discord.JdaDiscordBot;
import com.blocketing.events.MinecraftChatHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * Utility class responsible for the registration and execution of ingame blocketing-mod configuration commands.
 * <p>
 * This class should not be instantiated.
 */
public final class ConfigurationCommand {

    /**
     * Registers the configuration command and its subcommands.
     *
     * @param dispatcher The dispatcher where the commands are registered.
     */
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("blocketing")
                .requires(source -> source.hasPermissionLevel(4)) // Only allow OPs
                // Setup subcommands for Discord integration
                .then(CommandManager.literal("setup")
                        // Set Discord bot token
                        .then(CommandManager.literal("token")
                                .then(CommandManager.argument("token", StringArgumentType.string())
                                        .executes(context -> {
                                            final String token = StringArgumentType.getString(context, "token");
                                            ConfigLoader.setProperty("BOT_TOKEN", token);
                                            context.getSource().sendFeedback(() -> Text.of("Bot token set"), true);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    context.getSource().sendFeedback(() -> Text.of("This command sets the bot token for Discord integration."), true);
                                    return 1;
                                })
                        )
                        // Set Discord guild (server) ID
                        .then(CommandManager.literal("guild")
                                .then(CommandManager.argument("guild", StringArgumentType.string())
                                        .executes(context -> {
                                            final String guild = StringArgumentType.getString(context, "guild");
                                            ConfigLoader.setProperty("GUILD_ID", guild);
                                            context.getSource().sendFeedback(() -> Text.of("Guild ID set"), true);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    context.getSource().sendFeedback(() -> Text.of("This command sets the guild (server) ID for Discord integration."), true);
                                    return 1;
                                })
                        )
                        // Set Discord channel ID
                        .then(CommandManager.literal("channel")
                                .then(CommandManager.argument("channel", StringArgumentType.string())
                                        .executes(context -> {
                                            final String channel = StringArgumentType.getString(context, "channel");
                                            ConfigLoader.setProperty("CHANNEL_ID", channel);
                                            context.getSource().sendFeedback(() -> Text.of("Channel ID set"), true);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    context.getSource().sendFeedback(() -> Text.of("This command sets the channel ID for Discord integration."), true);
                                    return 1;
                                })
                        )
                        // Set Discord operator role ID for command permissions
                        .then(CommandManager.literal("op_role")
                                .then(CommandManager.argument("op_role", StringArgumentType.string())
                                        .executes(context -> {
                                            final String opRole = StringArgumentType.getString(context, "op_role");
                                            ConfigLoader.setProperty("OP_ROLE_ID", opRole);
                                            context.getSource().sendFeedback(() -> Text.of("Operator role ID set"), true);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    context.getSource().sendFeedback(() -> Text.of("This command sets the operator role ID for Discord command permissions."), true);
                                    return 1;
                                })
                        )
                        // Set Discord webhook URL for player chat mode
                        .then(CommandManager.literal("webhook_url")
                                .then(CommandManager.argument("webhook_url", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            final String webhookUrl = StringArgumentType.getString(context, "webhook_url");
                                            ConfigLoader.setProperty("WEBHOOK_URL", webhookUrl);
                                            context.getSource().sendFeedback(() -> Text.of("Webhook URL set"), true);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    context.getSource().sendFeedback(() -> Text.of("This command sets the Discord webhook URL for player chat mode."), true);
                                    return 1;
                                })
                        )
                        // List available setup commands
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.of("Available setup commands: token, guild, channel, op_role, webhook_url"), true);
                            return 1;
                        })
                )
                // Toggle subcommands for various features
                .then(CommandManager.literal("toggle")
                        // Toggle advancements notifications
                        .then(CommandManager.literal("advancements")
                                .executes(context -> {
                                    MinecraftChatHandler.toggleAdvancementsEnabled();
                                    final boolean status = MinecraftChatHandler.isAdvancementsEnabled();
                                    context.getSource().sendFeedback(() -> Text.of("Advancements " + (status ? "enabled" : "disabled")), true);
                                    return 1;
                                })
                        )
                        // Toggle death notifications
                        .then(CommandManager.literal("deaths")
                                .executes(context -> {
                                    MinecraftChatHandler.toggleDeathsEnabled();
                                    final boolean status = MinecraftChatHandler.isDeathsEnabled();
                                    context.getSource().sendFeedback(() -> Text.of("Deaths " + (status ? "enabled" : "disabled")), true);
                                    return 1;
                                })
                        )
                        // Toggle webhook chat mode
                        .then(CommandManager.literal("player_chat_mode")
                                .executes(context -> {
                                    final boolean enabled = !ConfigLoader.getBooleanProperty("WEBHOOK_CHAT_ENABLED", false);
                                    ConfigLoader.setProperty("WEBHOOK_CHAT_ENABLED", String.valueOf(enabled));
                                    context.getSource().sendFeedback(() -> Text.of("Webhook chat mode " + (enabled ? "enabled" : "disabled")), true);
                                    return 1;
                                })
                        )
                        // Toggle update info notifications
                        .then(CommandManager.literal("update_info")
                                .executes(context -> {
                                    final boolean enabled = !ConfigLoader.getBooleanProperty("UPDATE_INFO_ENABLED", true);
                                    ConfigLoader.setProperty("UPDATE_INFO_ENABLED", String.valueOf(enabled));
                                    context.getSource().sendFeedback(() -> Text.of("Update info notifications are now " + (enabled ? "enabled" : "disabled") + "."), true);
                                    return 1;
                                })
                        )
                        // Toggle Discord chat logging
                        .then(CommandManager.literal("discord_chat_log")
                                .executes(context -> {
                                    boolean enabled = !ConfigLoader.getBooleanProperty("DISCORD_CHAT_LOG_ENABLED", false);
                                    ConfigLoader.setProperty("DISCORD_CHAT_LOG_ENABLED", String.valueOf(enabled));
                                    context.getSource().sendFeedback(() -> Text.of("Discord-Chat-Logging is now " + (enabled ? "enabled" : "disabled") + "."), true);
                                    return 1;
                                })
                        )
                        // List available toggles
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.of("Available toggles: advancements, deaths, player_chat_mode, update-info, discord_chat_log"), true);
                            return 1;
                        })
                )
                // Reload configuration and restart Discord bot
                .then(CommandManager.literal("reload")
                        .executes(context -> {
                            ConfigLoader.reloadConfig();
                            JdaDiscordBot.restart();
                            context.getSource().sendFeedback(() -> Text.of("Blocketing config and Discord bot reloaded."), true);
                            return 1;
                        })
                )
        );
    }
}