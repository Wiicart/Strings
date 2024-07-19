package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {

    private final Strings strings;
    private final ConcurrentHashMap<String, Channel> channels;
    private final FileConfiguration config;

    public ChannelManager(@NotNull Strings strings){
        this.strings = strings;
        this.channels = new ConcurrentHashMap<>();
        this.config = strings.getChannelsFileConfig();
        loadChannelsFromConfig();
    }

    private void loadChannelsFromConfig(){
        boolean globalExists = false;
        boolean helpOpExists = false;
        if(config.contains("channels")){
            ConfigurationSection channels = config.getConfigurationSection("channels");
            if(channels != null){
                for(String channelName : channels.getKeys(false)){
                    ConfigurationSection channel = channels.getConfigurationSection(channelName);
                    if(channel != null){
                        String format = channel.getString("format", "{prefix}{displayname}{suffix} &7» {message}");
                        String defaultColor = channel.getString("default-color", "&f");
                        boolean callEvent = channel.getBoolean("call-event", true);
                        boolean urlFilter = channel.getBoolean("block-urls", false);
                        boolean profanityFilter = channel.getBoolean("filter-profanity", false);
                        boolean doCooldown = channel.getBoolean("cooldown", false);
                        if(channelName.equalsIgnoreCase("helpop")){
                            new HelpOPChannel(strings,channelName,format,defaultColor,this, callEvent, urlFilter, profanityFilter);
                            helpOpExists = true;
                        }else{
                            Bukkit.getLogger().info("[Strings] Loading channel '" + channelName + "'..");
                            new StringChannel(strings, channelName, format, defaultColor, this, callEvent, urlFilter, profanityFilter, doCooldown);
                            if(channelName.equalsIgnoreCase("global")){
                                globalExists = true;
                            }
                        }
                    }
                }
            }
        }
        if(!globalExists){
            Bukkit.getLogger().info("[Strings] Creating global channel");
            new StringChannel(strings,"global","{prefix}{displayname}{suffix} &7» {message}", "&f", this, true, false, false, false);
        }
        if(!helpOpExists){
            Bukkit.getLogger().info("[Strings] Creating help op channel");
            new HelpOPChannel(strings,"helpop","&8[&4HelpOP&8] &f{displayname} &7» {message}", "&7",this, false, false, false);
        }
        String socialSpyFormat = strings.getConfig().getString("social-spy-format");
        Bukkit.getLogger().info("[Strings] Loading channel 'socialspy'..");
        new SocialSpyChannel(this, strings.getPlayerDirectMessenger(), socialSpyFormat);

    }

    public void registerChannel(Channel channel){
        channels.put(channel.getName(), channel);
    }

    public void unregisterChannel(Channel channel){
        if(channel.getName().equalsIgnoreCase("global")){
            Bukkit.getLogger().info("[Strings] Channel 'global' cannot be closed.");
            return;
        }
        Collection<User> users = UserUtil.UserMap.getUserSet();
        Channel global = strings.getChannel("global");
        for(User user : users){
            if(user.getActiveChannel().equals(channel)){
                user.setActiveChannel(global);
                user.leaveChannel(channel);
            }
        }
        Bukkit.getLogger().info("[Strings] Channel " + channel.getName() + " unregistered and removed from config.");
        config.set("channels." + channel.getName(), null);
        strings.saveChannelsFile();
        channels.remove(channel.getName());
    }

    public Channel getChannel(String channel){
        return channels.get(channel);
    }

    public List<String> getChannelNames(){
        return Collections.list( channels.keys());
    }

    public List<String> getNonProtectedChannelNames(){
        List<Channel> list = getNonProtectedChannels();
        ArrayList<String> nonProtected = new ArrayList<>();
        for(Channel c : list){
            nonProtected.add(c.getName());
        }
        return nonProtected;
    }

    public ArrayList<Channel> getChannelList(){ return new ArrayList<>(channels.values()); }

    public ArrayList<Channel> getNonProtectedChannels(){
        ArrayList<Channel> list = new ArrayList<>();
        for(Channel c : getChannelList()){
            if(c.getType() != Type.PROTECTED){
                list.add(c);
            }
        }
        return list;
    }
    public ArrayList<Channel> getProtectedChannels(){
        ArrayList<Channel> list = new ArrayList<>();
        for(Channel c : getChannelList()){
            if(c.getType() == Type.PROTECTED){
                list.add(c);
            }
        }
        return list;
    }
}

