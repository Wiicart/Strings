package com.pedestriamc.strings.api;

import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.local.LocalityManager;
import com.pedestriamc.strings.api.command.Source;
import com.pedestriamc.strings.api.event.strings.EventManager;
import com.pedestriamc.strings.api.files.FileManager;
import com.pedestriamc.strings.api.managers.Mentioner;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.platform.EventFactory;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.text.EmojiManager;
import com.pedestriamc.strings.api.text.StringsAudienceProvider;
import com.pedestriamc.strings.api.user.UserManager;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface StringsPlatform {

    @NotNull PlatformAdapter getAdapter();

    @NotNull ChannelLoader getChannelLoader();

    @NotNull UserManager users();

    @NotNull Settings settings();

    @NotNull FileManager files();

    @NotNull Source serverSource();

    @NotNull EventManager eventManager();

    @NotNull EventFactory eventFactory();

    @NotNull EmojiManager emojiManager();

    @NotNull LocalityManager<?> localityManager();

    @NotNull Messenger messenger();

    @NotNull Mentioner mentioner();

    @NotNull StringsAudienceProvider audiences();

    void async(@NotNull Runnable runnable);

    void sync(@NotNull Runnable runnable);

    /** Executes work on the entity context of a user where the platform has one. */
    default void sync(@NotNull com.pedestriamc.strings.api.user.StringsUser user,
                      @NotNull Runnable runnable) {
        sync(runnable);
    }

    /** Sends an action bar through the platform-supported player API. */
    default void sendActionBar(@NotNull com.pedestriamc.strings.api.user.StringsUser user,
                               @NotNull Component message) {
        sync(user, () -> user.audience().sendActionBar(message));
    }

    void info(@NotNull String message);

    void warning(@NotNull String message);

    boolean isUsingPlaceholderAPI();

    boolean isPaper();

}
