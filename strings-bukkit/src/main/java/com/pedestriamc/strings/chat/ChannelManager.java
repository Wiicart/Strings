package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.chat.channels.*;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserUtil;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.Type;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class to load, maintain references to and manage all Channels.
 */
public class ChannelManager {

    private final Strings strings;
    private final ConcurrentHashMap<String, Channel> channels;
    private final ConcurrentHashMap<World, Channel> worldChannels;
    private final ConcurrentHashMap<World, Channel[]> wChannelsByPriority;
    private final HashMap<String, Channel> channelSymbols;
    private final Set<Channel> defaultMembershipChannels;
    private final FileConfiguration config;
    private ArrayList<Channel> nonProtectedChannels;
    private Channel[] channelsPrioritySorted;

    /**
     * Creates a new instance of the ChannelManager and loads Channels from the channels.yml file.
     * @param strings The instance of the Strings plugin.
     */
    public ChannelManager(@NotNull Strings strings){
        this.strings = strings;
        this.channels = new ConcurrentHashMap<>();
        this.worldChannels = new ConcurrentHashMap<>();
        this.wChannelsByPriority = new ConcurrentHashMap<>();
        this.config = strings.getChannelsFileConfig();
        this.defaultMembershipChannels = Collections.synchronizedSet(new HashSet<>());
        this.channelSymbols = new HashMap<>();
        for(World world : Bukkit.getWorlds()){
            wChannelsByPriority.put(world, new Channel[0]);
        }
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
                        double distance = channel.getDouble("distance");
                        String format = channel.getString("format", "{prefix}{displayname}{suffix} &7» {message}");
                        String defaultColor = channel.getString("default-color", "&f");
                        String symbol = channel.getString("symbol", "");
                        boolean callEvent = channel.getBoolean("call-event", true);
                        boolean urlFilter = channel.getBoolean("block-urls", false);
                        boolean profanityFilter = channel.getBoolean("filter-profanity", false);
                        boolean doCooldown = channel.getBoolean("cooldown", false);
                        int priority = channel.getInt("priority", -1);
                        String membershipString = channel.getString("membership");
                        Membership membership;
                        if(membershipString == null){
                            membershipString = "permission";
                        }
                        switch(membershipString){
                            case "default" -> membership = Membership.DEFAULT;
                            case "permission" -> membership = Membership.PERMISSION;
                            default -> membership = Membership.PROTECTED;
                        }
                        switch(type){
                            case "stringchannel" -> {
                                Bukkit.getLogger().info("[Strings] Loading stringchannel '" + channelName + "'...");
                                Channel c = new StringChannel(strings, channelName, format, defaultColor, this, callEvent, urlFilter, profanityFilter, doCooldown, membership, priority);
                                if(!Objects.equals(symbol, "")){
                                    channelSymbols.put(symbol, c);
                                }
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
                                Channel c = new WorldChannel(strings, channelName, format, defaultColor,this, callEvent, urlFilter, profanityFilter, doCooldown, actualWorld, membership, priority);
                                if(!Objects.equals(symbol, "")){
                                    channelSymbols.put(symbol, c);
                                }
                            }
                            case "proximity" -> {
                                Bukkit.getLogger().info("[Strings] Loading proximity channel '" + channelName + "'...");
                                if(world == null){
                                    Bukkit.getLogger().info("[Strings] Failed to load proximity channel '" + channelName + "', world undefined in channels.yml file.");
                                    break;
                                }
                                World actualWorld = Bukkit.getWorld(world);
                                if(actualWorld == null){
                                    Bukkit.getLogger().info("[Strings] Failed to load proximity channel '" + channelName + "'. Invalid world '" + world + "' defined.");
                                    break;
                                }
                                Channel c = new ProximityChannel(strings, channelName, format, defaultColor, this, callEvent, urlFilter, profanityFilter, doCooldown, distance, membership, priority, actualWorld);
                                if(!Objects.equals(symbol, "")){
                                    channelSymbols.put(symbol, c);
                                }

                            }
                            case "helpop" -> {
                                Bukkit.getLogger().info("[Strings] Loading channel 'helpop'..");
                                Channel c = new HelpOPChannel(strings,channelName,format,defaultColor,this, callEvent, urlFilter, profanityFilter);
                                if(!Objects.equals(symbol, "")){
                                    channelSymbols.put(symbol, c);
                                }
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
            new StringChannel(strings,"global","{prefix}{displayname}{suffix} &7» {message}", "&f", this, true, false, false, false, Membership.DEFAULT, 0);
        }
        if(!helpOpExists){
            Bukkit.getLogger().info("[Strings] Creating help op channel");
            new HelpOPChannel(strings,"helpop","&8[&4HelpOP&8] &f{displayname} &7» {message}", "&7",this, false, false, false);
        }
        String socialSpyFormat = strings.getConfig().getString("social-spy-format");
        Bukkit.getLogger().info("[Strings] Loading channel 'socialspy'..");
        new SocialSpyChannel(this, strings.getPlayerDirectMessenger(), socialSpyFormat);
        new DefaultChannel(strings, this);

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
        if(channel.getName().equalsIgnoreCase("global") || channel.getName().equalsIgnoreCase("default")){
            Bukkit.getLogger().info("[Strings] Unable to delete global/default channels.");
            return;
        }
        this.unregisterChannel(channel);
        config.set("channels." + channel.getName(), null);
    }

    /**
     * Registers a Channel.
     * @param channel The Channel to be registered.
     */
    public void registerChannel(Channel channel){
        channels.put(channel.getName(), channel);
        boolean isWorldOrProximity = false;
        World world = null;
        if(channel instanceof WorldChannel){
            worldChannels.put(((WorldChannel) channel).getWorld(), channel);
            world = ((WorldChannel) channel).getWorld();
            isWorldOrProximity = true;
        }
        if(channel instanceof ProximityChannel){
            worldChannels.put(((ProximityChannel) channel).getWorld(), channel);
            world = ((ProximityChannel) channel).getWorld();
            isWorldOrProximity = true;
        }
        if(channel.getMembership() == Membership.DEFAULT && !isWorldOrProximity){
            defaultMembershipChannels.add(channel);
        }

        if(!isWorldOrProximity){
            ArrayList<Channel> prChannels = new ArrayList<>();
            for(Map.Entry<String, Channel> entry : channels.entrySet()){
                if(entry.getValue().getMembership() == Membership.DEFAULT && !(entry.getValue() instanceof ProximityChannel) && !(entry.getValue() instanceof WorldChannel)){
                    prChannels.add(entry.getValue());
                }
            }
            prChannels.sort(Comparator.comparing(Channel::getPriority).reversed());
            channelsPrioritySorted = prChannels.toArray(new Channel[0]);
        }
        if(isWorldOrProximity){
            if(world == null){
                if(channel instanceof WorldChannel){
                    world = ((WorldChannel) channel).getWorld();
                }
                if(channel instanceof ProximityChannel){
                    world = ((ProximityChannel) channel).getWorld();
                }

            }

            ArrayList<Channel> prWChannels = getChannels(world);
            prWChannels.sort(Comparator.comparing(Channel::getPriority).reversed());
            wChannelsByPriority.put(world, prWChannels.toArray(new Channel[0]));
        }



        Bukkit.getLogger().info("[Strings] Channel '" + channel.getName() + "' registered.");
    }

    private @NotNull ArrayList<Channel> getChannels(World world) {
        ArrayList<Channel> prWChannels = new ArrayList<>();
        for(Map.Entry<String, Channel> entry : channels.entrySet()){
            Channel c = entry.getValue();
            World w = null;

            if(c instanceof WorldChannel){
                w = ((WorldChannel) c).getWorld();
            }
            if(c instanceof ProximityChannel){
                w = ((ProximityChannel) c).getWorld();
            }
            if(w != null && w.equals(world) && c.getMembership() == Membership.DEFAULT){
                prWChannels.add(c);
            }

        }
        return prWChannels;
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
        if(nonProtectedChannels == null){
            ArrayList<Channel> list = new ArrayList<>();
            for(Channel c : getChannelList()){
                if(c.getType() != Type.PROTECTED){
                    list.add(c);
                }
            }
            nonProtectedChannels = list;
        }
        return new ArrayList<>(nonProtectedChannels);
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

    public Channel[] getWorldChannels(World world){
        ArrayList<Channel> wChannels = new ArrayList<>();
        for(Map.Entry<World, Channel> entry : worldChannels.entrySet()){
            if(entry.getKey().equals(world)){
                wChannels.add(entry.getValue());
            }
        }
        wChannels.sort(Comparator.comparing(Channel::getPriority).reversed());
        return wChannels.toArray(new Channel[0]);
    }

    public Channel[] getPriorityChannels(){
        return Arrays.copyOf(channelsPrioritySorted, channelsPrioritySorted.length);
    }

    public Channel[] getWorldPriorityChannels(World world){
        return Arrays.copyOf(wChannelsByPriority.get(world), wChannelsByPriority.get(world).length);
    }

    public Map<String, Channel> getChannelSymbols() {
        return channelSymbols;
    }

}
