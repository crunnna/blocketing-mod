package com.blocketing;

import com.blocketing.config.ConfigLoader;
import com.blocketing.events.MinecraftChatHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * This class would be responsible for the registration and execution of ingame blocketing-mod configuration commands.
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
                        .then(CommandManager.literal("port")
                                .then(CommandManager.argument("port", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            int port = IntegerArgumentType.getInteger(context, "port");
                                            ConfigLoader.setProperty("PORT", String.valueOf(port));
                                            context.getSource().sendFeedback(() -> Text.of("Port set to " + port), true);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    context.getSource().sendFeedback(() -> Text.of("This command sets the port for the HTTP server."), true);
                                    return 1;
                                })
                        )
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
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.of("This command toggles the sending of advancement messages to Discord."), true);
                            return 1;
                        })
                )
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.of("Available commands: setup, toggle"), true);
                    return 1;
                })
        );
    }
}