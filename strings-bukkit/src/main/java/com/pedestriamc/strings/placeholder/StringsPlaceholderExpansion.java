package com.pedestriamc.strings.placeholder;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.channel.DefaultChannel;
import com.pedestriamc.strings.user.UserUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PlaceholderAPI expansion for Strings
 * Valid Placeholders: %strings_active_channel%, %strings_chat_color%
 */
public final class StringsPlaceholderExpansion extends PlaceholderExpansion {

    private final String version;
    private final UserUtil userUtil;

    public StringsPlaceholderExpansion(Strings strings) {
        version = strings.getDescription().getVersion();
        userUtil = strings.getUserUtil();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "strings";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "wiicart";
    }

    @Override
    public @NotNull String getVersion() {
        return version;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        StringsUser user = getUser(player);
        return switch(params.toUpperCase()) {
            case "ACTIVE_CHANNEL" -> determineChannel(user, player);
            case "CHAT_COLOR" -> user.getChatColor();
            default -> null;
        };
    }

    private @NotNull StringsUser getUser(Player player) {
        return userUtil.getUser(player);
    }

    private @Nullable String determineChannel(StringsUser user, Player player) {
        Channel c = user.getActiveChannel();
        if(c instanceof DefaultChannel defaultChannel) {
            c = defaultChannel.determineChannel(player);
        }
        if(c != null) {
            return c.getName();
        }

        return null;
    }

}
