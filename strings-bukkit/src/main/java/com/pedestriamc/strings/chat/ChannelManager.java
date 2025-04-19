package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.data.ChannelData;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.chat.channel.DefaultChannel;
import com.pedestriamc.strings.chat.channel.HelpOPChannel;
import com.pedestriamc.strings.chat.channel.local.ProximityChannel;
import com.pedestriamc.strings.chat.channel.local.StrictProximityChannel;
import com.pedestriamc.strings.chat.channel.local.StrictWorldChannel;
import com.pedestriamc.strings.chat.channel.StringChannel;
import com.pedestriamc.strings.chat.channel.local.WorldChannel;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserUtil;
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
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Stores Channels
 */
public final class ChannelManager implements ChannelLoader {

    private final Strings strings;
    private final HashMap<String, Channel> channels;
    private final FileConfiguration config;
    private final HashMap<String, Channel> channelSymbols;
    private final TreeSet<Channel> priorityChannels;
    private final UserUtil userUtil;

    public ChannelManager(Strings strings) {
        this.strings = strings;
        channels = new HashMap<>();
        config = strings.files().getChannelsFileConfig();
        channelSymbols = new HashMap<>();
        priorityChannels = new TreeSet<>();
        userUtil = strings.getUserUtil();
    }

    public void loadChannels() {
        ChannelFileReader reader = new ChannelFileReader(strings, config, this);
        reader.read();
    }

    @Override
    public @Nullable Channel getChannel(@NotNull String name) {
        return channels.get(name);
    }

    public Set<Channel> getChannels() {
        return new HashSet<>(channels.values());
    }

    /**
     * Registers a Channel with the plugin
     * @param channel The channel to be registered
     */
    @Override
    public void registerChannel(@NotNull Channel channel) {
        Objects.requireNonNull(channel, "Channel cannot be null");
        String channelName = channel.getName();
        if(channels.containsKey(channelName)) {
            log("A Channel with the name '" + channelName + "' already exists, channels with the same name cannot be registered.");
            return;
        }

        if(channel.getPriority() > -1) {
            priorityChannels.add(channel);
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
    @Override
    public void unregisterChannel(@NotNull Channel channel) {
        Objects.requireNonNull(channel, "Channel cannot be null");
        if(!channels.containsKey(channel.getName())) {
            throw new NoSuchElementException("Channel '" + channel.getName() + "' is not registered.");
        }

        Set<User> users = strings.getUserUtil().getUsers();

        Channel defaultChannel = Objects.requireNonNullElseGet(getChannel("default"), () -> {
            Channel c = new DefaultChannel(strings, this);
            registerChannel(c);
            return c;
        });

        for(User user : users) {
            if(user.getActiveChannel().equals(channel)) {
                user.setActiveChannel(defaultChannel);
                user.leaveChannel(channel);
                userUtil.saveUser(user);
            }
        }

        for(Map.Entry<String, Channel> entry : channelSymbols.entrySet()) {
            if(entry.getValue().equals(channel)) {
                channelSymbols.remove(entry.getKey());
            }
        }

        priorityChannels.remove(channel);
        channels.remove(channel.getName());
        setToChanged();
    }

    @SuppressWarnings("unused")
    public Channel getDefaultChannel() {
        Channel defaultChannel = channels.get("default");
        if(defaultChannel == null) {
            defaultChannel = new DefaultChannel(strings, this);
            registerChannel(defaultChannel);
        }
        return defaultChannel;
    }

    /**
     * Saves a Channel to channels.yml, using the Channel's getData() implementation
     * @param channel The channel to be saved
     */
    @Override
    public void saveChannel(@NotNull Channel channel) {
        Objects.requireNonNull(channel, "Cannot save null channel");
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
        strings.files().saveChannelsFile();
    }

    @Override
    public Channel build(ChannelData data, @NotNull String type) throws UnsupportedOperationException {
        type = type.toLowerCase();
        switch(type) {
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

    public void addChannelSymbol(String symbol, Channel channel) {
        channelSymbols.put(symbol, channel);
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
        if(count == 3) {
            count = 0;
            changed = false;
        }
    }

    public SortedSet<Channel> getSortedChannelSet() {
        return new TreeSet<>(priorityChannels);
    }

    private List<Channel> nonProtectedChannels = new ArrayList<>();
    public List<Channel> getNonProtectedChannels() {
        if(isChanged() || nonProtectedChannels == null) {
            nonProtectedChannels = getChannels().stream()
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
            nonProtectedChannelNames = getChannels().stream()
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
            protectedChannels = getChannels().stream()
                    .filter(channel -> channel.getType() == Type.PROTECTED)
                    .collect(Collectors.toSet());
            datasetUpdated();
        }
        return new HashSet<>(protectedChannels);
    }
}
