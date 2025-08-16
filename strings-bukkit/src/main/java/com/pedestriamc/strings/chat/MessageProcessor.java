package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class MessageProcessor {

    private final UserUtil userUtil;
    private final Channel channel;
    private final Logger logger;

    private final boolean usingPlaceholderAPI;
    private final boolean processingMessagePlaceholders;
    private final boolean parsingMessageChatColors;
    private final String mentionColor;

    public MessageProcessor(@NotNull Strings strings, Channel channel) {
        this.channel = channel;
        userUtil = strings.users();
        logger = strings.getLogger();
        Configuration config = strings.getConfiguration();
        usingPlaceholderAPI = strings.isUsingPlaceholderAPI();
        processingMessagePlaceholders = config.getBoolean(Option.Bool.PROCESS_PLACEHOLDERS) && usingPlaceholderAPI;
        parsingMessageChatColors = config.getBoolean(Option.Bool.PROCESS_CHATCOLOR);
        mentionColor = config.getColored(Option.Text.MENTION_COLOR);
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
        if(shouldReplacePlaceholders(sender)) {
            message = setPlaceholders(sender, message);
        }
        if(shouldColorMessage(sender)) {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }
        return message;
    }

    public String processMentions(Player sender, @NotNull String str) {
        if (!str.contains("@")) {
            return str;
        }

        String chatColor = userUtil.getUser(sender).getChatColor(channel);
        String[] splitStr = str.split("((?=@))"); //https://www.baeldung.com/java-split-string-keep-delimiters
        StringBuilder sb = new StringBuilder();
        for (String segment : splitStr) {
            if (!segment.contains("@")) {
                sb.append(chatColor).append(segment);
                continue;
            }

            if (sender.hasPermission("strings.mention.all") && segment.contains("@everyone")) {
                segment = segment.replace("@everyone", mentionColor + "@everyone" + ChatColor.RESET + chatColor);
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (userUtil.getUser(p).isMentionsEnabled() && segment.contains(p.getName())) {
                    String original = "@" + p.getName();
                    String replacement = mentionColor + "@" + p.getName() + ChatColor.RESET + chatColor;
                    segment = segment.replace(original, replacement);
                }
            }

            sb.append(segment);
        }
        return sb.toString();
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
