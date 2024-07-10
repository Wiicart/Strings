package com.pedestriamc.strings;

import com.pedestriamc.strings.commands.ClearChatCommand;
import com.pedestriamc.strings.listeners.ChatListener;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class Strings extends JavaPlugin {
    private static Strings instance;
    private final Logger logger = Bukkit.getLogger();
    private final String version = "1.0";
    private final String distributor = "spigot";
    private String messageFormat;
    private String defaultColor;
    private final FileConfiguration config = this.getConfig();
    private ChatManager chatManager;
    private boolean usingPlaceholderAPI = false;

    @Override
    public void onEnable() {
        instance = this;
        logger.info("[Strings] Loading...");
        this.saveDefaultConfig();
        this.updateConfigs();
        this.loadConfigOptions();
        this.instantiateObjects();
        this.registerClasses();
        int pluginId = 22597;
        Metrics metrics = new Metrics(this, pluginId);
        logger.info("[Strings] Enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /*
    Private methods
     */
    //Register commands and listeners
    private void registerClasses(){
        this.getCommand("clearchat").setExecutor(new ClearChatCommand());
        this.getCommand("chatclear").setExecutor(new ClearChatCommand());
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);

    }
    //Load options from the config
    private void loadConfigOptions(){
        FileConfiguration config = this.getConfig();
        this.messageFormat = config.getString("message-format", "%prefix% %displayname% %suffix% &7» %message%");
        this.defaultColor = config.getString("default-color", "&f");
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
        //Placeholder API
        if(config.getBoolean("placeholder-api") && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            this.usingPlaceholderAPI = true;
        }
    }
    /*
    Public getter and setter methods
     */
    public String getVersion(){ return version; }
    public String getMessageFormat() { return messageFormat; }
    public String getDefaultColor(){ return defaultColor; }
    public ChatManager getChatManager(){ return chatManager; }
    public boolean usePlaceholderAPI(){ return this.usingPlaceholderAPI; }
    public static Strings getInstance(){ return instance; }
}
