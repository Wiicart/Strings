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
    private final ArrayList<String> motd;
    private final boolean usePAPI;

    public ServerMessages(@NotNull Strings strings) {
        this.strings = strings;
        joinMessageTemplate = strings.getConfig().getString("join-message");
        leaveMessageTemplate = strings.getConfig().getString("leave-message");
        usePAPI = strings.usePlaceholderAPI();
        motd = new ArrayList<>();
        List<?> list = strings.getConfig().getList("motd");
        if(list != null) {
            for(Object obj : list) {
                if(obj instanceof String string) {
                    motd.add(string);
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
        message = applyPlaceholders(message, user);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String leaveMessage(Player player) {
        String message = leaveMessageTemplate;
        User user = strings.getUser(player);
        if(usePAPI) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        message = applyPlaceholders(message, user);
        return ChatColor.translateAlternateColorCodes('&',message);
    }

    public void sendMOTD(Player player) {
        ArrayList<String> playerMOTD = new ArrayList<>(motd);
        User user = strings.getUser(player);
        for(String message: playerMOTD) {
            message = applyPlaceholders(message, user);
            if(usePAPI) {
                message = PlaceholderAPI.setPlaceholders(player, message);
            }
            message = ChatColor.translateAlternateColorCodes('&', message);
            player.sendMessage(message);
        }
    }

    private String applyPlaceholders(@NotNull String message, User user) {
        Player player = user.getPlayer();
        return message
                .replace("{displayname}", player.getDisplayName())
                .replace("{username}", player.getName())
                .replace("{prefix}", user.getPrefix())
                .replace("{suffix}", user.getSuffix());
    }
}
