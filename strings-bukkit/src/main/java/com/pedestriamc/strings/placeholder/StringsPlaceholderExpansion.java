package com.pedestriamc.strings.placeholder;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.user.util.UserUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * PlaceholderAPI expansion for Strings
 * Valid Placeholders: {@code %strings_active_channel%}, {@code %strings_chat_color%}
 */
public final class StringsPlaceholderExpansion extends PlaceholderExpansion {

    private final UserUtil userUtil;

    public StringsPlaceholderExpansion(@NotNull Strings strings) {
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
        StringsUser user = userUtil.getUser(player);
        try {
            return switch(params.toUpperCase(Locale.ROOT)) {
                case "CURRENT_CHANNEL" -> user.getActiveChannel().resolve(player).getName();
                case "ACTIVE_CHANNEL" -> user.getActiveChannel().getName();
                case "CHAT_COLOR" -> user.getChatColorComponent().toString();
                default -> null;
            };
        } catch(Exception e) {
            return null;
        }
    }
}
