<div align="center">
    <img src="src/main/resources/blocketing.jpeg" alt="Blocketing Logo" width="300" height="300">
  <h1 align="center">Blocketing Fabric Mod</h1>
  <h3>Integrate Discord with Minecraft for seamless communication and command execution</h3>
</div>

## 🔗 Related Repositories
- [Blocketing Bot](https://github.com/crunnna/blocketing-bot): A Discord bot for seamless communication between a Discord channel and a Minecraft server, enabling bidirectional message synchronization.

## ✨ Features
- [x] **Message Relay**: Send messages from a specific Discord channel to a Minecraft server. 💬
- [x] **Join/Leave Notifications**: Send join and leave messages to a Discord channel as embeds with the player skin avatar. 🖼️
- [x] **Server Start/Stop Notifications**: Send server start and stop messages to a Discord channel as embeds. 🚀
- [x] **Command Execution**: Execute Minecraft commands from Discord with proper permissions. 🛠️
- [x] **HTTP Server**: Receive messages and commands from Discord via HTTP requests. 🌐
- [x] **In-Game Configuration**: Configure bot token, channel ID, and port directly from in-game commands. 🎮

## 🔨 Before using this mod
### 🤖 Discord Bot:
- Before using this mod, you need to create a Discord bot. You can follow a YouTube video for guidance on how to create a Discord bot.
- Additionally, you must also configure and run your bot from the [Blocketing Bot](#-related-repositories) repository.

### 🔧Open a Port:
- To open a port on your Minecraft server, you can watch a YouTube video that explains the process depending on whether you are hosting locally or with a provider.

## ⚡ Getting Started
> [!IMPORTANT]
> To set up your Blocketing Mod, download the latest release from the [releases page] () and add it to your Minecraft server's `mods` folder.

## 🚀 Usage
> ### Running the Mod
> To run the mod, start your Minecraft server with the mod installed. The mod will log in to Discord and start listening for messages and commands.

> ### In-Game Configuration Commands
> You can configure the bot token, channel ID, and port directly from in-game using the following commands:
> ```sh
> /blocketing token <your_discord_bot_token>
> /blocketing channel <your_discord_channel_id>
> /blocketing port <your_http_server_minecraft_port>
> ```

## :globe_with_meridians: Environment Variables
- `BOT_TOKEN`: The token of the Discord bot.
- `CHANNEL_ID`: The ID of the Discord channel.
- `PORT`: The port number for the HTTP server.

## :pray: Acknowledgements
- [Fabric](https://fabricmc.net/) for the Minecraft modding framework.
- [Mineatar](https://mineatar.io/) for providing player skin-api

### For any questions or issues, please open an issue on this repository. I'm here to help!