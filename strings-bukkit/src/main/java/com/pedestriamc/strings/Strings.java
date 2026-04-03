package com.pedestriamc.strings;

import com.pedestriamc.strings.common.CommonStrings;
import com.pedestriamc.strings.common.event.StringsEventManager;
import com.pedestriamc.strings.api.APIRegistrar;
import com.pedestriamc.strings.api.channel.data.BuildableRegistrar;
import com.pedestriamc.strings.api.channel.local.LocalityManager;
import com.pedestriamc.strings.api.event.StringsReloader;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.text.EmojiManager;
import com.pedestriamc.strings.common.chat.EmojiProvider;
import com.pedestriamc.strings.common.external.ModrinthService;
import com.pedestriamc.strings.common.manager.DirectMessageManager;
import com.pedestriamc.strings.bukkit.BukkitEventFactory;
import com.pedestriamc.strings.bukkit.BukkitPlatformAdapter;
import com.pedestriamc.strings.bukkit.ServerSource;
import com.pedestriamc.strings.bukkit.StringsBukkitEventManager;
import com.pedestriamc.strings.bukkit.locality.BukkitLocalityManager;
import com.pedestriamc.strings.placeholder.StringsPlaceholderExpansion;
import com.pedestriamc.strings.chat.ChannelManager;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.bukkit.Configuration;
import com.pedestriamc.strings.bukkit.StringsImpl;
import com.pedestriamc.strings.log.LogManager;
import com.pedestriamc.strings.bukkit.BukkitMessenger;
import com.pedestriamc.strings.manager.ClassRegistryManager;
import com.pedestriamc.strings.manager.BukkitFileManager;
import com.pedestriamc.strings.misc.AutoBroadcasts;
import com.pedestriamc.strings.misc.ServerMessages;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import com.pedestriamc.strings.user.util.YamlUserUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collection;
import java.util.UUID;

public final class Strings extends JavaPlugin implements CommonStrings {

    public static final String VERSION = "1.7.0";
    public static final short VERSION_NUM = 7;
    public static final int METRICS_ID = 22597;
    public static final String DISTRIBUTOR = "github";

    private boolean usingPlaceholderAPI = false;
    private boolean isPaper = false;
    private boolean usingVault;
    private boolean usingLuckPerms = false;

    private BukkitAudiences adventure;

    private BukkitFileManager fileManager;
    private UserUtil userUtil;
    private Chat chat = null;
    private ServerMessages serverMessages;
    private DirectMessageManager directMessageManager;
    private StringsImpl stringsImpl;
    private Mentioner mentioner;
    private UUID apiUUID;
    private BukkitMessenger messenger;
    private ChannelManager channelLoader;
    @SuppressWarnings("unused")
    private LogManager logManager;
    private Configuration configClass;
    private EmojiManager emojiManager;
    private ModrinthService modrinth;
    private ServerSource serverSource;
    private BukkitPlatformAdapter platformAdapter;
    private StringsBukkitEventManager eventDispatcher;
    private BukkitEventFactory eventFactory;
    private BukkitLocalityManager localityManager;

    public Strings() {
        super();
    }

