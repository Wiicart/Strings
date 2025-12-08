package com.pedestriamc.strings.api.platform;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

@Internal
public interface PlatformAdapter {

    @NotNull
    @UnmodifiableView
    Collection<StringsUser> getOnlineUsers();

    String colorHex(@NotNull String input);

    // Only has effect on Bukkit based platforms
    String translateBukkitColor(@NotNull String input);

    // Only has effect on Bukkit based platforms
    String stripBukkitColor(@NotNull String input);

    String applyPlaceholders(@NotNull StringsUser source, @NotNull String input);

    // Ideally move over to common module, does not need to be platform-specific
    String processMentions(@NotNull StringsUser sender, @NotNull Channel channel, @NotNull String str);

                           // Only has effect on Bukkit based platforms
    void removePermission(@NotNull String... permission);

    // Only has effect on Bukkit based platforms
    void addPermission(@NotNull String... permission);

    // Prints to console w/o plugin prefix
    void print(@NotNull String message);

}
