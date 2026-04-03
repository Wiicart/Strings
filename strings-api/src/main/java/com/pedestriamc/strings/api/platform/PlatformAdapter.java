package com.pedestriamc.strings.api.platform;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;

@Internal
public interface PlatformAdapter {

    @NotNull
    @UnmodifiableView
    Collection<StringsUser> getOnlineUsers();

    @NotNull
    @UnmodifiableView
    Collection<StringsUser> getOperators();

    @Nullable
    default StringsUser getUser(@NotNull String name) {
        return getOnlineUsers().stream()
                .filter(user -> user.getName().equalsIgnoreCase(name))
                .toList()
                .getFirst();
    }

    String colorHex(@NotNull String input);

    // Only has effect on Bukkit based platforms
    String translateBukkitColor(@NotNull String input);

    // Only has effect on Bukkit based platforms
    String stripBukkitColor(@NotNull String input);

    String applyPlaceholders(@NotNull StringsUser source, @NotNull String input);

    // Ideally move over to common module, does not need to be platform-specific
    String processMentions(@NotNull StringsUser sender, @NotNull Channel channel, @NotNull String str);

    String setPlaceholders(@NotNull StringsUser user, @NotNull String input);

    // First checks for Bukkit/Bungee color codes if on Bukkit derivatives, then resorts to HEX.
    @Nullable
    TextColor parseColor(@NotNull String input);

    // Only has effect on Bukkit based platforms
    void removePermission(@NotNull String... permission);

    // Only has effect on Bukkit based platforms
    void addPermission(@NotNull String... permission);

    // Prints to console w/o plugin prefix
    void print(@NotNull String message);

    default void print(@NotNull Object object) {
        this.print(object.toString());
    }

    boolean isOnline(@NotNull UUID uuid);

    boolean isOnline(@NotNull StringsUser user);

}
