package com.pedestriamc.strings.common.mock.environment;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.local.LocalityManager;
import com.pedestriamc.strings.api.command.Source;
import com.pedestriamc.strings.api.event.strings.EventManager;
import com.pedestriamc.strings.api.files.FileManager;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.platform.EventFactory;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.text.EmojiManager;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.user.UserManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Environment implements StringsPlatform {

    public static EnvironmentBuilder builder() {
        return new EnvironmentBuilder();
    }

    private final UserManager userManager;
    private final PlatformAdapter platformAdapter;
    private final ChannelLoader channelLoader;
    private final Settings settings;
    private final FileManager fileManager;
    private final Source serverSource;
    private final EventManager eventManager;
    private final EventFactory eventFactory;
    private final EmojiManager emojiManager;
    private final LocalityManager<?> localityManager;
    private final Messenger messenger;

    Environment(EnvironmentBuilder builder) {
        settings = MockSettings.SETTINGS;

        userManager = mock(UserManager.class);
        when(userManager.getUsers()).thenReturn(builder.users);

        platformAdapter = mock(PlatformAdapter.class);
        channelLoader = mock(ChannelLoader.class);

        fileManager = mock(FileManager.class);
        serverSource = mock(Source.class);
        eventManager = mock(EventManager.class);
        eventFactory = mock(EventFactory.class);
        emojiManager = mock(EmojiManager.class);
        localityManager = mock(LocalityManager.class);
        messenger = mock(Messenger.class);
    }

    @Override
    public @NotNull PlatformAdapter getAdapter() {
        return platformAdapter;
    }

    @Override
    public @NotNull ChannelLoader getChannelLoader() {
        return channelLoader;
    }

    @Override
    public @NotNull UserManager users() {
        return userManager;
    }

    @Override
    public @NotNull Settings settings() {
        return settings;
    }

    @Override
    public @NotNull FileManager files() {
        return fileManager;
    }

    @Override
    public @NotNull Source serverSource() {
        return serverSource;
    }

    @Override
    public @NotNull EventManager eventManager() {
        return eventManager;
    }

    @Override
    public @NotNull EventFactory eventFactory() {
        return eventFactory;
    }

    @Override
    public @NotNull EmojiManager emojiManager() {
        return emojiManager;
    }

    @Override
    public @NotNull LocalityManager<?> localityManager() {
        return localityManager;
    }

    @Override
    public @NotNull Messenger messenger() {
        return messenger;
    }

    @Override
    public void async(@NotNull Runnable runnable) {
        runnable.run();
    }

    @Override
    public void sync(@NotNull Runnable runnable) {
        runnable.run();
    }

    @Override
    public void info(@NotNull String message) {
        System.out.println(message);
    }

    @Override
    public void warning(@NotNull String message) {
        System.out.println(message);
    }

    @Override
    public boolean isUsingPlaceholderAPI() {
        return false;
    }

    public static class EnvironmentBuilder {

        Set<StringsUser> users = new HashSet<>();

        public Environment build() {
            return new Environment(this);
        }

        public EnvironmentBuilder withUsers(StringsUser... users) {
            this.users = new HashSet<>(List.of(users));
            return this;
        }
    }
}