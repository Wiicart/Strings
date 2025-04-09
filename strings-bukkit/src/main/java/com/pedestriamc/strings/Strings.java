package com.pedestriamc.strings;

import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.chat.*;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import com.pedestriamc.strings.impl.StringsImpl;
import com.pedestriamc.strings.log.LogManager;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.managers.ClassRegistryManager;
import com.pedestriamc.strings.misc.AutoBroadcasts;
import com.pedestriamc.strings.misc.ServerMessages;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserUtil;
import com.pedestriamc.strings.user.YamlUserUtil;
import com.tchristofferson.configupdater.ConfigUpdater;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Logger;

public final class Strings extends JavaPlugin {

    private final Logger logger = Bukkit.getLogger();

    private static Strings instance;

    private static final int METRICS_ID = 22597;
    private static final String VERSION = "1.5";
    private static final String DISTRIBUTOR = "modrinth";
    private static final short PLUGIN_NUM = 5;

    @SuppressWarnings("unused")
    private AutoBroadcasts autoBroadcasts;

    private boolean usingPlaceholderAPI = false;
    private boolean usingVault;
    private boolean isPaper = false;

    private File broadcastsFile;
    private File messagesFile;
    private File usersFile;
    private File channelsFile;

    private final FileConfiguration config = this.getConfig();
    private FileConfiguration logsFileConfig;
    private FileConfiguration broadcastsFileConfig;
    private FileConfiguration messagesFileConfig;
    private FileConfiguration usersFileConfig;
    private FileConfiguration channelsFileConfig;

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
    private YamlConfiguration moderationFileConfig;
    private Configuration configClass;

    @Override
    public void onLoad() {
        instance = this;
        logger.info("[Strings] Loading...");
        saveDefaultConfig();
        setupCustomConfigs();
        updateConfigs();
        loadConfigOptions();
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
        checkUpdate();
        instantiateObjectsTwo();
        loadMetrics();
        logger.info("[Strings] Enabled!");
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

        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
        getServer().getServicesManager().unregister(StringsAPI.class, stringsImpl);

        try {
            StringsProvider.unregister(apiUUID);
        } catch(IllegalStateException | SecurityException ignored) {}
        this.stringsImpl = null;

        instance = null;
        logger.info("[Strings] Disabled");
    }

    private void loadMetrics() {
        Metrics metrics = new Metrics(this, METRICS_ID);
        metrics.addCustomChart(new SimplePie("distributor", this::getDistributor));
        metrics.addCustomChart(new SimplePie("using_stringsapi", this::isAPIUsed));
        metrics.addCustomChart(new SimplePie("using_stringsmoderation_expansion", this::isUsingStringsModeration));
    }

    private void logOutAll() {
        for(User user : userUtil.getUsers()) {
            user.logOff();
        }
    }

    //Load options from the config
    private void loadConfigOptions() {
        if(config.getBoolean("placeholder-api") && getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.usingPlaceholderAPI = true;
        } try {
            Class.forName("com.destroystokyo.paper.util.VersionFetcher");
            isPaper = true;
        } catch(ClassNotFoundException ignored) {}
    }

    /**
     * Updates YML files
     * <a href="https://github.com/tchristofferson/Config-Updater">...</a>
     */
    @SuppressWarnings("CallToPrintStackTrace")
    private void updateConfigs() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        File messageFile = new File(this.getDataFolder(), "messages.yml");
        File moderationFile = new File(getDataFolder(), "moderation.yml");
        if(configFile.exists()) {
            try {
                ConfigUpdater.update(this,"config.yml", configFile);
            } catch(IOException e) {
                Bukkit.getLogger().info("[Strings] Updating config file failed.");
                e.printStackTrace();
            }
        }

        if(messageFile.exists()) {
            try {
                ConfigUpdater.update(this,"messages.yml", messageFile);
            } catch(IOException e) {
                Bukkit.getLogger().info("[Strings] Updating messages file failed.");
                e.printStackTrace();
            }
        }

