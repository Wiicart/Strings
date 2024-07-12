package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.Strings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.HashSet;

public class ChannelManager {

    private final Strings strings;
    private final HashMap<String, Channel> channels;
    private final FileConfiguration config;

    public ChannelManager(Strings strings){
        this.strings = strings;
        this.channels = new HashMap<>();
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
                        Bukkit.getLogger().info("[Strings] Loaded channel " + channelName);
                        Bukkit.getLogger().info("[Strings] Format: " + format);
                        Bukkit.getLogger().info("[Strings] Default color: " + defaultColor);
                        new Channel(strings, channelName, format, defaultColor);
                        if(channelName.equalsIgnoreCase("global")){
                            globalExists = true;
                        }
                    }
                }
            }
        }
        if(!globalExists){
            Bukkit.getLogger().info("[Strings] Creating global channel");
            new Channel(strings,"global","{prefix}{displayname}{suffix} &7» {message}", "&f");
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

    public HashSet<Channel> getChannels(){
        return null;
    }
}
