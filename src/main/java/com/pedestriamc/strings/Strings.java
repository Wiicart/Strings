package com.pedestriamc.strings;

import com.pedestriamc.strings.commands.BroadcastCommand;
import com.pedestriamc.strings.commands.ClearChatCommand;
import com.pedestriamc.strings.listeners.ChatListener;
import com.pedestriamc.strings.listeners.JoinListener;
import com.pedestriamc.strings.listeners.LeaveListener;
import com.pedestriamc.strings.message.Messenger;
import com.tchristofferson.configupdater.ConfigUpdater;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public final class Strings extends JavaPlugin {
    private static Strings instance;
    private final Logger logger = Bukkit.getLogger();
    private static Chat chat = null;
    private final String version = "1.0";
    private final String distributor = "spigot";
    private String messageFormat;
    private String defaultColor;
    private String joinMessageFormat;
    private String leaveMessageFormat;
    private String broadcastFormat;
    private String coolDownLength;
    private String directMessageFormatSender;
    private String directMessageFormatRecipient;
    private final FileConfiguration config = this.getConfig();
    private AutoBroadcasts autoBroadcasts;
    private ServerMessages serverMessages;
    private PlayerDirectMessenger playerDirectMessenger;
    private File broadcastsFile;
    private File messagesFile;
    private File usersFile;
    private FileConfiguration broadcastsFileConfig;
    private FileConfiguration messagesFileConfig;
    private FileConfiguration usersFileConfig;
    private ChatManager chatManager;
    private boolean usingPlaceholderAPI = false;
    private boolean processPlayerMessageColors;
    private boolean processPlayerMessagePlaceholders;
    private boolean usingVault;
    private boolean useCustomJoinLeave;
    private boolean doCoolDown;
    private boolean directMessengerUsed;

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
        int pluginId = 22597;
        Metrics metrics = new Metrics(this, pluginId);
        logger.info("[Strings] Enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("[Strings] Disabled");
    }

    /*
    Private methods
     */
    //Register commands and listeners
    private void registerClasses(){
        this.getCommand("broadcast").setExecutor(new BroadcastCommand());
        this.getCommand("announce").setExecutor(new BroadcastCommand());
        this.getCommand("clearchat").setExecutor(new ClearChatCommand());
        this.getCommand("chatclear").setExecutor(new ClearChatCommand());
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new JoinListener(),this);
        this.getServer().getPluginManager().registerEvents(new LeaveListener(), this);

    }
    //Load options from the config
    private void loadConfigOptions(){
        FileConfiguration config = this.getConfig();
        this.messageFormat = config.getString("message-format", "{prefix} {displayname} {suffix} &7» {message}");
        if(!messageFormat.contains("{displayname}") || !messageFormat.contains("{message}")){
            this.messageFormat = "{prefix} {displayname} {suffix} &7» {message}";
        }
        this.directMessengerUsed = config.getBoolean("enable-msg", false);
        this.doCoolDown = config.getBoolean("cooldown", false);
        this.defaultColor = ChatColor.translateAlternateColorCodes('&', config.getString("default-color", "&f"));
        this.processPlayerMessageColors = config.getBoolean("process-in-chat-colors", true);
        this.processPlayerMessagePlaceholders = config.getBoolean("process-in-chat-placeholders", false);
        this.useCustomJoinLeave = config.getBoolean("custom-join-leave-message");
        this.joinMessageFormat = config.getString("join-message");
        this.leaveMessageFormat = config.getString("leave-message");
        this.broadcastFormat = config.getString("broadcast-format");
        this.coolDownLength = config.getString("cooldown-time", "30s");
        this.directMessageFormatSender = config.getString("msg-format-sender");
        this.directMessageFormatRecipient = config.getString("msg-format-recipient");
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
        chatManager = new ChatManager(this);
        autoBroadcasts = new AutoBroadcasts(this);
        serverMessages = new ServerMessages(this);
        playerDirectMessenger = new PlayerDirectMessenger(this);

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
        broadcastsFileConfig = YamlConfiguration.loadConfiguration(broadcastsFile);
        messagesFileConfig = YamlConfiguration.loadConfiguration(messagesFile);
        usersFileConfig = YamlConfiguration.loadConfiguration(usersFile);
    }
    /*
    Public getter and setter methods
     */
    public String getVersion(){ return version; }
    public String getDirectMessageFormatSender(){ return directMessageFormatSender; }
    public String getDirectMessageFormatRecipient(){ return directMessageFormatRecipient; }
    public String getBroadcastFormat(){ return broadcastFormat; }
    public String getMessageFormat() { return messageFormat; }
    public String getDefaultColor(){ return defaultColor; }
    public String getJoinMessageFormat(){ return joinMessageFormat; }
    public String getLeaveMessageFormat(){ return leaveMessageFormat; }
    public String getCoolDownLength(){ return coolDownLength; }
    public ChatManager getChatManager(){ return chatManager; }
    public ServerMessages getJoinLeaveMessage(){ return serverMessages; }
    public PlayerDirectMessenger getPlayerDirectMessenger(){ return playerDirectMessenger; }
    public Chat getVaultChat(){ return chat; }
    public FileConfiguration getUsersFileConfig(){ return usersFileConfig; }
    public FileConfiguration getBroadcastsFileConfig(){ return broadcastsFileConfig; }
    public FileConfiguration getMessagesFileConfig(){ return messagesFileConfig; }
    public boolean managePlayerDirectMessages(){ return directMessengerUsed; }
    public boolean usePlaceholderAPI(){ return usingPlaceholderAPI; }
    public boolean processMessageColors(){ return processPlayerMessageColors; }
    public boolean processMessagePlaceholders(){ return processPlayerMessagePlaceholders; }
    public boolean useVault(){ return usingVault; }
    public boolean modifyJoinLeaveMessages(){ return useCustomJoinLeave; }
    public boolean isDoCoolDown(){ return doCoolDown; }
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
    /**
     * Returns a User object that contains info Strings uses.
     * @param uuid The uuid of the player to get the User of.
     * @return User object of the player matching the UUID.
     */
    public User getUser(UUID uuid){
        return UserUtil.UserMap.getUser(uuid);
    }

    /**
     * Returns a User object that contains info Strings uses.
     * @param player the player to get the User of.
     * @return User object of the player.
     */
    public User getUser(Player player){
        return UserUtil.UserMap.getUser(player.getUniqueId());
    }
}
