package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.Strings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {

    private final Strings strings;
    private final ConcurrentHashMap<String, Channel> channels;
    private final FileConfiguration config;

    public ChannelManager(@NotNull Strings strings){
        this.strings = strings;
        this.channels = new ConcurrentHashMap<String, Channel>();
        this.config = strings.getChannelsFileConfig();
        loadChannelsFromConfig();
    }

    private void loadChannelsFromConfig(){
        boolean globalExists = false;
        if(config.contains("channels")){
            ConfigurationSection channels = config.getConfigurationSection("channels");
            if(channels != null){
                for(String channelName : channels.getKeys(false)){
                    ConfigurationSection channel = channels.getConfigurationSection(channelName);
                    if(channel != null){
                        String format = channel.getString("format", "{prefix}{displayname}{suffix} &7» {message}");
                        String defaultColor = channel.getString("default-color", "&f");
                        new Channel(strings, channelName, format, defaultColor, this);
                        Bukkit.getLogger().info("[Strings] Loaded channel " + channelName);
                        if(channelName.equalsIgnoreCase("global")){
                            globalExists = true;
                        }
                    }
                }
            }
        }
        if(!globalExists){
            Bukkit.getLogger().info("[Strings] Creating global channel");
            new Channel(strings,"global","{prefix}{displayname}{suffix} &7» {message}", "&f", this);
        }

    }

    public void registerChannel(Channel channel){
        channels.put(channel.getName(), channel);
    }

    public void unregisterChannel(Channel channel){
        channels.remove(channel.getName());
    }

    public Channel getChannel(String channel){
        return channels.get(channel);
    }
}
