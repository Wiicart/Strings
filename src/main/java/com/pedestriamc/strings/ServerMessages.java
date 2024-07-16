package com.pedestriamc.strings;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ServerMessages {

    private final Strings strings;
    private final String joinMessageTemplate;
    private final String leaveMessageTemplate;
    private final boolean usePAPI;

    public ServerMessages(Strings strings){
        this.strings = strings;
        this.joinMessageTemplate = strings.getConfig().getString("join-message");
        this.leaveMessageTemplate = strings.getConfig().getString("leave-message");
        this.usePAPI = strings.usePlaceholderAPI();
    }
    public String joinMessage(Player player){
        String message = joinMessageTemplate;
        User user = strings.getUser(player);
        if(usePAPI){
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        message = message.replace("{displayname}", player.getName());
        message = message.replace("{username}", player.getName());
        message = message.replace("{prefix}", user.getPrefix());
        message = message.replace("{suffix}", user.getSuffix());
        return ChatColor.translateAlternateColorCodes('&',message);
    }
    public String leaveMessage(Player player){
        String message = leaveMessageTemplate;
        User user = strings.getUser(player);
        if(usePAPI){
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        message = message.replace("{displayname}", player.getName());
        message = message.replace("{username}", player.getName());
        message = message.replace("{prefix}", user.getPrefix());
        message = message.replace("{suffix}", user.getSuffix());
        return ChatColor.translateAlternateColorCodes('&',message);
    }
    public String deathMessage(Player player){
        return null;
    }
}
