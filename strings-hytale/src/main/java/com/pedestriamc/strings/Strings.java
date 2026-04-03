package com.pedestriamc.strings;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.util.Config;
import com.pedestriamc.strings.api.channel.local.LocalityManager;
import com.pedestriamc.strings.api.command.Source;
import com.pedestriamc.strings.api.event.strings.EventManager;
import com.pedestriamc.strings.api.files.FileManager;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.platform.EventFactory;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.text.EmojiManager;
import com.pedestriamc.strings.api.user.UserManager;
import com.pedestriamc.strings.chat.channel.ChannelManager;
import com.pedestriamc.strings.chat.channel.ChannelStore;
import com.pedestriamc.strings.common.CommonStrings;
import com.pedestriamc.strings.common.event.StringsEventManager;
import com.pedestriamc.strings.common.manager.DirectMessageManager;
import com.pedestriamc.strings.hytale.HytalePlatformAdapter;
import com.pedestriamc.strings.hytale.locality.HytaleLocalityManager;
import com.pedestriamc.strings.user.StringsUserManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class Strings extends JavaPlugin implements CommonStrings {

    private final Config<ChannelStore> channelStore = this.withConfig("Channels", ChannelStore.CODEC);

    private final LocalityManager<World> localityManager = new HytaleLocalityManager(this);
    private final EventManager eventManager = new StringsEventManager(this);
    private final StringsUserManager userManager;
    private final PlatformAdapter platformAdapter;

    private ChannelManager channelManager;
    private DirectMessageManager directMessageManager;

    public Strings(@NotNull JavaPluginInit init) {
        super(init);


        userManager = new StringsUserManager(this);
        platformAdapter = new HytalePlatformAdapter(this);
        //universe().getEventRegistry().register()
    }

    @Override
    public void setup() {
        channelManager = new ChannelManager(this);
        directMessageManager = new DirectMessageManager(this);
    }

    @Override
    public @NotNull DirectMessageManager getDirectMessageManager() {
        return directMessageManager;
    }

    @Override
    public @NotNull PlatformAdapter getAdapter() {
        return platformAdapter;
    }

    @Override
    public @NotNull ChannelManager getChannelLoader() {
        return channelManager;
    }

    @Override
    public @NotNull UserManager users() {
        return userManager;
    }

    @Override
    public @NotNull Settings settings() {
        return null;
    }

    @Override
    public @NotNull FileManager files() {
        return null;
    }

    @Override
    public @NotNull Source serverSource() {
        return null;
    }

    @Override
    public @NotNull EventManager eventManager() {
        return eventManager;
    }

    @Override
    public @NotNull EventFactory eventFactory() {
        return null;
    }

    @Override
    public @NotNull EmojiManager emojiManager() {
        return null;
    }

    @Override
    public @NotNull LocalityManager<World> localityManager() {
        return localityManager;
    }

    @Override
    public @NotNull Messenger messenger() {
        return null;
    }

    @Override
    public void async(@NotNull Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }

    @Override
    public void sync(@NotNull Runnable runnable) {
        runnable.run();
    }

    @Override
    public void info(@NotNull String message) {
        getLogger().atInfo().log(message);
    }

    @Override
    public void warning(@NotNull String message) {
        getLogger().atWarning().log(message);
    }

    @Override
    public boolean isUsingPlaceholderAPI() {
        return false;
    }

    @NotNull
    public Universe universe() {
        return Universe.get();
    }

    public ChannelStore channelStore() {
        return channelStore.get();
    }

    public void saveChannelStore() {
        channelStore.save();
    }
}