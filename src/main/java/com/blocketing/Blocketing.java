package com.blocketing;

import net.fabricmc.api.ModInitializer;
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

		// Registers the chat handler
		ChatHandlerMinecraft.register();

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
		ChatHandlerMinecraft.sendServerStartMessage(minecraftServer.getServerMotd()); // Sends a server start message to the Discord-Bot
	}

	/**
	 * This method is called when the server stops.
	 *
	 * @param minecraftServer The Minecraft server.
	 */
	private void onServerStop(MinecraftServer minecraftServer) {
		System.out.println("Server has stopped. Stopping Discord HTTP server...");
		ChatHandlerMinecraft.sendServerStopMessage(); // Sends a server stop message to the Discord-Bot
	}
}