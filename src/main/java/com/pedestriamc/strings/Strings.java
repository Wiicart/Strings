package com.pedestriamc.strings;

import com.pedestriamc.strings.channels.Channel;
import com.pedestriamc.strings.channels.ChannelManager;
import com.pedestriamc.strings.commands.*;
import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import com.pedestriamc.strings.listeners.ChatListener;
import com.pedestriamc.strings.listeners.DirectMessageListener;
import com.pedestriamc.strings.listeners.JoinListener;
import com.pedestriamc.strings.listeners.LeaveListener;
import com.pedestriamc.strings.message.Messenger;
import com.pedestriamc.strings.tabcompleters.ChannelTabCompleter;
import com.pedestriamc.strings.tabcompleters.ClearChatTabCompleter;
import com.pedestriamc.strings.tabcompleters.MessageTabCompleter;
import com.pedestriamc.stringscustoms.AutoBroadcasts;
import com.pedestriamc.stringscustoms.ChatFilter;
import com.pedestriamc.stringscustoms.ServerMessages;
import com.pedestriamc.stringscustoms.commands.BroadcastCommand;
import com.pedestriamc.stringscustoms.commands.ClearChatCommand;
import com.tchristofferson.configupdater.ConfigUpdater;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public final class Strings extends JavaPlugin {
    private static Strings instance;
    private final Logger logger = Bukkit.getLogger();
    private static Chat chat = null;
    private final String version = "1.1";
    private final String distributor = "modrinth";
    private String coolDownLength;
    private final FileConfiguration config = this.getConfig();
    private AutoBroadcasts autoBroadcasts;
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
        Messenger.initialize();
        setupChannels();
        int pluginId = 22597;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SimplePie("distributor", this::getDistributor));
        checkIfReload();
        logger.info("[Strings] Enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("[Strings] Disabled");
    }

    /*
    Private methods
     */
    private void setupChannels(){

    }
    //Register commands and listeners
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
            getLogger().info("[Strings] Vault not found, using built in methods.");
            usingVault = false;
        }else{
            RegisteredServiceProvider<Chat> serviceProvider = getServer().getServicesManager().getRegistration(Chat.class);
            chat = serviceProvider.getProvider();
            usingVault = true;
        }
    }
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
            channelsFileConfig.save(usersFile);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void saveMessagesFile(){
        try{
            messagesFileConfig.save(messagesFile);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void saveBroadcastsFile(){
        try{
            broadcastsFileConfig.save(broadcastsFile);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void reload(){
        onDisable();
        logger.info("[Strings] Loading...");
        this.saveDefaultConfig();
        this.setupCustomConfigs();
        this.updateConfigs();
        this.loadConfigOptions();
        this.instantiateObjects();
        this.setupVault();
        Messenger.initialize();
        setupChannels();
        int pluginId = 22597;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SimplePie("distributor", this::getDistributor));
        checkIfReload();
        logger.info("[Strings] Enabled!");
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
}

