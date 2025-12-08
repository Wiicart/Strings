package com.pedestriamc.strings.api;

import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.local.LocalityManager;
import com.pedestriamc.strings.api.command.Source;
import com.pedestriamc.strings.api.event.strings.EventDispatcher;
import com.pedestriamc.strings.api.files.FileManager;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.platform.EventFactory;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.text.EmojiManager;
import com.pedestriamc.strings.api.user.UserManager;
import org.jetbrains.annotations.NotNull;

import static org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface StringsPlatform {

    @NotNull PlatformAdapter getAdapter();

    @NotNull ChannelLoader getChannelLoader();

    @NotNull UserManager users();

    @NotNull Settings getSettings();

    @NotNull FileManager files();

    @NotNull Source serverSource();

    @NotNull EventDispatcher getEventDispatcher();

    @NotNull EventFactory getEventFactory();

    @NotNull EmojiManager getEmojiManager();

    @NotNull LocalityManager<?> getLocalityManager();

    @NotNull Messenger getMessenger();

    void async(@NotNull Runnable runnable);

    void info(@NotNull String message);

    void warning(@NotNull String message);

    boolean isUsingPlaceholderAPI();

}
