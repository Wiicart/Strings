package com.pedestriamc.strings;

import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.channels.ChannelLoader;
import com.pedestriamc.strings.chat.*;
import com.pedestriamc.strings.api.channels.Channel;
import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import com.pedestriamc.strings.impl.StringsImpl;
import com.pedestriamc.strings.listeners.*;
import com.pedestriamc.strings.log.LogManager;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.misc.AutoBroadcasts;
import com.pedestriamc.strings.misc.ServerMessages;
import com.pedestriamc.strings.tabcompleters.*;
import com.pedestriamc.strings.commands.BroadcastCommand;
import com.pedestriamc.strings.commands.ClearChatCommand;
import com.pedestriamc.strings.commands.*;
import com.pedestriamc.strings.user.User;
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
import java.util.UUID;
import java.util.logging.Logger;

public final class Strings extends JavaPlugin {

    @SuppressWarnings("FieldCanBeLocal")
    private final String version = "1.5";
    @SuppressWarnings("FieldCanBeLocal")
    private final String distributor = "github";
    @SuppressWarnings("FieldCanBeLocal")
    private final short pluginNum = 5;

    @SuppressWarnings("unused")
    private AutoBroadcasts autoBroadcasts;

    private static Strings instance;
    private final Logger logger = Bukkit.getLogger();
    private static Chat chat = null;
    private String coolDownLength;
    private final FileConfiguration config = this.getConfig();
    private ServerMessages serverMessages;
    private PlayerDirectMessenger playerDirectMessenger;
    private File broadcastsFile;
    private File messagesFile;
    private File usersFile;
    private File channelsFile;
    private FileConfiguration logsFileConfig;
    private FileConfiguration broadcastsFileConfig;
    private FileConfiguration messagesFileConfig;
    private FileConfiguration usersFileConfig;
    private FileConfiguration channelsFileConfig;
    private ChatManager chatManager;
    private boolean usingPlaceholderAPI = false;
    private boolean processPlayerMessageColors;
    private boolean processPlayerMessagePlaceholders;
    private boolean usingVault;
    private boolean isPaper = false;
    private StringsImpl stringsImpl;
    private Mentioner mentioner;
    private UUID apiUUID;
    private Messenger messenger;
    private ChannelLoader channelLoader;
    private LogManager logManager;
    private YamlConfiguration moderationFileConfig;

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
        this.registerClasses();
        this.setupVault();
        int pluginId = 22597;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SimplePie("distributor", this::getDistributor));
        metrics.addCustomChart(new SimplePie("using_stringsapi", this::isAPIUsed));
        metrics.addCustomChart(new SimplePie("using_stringsmoderation_expansion", this::isUsingStringsModeration));
        checkIfReload();
        checkUpdate();
        instantiateObjectsTwo();
        logger.info("[Strings] Enabled!");
    }

    @Override
    public void onDisable() {
        for(User user : YamlUserUtil.UserMap.getUserSet()){
            user.logOff();
        }
        YamlUserUtil.UserMap.clear();
        this.serverMessages = null;
        this.playerDirectMessenger = null;
        this.chatManager = null;
        this.channelLoader = null;
        this.autoBroadcasts = null;
        this.mentioner = null;
        this.logManager = null;
        HandlerList.unregisterAll(this);
        this.getServer().getScheduler().cancelTasks(this);
        this.getServer().getServicesManager().unregister(StringsAPI.class, stringsImpl);
        try {
            StringsProvider.unregister(apiUUID);
        } catch(IllegalStateException | SecurityException ignored) {}
        this.stringsImpl = null;
        logger.info("[Strings] Disabled");
    }

    /*
    Private methods
     */
    //Register commands and listeners

    @SuppressWarnings("ConstantConditions")
    private void registerClasses(){
        this.getCommand("strings").setExecutor(new StringsCommand(this));
        this.getCommand("broadcast").setExecutor(new BroadcastCommand(this));
        this.getCommand("announce").setExecutor(new BroadcastCommand(this));
        this.getCommand("clearchat").setExecutor(new ClearChatCommand(this));
        this.getCommand("chatclear").setExecutor(new ClearChatCommand(this));
        this.getCommand("clearchat").setTabCompleter(new ClearChatTabCompleter());
        this.getCommand("chatclear").setTabCompleter(new ClearChatTabCompleter());
        this.getCommand("socialspy").setExecutor(new SocialSpyCommand(this));
        this.getCommand("socialspy").setTabCompleter(new SocialSpyTabCompleter());
        this.getCommand("msg").setExecutor(new DirectMessageCommand(this));
        this.getCommand("message").setExecutor(new DirectMessageCommand(this));
        this.getCommand("msg").setTabCompleter(new MessageTabCompleter());
        this.getCommand("message").setTabCompleter(new MessageTabCompleter());
        this.getCommand("reply").setExecutor(new ReplyCommand(this));
        this.getCommand("r").setExecutor(new ReplyCommand(this));
        this.getCommand("channel").setExecutor(new ChannelCommand(this));
        this.getCommand("c").setExecutor(new ChannelCommand(this));
        this.getCommand("channel").setTabCompleter(new ChannelTabCompleter(this));
        this.getCommand("c").setTabCompleter(new ChannelTabCompleter(this));
        this.getCommand("mention").setExecutor(new MentionCommand(this));
        this.getCommand("mentions").setExecutor(new MentionCommand(this));
        this.getCommand("mention").setTabCompleter(new MentionCommandTabCompleter());
        this.getCommand("mentions").setTabCompleter(new MentionCommandTabCompleter());

        if(config.getBoolean("enable-helpop")) {
            this.getCommand("helpop").setExecutor(new HelpOPCommand(this));
        } else {

            if(!config.getBoolean("other-helpop")) {
                this.getCommand("helpop").setExecutor(new DisabledCommand(this, Message.HELPOP_DISABLED));
            }

            try {
                Channel helpOpChannel = channelLoader.getChannel("helpop");
                channelLoader.unregisterChannel(helpOpChannel);
            } catch(Exception ignored) {}

        }

        if(config.getBoolean("enable-chatcolor")){
            this.getCommand("chatcolor").setExecutor(new ChatColorCommand(this));
        }
        this.getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        this.getServer().getPluginManager().registerEvents(new JoinListener(this),this);
        this.getServer().getPluginManager().registerEvents(new LeaveListener(this), this);
        this.getServer().getPluginManager().registerEvents(new DirectMessageListener(this), this);
        if(config.getBoolean("enable-mentions")){
            this.getServer().getPluginManager().registerEvents(new MentionListener(this), this);
        }
    }

    //Load options from the config
    private void loadConfigOptions(){
        FileConfiguration config = this.getConfig();
        this.processPlayerMessageColors = config.getBoolean("process-in-chat-colors", true);
        this.processPlayerMessagePlaceholders = config.getBoolean("process-in-chat-placeholders", false);
        this.coolDownLength = config.getString("cooldown-time", "30s");
        if(config.getBoolean("placeholder-api") && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            this.usingPlaceholderAPI = true;
        }
        try{
            Class.forName("com.destroystokyo.paper.util.VersionFetcher");
            isPaper = true;
        } catch(ClassNotFoundException ignored) {
        }
    }
    //Update yml files
    private void updateConfigs(){
        //Config updater using https://github.com/tchristofferson/Config-Updater
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
            try{
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

    private void instantiateObjects(){
        messenger = new Messenger(getMessagesFileConfig());
        chatManager = new ChatManager(this);
        playerDirectMessenger = new PlayerDirectMessenger(this);
        channelLoader = new StringsChannelLoader(this);
        serverMessages = new ServerMessages(this);
        mentioner = new Mentioner(this);
    }

    private void instantiateObjectsTwo() {
        autoBroadcasts = new AutoBroadcasts(this);
    }

    private void setupVault() {
        try{
            RegisteredServiceProvider<Chat> serviceProvider = getServer().getServicesManager().getRegistration(Chat.class);
            if(serviceProvider == null){
                getLogger().info("Vault not found, using built in methods.");
                usingVault = false;
            }else{
                chat = serviceProvider.getProvider();
                usingVault = true;
                getLogger().info("Vault found.");
            }
        }catch(NoClassDefFoundError a){
            getLogger().info("Vault not found, using built in methods.");
            usingVault = false;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void setupCustomConfigs(){
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
        if(!Bukkit.getOnlinePlayers().isEmpty()){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(YamlUserUtil.loadUser(p.getUniqueId()) == null){
                    new User(p.getUniqueId());
                }
            }
        }
    }

    private void checkUpdate() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://www.wiicart.net/strings/version.txt").openConnection();
            connection.setRequestMethod("GET");
            String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            short latest = Short.parseShort(raw);
            if(latest > pluginNum) {
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
     * Calculates tick equivalent of seconds or minutes. Example: 1m, 1s, etc..
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

    /*
    Public getter and setter methods
     */
    public String getDistributor(){ return distributor; }

    public String getVersion(){ return version; }

    public String getCoolDownLength(){ return coolDownLength; }

    public ChatManager getChatManager(){ return chatManager; }

    public ServerMessages getServerMessages(){ return serverMessages; }

    public PlayerDirectMessenger getPlayerDirectMessenger(){ return playerDirectMessenger; }

    public Messenger getMessenger(){ return messenger; }


    public Chat getVaultChat(){ return chat; }

    public FileConfiguration getUsersFileConfig(){ return usersFileConfig; }

    public FileConfiguration getBroadcastsFileConfig(){ return broadcastsFileConfig; }

    public FileConfiguration getMessagesFileConfig(){ return messagesFileConfig; }

    public FileConfiguration getChannelsFileConfig(){ return channelsFileConfig; }

    public FileConfiguration getLogsFileConfig() { return logsFileConfig; }

    public boolean usePlaceholderAPI(){ return usingPlaceholderAPI; }

    public boolean processMessageColors(){ return processPlayerMessageColors; }

    public boolean processMessagePlaceholders(){ return processPlayerMessagePlaceholders; }

    public boolean useVault(){ return usingVault; }

    public boolean isPaper(){ return this.isPaper; }

    public static Strings getInstance(){ return instance; }

    public Mentioner getMentioner(){ return mentioner; }

    public short getPluginNum(){ return pluginNum; }

    public ChannelLoader getChannelLoader() { return channelLoader; }

    public LogManager getLogManager() { return logManager; }

    /*
    Other methods
     */
    public void saveUsersFile() {
        try{
            usersFileConfig.save(usersFile);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void saveChannelsFile() {
        try{
            channelsFileConfig.save(channelsFile);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public void saveMessagesFile() {
        try{
            messagesFileConfig.save(messagesFile);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public void saveBroadcastsFile() {
        try{
            broadcastsFileConfig.save(broadcastsFile);
        }catch(IOException e){
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
        return YamlUserUtil.UserMap.getUser(uuid);
    }

    /**
     * Returns a User object that contains info Strings uses.
     * @param player the player to get the User of.
     * @return User object of the player.
     */
    public User getUser(@NotNull Player player) {
        return YamlUserUtil.UserMap.getUser(player.getUniqueId());
    }

    public Channel getChannel(String channel) {
        return channelLoader.getChannel(channel);
    }

    public String isAPIUsed() {
        return "" + stringsImpl.isApiUsed();
    }

    public YamlConfiguration getModerationFileConfig() {
        return moderationFileConfig;
    }

    public String isUsingStringsModeration() {
        boolean using = getServer().getPluginManager().getPlugin("StringsModeration") != null;
        return String.valueOf(using);
    }
}
