package com.pedestriamc.strings;

import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.channels.Channel;
import com.pedestriamc.strings.channels.ChannelManager;
import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import com.pedestriamc.strings.impl.StringsImpl;
import com.pedestriamc.strings.listeners.ChatListener;
import com.pedestriamc.strings.listeners.DirectMessageListener;
import com.pedestriamc.strings.listeners.JoinListener;
import com.pedestriamc.strings.listeners.LeaveListener;
import com.pedestriamc.strings.message.Messenger;
import com.pedestriamc.strings.tabcompleters.ChannelTabCompleter;
import com.pedestriamc.strings.tabcompleters.ClearChatTabCompleter;
import com.pedestriamc.strings.tabcompleters.MessageTabCompleter;
import com.pedestriamc.strings.tabcompleters.SocialSpyTabCompleter;
import com.pedestriamc.strings.commands.BroadcastCommand;
import com.pedestriamc.strings.commands.ClearChatCommand;
import com.pedestriamc.strings.commands.*;
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

    private final String version = "1.1";
    private final String distributor = "modrinth";
    private final short pluginNum = 1;

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
    private FileConfiguration broadcastsFileConfig;
    private FileConfiguration messagesFileConfig;
    private FileConfiguration usersFileConfig;
    private FileConfiguration channelsFileConfig;
    private ChatManager chatManager;
    private ChannelManager channelManager;
    private ChatFilter chatFilter;
    private boolean usingPlaceholderAPI = false;
    private boolean processPlayerMessageColors;
    private boolean processPlayerMessagePlaceholders;
    private boolean usingVault;
    private boolean useCustomJoinLeave;
    private boolean isPaper = false;
    private StringsImpl stringsImpl;

    @Override
    public void onEnable() {
        instance = this;
        logger.info("[Strings] Loading...");
        this.saveDefaultConfig();
        this.setupCustomConfigs();
        this.updateConfigs();
        this.loadConfigOptions();
        this.instantiateObjects();
        this.registerClasses();
        this.setupVault();
        this.setupAPI();
        Messenger.initialize();
        int pluginId = 22597;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SimplePie("distributor", this::getDistributor));
        metrics.addCustomChart(new SimplePie("using_stringsapi", this::isAPIUsed));
        checkIfReload();
        checkUpdate();
        logger.info("[Strings] Enabled!");
    }

    @Override
    public void onDisable() {
        for(User user : UserUtil.UserMap.getUserSet()){
            user.logOff();
        }
        UserUtil.UserMap.clear();
        this.serverMessages = null;
        this.playerDirectMessenger = null;
        this.chatManager = null;
        this.channelManager.disable();
        this.channelManager = null;
        this.chatFilter = null;
        this.autoBroadcasts = null;
        HandlerList.unregisterAll(this);
        this.getServer().getScheduler().cancelTasks(this);
        this.getServer().getServicesManager().unregister(StringsAPI.class, stringsImpl);
        this.stringsImpl = null;
        logger.info("[Strings] Disabled");
    }

    /*
    Private methods
     */
    //Register commands and listeners

    @SuppressWarnings("ConstantConditions")
    private void registerClasses(){
        this.getCommand("strings").setExecutor(new StringsCommand());
        this.getCommand("helpop").setExecutor(new HelpOPCommand());
        this.getCommand("broadcast").setExecutor(new BroadcastCommand());
        this.getCommand("announce").setExecutor(new BroadcastCommand());
        this.getCommand("clearchat").setExecutor(new ClearChatCommand());
        this.getCommand("chatclear").setExecutor(new ClearChatCommand());
        this.getCommand("clearchat").setTabCompleter(new ClearChatTabCompleter());
        this.getCommand("chatclear").setTabCompleter(new ClearChatTabCompleter());
        this.getCommand("socialspy").setExecutor(new SocialSpyCommand());
        this.getCommand("socialspy").setTabCompleter(new SocialSpyTabCompleter());
        this.getCommand("msg").setExecutor(new DirectMessageCommand());
        this.getCommand("message").setExecutor(new DirectMessageCommand());
        this.getCommand("msg").setTabCompleter(new MessageTabCompleter());
        this.getCommand("message").setTabCompleter(new MessageTabCompleter());
        this.getCommand("reply").setExecutor(new ReplyCommand());
        this.getCommand("r").setExecutor(new ReplyCommand());
        this.getCommand("channel").setExecutor(new ChannelCommand());
        this.getCommand("c").setExecutor(new ChannelCommand());
        this.getCommand("channel").setTabCompleter(new ChannelTabCompleter());
        this.getCommand("c").setTabCompleter(new ChannelTabCompleter());
        if(config.getBoolean("enable-chatcolor")){
            this.getCommand("chatcolor").setExecutor(new ChatColorCommand());
        }
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new JoinListener(),this);
        this.getServer().getPluginManager().registerEvents(new LeaveListener(), this);
        this.getServer().getPluginManager().registerEvents(new DirectMessageListener(), this);
    }

    //Load options from the config
    private void loadConfigOptions(){
        FileConfiguration config = this.getConfig();
        this.processPlayerMessageColors = config.getBoolean("process-in-chat-colors", true);
        this.processPlayerMessagePlaceholders = config.getBoolean("process-in-chat-placeholders", false);
        this.useCustomJoinLeave = config.getBoolean("custom-join-leave-message");
        this.coolDownLength = config.getString("cooldown-time", "30s");
        if(config.getBoolean("placeholder-api") && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            this.usingPlaceholderAPI = true;
        }
        try{
            Class.forName("com.destroystokyo.paper.util.VersionFetcher");
            isPaper = true;
        }catch(ClassNotFoundException ignored){
        }
    }
    //Update yml files
    private void updateConfigs(){
        //Config updater using https://github.com/tchristofferson/Config-Updater
        File configFile = new File(this.getDataFolder(), "config.yml");
        File messageFile = new File(this.getDataFolder(), "messages.yml");
        File broadcastFile = new File(this.getDataFolder(), "broadcasts.yml");
        if(configFile.exists()){
            try{
                ConfigUpdater.update(this,"config.yml", configFile);
            }catch(IOException e){
                Bukkit.getLogger().info("[Strings] Updating config file failed.");
                e.printStackTrace();
            }
        }
        if(messageFile.exists()){
            try{
                ConfigUpdater.update(this,"messages.yml", messageFile);
            }catch(IOException e){
                Bukkit.getLogger().info("[Strings] Updating messages file failed.");
                e.printStackTrace();
            }
        }
        if(broadcastFile.exists()){
            try{
                ConfigUpdater.update(this,"broadcasts.yml", broadcastFile);
            }catch(IOException e){
                Bukkit.getLogger().info("[Strings] Updating broadcasts file failed.");
                e.printStackTrace();
            }
        }
    }
    private void instantiateObjects(){
        chatFilter = new ChatFilter(this);
        chatManager = new ChatManager(this);
        playerDirectMessenger = new PlayerDirectMessenger(this);
        channelManager = new ChannelManager(this);
        autoBroadcasts = new AutoBroadcasts(this);
        serverMessages = new ServerMessages(this);

    }

    private void setupVault(){
        if(getServer().getPluginManager().getPlugin("Vault") == null){
            getLogger().info("Vault not found, using built in methods.");
            usingVault = false;
        }else{
            RegisteredServiceProvider<Chat> serviceProvider = getServer().getServicesManager().getRegistration(Chat.class);
            chat = serviceProvider.getProvider();
            usingVault = true;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void setupCustomConfigs(){
        broadcastsFile = new File(getDataFolder(), "broadcasts.yml");
        messagesFile = new File(getDataFolder(), "messages.yml");
        usersFile = new File(getDataFolder(), "users.yml");
        channelsFile = new File(getDataFolder(), "channels.yml");
        if(!broadcastsFile.exists()){
            broadcastsFile.getParentFile().mkdirs();
            saveResource("broadcasts.yml", false);
        }
        if(!messagesFile.exists()){
            messagesFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }
        if(!usersFile.exists()){
            usersFile.getParentFile().mkdirs();
            saveResource("users.yml", false);
        }
        if(!channelsFile.exists()){
            channelsFile.getParentFile().mkdirs();
            saveResource("channels.yml", false);
        }
        broadcastsFileConfig = YamlConfiguration.loadConfiguration(broadcastsFile);
        messagesFileConfig = YamlConfiguration.loadConfiguration(messagesFile);
        usersFileConfig = YamlConfiguration.loadConfiguration(usersFile);
        channelsFileConfig = YamlConfiguration.loadConfiguration(channelsFile);
    }
    private void checkIfReload(){
        if(Bukkit.getOnlinePlayers().size() > 0){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(UserUtil.loadUser(p.getUniqueId()) == null){
                    new User(p.getUniqueId());
                }
            }
        }
    }

    private void checkUpdate(){
        try{
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://www.wiicart.net/strings/version.txt").openConnection();
            connection.setRequestMethod("GET");
            String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            short latest = Short.parseShort(raw);
            if(latest > pluginNum){
                Bukkit.getLogger().info("+------------[Strings]------------+");
                Bukkit.getLogger().info("|    A new update is available!   |");
                Bukkit.getLogger().info("|          Download at:           |");
                Bukkit.getLogger().info("|   https://wiicart.net/strings   |");
                Bukkit.getLogger().info("+---------------------------------+");
            }
        } catch(IOException a){
            Bukkit.getLogger().info("[Strings] Unable to check for updates.");
        }
    }

    /*private void setupLogger(){
        File file = new File(getDataFolder(), "log.txt");
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            saveResource("log.txt", false);
        }
        stringsLogger = new StringsLogger(this, file);
    }*/

    private void setupAPI(){
        stringsImpl = new StringsImpl(this);
        StringsProvider.register(stringsImpl, this);
    }

    /*
    Public getter and setter methods
     */
    public String getDistributor(){ return distributor; }
    public String getVersion(){ return version; }
    public String getCoolDownLength(){ return coolDownLength; }
    public ChatManager getChatManager(){ return chatManager; }
    public ChannelManager getChannelManager(){ return channelManager; }
    public ServerMessages getJoinLeaveMessage(){ return serverMessages; }
    public PlayerDirectMessenger getPlayerDirectMessenger(){ return playerDirectMessenger; }
    public ChatFilter getChatFilter(){ return chatFilter; }
    public Chat getVaultChat(){ return chat; }
    public FileConfiguration getUsersFileConfig(){ return usersFileConfig; }
    public FileConfiguration getBroadcastsFileConfig(){ return broadcastsFileConfig; }
    public FileConfiguration getMessagesFileConfig(){ return messagesFileConfig; }
    public FileConfiguration getChannelsFileConfig(){ return channelsFileConfig; }
    public boolean usePlaceholderAPI(){ return usingPlaceholderAPI; }
    public boolean processMessageColors(){ return processPlayerMessageColors; }
    public boolean processMessagePlaceholders(){ return processPlayerMessagePlaceholders; }
    public boolean useVault(){ return usingVault; }
    public boolean modifyJoinLeaveMessages(){ return useCustomJoinLeave; }
    public boolean isPaper(){ return this.isPaper; }
    public static Strings getInstance(){ return instance; }
    /*
    Other methods
     */
    public void saveUsersFile(){
        try{
            usersFileConfig.save(usersFile);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void saveChannelsFile(){
        try{
            channelsFileConfig.save(channelsFile);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public void saveMessagesFile(){
        try{
            messagesFileConfig.save(messagesFile);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public void saveBroadcastsFile(){
        try{
            broadcastsFileConfig.save(broadcastsFile);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void reload(){
        onDisable();
        onEnable();
    }

    /**
     * Returns a User object that contains info Strings uses.
     * @param uuid The uuid of the player to get the User of.
     * @return User object of the player matching the UUID.
     */
    public User getUser(@NotNull UUID uuid){
        return UserUtil.UserMap.getUser(uuid);
    }

    /**
     * Returns a User object that contains info Strings uses.
     * @param player the player to get the User of.
     * @return User object of the player.
     */
    public User getUser(@NotNull Player player){
        return UserUtil.UserMap.getUser(player.getUniqueId());
    }

    public Channel getChannel(String channel){
        return channelManager.getChannel(channel);
    }

    public String isAPIUsed(){
        return "" + stringsImpl.isApiUsed();
    }
}
