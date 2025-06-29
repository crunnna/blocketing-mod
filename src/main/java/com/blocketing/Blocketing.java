package com.blocketing;

import com.blocketing.commands.ConfigurationCommand;
import com.blocketing.discord.JdaDiscordBot;
import com.blocketing.events.MinecraftChatHandler;
import com.blocketing.events.PlayerEventHandler;
import com.blocketing.utils.ServerMetrics;
import com.blocketing.utils.UpdateChecker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main mod class for Blocketing.
 * Handles initialization, server lifecycle events, and provides access to the Minecraft server instance.
 */
public final class Blocketing implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Blocketing");

	// Holds the current Minecraft server instance
	private static MinecraftServer minecraftServer;

	/**
	 * Called by Fabric when the mod is initialized.
	 * Registers event handlers, commands, and starts the Discord bot.
	 */
	@Override
	public void onInitialize() {
		LOGGER.info("Blocketing Mod is initializing...");

		// Start the Discord bot
		try {
			JdaDiscordBot.start();
		} catch (Exception e) {
			LOGGER.error("Failed to start Discord bot", e);
		}

		// Register server metrics (TPS, MSPT, CPU, RAM)
		ServerMetrics.register();

		// Register server lifecycle events
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStart);
		ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStop);

		// Register player and chat event handlers
		PlayerEventHandler.register();
		MinecraftChatHandler.register();

		// Register the configuration command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ConfigurationCommand.register(dispatcher);
		});

		LOGGER.info("Blocketing Mod has been initialized.");
	}

	/**
	 * Called when the server starts.
	 * Stores the server instance, sends a start message, and checks for updates.
	 *
	 * @param server The Minecraft server instance.
	 */
	private void onServerStart(final MinecraftServer server) {
		LOGGER.info("Server has started.");
		minecraftServer = server;
		// Notify Discord about server start
		MinecraftChatHandler.sendServerStartMessage(minecraftServer.getServerMotd());
		// Check for mod updates and notify Discord if available
		UpdateChecker.checkForUpdateAndNotifyDiscord(server);
	}

	/**
	 * Called when the server stops.
	 * Sends a stop message and clears the server instance.
	 *
	 * @param server The Minecraft server instance.
	 */
	private void onServerStop(final MinecraftServer server) {
		LOGGER.info("Server has stopped.");
		// Notify Discord about server stop
		MinecraftChatHandler.sendServerStopMessage();
		minecraftServer = null;
	}

	/**
	 * Returns the current Minecraft server instance, or null if not available.
	 *
	 * @return The MinecraftServer instance or null.
	 */
	public static MinecraftServer getMinecraftServer() {
		return minecraftServer;
	}
}