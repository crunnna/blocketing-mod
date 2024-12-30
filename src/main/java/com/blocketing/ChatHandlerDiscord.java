package com.blocketing;

import com.google.gson.JsonObject;
import com.mojang.brigadier.ParseResults;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;

/**
 * This class would be responsible for receiving messages from Discord and then forwarding them to the Minecraft chat.
 */
public class ChatHandlerDiscord {

    /**
     * Starts the HTTP server to listen for messages from Discord.
     *
     * @param minecraftServer The Minecraft server.
     */
    public static void startServer(MinecraftServer minecraftServer) {
        try {
            // Starts the HTTP server on port 3001
            HttpServer server = HttpServer.create(new InetSocketAddress(7169), 0); // Start HTTP server on port 3001
            server.createContext("/discord-to-minecraft", exchange -> handleDiscordMessage(exchange, minecraftServer));
            server.createContext("/execute-command", exchange -> handleCommand(exchange, minecraftServer));
            server.setExecutor(null);
            server.start();
            System.out.println("DiscordHandler listening on port 3001");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called when a message is received from Discord.
     *
     * @param exchange The HTTP exchange.
     * @param minecraftServer The Minecraft server.
     */
    private static void handleDiscordMessage(HttpExchange exchange, MinecraftServer minecraftServer) {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                // Processes the request
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String body = reader.lines().collect(Collectors.joining("\n"));
                JsonObject json = com.google.gson.JsonParser.parseString(body).getAsJsonObject();
                String username = json.get("username").getAsString();
                String content = json.get("content").getAsString();

                // Sends the message to all players in the Minecraft chat
                ChatHandlerMinecraft.sendMessageToAllPlayers(minecraftServer, username, content);

                // Returns an HTTP response
                String response = "Message received";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    exchange.sendResponseHeaders(500, 0);
                    exchange.close();
                } catch (Exception ignored) {}
            }
        } else {
            try {
                exchange.sendResponseHeaders(405, 0); // Method Not Allowed
                exchange.close();
            } catch (Exception ignored) {}
        }
    }

    /**
     * This method is called when a command is received from Discord.
     *
     * @param exchange The HTTP exchange.
     * @param minecraftServer The Minecraft server.
     */
    private static void handleCommand(HttpExchange exchange, MinecraftServer minecraftServer) {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String body = reader.lines().collect(Collectors.joining("\n"));
                JsonObject json = com.google.gson.JsonParser.parseString(body).getAsJsonObject();
                String command = json.get("command").getAsString();

                // Parse and execute the command on the Minecraft server
                ServerCommandSource commandSource = minecraftServer.getCommandSource();
                ParseResults<ServerCommandSource> parseResults = minecraftServer.getCommandManager().getDispatcher().parse(command, commandSource);
                minecraftServer.getCommandManager().getDispatcher().execute(parseResults);

                // Send HTTP response
                String response = "Command executed";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    exchange.sendResponseHeaders(500, 0);
                    exchange.close();
                } catch (Exception ignored) {}
            }
        } else {
            try {
                exchange.sendResponseHeaders(405, 0); // Method Not Allowed
                exchange.close();
            } catch (Exception ignored) {}
        }
    }
}