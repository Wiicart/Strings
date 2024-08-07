package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
                        String type = channel.getString("type", "stringchannel");
                        String world = channel.getString("world");
                        int distance = channel.getInt("distance");
                        String format = channel.getString("format", "{prefix}{displayname}{suffix} &7» {message}");
                        String defaultColor = channel.getString("default-color", "&f");
                        boolean callEvent = channel.getBoolean("call-event", true);
                        boolean urlFilter = channel.getBoolean("block-urls", false);
                        boolean profanityFilter = channel.getBoolean("filter-profanity", false);
                        boolean doCooldown = channel.getBoolean("cooldown", false);
                        boolean active = channel.getBoolean("active", true);
                        switch(type){
                            case "stringchannel" -> {
                                Bukkit.getLogger().info("[Strings] Loading stringchannel '" + channelName + "'...");
                                new StringChannel(strings, channelName, format, defaultColor, this, callEvent, urlFilter, profanityFilter, doCooldown, active);
                                if(channelName.equalsIgnoreCase("global")) {
                                    globalExists = true;
                                }
                            }
                            case "world" -> {
                                Bukkit.getLogger().info("[Strings] Loading world channel '" + channelName + "'...");
                                if(world == null){
                                    Bukkit.getLogger().info("[Strings] Failed to load world channel '" + channelName + "', world undefined in channels.yml file.");
                                    break;
                                }
                                World actualWorld = Bukkit.getWorld(world);
                                if(actualWorld == null){
                                    Bukkit.getLogger().info("[Strings] Failed to load world channel '" + channelName + "'. Invalid world '" + world + "' defined.");
                                    break;
                                }
                                new WorldChannel(channelName,format,defaultColor,this, strings.getChatManager(), callEvent, urlFilter, profanityFilter, doCooldown, actualWorld, active);
                            }
                            case "proximity" -> {
                                Bukkit.getLogger().info("[Strings] Loading proximity channel '" + channelName + "'...");
                                new ProximityChannel(strings,channelName, format, defaultColor, this, strings.getChatManager(), callEvent, urlFilter, profanityFilter, doCooldown, distance, active);

                            }
                            case "helpop" -> {
                                Bukkit.getLogger().info("[Strings] Loading channel 'helpop'..");
                                new HelpOPChannel(strings,channelName,format,defaultColor,this, callEvent, urlFilter, profanityFilter);
                                helpOpExists = true;
                            }
                            default -> Bukkit.getLogger().info("Failed to load channel " + channelName + ", channel type is invalid in channels.yml");
                        }
                    }
                }
            }
        }
        if(!globalExists){
            Bukkit.getLogger().info("[Strings] Creating global channel");
            new StringChannel(strings,"global","{prefix}{displayname}{suffix} &7» {message}", "&f", this, true, false, false, false, true);
        }
        if(!helpOpExists){
            Bukkit.getLogger().info("[Strings] Creating help op channel");
            new HelpOPChannel(strings,"helpop","&8[&4HelpOP&8] &f{displayname} &7» {message}", "&7",this, false, false, false);
        }
        String socialSpyFormat = strings.getConfig().getString("social-spy-format");
        Bukkit.getLogger().info("[Strings] Loading channel 'socialspy'..");
        new SocialSpyChannel(this, strings.getPlayerDirectMessenger(), socialSpyFormat);

    }

    public void saveChannel(@NotNull Channel channel){
        if (channel.getType() == Type.PROTECTED) {
            Bukkit.getLogger().info("[Strings] Unable to save protected channels.  These channels must be modified in config files.");
            return;
        }
        Map<String, String> dataMap = channel.getData();
        String channelName = channel.getName();
        ConfigurationSection channelSection = config.getConfigurationSection("channels." + channelName);
        if(channelSection == null){
            channelSection = config.createSection("channels." + channelName);
        }
        dataMap.forEach(channelSection::set);
        strings.saveChannelsFile();
    }

    public void deleteChannel(@NotNull Channel channel){
        if(channel.getName().equalsIgnoreCase("global")){
            Bukkit.getLogger().info("[Strings] Unable to delete global channel.");
            return;
        }
        config.set("channels." + channel.getName(), null);
    }

    public void registerChannel(Channel channel){
        channels.put(channel.getName(), channel);
        Bukkit.getLogger().info("[Strings] Channel '" + channel.getName() + "' registered.");
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

