package com.blocketing;

import com.sun.net.httpserver.HttpServer;
import net.minecraft.server.MinecraftServer;
import java.net.InetSocketAddress;

// This class would be responsible for starting the HTTP server to listen for messages from Discord.
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
            server.createContext("/discord-to-minecraft", exchange -> ChatHandlerDiscord.handleDiscordMessage(exchange, minecraftServer));
            server.createContext("/execute-command", exchange -> ChatHandlerDiscord.handleCommand(exchange, minecraftServer));
            server.setExecutor(null);
            server.start();
            System.out.println("DiscordHandler listening on port " + port);

            // Sends a message to the Discord-Bot when the server starts
            ChatHandlerMinecraft.sendServerStartMessage();

            // Sends a message to the Discord-Bot when the server stops
            Runtime.getRuntime().addShutdownHook(new Thread(ChatHandlerMinecraft::sendServerStopMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}