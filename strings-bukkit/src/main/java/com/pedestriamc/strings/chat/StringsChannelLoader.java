package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channels.Membership;
import com.pedestriamc.strings.api.channels.data.ChannelData;
import com.pedestriamc.strings.api.channels.ChannelLoader;
import com.pedestriamc.strings.api.channels.Type;
import com.pedestriamc.strings.api.channels.Channel;
import com.pedestriamc.strings.chat.channels.HelpOPChannel;
import com.pedestriamc.strings.chat.channels.ProximityChannel;
import com.pedestriamc.strings.chat.channels.StringChannel;
import com.pedestriamc.strings.chat.channels.WorldChannel;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.YamlUserUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StringsChannelLoader implements ChannelLoader {

    private final Strings strings;
    private final ConcurrentHashMap<String, Channel> channels;
    private final FileConfiguration config;
    private final HashMap<String, Channel> channelSymbols;
    private final List<Channel> prioritySorted;
    private final Set<Channel> worldChannels;

    public StringsChannelLoader(Strings strings) {
        this.strings = strings;
        this.channels = new ConcurrentHashMap<>();
        this.config = strings.getChannelsFileConfig();
        this.channelSymbols = new HashMap<>();
        this.prioritySorted = new ArrayList<>();
        this.worldChannels = new HashSet<>();

        ChannelFileReader reader = new ChannelFileReader(strings, config, this);
        reader.read();
    }

    @Override
    public @Nullable Channel getChannel(String name) {
        return channels.get(name);
    }

    /**
     * Registers a Channel with the plugin
     * @param channel The channel to be registered
     */
    @Override
    public void registerChannel(Channel channel) {

        String channelName = channel.getName();

        if(channels.containsKey(channelName)) {
            log("A Channel with the name '" + channelName + "' already exists, channels with the same name cannot be registered.");
            return;
        }

        if(channel.getMembership() == Membership.DEFAULT) {
            prioritySorted.add(channel);
            prioritySorted.sort(Comparator.comparingInt(Channel::getPriority).reversed());
        }

        if(channel instanceof WorldChannel || channel instanceof ProximityChannel) {
            worldChannels.add(channel);
        }

        channels.put(channel.getName(), channel);
        setToChanged();
        log("Channel '" + channel.getName() + "' registered.");
    }

    /**
     * Unregisters a Channel from the plugin, but does not remove it from Channels.yml
     * Does not persist after restart if the Channel is channels.yml
     * @param channel The Channel to be unregistered
     */
    public void unregisterChannel(Channel channel) {

        Collection<User> users = YamlUserUtil.UserMap.getUserSet();
        Channel global = strings.getChannel("global");
        for(User user : users){
            if(user.getActiveChannel().equals(channel)) {
                user.setActiveChannel(global);
                user.leaveChannel(channel);
            }
        }

        for(Map.Entry<String, Channel> entry : channelSymbols.entrySet()) {
            if(entry.getValue().equals(channel)) {
                channelSymbols.remove(entry.getKey());
            }
        }

        worldChannels.remove(channel);
        prioritySorted.remove(channel);
        channels.remove(channel.getName());
        setToChanged();
    }

    /**
     * Saves a Channel to channels.yml, using the Channel's getData() implementation
     * @param channel The channel to be saved
     */
    @Override
    public void saveChannel(Channel channel) {

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
    public Channel build(ChannelData data, @NotNull Type type) throws UnsupportedOperationException {

        switch(type){

            case NORMAL ->  { return new StringChannel(strings, data); }

            case PROXIMITY ->  { return new ProximityChannel(strings, data); }

            case WORLD -> { return new WorldChannel(strings, data); }

            case PROTECTED -> {
                if(!data.getName().equals("helpop")) {
                    throw new UnsupportedOperationException("Building with Channels that are Type PROTECTED only supports helpop channels");
                }
                return new HelpOPChannel(strings, data);
            }

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


    private List<String> nonProtectedChannelNames;

    public List<String> getNonProtectedChannelNames() {
        if(isChanged() || nonProtectedChannelNames == null) {
            nonProtectedChannelNames = getChannelList().stream()
                    .filter(channel -> channel.getType() != Type.PROTECTED)
                    .map(Channel::getName)
                    .toList();
            datasetUpdated();
        }
        return  nonProtectedChannelNames;
    }


    private Set<Channel> protectedChannels;

    public Set<Channel> getProtectedChannels() {
        if(isChanged() || protectedChannels == null) {
            protectedChannels = getChannelList().stream()
                    .filter(channel -> channel.getType() == Type.PROTECTED)
                    .collect(Collectors.toSet());
            datasetUpdated();
        }
        return protectedChannels;
    }


    private final Map<World, Set<Channel>> worldPriorityChannelMap = new HashMap<>();

    public Set<Channel> getWorldPriorityChannels(World world) {
        if(isChanged()) {
            for(World w : Bukkit.getWorlds()) {
                updateWorldPriorityChannelMap(w);
            }
        } else if(worldPriorityChannelMap.get(world) == null) {
            updateWorldPriorityChannelMap(world);
        }
        return worldPriorityChannelMap.get(world);
    }

    private void updateWorldPriorityChannelMap(World world) {
        worldPriorityChannelMap.put(world, getChannels(world).stream()
                .filter(channel -> channel.getMembership() == Membership.DEFAULT)
                .sorted(Comparator.comparingInt(Channel::getPriority).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new)));
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
        return worldChannelMap.get(world);
    }

    private void updateWorldChannelMap(World world) {
        worldChannelMap.put(
                world,
                worldChannels.stream()
                        .filter(channel -> {
                            if(channel instanceof WorldChannel w) {
                                return w.getWorlds().contains(world);
                            }

                            if(channel instanceof ProximityChannel p) {
                                return p.getWorlds().contains(world);
                            }

                            return false;
                        })
                        .collect(Collectors.toSet())
        );
    }

    public List<Channel> getChannelsByPriority() {
        return new ArrayList<>(prioritySorted);
    }

}
