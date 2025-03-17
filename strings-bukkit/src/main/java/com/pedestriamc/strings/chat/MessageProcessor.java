package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.user.User;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.configuration.ConfigurationOption.*;

public class MessageProcessor {

    private final Strings strings;
    private final Channel channel;

    private final boolean usingPlaceholderAPI;
    private final boolean processingMessagePlaceholders;
    private final boolean parsingMessageChatColors;
    private final String mentionColor;

    public MessageProcessor(@NotNull Strings strings, Channel channel) {
        this.strings = strings;
        this.channel = channel;
        Configuration config = strings.getConfigClass();
        usingPlaceholderAPI = strings.usePlaceholderAPI();
        processingMessagePlaceholders = config.getBoolean(PROCESS_PLACEHOLDERS) && usingPlaceholderAPI;
        parsingMessageChatColors = config.getBoolean(PROCESS_CHATCOLOR);
        mentionColor = config.colored(MENTION_COLOR);
    }

    /**
     * Generates the message template with the Player's prefix, display name, etc.
     * Does not fill in the {message} placeholder.
     * @param player The Player sending the message
     * @return A formatted String
     */
    public String generateTemplate(Player player) {
        User user = strings.getUser(player);
        String template = channel.getFormat();

        template = template
                .replace("{prefix}", user.getPrefix())
                .replace("{suffix}", user.getSuffix())
                .replace("{displayname}", user.getDisplayName())
                .replace("{message}", user.getChatColor(channel) + "{message}");

        if(usingPlaceholderAPI) {
            template = setPlaceholders(player, template);
        }
        template = org.bukkit.ChatColor.translateAlternateColorCodes('&', template);

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
            message = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
        }
        return message;
    }

    public String processMentions(Player sender, @NotNull String str)
    {
        if(!str.contains("@")){
            return str;
        }

        String[] splitStr = str.split("((?=@))"); //https://www.baeldung.com/java-split-string-keep-delimiters
        StringBuilder sb = new StringBuilder();
        String color = "";
        for(String segment : splitStr){
            if(!segment.contains("@")){
                color = ChatColor.getLastColors(segment);
                sb.append(segment);
                continue;
            }
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(!strings.getUser(p).isMentionsEnabled() || !segment.contains(p.getName())) {
                    continue;
                }
                segment = segment.replace("@" + p.getName(), mentionColor + "@" + p.getName() + ChatColor.RESET + color);
            }
            if(sender.hasPermission("strings.mention.all") && segment.contains("@everyone")) {
                segment = segment.replace("@everyone", mentionColor + "@everyone" + ChatColor.RESET + color);
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
    private String setPlaceholders(Player sender, String str) {
        try {
            return PlaceholderAPI.setPlaceholders(sender, str);
        } catch (NoClassDefFoundError e) {
            return str;
        }
    }


    private boolean shouldReplacePlaceholders(Player sender) {
        if(processingMessagePlaceholders) {
            return (
                    sender.hasPermission("strings.*") ||
                    sender.hasPermission("strings.chat.*") ||
                    sender.hasPermission("strings.chat.placeholdermsg")
            );
        }
        return false;
    }

    private boolean shouldColorMessage(Player sender) {
        if(parsingMessageChatColors) {
            return (
                    sender.hasPermission("strings.*") ||
                    sender.hasPermission("strings.chat.*") ||
                    sender.hasPermission("strings.chat.colormsg")
            );
        }
        return false;
    }

}
