package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.User;
import com.pedestriamc.strings.UserUtil;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.Type;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class to load, maintain references to and manage all Channels.
 */
public class ChannelManager {

    private final Strings strings;
    private final ConcurrentHashMap<String, Channel> channels;
    private final ConcurrentHashMap<World, Channel> worldChannels;
    private final FileConfiguration config;

    /**
     * Creates a new instance of the ChannelManager and loads Channels from the channels.yml file.
     * @param strings The instance of the Strings plugin.
     */
    public ChannelManager(@NotNull Strings strings){
        this.strings = strings;
        this.channels = new ConcurrentHashMap<>();
        this.worldChannels = new ConcurrentHashMap<>();
        this.config = strings.getChannelsFileConfig();
        loadChannelsFromConfig();
    }

    /**
     * Dereferences all Channels from this class.
     */
    public void disable(){
        channels.clear();
    }

    /**
     * Loads all Channels in the channels.yml file.
     */
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

    /**
     * Saves a Channel to the channels.yml file.
     * @param channel The Channel to be saved.
     */
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

    /**
     * Deletes a Channel and removes it from the channels.yml file.
     * @param channel The Channel to be deleted.
     */
    public void deleteChannel(@NotNull Channel channel){
        if(channel.getName().equalsIgnoreCase("global")){
            Bukkit.getLogger().info("[Strings] Unable to delete global channel.");
            return;
        }
        config.set("channels." + channel.getName(), null);
    }

    /**
     * Registers a Channel.
     * @param channel The Channel to be registered.
     */
    public void registerChannel(Channel channel){
        channels.put(channel.getName(), channel);
        if(channel instanceof WorldChannel){
            worldChannels.put(((WorldChannel) channel).getWorld(), channel);
        }
        Bukkit.getLogger().info("[Strings] Channel '" + channel.getName() + "' registered.");
    }

    /**
     * Unregisters a Channel.
     * @param channel The Channel to be unregistered.
     */
    public void unregisterChannel(@NotNull Channel channel){
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

    /**
     * Provides a Channel with the stated name, if it exists.
     * @param name The name of the Channel to search for.
     * @return The Channel with the name provided, if it exists.
     */
    @Nullable
    public Channel getChannel(String name){
        return channels.get(name);
    }

    /**
     * Provides the WorldChannel for a specified World, if it exists.
     * @param world The World the channel should be for
     * @return The Channel for the specified world, if it exists.
     */
    @Nullable
    public Channel getWorldChannel(World world){ return worldChannels.get(world); }

    /**
     * Provides the WorldChannel for a specified World name, if it exists.
     * @param world The name of the World the channel should be for
     * @return The Channel for the specified world, if it exists.
     */
    @Nullable
    public Channel getWorldChannel(String world){ return getWorldChannel(Bukkit.getWorld(world)); }

    /**
     * Provides a List of the names of all Channels.
     * @return A populated List of Strings.
     */
    public List<String> getChannelNames(){
        return Collections.list(channels.keys());
    }

    /**
     * Provides a list of the names of all Channels that are not protected.
     * @return A populated List of Strings.
     */
    public List<String> getNonProtectedChannelNames(){
        List<Channel> list = getNonProtectedChannels();
        ArrayList<String> nonProtected = new ArrayList<>();
        for(Channel c : list){
            nonProtected.add(c.getName());
        }
        return nonProtected;
    }

    /**
     * Provides an ArrayList of all Channels.
     * @return A populated ArrayList of Channels.
     */
    public ArrayList<Channel> getChannelList(){ return new ArrayList<>(channels.values()); }

    /**
     * Provides an ArrayList of all non-protected Channels.
     * @return A populated ArrayList of Channels.
     */
    public ArrayList<Channel> getNonProtectedChannels(){
        ArrayList<Channel> list = new ArrayList<>();
        for(Channel c : getChannelList()){
            if(c.getType() != Type.PROTECTED){
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Provides an ArrayList of all protected Channels.
     * @return An ArrayList of Channels.
     */
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