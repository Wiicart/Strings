package com.pedestriamc.strings.placeholder;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.channel.DefaultChannel;
import com.pedestriamc.strings.user.util.UserUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * PlaceholderAPI expansion for Strings
 * Valid Placeholders: %strings_active_channel%, %strings_chat_color%
 */
public final class StringsPlaceholderExpansion extends PlaceholderExpansion {

    private final UserUtil userUtil;

    public StringsPlaceholderExpansion(Strings strings) {
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
        return Strings.VERSION;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        StringsUser user = getUser(player);
        return switch(params.toUpperCase(Locale.ROOT)) {
            case "CURRENT_CHANNEL" -> determineChannel(user, player);
            case "ACTIVE_CHANNEL" -> determineActive(user);
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

    private String determineActive(StringsUser user) {
        Channel c = user.getActiveChannel();
        if(c != null) {
            return c.getName();
        }
        return null;
    }

}
