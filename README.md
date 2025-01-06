<div align="center">
  <h1 align="center">Blocketing Fabric Mod</h1>
  <h3>Integrate Discord with Minecraft for seamless communication and command execution</h3>
</div>

## 🔗 Related Repositories
- [Blocketing Bot](https://github.com/crunnna/blocketing-bot): A Discord bot for seamless communication between a Discord channel and a Minecraft server, enabling bidirectional message synchronization.

## ✨ Features
- **Message Relay**: Send messages from a specific Discord channel to a Minecraft server. 💬
- **Join/Leave Notifications**: Send join and leave messages to a Discord channel as embeds. 🚪
- **Server Start/Stop Notifications**: Send server start and stop messages to a Discord channel as embeds. 🚀
- **Command Execution**: Execute Minecraft commands from Discord with proper permissions. 🛠️
- **HTTP Server**: Receive messages and commands from Discord via HTTP requests. 🌐

## 🔨 Before using this mod
### 🤖 Discord Bot:
- Before using this mod, you need to create a Discord bot. You can follow a YouTube video for guidance on how to create a Discord bot.

### 🔧Open a Port:
- To open a port on your Minecraft server, you can watch a YouTube video that explains the process depending on whether you are hosting locally or with a provider.

## ⚡ Getting Started
> [!IMPORTANT]
> To set up your Blocketing Mod, begin by cloning the repository and building the project. Once built, create a `config.properties` file in the `src/main/resources` directory and add your configuration.
> ```sh
> git clone https://github.com/crunnna/blocketing-fabric-mod.git
> cd blocketing-fabric-mod/
> ./gradlew build
> ```
> Add your configuration to the config.properties file:
> ```env
> BOT_TOKEN=your_discord_bot_token
> CHANNEL_ID=your_discord_channel_id
> PORT=your_http_server_minedraft_port
> ```

## 🚀 Usage
> ### Running the Mod
> To run the mod, start your Minecraft server with the mod installed. The mod will log in to Discord and start listening for messages and commands.

## 🌐 Environment Variables
- `BOT_TOKEN`: The token of the Discord bot.
- `CHANNEL_ID`: The ID of the Discord channel.
- `PORT`: The port number for the HTTP server.

## 🙏 Acknowledgements
- [Fabric](https://fabricmc.net/) for the Minecraft modding framework.

### For any questions or issues, please open an issue on this repository. I'm here to help!