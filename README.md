![banner](https://cdn.modrinth.com/data/cached_images/9cf4de9eef2f71dcbecbf70f9b8d9f7dc8ffa7cd.jpeg)
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

<table>
  <tr>
    <td>
      <h3>Channels</h3>
      Strings is centered around an advanced
      channel system with various channel types,
      settings, permissions, and more.
      Channel types include <em>world-specific</em> channels,
      and <em>proximity-based</em> channels.
    </td>
    <td><img alt="channel demo" src="https://cdn.modrinth.com/data/cached_images/74ea078ab2aa35b51b34447e18af85c29e30e287.gif"></img></td>
  </tr>
  <tr>
    <td>
      <h3>Formatting</h3>
      Each channel can be formatted, with support for MiniMessage!
      Group formatting is also supported.
    </td>
    <td>
      <img src="https://cdn.modrinth.com/data/cached_images/46f732932137b4628976ae81f1723acc16a6c3d6.png"></img>
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://cdn.modrinth.com/data/cached_images/6cbb398e0864538796c5586469eb3f844e44e9e8.png"></img>
    </td>
  <td>
    <h3>Mentions</h3>
    Strings' permission based mention system can play sounds to players,
send messages in the action bar, and color the mention based off whether the recipient is being mentioned or not.
  </td>
  </tr>
  <tr>
    <td>
      <img alt="emoji demo" src="https://www.pedestria.com/img/strings/emoji-demo-2.gif"></img>
  <span><em>*Textures from Pixel Twemoji 18x</em></span>
    </td>
      <td>
      <h3>Emojis</h3>
      The <code>/emoji</code> lets players easily use
      emojis in chat!
      Discord style codes such as <code>:smile:</code> are supported.
      Strings can also have players apply resource packs like <a href="https://modrinth.com/resourcepack/pixel-twemoji-18x">Pixel Twemoji 18x</a> for better emojis.
    </td>
  </tr>
  <tr>
    <td>
      <h3>Broadcasts</h3>
      Strings improves manual broadcasts,
      and also can send out scheduled automated broadcasts.
    </td>
    <td>
      <img src="https://cdn.modrinth.com/data/cached_images/32232e4d2f5b1678e6c00891fbe61db6a3d82f1e.png"></img>
    </td>
  </tr>
  <tr>
      <td>
      <h3>Message Deletion</h3>
      Message deletion can be enabled,
      where players will have a button to
      the side of their message to delete it.
    </td>
    <td>
            <img alt="emoji demo" src="https://www.pedestria.com/img/strings/message-deletion-demo.gif"></img>
  </tr>
  <tr>
    <td>
      <img src="https://cdn.modrinth.com/data/cached_images/d63f75ca180e49957410eeef0154fe9aa713153d.png"></img>
    </td>
      <td>
      <h3>Customizable Join/Leave Messages</h3>
      If enabled, Strings can let you customize
      player join and leave messages.
      You can also disable these messages altogether.
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://cdn.modrinth.com/data/cached_images/25eecfde237d927c8db67ae3403ba2482c5e64c0_0.webp"></img>
    </td>
  <td>
    <h3>LuckPerms & Vault Support</h3>
    Strings seamlessly integrates with LuckPerms
    and any other plugin that supports Vault for prefixes and suffixes.
  </td>
  </tr>
  <tr>
    <td>
      <h3>MiniMessage & Player Head Support</h3>
      MiniMessage makes all kinds of chat formatting possible in Strings, including putting player heads in chat!
    </td>
    <td>
      <img src="https://cdn.modrinth.com/data/cached_images/3e9eed7bb512364026808173b2cd8aae14008913.png"></img>
    </td>
  </tr>
</table>

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
Integrations with Strings can be made with the StringsAPI

## Developer
Integrations with Strings can be made with the StringsAPI.

### Maven
```xml
<repositories>
    <repository>
        <id>pedestriamc-repo</id>
        <url>https://repo.pedestriamc.com/release/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.pedestriamc.strings</groupId>
        <artifactId>strings-api</artifactId>
        <version>1.7.0</version>
    </dependency>
</dependencies>
```

## Additional Links
[Website](https://www.wiicart.net/strings.html) |
[Wiki](https://github.com/Wiicart/Strings/wiki) |
[Modrinth](https://modrinth.com/plugin/strings) |
[Spigot](https://www.spigotmc.org/resources/strings-chat-plugin.118186/) |
[Hangar](https://hangar.papermc.io/wiicart/Strings) |
[bStats](https://bstats.org/plugin/bukkit/Strings/22597) |
[Discord](https://discord.gg/meYfEJcf9P) 

![bStats](https://bstats.org/signatures/bukkit/strings.svg)
 
