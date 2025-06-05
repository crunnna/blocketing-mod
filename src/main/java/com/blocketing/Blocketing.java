package com.blocketing;

import com.blocketing.commands.ConfigurationCommand;
import com.blocketing.discord.JdaDiscordBot;
import com.blocketing.events.MinecraftChatHandler;
import com.blocketing.events.PlayerEventHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class of the mod. This is where the mod is initialized.
 */
public class Blocketing implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Blocketing");

	private static MinecraftServer minecraftServer;

	@Override
	public void onInitialize() {
		LOGGER.info("Blocketing Mod is initializing...");

		try {
			JdaDiscordBot.start();
		} catch (Exception e) {
			LOGGER.error("Failed to start Discord bot", e);
		}

		// Registers the server start event
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStart);
		// Registers the server stop event
		ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStop);

		// Registers the player event handler
		PlayerEventHandler.register();
		// Registers the chat handler
		MinecraftChatHandler.register();

		// Registers the config command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ConfigurationCommand.register(dispatcher);
		});

		LOGGER.info("Blocketing Mod has been initialized.");
	}

	/**
	 * This method is called when the server starts.
	 *
	 * @param server The Minecraft server.
	 */
	private void onServerStart(MinecraftServer server) {
		LOGGER.info("Server has started.");
		minecraftServer = server;
		MinecraftChatHandler.sendServerStartMessage(minecraftServer.getServerMotd()); // Sends a server start message to the Discord-Bot
	}

	/**
	 * This method is called when the server stops.
	 *
	 * @param server The Minecraft server.
	 */
	private void onServerStop(MinecraftServer server) {
		LOGGER.info("Server has stopped.");
		MinecraftChatHandler.sendServerStopMessage(); // Sends a server stop message to the Discord-Bot
		minecraftServer = null;
	}

	public static MinecraftServer getMinecraftServer() {
		return minecraftServer;
	}
}