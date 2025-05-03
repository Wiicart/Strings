package com.pedestriamc.strings;

import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.event.StringsReloadEvent;
import com.pedestriamc.strings.placeholder.StringsPlaceholderExpansion;
import com.pedestriamc.strings.chat.ChannelManager;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import com.pedestriamc.strings.impl.StringsImpl;
import com.pedestriamc.strings.log.LogManager;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.manager.ClassRegistryManager;
import com.pedestriamc.strings.manager.FileManager;
import com.pedestriamc.strings.misc.AutoBroadcasts;
import com.pedestriamc.strings.misc.ServerMessages;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserUtil;
import com.pedestriamc.strings.user.YamlUserUtil;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Logger;

public final class Strings extends JavaPlugin {

    private final Logger logger = Bukkit.getLogger();

    public static final int METRICS_ID = 22597;
    public static final String VERSION = "1.5.1";
    public static final String DISTRIBUTOR = "github";
    public static final short PLUGIN_NUM = 5;

    @SuppressWarnings("unused")
    private AutoBroadcasts autoBroadcasts;

    private boolean usingPlaceholderAPI = false;
    private boolean usingVault;
    private boolean isPaper = false;

    private FileManager fileManager;
    private UserUtil userUtil;
    private Chat chat = null;
    private ServerMessages serverMessages;
    private PlayerDirectMessenger playerDirectMessenger;
    private StringsImpl stringsImpl;
    private Mentioner mentioner;
    private UUID apiUUID;
    private Messenger messenger;
    private ChannelManager channelLoader;
    private LogManager logManager;
    private Configuration configClass;

    @Override
    public void onLoad() {
        logger.info("[Strings] Loading...");
        fileManager = new FileManager(this);
        checkEnvironment();
        instantiateObjects();
        setupAPI();
    }

    @Override
    public void onEnable() {
        logManager = new LogManager(this);
        this.setupVault();
        channelLoader.loadChannels();
        ClassRegistryManager.register(this);
        checkIfReload();
        checkForUpdate();
        instantiateObjectsTwo();
        registerPlaceholders();
        loadMetrics();
        logger.info("[Strings] Enabled");
    }

    @Override
    public void onDisable() {
        logOutAll();
        this.userUtil = null;
        this.serverMessages = null;
        this.playerDirectMessenger = null;
        this.channelLoader = null;
        this.autoBroadcasts = null;
        this.mentioner = null;
        this.logManager = null;
        this.fileManager = null;

        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
        getServer().getServicesManager().unregister(StringsAPI.class, stringsImpl);

        try {
            StringsProvider.unregister(apiUUID);
        } catch(IllegalStateException | SecurityException ignored) {}
        this.stringsImpl = null;
        logger.info("[Strings] Disabled");
    }

    public void reload() {
        onDisable();
        onLoad();
        onEnable();
        getServer().getPluginManager().callEvent(new StringsReloadEvent());
    }

    private void loadMetrics() {
        Metrics metrics = new Metrics(this, METRICS_ID);
        metrics.addCustomChart(new SimplePie("distributor", this::getDistributor));
        metrics.addCustomChart(new SimplePie("using_stringsapi", this::isAPIUsed));
        metrics.addCustomChart(new SimplePie("using_stringsmoderation_expansion", this::isUsingStringsModeration));
    }

    private void logOutAll() {
        userUtil.getUsers().forEach(User::logOff);
    }

    private void checkEnvironment() {
        if(getConfig().getBoolean("placeholder-api") && getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("PlaceholderAPI detected.");
            usingPlaceholderAPI = true;
        } try {
            Class.forName("com.destroystokyo.paper.util.VersionFetcher");
            isPaper = true;
        } catch(ClassNotFoundException ignored) {}
    }


    private void instantiateObjects() {
        userUtil = new YamlUserUtil(this);
        configClass = new Configuration(getConfig());
        messenger = new Messenger(fileManager.getMessagesFileConfig());
        playerDirectMessenger = new PlayerDirectMessenger(this);
        channelLoader = new ChannelManager(this);
        serverMessages = new ServerMessages(this);
        mentioner = new Mentioner(this);
    }

    private void instantiateObjectsTwo() {
        autoBroadcasts = new AutoBroadcasts(this);
    }

    private void setupVault() {
        try {
            RegisteredServiceProvider<Chat> serviceProvider = getServer().getServicesManager().getRegistration(Chat.class);
            if(serviceProvider == null) {
                getLogger().info("Vault not found, using built in methods.");
                usingVault = false;
            } else {
                chat = serviceProvider.getProvider();
                usingVault = true;
                getLogger().info("Vault found.");
            }
        } catch(NoClassDefFoundError a) {
            getLogger().info("Vault not found, using built in methods.");
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
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://www.wiicart.net/strings/version.txt").openConnection();
            connection.setRequestMethod("GET");
            String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            short latest = Short.parseShort(raw);
            if(latest > PLUGIN_NUM) {
                Bukkit.getLogger().info("+------------[Strings]------------+");
                Bukkit.getLogger().info("|    A new update is available!   |");
                Bukkit.getLogger().info("|          Download at:           |");
                Bukkit.getLogger().info("|   https://wiicart.net/strings   |");
                Bukkit.getLogger().info("+---------------------------------+");
            }
        } catch(IOException a) {
            Bukkit.getLogger().info("[Strings] Unable to check for updates.");
        }
    }

    private void setupAPI() {
        apiUUID = UUID.randomUUID();
        stringsImpl = new StringsImpl(this);
        try {
            StringsProvider.register(stringsImpl, this, apiUUID);
        } catch(IllegalStateException a) {
            Bukkit.getLogger().info("Failed to register StringsAPI");
        }
    }

    public void async(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    public FileManager files() {
        return fileManager;
    }

    public ServerMessages getServerMessages() {
        return serverMessages;
    }

    public PlayerDirectMessenger getPlayerDirectMessenger() {
        return playerDirectMessenger;
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public Chat getVaultChat() {
        return chat;
    }

    public boolean usingPlaceholderAPI() {
        return usingPlaceholderAPI;
    }

    public boolean usingVault() {
        return usingVault;
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

    public UserUtil getUserUtil() {
        return userUtil;
    }

    @SuppressWarnings("unused")
    public LogManager getLogManager() {
        return logManager;
    }

    public Configuration getConfigClass() {
        return configClass;
    }

    public String getVersion() {
        return VERSION;
    }

    public short getPluginNum() {
        return PLUGIN_NUM;
    }

    public String getDistributor() {
        return DISTRIBUTOR;
    }

    public @NotNull String isAPIUsed() {
        return String.valueOf(StringsProvider.isInvoked());
    }

    public @NotNull String isUsingStringsModeration() {
        boolean using = getServer().getPluginManager().getPlugin("StringsModeration") != null;
        return String.valueOf(using);
    }
}
