package com.pedestriamc.strings.api;

import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.user.UserManager;
import org.jetbrains.annotations.NotNull;

import static org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface StringsPlatform {

    @NotNull ChannelLoader getChannelLoader();

    @NotNull UserManager users();

    @NotNull Settings getSettings();

    void info(@NotNull String message);

    void warning(@NotNull String message);

}
