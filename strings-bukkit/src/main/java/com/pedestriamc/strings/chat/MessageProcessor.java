package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.utlity.Permissions;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.pedestriamc.strings.configuration.Option.*;

public class MessageProcessor {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#[0-9A-Fa-f]{6}");

    private final UserUtil userUtil;
    private final Channel channel;
    private final Logger logger;

    private final boolean usingPlaceholderAPI;
    private final boolean processingMessagePlaceholders;
    private final boolean parsingMessageChatColors;
    private final String mentionColor;

    public MessageProcessor(@NotNull Strings strings, Channel channel) {
        this.channel = channel;
        userUtil = strings.getUserUtil();
        logger = strings.getLogger();
        Configuration config = strings.getConfiguration();
        usingPlaceholderAPI = strings.isUsingPlaceholderAPI();
        processingMessagePlaceholders = config.getBoolean(PROCESS_PLACEHOLDERS) && usingPlaceholderAPI;
        parsingMessageChatColors = config.getBoolean(PROCESS_CHATCOLOR);
        mentionColor = config.getColored(MENTION_COLOR);
    }

    /**
     * Generates the message template with the Player's prefix, display name, etc.
     * Does not fill in the {message} placeholder.
     * @param player The Player sending the message
     * @return A formatted String
     */
    public String generateTemplate(Player player) {
        User user = userUtil.getUser(player);
        String template = channel.getFormat();

        template = template
                .replace("{prefix}", user.getPrefix())
                .replace("{suffix}", user.getSuffix())
                .replace("{displayname}", user.getDisplayName())
                .replace("{message}", user.getChatColor(channel) + "{message}");

        if(usingPlaceholderAPI) {
            template = setPlaceholders(player, template);
        }

        template = setHex(template);
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
        if(!str.contains("@")) {
            return str;
        }

        String chatColor = userUtil.getUser(sender).getChatColor(channel);
        String[] splitStr = str.split("((?=@))"); //https://www.baeldung.com/java-split-string-keep-delimiters
        StringBuilder sb = new StringBuilder();
        for(String segment : splitStr) {
            if(!segment.contains("@")) {
                sb.append(chatColor).append(segment);
                continue;
            }

            if(sender.hasPermission("strings.mention.all") && segment.contains("@everyone")) {
                segment = segment.replace("@everyone", mentionColor + "@everyone" + ChatColor.RESET + chatColor);
            }

            for(Player p : Bukkit.getOnlinePlayers()) {
                if(!userUtil.getUser(p).isMentionsEnabled() || !segment.contains(p.getName())) {
                    continue;
                }
                segment = segment.replace("@" + p.getName(), mentionColor + "@" + p.getName() + ChatColor.RESET + chatColor);
            }

            sb.append(segment);
        }
        return sb.toString();
    }

    /**
     * Translates any HEX color codes (formatted &#<HEX> to ChatColor).
     * @param string The String to translate
     * @return A translated String;
     */
    private @NotNull String setHex(@NotNull String string) {
        Matcher matcher = HEX_PATTERN.matcher(string);
        StringBuilder sb = new StringBuilder();

        while(matcher.find()) {
            try {
                String stringHex = matcher.group();
                ChatColor colorCode;

                Color color = Color.decode(stringHex.substring(1));
                colorCode = ChatColor.of(color);

                matcher.appendReplacement(sb, colorCode.toString());
            } catch(NumberFormatException e) {
                matcher.appendReplacement(sb, matcher.group());
            }
        }

        matcher.appendTail(sb);
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
