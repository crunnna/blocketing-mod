<div align="center">
  <h1 align="center">Blocketing Fabric Mod</h1>
  <h3>Integrate Discord with Minecraft for seamless communication and command execution</h3>
</div>

## ✨ Features
- **Message Relay**: Send messages from a specific Discord channel to a Minecraft server. 💬
- **Command Execution**: Execute Minecraft commands from Discord with proper permissions. 🛠️
- **HTTP Server**: Receive messages and commands from Discord via HTTP requests. 🌐

## ⚡ Getting Started
> [!IMPORTANT]
> To set up your Blocketing Mod, begin by cloning the repository and building the project. Once built, create a `.env` file in the root directory and add your configuration.
> ```sh
> git clone https://github.com/crunnna/blocketing-mod.git
> cd blocketing-mod
> ./gradlew build
> ```
> Add your configuration to the `.env` file:
> ```env
> BOT_TOKEN=your_discord_bot_token
> CHANNEL_ID=your_discord_channel_id
> ```

## 🚀 Usage
> ### Running the Mod
> To run the mod, start your Minecraft server with the mod installed. The mod will log in to Discord and start listening for messages and commands.

## 🌐 Environment Variables
- `BOT_TOKEN`: The token of the Discord bot.
- `CHANNEL_ID`: The ID of the Discord channel.

## 🔗 Related Repositories
- [Blocketing Bot](https://github.com/crunnna/blocketing-bot): A Discord bot for seamless communication between a Discord channel and a Minecraft server, enabling bidirectional message synchronization.
- 
## 🙏 Acknowledgements
- [Fabric](https://fabricmc.net/) for the Minecraft modding framework.
- [dotenv](https://github.com/cdimascio/dotenv-java) for managing environment variables.

### For any questions or issues, please open an issue on this repository. I'm here to help!