package com.pedestriamc.strings.misc;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.user.User;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ServerMessages {

    private final Strings strings;
    private final String joinMessageTemplate;
    private final String leaveMessageTemplate;
    private final boolean enableJoinLeaveMessage;
    private final ArrayList<String> motd;
    private final boolean usePAPI;

    public ServerMessages(@NotNull Strings strings) {
        this.strings = strings;
        this.joinMessageTemplate = strings.getConfig().getString("join-message");
        this.leaveMessageTemplate = strings.getConfig().getString("leave-message");
        this.enableJoinLeaveMessage = strings.getConfig().getBoolean("enable-join-leave-messages");
        this.usePAPI = strings.usePlaceholderAPI();
        this.motd = new ArrayList<>();
        List<?> list = strings.getConfig().getList("motd");
        if(list != null){
            for(Object obj : list){
                if(obj instanceof String){
                    motd.add((String) obj);
                }
            }
        }
    }

    public String joinMessage(Player player) {
        String message = joinMessageTemplate;
        User user = strings.getUser(player);
        if(usePAPI) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        message = message.replace("{displayname}", player.getName());
        message = message.replace("{username}", player.getName());
        message = message.replace("{prefix}", user.getPrefix());
        message = message.replace("{suffix}", user.getSuffix());
        return ChatColor.translateAlternateColorCodes('&',message);
    }

    public String leaveMessage(Player player) {
        String message = leaveMessageTemplate;
        User user = strings.getUser(player);
        if(usePAPI) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        message = message.replace("{displayname}", player.getName());
        message = message.replace("{username}", player.getName());
        message = message.replace("{prefix}", user.getPrefix());
        message = message.replace("{suffix}", user.getSuffix());
        return ChatColor.translateAlternateColorCodes('&',message);
    }

    public void sendMOTD(Player player) {
        ArrayList<String> playerMOTD = new ArrayList<>(motd);
        User user = strings.getUser(player);
        for(String message: playerMOTD) {
            message = message.replace("{displayname}", player.getName());
            message = message.replace("{username}", player.getName());
            message = message.replace("{prefix}", user.getPrefix());
            message = message.replace("{suffix}", user.getSuffix());
            if(usePAPI) {
                message = PlaceholderAPI.setPlaceholders(player, message);
            }
            message = ChatColor.translateAlternateColorCodes('&', message);
            player.sendMessage(message);
        }
    }
}
