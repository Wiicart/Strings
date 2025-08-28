package com.pedestriamc.strings.misc;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
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
    private final List<String> motd;
    private final boolean usePAPI;

    public ServerMessages(@NotNull Strings strings) {
        userUtil = strings.users();
        usePAPI = strings.isUsingPlaceholderAPI();

        Configuration config = strings.getConfiguration();
        joinMessageTemplate = config.get(Option.Text.JOIN_MESSAGE);
        leaveMessageTemplate = config.get(Option.Text.LEAVE_MESSAGE);
        motd = config.get(Option.StringList.MOTD);
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
        Player player = user.player();
        return message
                .replace("{displayname}", player.getDisplayName())
                .replace("{username}", player.getName())
                .replace("{prefix}", user.getPrefix())
                .replace("{suffix}", user.getSuffix());
    }
}
