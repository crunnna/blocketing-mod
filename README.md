<div align="center">
    <img src="src/main/resources/blocketing.jpeg" alt="Blocketing Logo" width="300" height="300">
  <h1 align="center">Blocketing Fabric Mod</h1>
  <h3>Integrate Discord with Minecraft for seamless communication and command execution</h3>
</div>

## 🔗 Related Repositories
- [Blocketing Bot](https://github.com/crunnna/blocketing-bot): A Discord bot for seamless communication between a Discord channel and a Minecraft server, enabling bidirectional message synchronization.

---

## ✨ Features
- ✅ **Message Relay**: Sync messages between a Minecraft server and a Discord channel. 💬
- ✅ **Join/Leave Notifications**: Send join and leave messages to Discord with player avatars. 🖼️
- ✅ **Server Start/Stop Notifications**: Notify Discord when the server starts or stops. 🚀
- ✅ **Advancement Messages**: Toggle the sending of advancement messages to Discord. 🏆
- ✅ **Death Messages**: Toggle the sending of death messages to Discord. 💀
- ✅ **Command Execution**: Execute Minecraft commands from Discord (with permissions). 🛠️
- ✅ **In-Game Configuration**: Configure bot token, channel ID, and port directly from in-game commands. 🎮

---

## 🔨 Before using this mod
### 🤖 Setting Up the Discord Bot:
- **Create a new Discord bot** (Follow a YouTube guide on how to create a Discord bot.)
- Configure and run the bot from the **[Blocketing Bot](#-related-repositories) repository**.

### 🔧 Opening a Port:
- Ensure your **Minecraft server** allows incoming HTTP requests.
- Follow a Youtube guide based on your hosting provider to open a port.

---

## 🚀 Installation
> [!IMPORTANT]
1️⃣ **Download the latest release** from the [Releases Page](#) (link missing).  
2️⃣ **Place the `.jar` file in your server’s `mods/` folder**.  
3️⃣ **Start your Minecraft server** with Fabric installed.  
4️⃣ **Configure the bot** using the in-game commands (see below).  
5️⃣ The mod will log in to Discord and start listening for messages and commands.

---

## ⚙️ Configuration Commands
### 🎮 In-Game Setup
> ```sh
> /blocketing setup token <your_discord_bot_token>
> /blocketing setup channel <your_discord_channel_id>
> /blocketing setup port <your_http_server_minecraft_port>
> ```

### 🔄 Toggle Features
> ```sh
> /blocketing toggle advancements   # Toggle the sending of advancements to Discord.
> /blocketing toggle deaths   # Toggle the sending of death messages to Discord.
> ```

---

### 📌 Need help? Feel free to ask questions or open an issue!