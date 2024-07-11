package com.pedestriamc.strings;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ChatManager {
    private final String messageFormat;
    private final String defaultColor;
    private final boolean usePAPI;
    private final boolean messagePlaceholders;
    private final boolean parseChatColors;

    public ChatManager(@NotNull Strings strings){
        this.messageFormat = ChatColor.translateAlternateColorCodes('&', strings.getMessageFormat());
        this.defaultColor = ChatColor.translateAlternateColorCodes('&',strings.getDefaultColor());
        this.usePAPI = strings.usePlaceholderAPI();
        this.parseChatColors = strings.processMessageColors();
        this.messagePlaceholders = strings.processMessagePlaceholders();
    }

    public @NotNull String formatMessage(Player sender){
        String newMessageFormat = messageFormat;
        if(usePAPI){
            newMessageFormat = PlaceholderAPI.setPlaceholders(sender,newMessageFormat);
        }
        newMessageFormat = newMessageFormat.replace("{displayname}", "%s");
        newMessageFormat = newMessageFormat.replace("{message}", "%s");

        return newMessageFormat;
    }
    public String processMessage(Player sender, String message){
        message = defaultColor + message;
        if(parseChatColors && (sender.hasPermission("strings.*") || sender.hasPermission("strings.chat.*") || sender.hasPermission("strings.chat.colormsg"))){
            message = ChatColor.translateAlternateColorCodes('&',message);
        }
        if(usePAPI && messagePlaceholders && (sender.hasPermission("strings.*") || sender.hasPermission("strings.chat.*") || sender.hasPermission("strings.chat.placeholdermsg"))){
            message = PlaceholderAPI.setPlaceholders(sender,message);
        }
        return message;
    }
}
