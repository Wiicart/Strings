package com.pedestriamc.strings.misc;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.configuration.ConfigurationOption;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ServerMessages {

    private final UserUtil userUtil;
    private final String joinMessageTemplate;
    private final String leaveMessageTemplate;
    private final ArrayList<String> motd;
    private final boolean usePAPI;

    public ServerMessages(@NotNull Strings strings) {
        userUtil = strings.getUserUtil();
        usePAPI = strings.usingPlaceholderAPI();

        Configuration config = strings.getConfigClass();
        joinMessageTemplate = config.getString(ConfigurationOption.JOIN_MESSAGE);
        leaveMessageTemplate = config.getString(ConfigurationOption.LEAVE_MESSAGE);
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
        User user = userUtil.getUser(player);
        if(usePAPI) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        message = applyPlaceholders(message, user);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String leaveMessage(Player player) {
        String message = leaveMessageTemplate;
        User user = userUtil.getUser(player);
        if(usePAPI) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        message = applyPlaceholders(message, user);
        return ChatColor.translateAlternateColorCodes('&',message);
    }

    public void sendMOTD(Player player) {
        ArrayList<String> playerMOTD = new ArrayList<>(motd);
        User user = userUtil.getUser(player);
        for(String message: playerMOTD) {
            message = applyPlaceholders(message, user);
            if(usePAPI) {
                message = PlaceholderAPI.setPlaceholders(player, message);
            }
            message = ChatColor.translateAlternateColorCodes('&', message);
            player.sendMessage(message);
        }
    }

    private @NotNull String applyPlaceholders(@NotNull String message, User user) {
        Player player = user.getPlayer();
        return message
                .replace("{displayname}", player.getDisplayName())
                .replace("{username}", player.getName())
                .replace("{prefix}", user.getPrefix())
                .replace("{suffix}", user.getSuffix());
    }
}
