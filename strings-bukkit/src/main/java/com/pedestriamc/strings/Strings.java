package com.pedestriamc.strings;

import com.pedestriamc.strings.api.APIRegistrar;
import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.data.BuildableRegistrar;
import com.pedestriamc.strings.api.event.StringsReloader;
import com.pedestriamc.strings.placeholder.StringsPlaceholderExpansion;
import com.pedestriamc.strings.chat.ChannelManager;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import com.pedestriamc.strings.impl.StringsImpl;
import com.pedestriamc.strings.log.LogManager;
import com.pedestriamc.strings.impl.BukkitMessenger;
import com.pedestriamc.strings.manager.ClassRegistryManager;
import com.pedestriamc.strings.manager.FileManager;
import com.pedestriamc.strings.misc.AutoBroadcasts;
import com.pedestriamc.strings.misc.ServerMessages;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import com.pedestriamc.strings.user.util.YamlUserUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collection;
import java.util.UUID;

public final class Strings extends JavaPlugin implements StringsPlatform {

    public static final String VERSION = "1.7";
    public static final short VERSION_NUM = 7;
    public static final int METRICS_ID = 22597;
    public static final String DISTRIBUTOR = "github";

    private boolean usingPlaceholderAPI = false;
    private boolean isPaper = false;
    private boolean usingVault;
    private boolean usingLuckPerms = false;

    private BukkitAudiences adventure;

    private FileManager fileManager;
    private UserUtil userUtil;
    private Chat chat = null;
    private ServerMessages serverMessages;
    private PlayerDirectMessenger playerDirectMessenger;
    private StringsImpl stringsImpl;
    private Mentioner mentioner;
    private UUID apiUUID;
    private BukkitMessenger messenger;
    private ChannelManager channelLoader;
    @SuppressWarnings("unused")
    private LogManager logManager;
    private Configuration configClass;

    public Strings() {
        super();
    }

    @Override
    public void onLoad() {
        info("Loading...");

        fileManager = new FileManager(this);
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
        playerDirectMessenger = null;
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
        getServer().getPluginManager().callEvent(StringsReloader.createEvent());
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
    }

    private void logOutAll() {
        userUtil.getUsers().forEach(User::logOff);
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
        configClass = new Configuration(getConfig());
        messenger = new BukkitMessenger(fileManager.getMessagesFileConfig());
        playerDirectMessenger = new PlayerDirectMessenger(this);
        channelLoader = new ChannelManager(this);
        serverMessages = new ServerMessages(this);
        mentioner = new Mentioner(this);
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

    public void async(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    public @NotNull FileManager files() {
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

    public Mentioner getMentioner() {
        return mentioner;
    }

    public ChannelManager getChannelLoader() {
        return channelLoader;
    }

    public ServerMessages getServerMessages() {
        return serverMessages;
    }

    public PlayerDirectMessenger getPlayerDirectMessenger() {
        return playerDirectMessenger;
    }

    public BukkitMessenger getMessenger() {
        return messenger;
    }

    public @NotNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public Configuration getConfiguration() {
        return configClass;
    }

    public void info(@NotNull String message) {
        getLogger().info(message);
    }

    @SuppressWarnings("unused")
    public void info(@NotNull Object message) {
        info(message.toString());
    }

    public void warning(@NotNull String message) {
        getLogger().warning(message);
    }

    @SuppressWarnings("unused")
    public void warning(@NotNull Object message) {
        warning(message.toString());
    }

}
