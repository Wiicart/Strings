package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.wiicart.commands.permission.Permissions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class MessageProcessor {

    private final Strings strings;

    private final UserUtil userUtil;
    private final Channel channel;
    private final Logger logger;

    private final boolean usingPlaceholderAPI;
    private final boolean processingMessagePlaceholders;
    private final boolean parsingMessageChatColors;
    private final boolean emojisEnabled;

    public MessageProcessor(@NotNull Strings strings, Channel channel) {
        this.strings = strings;
        this.channel = channel;
        userUtil = strings.users();
        logger = strings.getLogger();
        Configuration config = strings.getSettings();
        usingPlaceholderAPI = strings.isUsingPlaceholderAPI();
        processingMessagePlaceholders = config.get(Option.Bool.PROCESS_PLACEHOLDERS) && usingPlaceholderAPI;
        parsingMessageChatColors = config.get(Option.Bool.PROCESS_CHATCOLOR);
        emojisEnabled = config.get(Option.Bool.ENABLE_EMOJI_REPLACEMENT);
    }

    /**
     * Generates the message template with the Player's prefix, display name, etc.
     * Does not fill in the {message} placeholder.
     * @param player The Player sending the message
     * @return A formatted String
     */
    public String generateTemplate(Player player) {
        User user = userUtil.getUser(player);
        return generateTemplateNonChatColor(player)
                .replace("{message}", user.getChatColor(channel) + "{message}");
    }

    public String generateTemplateNonChatColor(Player player) {
        User user = userUtil.getUser(player);
        String template = channel.getFormat();

        template = template
                .replace("{prefix}", user.getPrefix())
                .replace("{suffix}", user.getSuffix())
                .replace("{displayname}", user.getDisplayName());

        if(usingPlaceholderAPI) {
            template = setPlaceholders(player, template);
        }

        template = MessageUtilities.colorHex(template);
        template = ChatColor.translateAlternateColorCodes('&', template);

        return template;
    }

    /**
     * Processes the Player's actual message, adding color codes, placeholders, etc.
     * @param sender The sender
     * @param message The sender's message
     * @return A processed message
     */
    public String processMessage(Player sender, String message) {
        if (shouldReplacePlaceholders(sender)) {
            message = setPlaceholders(sender, message);
        }
        if (shouldColorMessage(sender)) {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        message = setEmojisIfAllowed(sender, message);
        return message;
    }

    /**
     * Sets PlaceholderAPI placeholders, but catches NoClassDefFoundError so messages can be sent if
     * PlaceholderAPI gets disabled
     * @param sender The sender
     * @param str The text to have placeholders replaced
     * @return String w/placeholders or null
     */
    protected String setPlaceholders(Player sender, String str) {
        try {
            return PlaceholderAPI.setPlaceholders(sender, str);
        } catch (NoClassDefFoundError e) {
            logger.warning("Failed to set placeholders for message from " + sender.getName() + ".");
            return str;
        }
    }

    private String setEmojisIfAllowed(@NotNull Player sender, @NotNull String input) {
        if (emojisEnabled && Permissions.anyOfOrAdmin(sender, "strings.*", "strings.chat.*", "strings.chat.emojis")) {
            return strings.getEmojiManager().applyEmojis(input);
        }
        return input;
    }

    private boolean shouldReplacePlaceholders(Player sender) {
        return processingMessagePlaceholders &&
                Permissions.anyOfOrAdmin(sender, "strings.*", "strings.chat.*", "strings.chat.placeholdermsg");
    }

    private boolean shouldColorMessage(Player sender) {
        return parsingMessageChatColors &&
                Permissions.anyOfOrAdmin(sender, "strings.*", "strings.chat.*", "strings.chat.colormsg");
    }

    protected User getUser(Player player) {
        return userUtil.getUser(player);
    }

    protected Channel getChannel() {
        return channel;
    }
}
