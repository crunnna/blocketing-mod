package com.blocketing;

import com.blocketing.config.ConfigLoader;
import com.blocketing.discord.DiscordMessageHandler;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.server.MinecraftServer;
import java.net.InetSocketAddress;

/**
 * This class would be responsible for starting the HTTP server to listen for messages from Discord.
 */
public class HttpMinecraftServer {

    /**
     * Starts the HTTP server to listen for messages from Discord.
     *
     * @param minecraftServer The Minecraft server.
     */
    public static void startServer(MinecraftServer minecraftServer) {
        try {
            // Loads the port from the configuration file
            int port = ConfigLoader.getIntProperty("PORT");
            if (port == -1) {
                throw new IllegalArgumentException("Invalid or missing port configuration.");
            }

            // Starts the HTTP server on the configured port
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/discord-to-minecraft", exchange -> DiscordMessageHandler.handleDiscordMessage(exchange, minecraftServer));
            server.createContext("/execute-command", exchange -> DiscordMessageHandler.handleCommand(exchange, minecraftServer));
            server.setExecutor(null);
            server.start();
            System.out.println("DiscordHandler listening on port " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}