package com.pedestriamc.strings.misc;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.integration.placeholderapi.PlaceholderAPISetter;
import com.pedestriamc.strings.manager.Configuration;
import com.pedestriamc.strings.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ServerMessages {

    private final PlaceholderAPISetter placeholderAPI;

    private final String joinMessageTemplate;
    private final String leaveMessageTemplate;
    private final String firstJoinMessageTemplate;
    private final List<String> motd;

    public ServerMessages(@NotNull Strings strings) {
        placeholderAPI = strings.placeholderAPI();

        Configuration config = strings.settings();
        joinMessageTemplate = config.get(Option.Text.JOIN_MESSAGE);
        leaveMessageTemplate = config.get(Option.Text.LEAVE_MESSAGE);
        firstJoinMessageTemplate = config.get(Option.Text.FIRST_JOIN_MESSAGE);
        motd = config.get(Option.StringList.MOTD);

    }

    @NotNull
    public String joinMessage(@NotNull StringsUser user) {
        return color(applyPlaceholders(joinMessageTemplate, user));
    }

    @NotNull
    public String leaveMessage(@NotNull StringsUser user) {
        return color(applyPlaceholders(leaveMessageTemplate, user));
    }

    @NotNull
    public String firstJoinMessage(@NotNull StringsUser user) {
        return color(applyPlaceholders(firstJoinMessageTemplate, user));
    }

    public void sendMOTD(@NotNull StringsUser user) {
        ArrayList<String> playerMOTD = new ArrayList<>(motd);
        for (String message: playerMOTD) {
            user.sendMessage(color(applyPlaceholders(message, user)));
        }
    }

    @NotNull
    private String applyPlaceholders(@NotNull String message, StringsUser user) {
        User bukkitUser = (User) user;
        Player player = bukkitUser.player();
        message = placeholderAPI.setPlaceholders(player, message);
        return message
                .replace("{displayname}", user.getDisplayName())
                .replace("{username}", user.getName())
                .replace("{prefix}", user.getPrefix())
                .replace("{suffix}", user.getSuffix());
    }

    private String color(@NotNull String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
