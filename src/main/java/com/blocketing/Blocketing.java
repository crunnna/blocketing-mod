package com.blocketing;

import com.blocketing.events.MinecraftChatHandler;
import com.blocketing.events.PlayerEventHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

/**
 * The main class of the mod. This is where the mod is initialized.
 */
public class Blocketing implements ModInitializer {

	@Override
	public void onInitialize() {
		System.out.println("Blocketing Mod is initializing...");

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

		System.out.println("Blocketing Mod has been initialized.");
	}

	/**
	 * This method is called when the server starts.
	 *
	 * @param minecraftServer The Minecraft server.
	 */
	private void onServerStart(MinecraftServer minecraftServer) {
		System.out.println("Server has started. Starting Discord HTTP server...");
		HttpMinecraftServer.startServer(minecraftServer); // Starts the HTTP server
		MinecraftChatHandler.sendServerStartMessage(minecraftServer.getServerMotd()); // Sends a server start message to the Discord-Bot
	}

	/**
	 * This method is called when the server stops.
	 *
	 * @param minecraftServer The Minecraft server.
	 */
	private void onServerStop(MinecraftServer minecraftServer) {
		System.out.println("Server has stopped. Stopping Discord HTTP server...");
		MinecraftChatHandler.sendServerStopMessage(); // Sends a server stop message to the Discord-Bot
	}
}