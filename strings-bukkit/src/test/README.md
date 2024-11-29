![banner](https://cdn.modrinth.com/data/cached_images/a8a22692f6bbd15d3bb12969826334e8796cf7f4.png)
## Channels, mentions, chat color, broadcasts, formatting, more - all in one.

<br/>

### Chat Colors that support Minecraft and Hex/RGB color codes.
<img src="https://cdn.modrinth.com/data/cached_images/2aefad36a9788df57ea37e0d415440b6bfd1b4ed.png" alt="chatcolor" width="700"/>
<br/><br/>

### Custom formatting on a per channel basis
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

## Nearly every aspect is customizable.


<details>
<summary>All Features</summary>

## Features:
- Channels
- Chat Formatting
- Mentions
- API
- World Channels
- Proximity Channels
- Chat Color
- Staff Chat
- HelpOP
- Chat Clearing
- Auto Broadcasts
- Custom Join and Leave Messages
- Optional URL Filter
- Social Spy
- Optional Chat Cooldowns
- PlaceholderAPI Support
- Vault Support
- Highly Customizable


</details>




<details>
<summary>Commands</summary>

## Commands:
- /broadcast <message> - broadcasts a message to the server.
- /clearchat - clears chat, optionally for all players with the argument "all".
- /channel <channel> - sets your own active channel.
- /channel join <channel> - lets you join a channel.
- /channel leave <channel> - lets you leave a channel.
- /msg <msg> - message a player.
- /r <msg> - respond to a player.
- /helpop <message> - send a help op message.
- /socialspy - toggle socialspy to monitor players messaging each other.
  Add a player to the end of any of the channel commands to have the command work on said player.
- /chatcolor <color> - change your chat color.

</details>



<details>
<summary>Permissions</summary>

## Permissions: 
- strings.* - all permissions.
- strings.chat.colormsg - allows players to color their messages.
- strings.chat.placeholdermsg - allows players to use placeholders in their messages.
- strings.chat.clear - allows players to clear their own chat.
- strings.chat.clear.others - allows players to clear all player's chats.
- strings.chat.broadcast - allows sending broadcasts.
- strings.chat.msg - allows players to directly message other players.
- strings.socialspy - allows use of the /socialspy command.
- strings.channels.<channel> - allows a player to use the specified channel.
- strings.channel.modifyplayers - allows use of the /channel command on other players.
- strings.helpop.receive - permission to receive HelpOP messages.
- strings.helpop.use - allows players to send HelpOP messages.
- strings.chat.bypasscooldow - allows players to bypass chat cooldowns.
- strings.chat.chatcolor - allows changing your own chat color.
- strings.chat.chatcolor.other - allows changing other player's chat color.
- strings.channels.<channel>.receive - makes it so players receive messages from a channel by default.

</details>

## Installation:
Strings has two optional dependencies:
- Vault
- Placeholder API
- A permissions plugin such as LuckPerms

These are not necessary to run the plugin, however, Vault allows the use of prefixes and suffixes, and Placeholder API makes more placeholders available.
A permissions such as LuckPerms is needed to set prefixes.
Most features are disabled by default, but are easily enabled in the configuration files.

## Support
Reach me through Discord or GitHub (@wiicart).\
[Wiki](https://github.com/Wiicart/Strings/wiki)