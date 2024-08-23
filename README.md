```
 ____    __                                         
/\  _`\ /\ \__         __                           
\ \,\L\_\ \ ,_\  _ __ /\_\    ___      __     ____  
 \/_\__ \\ \ \/ /\`'__\/\ \ /' _ `\  /'_ `\  /',__\ 
   /\ \L\ \ \ \_\ \ \/ \ \ \/\ \/\ \/\ \L\ \/\__, `\
   \ `\____\ \__\\ \_\  \ \_\ \_\ \_\ \____ \/\____/
    \/_____/\/__/ \/_/   \/_/\/_/\/_/\/___L\ \/___/ 
                                       /\____/      
                                       \_/__/
               Minecraft Chat Plugin
```
## Information
Strings is a Minecraft chat plugin written with the Spigot API, bringing lots of customizability to Minecraft server's chat.

![text example](https://www.spigotmc.org/attachments/strongs-png.841882)

**Features include:**

 - Customizability
 - Public Developer API
 - Channels
 - Chat Formatting
 - Chat Color
 - Staff Chat
 - HelpOP
 - Auto Broadcasts
 - Direct Messaging
 - Vault Support
 - Placeholder API Support
 
And much more!

## Channel Information
One of the biggest and most versatile features of this plugin is the channel system.
There are 3 main channel types: 

 - StringsChannel
 A channel that does not consider the player's world or proximity.
 - WorldChannel
 A channel that only works in one specific world, unless the channel manually joined.
 - ProximityChannel
 A channel that only sends messages to players within the right proximity of the sender, unless the channel is manually joined.
 
 Some additional features available to channels are per-channel formatting, priority and default membership.  

 **Channel Membership:**\
 There are two options for channel `membership`, and those are `default` and `permission`.  Default channels are available to all players, regardless of permissions, as long as they are within the scope of the channel.
 
 **Priority:**\
 Each channel can be designated a certain priority, the higher the number, the higher the priority.  This only matters for channels with `membership: default`, as priority will determine what channel a player's message will be sent in. 
 
  **Default Channels:**\
 Both channel membership and priority work together to offer a key piece of functionality, the default channel.  Every player has an active channel set, however, internally there is a channel type that does not process messages, rather it designates the appropriate channel a player's message should be sent in, considering world, priority and membership.
 
 
 **Developer:**\
 Nearly all of the things listed in this section, and more are customizable through the API.  For more information, check out the [wiki](https://github.com/Wiicart/Strings/wiki).
## Developer
I've added a public API which will be available through Maven soon.  The API is currently published through [GitHub releases](https://github.com/Wiicart/Strings/releases).

## Additional Links
[Website](https://www.wiicart.net/strings.html)\
[Wiki](https://github.com/Wiicart/Strings/wiki)\
[Modrinth](https://modrinth.com/plugin/strings)\
[Spigot](https://www.spigotmc.org/resources/strings-chat-plugin.118186/)\
[Hangar](https://hangar.papermc.io/wiicart/Strings)\
[bStats](https://bstats.org/plugin/bukkit/Strings/22597)
 
