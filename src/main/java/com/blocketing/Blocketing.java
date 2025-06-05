package com.blocketing;

import com.blocketing.commands.ConfigurationCommand;
import com.blocketing.discord.JdaDiscordBot;
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

		try {
			JdaDiscordBot.start();
		} catch (Exception e) {
			e.printStackTrace();
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

		System.out.println("Blocketing Mod has been initialized.");
	}

	private static MinecraftServer minecraftServer;

	public static MinecraftServer getMinecraftServer() {
		return minecraftServer;
	}

	/**
	 * This method is called when the server starts.
	 *
	 * @param minecraftServer The Minecraft server.
	 */
	private void onServerStart(MinecraftServer server) {
		System.out.println("Server has started.");
		minecraftServer = server;
		MinecraftChatHandler.sendServerStartMessage(minecraftServer.getServerMotd()); // Sends a server start message to the Discord-Bot
	}

	/**
	 * This method is called when the server stops.
	 *
	 * @param minecraftServer The Minecraft server.
	 */
	private void onServerStop(MinecraftServer server) {
		System.out.println("Server has stopped.");
		MinecraftChatHandler.sendServerStopMessage(); // Sends a server stop message to the Discord-Bot
		minecraftServer = null;
	}
}