        if(moderationFile.exists()) {
            try {
                ConfigUpdater.update(this,"moderation.yml", moderationFile);
            } catch(IOException e) {
                Bukkit.getLogger().info("[Strings] Updating moderation file failed.");
                e.printStackTrace();
            }
        }
    }

    private void instantiateObjects() {
        userUtil = new YamlUserUtil(this);
        configClass = new Configuration(getConfig());
        messenger = new Messenger(getMessagesFileConfig());
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void setupCustomConfigs() {
        broadcastsFile = new File(getDataFolder(), "broadcasts.yml");
        messagesFile = new File(getDataFolder(), "messages.yml");
        usersFile = new File(getDataFolder(), "users.yml");
        channelsFile = new File(getDataFolder(), "channels.yml");
        File logsFile = new File(getDataFolder(), "logs.yml");
        File moderationFile = new File(getDataFolder(), "moderation.yml");

        if(!broadcastsFile.exists()) {
            broadcastsFile.getParentFile().mkdirs();
            saveResource("broadcasts.yml", false);
        }

        if(!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        if(!usersFile.exists()) {
            usersFile.getParentFile().mkdirs();
            saveResource("users.yml", false);
        }

        if(!channelsFile.exists()) {
            channelsFile.getParentFile().mkdirs();
            saveResource("channels.yml", false);
        }

        if(!logsFile.exists()) {
            logsFile.getParentFile().mkdirs();
            saveResource("logs.yml", false);
        }

        if(!moderationFile.exists()) {
            moderationFile.getParentFile().mkdirs();
            saveResource("moderation.yml", false);
        }


        broadcastsFileConfig = YamlConfiguration.loadConfiguration(broadcastsFile);
        messagesFileConfig = YamlConfiguration.loadConfiguration(messagesFile);
        usersFileConfig = YamlConfiguration.loadConfiguration(usersFile);
        channelsFileConfig = YamlConfiguration.loadConfiguration(channelsFile);
        logsFileConfig = YamlConfiguration.loadConfiguration(logsFile);
        moderationFileConfig = YamlConfiguration.loadConfiguration(moderationFile);
    }

    private void checkIfReload() {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        if(!players.isEmpty()) {
            for(Player p : players) {
                userUtil.loadUser(p.getUniqueId());
            }
        }
    }

    private void checkUpdate() {
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

    /**
     * Calculates tick equivalent of seconds or minutes. Example: 1m, 1s, etc.
     * @param time the time to be converted
     * @return a long of the tick value. Returns -1 if syntax is invalid.
     */
    public static long calculateTicks(String time) {
        String regex = "^[0-9]+[sm]$";

        if(time == null || !time.matches(regex)) {
            return -1L;
        }

        char units = time.charAt(time.length() - 1);
        time = time.substring(0, time.length() - 1);
        int delayNum = Integer.parseInt(time);

        if(units == 'm') {
            delayNum *= 60;
        }

        return delayNum * 20L;
    }

    public static void async(Runnable runnable) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(instance, runnable);
    }

    /*
    Public getter and setter methods
     */
    public String getDistributor() {
        return DISTRIBUTOR;
    }

    public String getVersion() {
        return VERSION;
    }

    public UserUtil getUserUtil() {
        return userUtil;
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

    public FileConfiguration getUsersFileConfig() {
        return usersFileConfig;
    }

    public FileConfiguration getBroadcastsFileConfig() {
        return broadcastsFileConfig;
    }

    public FileConfiguration getMessagesFileConfig() {
        return messagesFileConfig;
    }

    public FileConfiguration getChannelsFileConfig() {
        return channelsFileConfig;
    }

    public FileConfiguration getLogsFileConfig() {
        return logsFileConfig;
    }

    public boolean usePlaceholderAPI() {
        return usingPlaceholderAPI;
    }

    public boolean useVault() {
        return usingVault;
    }

    public boolean isPaper() {
        return isPaper;
    }

    public Mentioner getMentioner() {
        return mentioner;
    }

    public short getPluginNum() {
        return PLUGIN_NUM;
    }

    public ChannelManager getChannelLoader() {
        return channelLoader;
    }

    @SuppressWarnings("unused")
    public LogManager getLogManager() {
        return logManager;
    }

    public Configuration getConfigClass() {
        return configClass;
    }

    /*
    Other methods
     */

    private static final Object lock = new Object();

    public void saveUsersFile() {
        async(() -> {
            synchronized(lock) {
                try {
                    usersFileConfig.save(usersFile);
                } catch(Exception e) {
                    Bukkit.getLogger().info("An error occurred while saving the users file: " + e.getMessage());
                }
            }
        });
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public synchronized void saveChannelsFile() {
        try {
            channelsFileConfig.save(channelsFile);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unused", "CallToPrintStackTrace"})
    public void saveMessagesFile() {
        try {
            messagesFileConfig.save(messagesFile);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unused", "CallToPrintStackTrace"})
    public void saveBroadcastsFile() {
        try {
            broadcastsFileConfig.save(broadcastsFile);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        onDisable();
        onLoad();
        onEnable();
    }

    /**
     * Returns a User object that contains info Strings uses.
     * @param uuid The uuid of the player to get the User of.
     * @return User object of the player matching the UUID.
     */
    public User getUser(@NotNull UUID uuid) {
        return userUtil.getUser(uuid);
    }

    /**
     * Returns a User object that contains info Strings uses.
     * @param player the player to get the User of.
     * @return User object of the player.
     */
    public User getUser(@NotNull Player player) {
        return userUtil.getUser(player.getUniqueId());
    }

    public Channel getChannel(String channel) {
        return channelLoader.getChannel(channel);
    }

    public @NotNull String isAPIUsed() {
        return "" + stringsImpl.isApiUsed();
    }

    @SuppressWarnings("unused")
    public YamlConfiguration getModerationFileConfig() {
        return moderationFileConfig;
    }

    public String isUsingStringsModeration() {
        boolean using = getServer().getPluginManager().getPlugin("StringsModeration") != null;
        return String.valueOf(using);
    }
}
