package com.blocketing;

import com.blocketing.config.ConfigLoader;
import com.blocketing.events.MinecraftChatHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * This class is responsible for the registration and execution of ingame blocketing-mod configuration commands.
 */
public class ConfigurationCommand {

    /**
     * Registers the configuration command.
     *
     * @param dispatcher The dispatcher where the commands are registered.
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("blocketing")
                .requires(source -> source.hasPermissionLevel(4))
                .then(CommandManager.literal("setup")
                        .then(CommandManager.literal("token")
                                .then(CommandManager.argument("token", StringArgumentType.string())
                                        .executes(context -> {
                                            String token = StringArgumentType.getString(context, "token");
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
                        .then(CommandManager.literal("guild")
                                .then(CommandManager.argument("guild", StringArgumentType.string())
                                        .executes(context -> {
                                            String guild = StringArgumentType.getString(context, "guild");
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
                        .then(CommandManager.literal("channel")
                                .then(CommandManager.argument("channel", StringArgumentType.string())
                                        .executes(context -> {
                                            String channel = StringArgumentType.getString(context, "channel");
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
                        .then(CommandManager.literal("op_role")
                                .then(CommandManager.argument("op_role", StringArgumentType.string())
                                        .executes(context -> {
                                            String opRole = StringArgumentType.getString(context, "op_role");
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
                        .then(CommandManager.literal("webhook_url")
                                .then(CommandManager.argument("webhook_url", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            String webhookUrl = StringArgumentType.getString(context, "webhook_url");
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
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.of("Available setup commands: token, guild, channel, op_role, webhook_url"), true);
                            return 1;
                        })
                )
                .then(CommandManager.literal("toggle")
                        .then(CommandManager.literal("advancements")
                                .executes(context -> {
                                    MinecraftChatHandler.toggleAdvancementsEnabled();
                                    boolean status = MinecraftChatHandler.isAdvancementsEnabled();
                                    context.getSource().sendFeedback(() -> Text.of("Advancements " + (status ? "enabled" : "disabled")), true);
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("deaths")
                                .executes(context -> {
                                    MinecraftChatHandler.toggleDeathsEnabled();
                                    boolean status = MinecraftChatHandler.isDeathsEnabled();
                                    context.getSource().sendFeedback(() -> Text.of("Deaths " + (status ? "enabled" : "disabled")), true);
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("player_chat_mode")
                                .executes(context -> {
                                    boolean enabled = !ConfigLoader.getBooleanProperty("WEBHOOK_CHAT_ENABLED", false);
                                    ConfigLoader.setProperty("WEBHOOK_CHAT_ENABLED", String.valueOf(enabled));
                                    context.getSource().sendFeedback(() -> Text.of("Webhook chat mode " + (enabled ? "enabled" : "disabled")), true);
                                    return 1;
                                })
                        )
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.of("Available toggles: advancements, deaths, player_chat_mode"), true);
                            return 1;
                        })
                )
        );
    }
}