![banner](https://cdn.modrinth.com/data/cached_images/a8a22692f6bbd15d3bb12969826334e8796cf7f4.png)
# StringsDiscord
Brings Discord-Minecraft chat integration, specifically tailored for the Strings plugin
in ways that other plugins don't offer.

## Note
For messages to be sent to Discord, Strings channels must have `call-event` set to true, otherwise StringsDiscord will not receive the messages.
For example, the default staff channel has `call-event` set to false, and as a result, StringsDiscord does not receive messages from the channel by default.

## Modes
StringsDiscord offers two modes, which are chosen with the `global` option in `config.yml`
All global means is that specific Channels in Strings are ignored, and all messages will output to the same Channel.
If you're using global mode, you'll need to configure the option `global-channels` in `config.yml`.

**Global Example:**
```
global-channels:
 - 694661573125472256
 - 694661910179610654
```
In the example, all chat messages will be sent to the Channels with IDs `694661573125472256` and `694661910179610654`


Non-global mode means that you can match specific channels from Strings to specific Discord channels.
A good use of this would be to link the staff channel in-game to a Discord staff channel.
If you're using non-global, you'll have to configure `channels` in `config.yml`

**Non-Global Example:**
```
channels:
  global: 694661573125472256
  staff: 694661910179610654
```
In the example, the Strings channel `global` is linked to a Discord Channel with an ID of `694661573125472256`


## Discord Instructions


### Bot Instructions
To use StringsDiscord, you must make a Discord Bot profile. 
Go to [Discord Developer Applications](https://discord.com/developers/applications),
and create a new application. In the application page, go to **Bot**.
Here, you can enter the Bot's username & profile picture. 
Once you have finished these customizations, scroll down and set the intents 
`Server Members Intent` and `Message Content Intent` to true. 
Then, scroll down to **Bot Permissions**.

**Set all the following permissions:**

In **General Permissions**
 - View Channels
 - View Server Insights
 - Manage Channels (If you want Minecraft server stats)

In **Text Permissions**
 - Send Messages
 - Send Messages In Threads
 - Read Message History
 - Mention Everyone

Once you have set permissions, scroll up to the **Reset Token** button.
Click the button and store the token somewhere safe. **Do not share the token;**
it's how your bot is logged in to. Put the token in the `token` option in `config.yml`.

**Inviting the Bot**\
On your Discord Developer Application, go to the **OAuth2** tab. In "Redirects" enter a link, such as `https://www.wwicart.net` for example.
Scroll down to **OAuth2 URL Generator** and select `bot`. Scroll to the bottom of the page, and click on the URL to invite the Bot.

### Channel Instructions
In `config.yml`, all Channels from the Strings plugin are referenced by their text-name,
however, Discord Channels must be referenced by their ID. To retrieve a Channel ID, simply right-click
on the Channel on Desktop, and at the bottom there should be an option to "Copy Channel ID"

Example Channel ID: `694661573125472256`

### Further instructions are in `config.yml`