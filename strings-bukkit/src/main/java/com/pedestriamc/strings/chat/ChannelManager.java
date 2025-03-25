package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.LocalChannel;
import com.pedestriamc.strings.api.channel.data.ChannelData;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.chat.channel.HelpOPChannel;
import com.pedestriamc.strings.chat.channel.local.ProximityChannel;
import com.pedestriamc.strings.chat.channel.local.StrictProximityChannel;
import com.pedestriamc.strings.chat.channel.local.StrictWorldChannel;
import com.pedestriamc.strings.chat.channel.StringChannel;
import com.pedestriamc.strings.chat.channel.local.WorldChannel;
import com.pedestriamc.strings.user.User;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ChannelManager implements ChannelLoader {

    private final Strings strings;
    private final ConcurrentHashMap<String, Channel> channels;
    private final FileConfiguration config;
    private final HashMap<String, Channel> channelSymbols;
    private final Set<Channel> worldChannels;
    private final TreeSet<Channel> priorityChannels;

    public ChannelManager(Strings strings) {
        this.strings = strings;
        this.channels = new ConcurrentHashMap<>();
        this.config = strings.getChannelsFileConfig();
        this.channelSymbols = new HashMap<>();
        this.worldChannels = new HashSet<>();
        this.priorityChannels = new TreeSet<>();

        ChannelFileReader reader = new ChannelFileReader(strings, config, this);
        reader.read();
    }

    @Override
    public @Nullable Channel getChannel(@NotNull String name) {
        return channels.get(name);
    }

    /**
     * Registers a Channel with the plugin
     * @param channel The channel to be registered
     */
    @Override
    public void registerChannel(@NotNull Channel channel) {
        String channelName = channel.getName();
        if(channels.containsKey(channelName)) {
            log("A Channel with the name '" + channelName + "' already exists, channels with the same name cannot be registered.");
            return;
        }

        if(channel instanceof LocalChannel) {
            worldChannels.add(channel);
        }

        priorityChannels.add(channel);
        channels.put(channel.getName(), channel);
        setToChanged();
        log("Channel '" + channel.getName() + "' registered.");
    }

    /**
     * Unregisters a Channel from the plugin, but does not remove it from Channels.yml
     * Does not persist after restart if the Channel is channels.yml
     * @param channel The Channel to be unregistered
     */
    @Override
    public void unregisterChannel(@NotNull Channel channel) {
        if(!channels.containsKey(channel.getName())) {
            throw new NoSuchElementException("Channel '" + channel.getName() + "' is not registered.");
        }

        Set<User> users = strings.getUserUtil().getUsers();
        Channel defaultChannel = strings.getChannel("default");
        for(User user : users){
            if(user.getActiveChannel().equals(channel)) {
                user.setActiveChannel(defaultChannel);
                user.leaveChannel(channel);
            }
        }

        for(Map.Entry<String, Channel> entry : channelSymbols.entrySet()) {
            if(entry.getValue().equals(channel)) {
                channelSymbols.remove(entry.getKey());
            }
        }

        worldChannels.remove(channel);
        priorityChannels.remove(channel);
        channels.remove(channel.getName());
        setToChanged();
    }

    /**
     * Saves a Channel to channels.yml, using the Channel's getData() implementation
     * @param channel The channel to be saved
     */
    @Override
    public void saveChannel(@NotNull Channel channel) {
        if (channel.getType() == Type.PROTECTED) {
            log("Unable to save protected channels, they must be modified in channels.yml");
            return;
        }

        Map<String, Object> dataMap = channel.getData();
        String channelName = channel.getName();
        ConfigurationSection channelSection = config.getConfigurationSection("channels." + channelName);
        if(channelSection == null) {
            channelSection = config.createSection("channels." + channelName);
        }
        dataMap.forEach(channelSection::set);
        strings.saveChannelsFile();
    }

    @Override
    public Channel build(ChannelData data, @NotNull String type) throws UnsupportedOperationException {
        type = type.toLowerCase();
        switch(type){
            case "stringchannel" ->  { return new StringChannel(strings, data); }
            case "proximity" ->  { return new ProximityChannel(strings, data); }
            case "world" -> { return new WorldChannel(strings, data); }
            case "proximity_strict" -> { return new StrictProximityChannel(strings, data); }
            case "world_strict" -> { return new StrictWorldChannel(strings, data); }
            case "helpop" -> { return new HelpOPChannel(strings, data); }
            default -> throw new UnsupportedOperationException("Unable to build Channels that are not Types NORMAL, PROXIMITY, WORLD. PROTECTED Channels must be HELPOP Channels");
        }
    }

    private void log(String message) {
        strings.getLogger().info(message);
    }

    public Map<String, Channel> getChannelSymbols() {
        return new HashMap<>(channelSymbols);
    }

    public void addChannelSymbol(String symbol, Channel channel){
        channelSymbols.put(symbol, channel);
    }

    public List<Channel> getChannelList() {
        return new ArrayList<>(channels.values());
    }


    private boolean changed = false;

    /**
     * Signifies that something with Channels has changed and datasets should be updated when called.
     */
    private void setToChanged() {
        changed = true;
        count = 0;
    }

    private boolean isChanged() {
        return changed;
    }


    int count = 0;

    /**
     * Signifies that a dataset has been updated
     */
    private void datasetUpdated() {
        count++;
        if(count == 4) {
            count = 0;
            changed = false;
        }
    }

    private List<Channel> nonProtectedChannels = new ArrayList<>();
    public List<Channel> getNonProtectedChannels() {
        if(isChanged() || nonProtectedChannels == null) {
            nonProtectedChannels = getChannelList().stream()
                    .filter(channel -> channel.getType() != Type.PROTECTED)
                    .toList();
            datasetUpdated();
        }
        return new ArrayList<>(nonProtectedChannels);
    }

    private List<String> nonProtectedChannelNames;
    @SuppressWarnings("unused")
    public List<String> getNonProtectedChannelNames() {
        if(isChanged() || nonProtectedChannelNames == null) {
            nonProtectedChannelNames = getChannelList().stream()
                    .filter(channel -> channel.getType() != Type.PROTECTED)
                    .map(Channel::getName)
                    .toList();
            datasetUpdated();
        }
        return new ArrayList<>(nonProtectedChannelNames);
    }


    private Set<Channel> protectedChannels;
    public Set<Channel> getProtectedChannels() {
        if(isChanged() || protectedChannels == null) {
            protectedChannels = getChannelList().stream()
                    .filter(channel -> channel.getType() == Type.PROTECTED)
                    .collect(Collectors.toSet());
            datasetUpdated();
        }
        return new HashSet<>(protectedChannels);
    }


    private final Map<World, Set<Channel>> worldChannelMap = new HashMap<>();
    public Set<Channel> getChannels(World world) {
        if(isChanged()) {
            for(World w : Bukkit.getWorlds()) {
                updateWorldChannelMap(w);
            }
            datasetUpdated();
        } else {
            if(worldChannelMap.get(world) == null) {
                updateWorldChannelMap(world);
            }
        }
        return new HashSet<>(worldChannelMap.get(world));
    }

    private void updateWorldChannelMap(World world) {
        worldChannelMap.put(world, worldChannels.stream()
                .filter(channel -> {
                    if(channel instanceof LocalChannel local) {
                        return local.getWorlds().contains(world);
                    }
                    return false;
                })
                .collect(Collectors.toSet())
        );
    }

    public SortedSet<Channel> getSortedChannelSet() {
        return new TreeSet<>(priorityChannels);
    }

}
