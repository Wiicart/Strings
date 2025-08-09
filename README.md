![banner](https://cdn.modrinth.com/data/cached_images/a8a22692f6bbd15d3bb12969826334e8796cf7f4.png)
## Channels, mentions, chat color, broadcasts, formatting, and more - all in one.

<p align="center">
  <a href="https://modrinth.com/plugin/strings">
    <img alt="Modrinth Downloads" src="https://img.shields.io/modrinth/dt/strings?logo=modrinth">
  </a>
  &nbsp;
  <a href="https://www.spigotmc.org/resources/strings-chat-plugin.118186/">
    <img alt="Spiget Downloads" src="https://img.shields.io/spiget/downloads/118186?logo=spigotmc">
  </a>
</p>



## Information
Strings is a Minecraft chat plugin written with the Spigot & Paper API,
bringing lots of customizability to your Minecraft server's chat.

**Features include:**

- Extensive Customizability
- Public Developer API
- Channels
- Chat Formatting
- Chat Color
- Staff Chat
- HelpOP
- Auto Broadcasts
- Direct Messaging
- Vault Support
- PlaceholderAPI Support
- Moderation 
- Mentions
- Discord Support

And much more!

## Channel System
Strings has a powerful channel system.
<details>
The most powerful feature of this plugin is the Channel system.
There are three main Channel types, and the Channel interface is publicly available
so developers can implement custom Channels too.

### Main Channel Types

- StringChannel\
  A standard channel that disregards worlds and proximity
- WorldChannel\
  A channel designed to be used in specific worlds
- ProximityChannel\
  A channel that sends messages to players within a certain distance of the sender
- DefaultChannel\
  Instead of forcing players to select a specific channel, the default Channel routes
  the sender's message to a Channel, based on membership and priority

### Channel Features
**Membership**
All channels have one of two `memberships` defined.
- `default` - available to all players, regardless of permissions
- `permission` - only allowed to players with permission, such as `strings.channels.<name>`

**Priority**
Channels can be designated a priority which helps determine what channel a message is sent to
when a player is in the *DefaultChannel*. The higher the number, the higher priority.

**Extendable**
The `Channel` interface is available through the `strings-api` module, so developers can
create custom Channel implementations.

</details>

## Images
Example images of Strings
<details>

### Chat Colors that support Minecraft and Hex/RGB color codes.
<img src="https://cdn.modrinth.com/data/cached_images/2aefad36a9788df57ea37e0d415440b6bfd1b4ed.png" alt="chatcolor" width="700"/>
<br/><br/>

### Custom formatting on a per-channel basis
<img src="https://cdn.modrinth.com/data/cached_images/3cced0075b41fd7bd822e226a703fea118eaa994.png" alt="formatting" width="700"/>
<br/><br/>

### 3 different channel types (Global, World, Proximity)
<img src="https://cdn.modrinth.com/data/cached_images/51e7c35edf9a6530ebd6091e7bbbb8261181a710.png" alt="channels" width="700"/>
<br/><br/>


### Customizable join and leave messages.
<img src="https://cdn.modrinth.com/data/cached_images/b7c5edebc8bcaab3d0413f984fe386d19cf1a85d.png" alt="jlmsg" width="700"/>
<br/><br/>

### Customizable player mention system with sounds.
<img src="https://cdn.modrinth.com/data/cached_images/b4c1c7aa48183ae8fa59faea99cfd15ae965bbfd.png" alt="mentions" width="700"/>
<br/><br/>

### Customizable automatic broadcasts
<img src="https://cdn.modrinth.com/data/cached_images/32232e4d2f5b1678e6c00891fbe61db6a3d82f1e.png" alt="auto" width="700"/>
<br/><br/>

### Prefix/Suffix support with Vault/LuckPerms and more.
<img src="https://cdn.modrinth.com/data/cached_images/2609ada09c59ee85badfb388862faa25edc59193.png" alt="auto" width="700"/>
<br/><br/>
</details>


## Developer
I've added a public API which will be available through Maven soon.  The API is currently published through [GitHub releases](https://github.com/Wiicart/Strings/releases).

## Additional Links
[Website](https://www.wiicart.net/strings.html) |
[Wiki](https://github.com/Wiicart/Strings/wiki) |
[Modrinth](https://modrinth.com/plugin/strings) |
[Spigot](https://www.spigotmc.org/resources/strings-chat-plugin.118186/) |
[Hangar](https://hangar.papermc.io/wiicart/Strings) |
[bStats](https://bstats.org/plugin/bukkit/Strings/22597) |
[Discord](https://discord.gg/meYfEJcf9P) 

![bStats](https://bstats.org/signatures/bukkit/strings.svg)
 