    @Override
    public void onLoad() {
        info("Loading...");

        fileManager = new BukkitFileManager(this);
        info("FileManager loaded.");
        userUtil = new YamlUserUtil(this);
        info("UserUtil loaded.");

        BuildableRegistrar.register(this);
        determineEnvironment();
        instantiateObjects();
        registerAPI();
    }

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);
        serverSource = new ServerSource(this);
        logManager = new LogManager(this);
        this.setupVault();
        channelLoader.loadChannels();
        ClassRegistryManager.register(this);
        checkIfReload();
        checkForUpdate();
        instantiateObjectsTwo();
        registerPlaceholders();
        loadMetrics();
        info("Enabled");
    }

    @Override
    public void onDisable() {
        logOutAll();
        userUtil = null;
        serverMessages = null;
        directMessageManager = null;
        channelLoader = null;
        mentioner = null;
        logManager = null;
        fileManager = null;

        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
        stringsImpl = null;

        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }

        try {
            APIRegistrar.unregister(apiUUID);
        } catch(IllegalStateException | SecurityException ignored) {}

        info("Disabled");
    }

    public void reload() {
        onDisable();
        reloadConfig();
        onLoad();
        onEnable();
        eventDispatcher.dispatch(StringsReloader.createEvent());
    }

    private void loadMetrics() {
        Metrics metrics = new Metrics(this, METRICS_ID);
        metrics.addCustomChart(new SimplePie("distributor", () -> DISTRIBUTOR));
        metrics.addCustomChart(new SimplePie(
                "using_stringsapi",
                () -> String.valueOf(APIRegistrar.isAPIUsed()))
        );
        metrics.addCustomChart(new SimplePie(
                "using_stringsmoderation_expansion",
                () -> String.valueOf(getServer().getPluginManager().getPlugin("StringsModeration") != null))
        );
        metrics.addCustomChart(new SimplePie(
                "using_stringsdiscord",
                () -> String.valueOf(getServer().getPluginManager().getPlugin("StringsDiscord") != null)
        ));
    }

    private void logOutAll() {
        userUtil.getUsers().forEach(u -> ((User) u).logOff());
    }

    private void determineEnvironment() {
        if(getConfig().getBoolean("placeholder-api") && getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            info("PlaceholderAPI detected.");
            usingPlaceholderAPI = true;
        }

        if(getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            usingLuckPerms = true;
        }

        try {
            Class.forName("io.papermc.paper.event.player.AsyncChatEvent");
            isPaper = true;
        } catch(ClassNotFoundException ignored) {}
    }


    private void instantiateObjects() {
        configClass = new Configuration(this);
        eventDispatcher = new StringsBukkitEventManager(this);
        eventFactory = new BukkitEventFactory();
        localityManager = new BukkitLocalityManager(this);
        platformAdapter = new BukkitPlatformAdapter(this);
        messenger = new BukkitMessenger(fileManager.getMessagesFileConfig());
        directMessageManager = new DirectMessageManager(this);
        channelLoader = new ChannelManager(this);
        serverMessages = new ServerMessages(this);
        mentioner = new Mentioner(this);

        if (settings().get(Option.Bool.ENABLE_EMOJI_REPLACEMENT)) {
            emojiManager = new EmojiProvider(this);
        }
        if (settings().get(Option.Bool.ENABLE_EMOJI_RESOURCE_PACK)) {
            modrinth = new ModrinthService(this);
        }
    }

    /**
     * Instantiates classes with dependencies not yet available when {@link Strings#instantiateObjects()} is called.
     */
    private void instantiateObjectsTwo() {
        new AutoBroadcasts(this);
    }

    private void setupVault() {
        try {
            RegisteredServiceProvider<Chat> serviceProvider = getServer().getServicesManager().getRegistration(Chat.class);
            if(serviceProvider == null) {
                info("Vault not found, using built in methods.");
                usingVault = false;
            } else {
                chat = serviceProvider.getProvider();
                usingVault = true;
                info("Vault found.");
            }
        } catch(NoClassDefFoundError a) {
            warning("Vault not found, using built in methods.");
            usingVault = false;
        }
    }

    private void checkIfReload() {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        if(!players.isEmpty()) {
            for(Player p : players) {
                userUtil.loadUser(p.getUniqueId());
            }
        }
    }

    private void registerPlaceholders() {
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new StringsPlaceholderExpansion(this).register();
        }
    }

    private void checkForUpdate() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) URI.create("https://www.wiicart.net/strings/version.txt").toURL().openConnection();
            connection.setRequestMethod("GET");
            String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            short latest = Short.parseShort(raw);
            if(latest > VERSION_NUM) {
                getServer().getLogger().info("+------------[Strings]------------+");
                getServer().getLogger().info("|    A new update is available!   |");
                getServer().getLogger().info("|          Download at:           |");
                getServer().getLogger().info("|   https://wiicart.net/strings   |");
                getServer().getLogger().info("+---------------------------------+");
            }
        } catch(IOException a) {
            warning("Unable to check for updates.");
        }
    }

    private void registerAPI() {
        apiUUID = UUID.randomUUID();
        stringsImpl = new StringsImpl(this);
        try {
            APIRegistrar.register(stringsImpl, this, apiUUID);
        } catch(IllegalStateException a) {
            info("Failed to register StringsAPI");
        }
    }

    public void async(@NotNull Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    public void sync(@NotNull Runnable runnable) {
        getServer().getScheduler().runTask(this, runnable);
    }

    @NotNull
    public String applyPlaceholders(@NotNull String string, @NotNull Player player) {
        if (usingPlaceholderAPI) {
            try {
                return PlaceholderAPI.setPlaceholders(player, string);
            } catch(Exception ignored) {}
        }
        return string;
    }

    public @NotNull BukkitFileManager files() {
        return fileManager;
    }

    public @NotNull UserUtil users() {
        return userUtil;
    }

    public Chat getVaultChat() {
        return chat;
    }

    public boolean isUsingPlaceholderAPI() {
        return usingPlaceholderAPI;
    }

    public boolean isUsingVault() {
        return usingVault;
    }

    public boolean isUsingLuckPerms() {
        return usingLuckPerms;
    }

    public boolean isPaper() {
        return isPaper;
    }

    public @NotNull Mentioner getMentioner() {
        return mentioner;
    }

    public @NotNull ChannelManager getChannelLoader() {
        return channelLoader;
    }

    public @NotNull ServerMessages getServerMessages() {
        return serverMessages;
    }

    public @NotNull DirectMessageManager getDirectMessageManager() {
        return directMessageManager;
    }

    public @NotNull BukkitMessenger messenger() {
        return messenger;
    }

    @Override
    @NotNull
    public EmojiManager emojiManager() {
        if (emojiManager == null) {
            emojiManager = new EmojiProvider(this);
        }
        return emojiManager;
    }

    @Override
    @NotNull
    public LocalityManager<World> localityManager() {
        return localityManager;
    }

    @Nullable
    public ModrinthService modrinth() {
        return modrinth;
    }

    public @NotNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    @NotNull
    public Configuration settings() {
        return configClass;
    }

    @Override
    public @NotNull ServerSource serverSource() {
        return serverSource;
    }

    @Override
    public @NotNull BukkitPlatformAdapter getAdapter() {
        return platformAdapter;
    }

    @NotNull
    @Override
    public StringsEventManager eventManager() {
        return eventDispatcher;
    }

    @NotNull
    @Override
    public BukkitEventFactory eventFactory() {
        return eventFactory;
    }

    @Override
    public void info(@NotNull String message) {
        getLogger().info(message);
    }

    @SuppressWarnings("unused")
    public void info(@NotNull Object message) {
        info(message.toString());
    }

    @Override
    public void warning(@NotNull String message) {
        getLogger().warning(message);
    }

    @SuppressWarnings("unused")
    public void warning(@NotNull Object message) {
        warning(message.toString());
    }

}
