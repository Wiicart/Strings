package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.channel.DefaultChannel;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Loads and stores Channels
 */
public final class ChannelManager implements ChannelLoader {

    @NotNull
    private final Strings strings;

    private final HashMap<String, Channel> channels;
    private final FileConfiguration config;
    private final HashMap<String, Channel> channelSymbols;
    private final TreeSet<Channel> priorityChannels;
    private final UserUtil userUtil;

    public ChannelManager(@NotNull Strings strings) {
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
        Objects.requireNonNull(name);
        Channel channel = channels.get(name);
        //Refresh to check for a change.
        if(channel == null) {
            refreshChannelMap();
            channel = channels.get(name);
        }
        if(!channel.getName().equals(name)) {
            refreshChannelMap();
            channel = channels.get(name);
        }
        return channel;
    }

    private void refreshChannelMap() {
        Set<Channel> channelSet = getChannels();
        channels.clear();
        for(Channel channel : channelSet) {
            Channel previous = channels.putIfAbsent(channel.getName(), channel);
            if(previous != null) {
                strings.warning("Two or more Channels are registered with the name '" + channel.getName() + "'.");
                strings.warning("Channel " + channel.getName() + " with type identifier '" + channel.getIdentifier() + "' is being skipped.");
            }
        }
    }

    @Override
    public @NotNull Set<Channel> getChannels() {
        return new HashSet<>(channels.values());
    }

    /**
     * Registers a Channel with the plugin
     * @param channel The channel to be registered
     */
    @Override
    public void register(@NotNull Channel channel) {
        Objects.requireNonNull(channel, "Channel cannot be null");
        String channelName = channel.getName();
        if(channels.containsKey(channelName)) {
            strings.warning("Failed to register Channel.");
            strings.warning("A Channel with the name '" + channelName + "' already exists, channels with the same name cannot be registered.");
            return;
        }

        if(channel.getPriority() > -1) {
            priorityChannels.add(channel);
        }

        channels.put(channel.getName(), channel);
        strings.info("Channel '" + channel.getName() + "' registered.");
    }

    /**
     * Unregisters a Channel from the plugin, but does not remove it from Channels.yml
     * Does not persist after restart if the Channel is channels.yml
     * @param channel The Channel to be unregistered
     */
    @Override
    public void unregister(@NotNull Channel channel) {
        Objects.requireNonNull(channel, "Channel cannot be null");
        if(!channels.containsKey(channel.getName())) {
            throw new NoSuchElementException("Channel '" + channel.getName() + "' is not registered.");
        }

        Set<User> users = strings.getUserUtil().getUsers();

        Channel defaultChannel = Objects.requireNonNullElseGet(getChannel("default"), () -> {
            Channel c = new DefaultChannel(strings, this);
            register(c);
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
    }

    public @NotNull Channel getDefaultChannel() {
        Channel defaultChannel = channels.get("default");
        if(defaultChannel == null) {
            defaultChannel = new DefaultChannel(strings, this);
            register(defaultChannel);
        }
        return defaultChannel;
    }

    /**
     * Saves a Channel to channels.yml, using the Channel's getData() implementation
     * @param channel The channel to be saved
     */
    @Override
    public void save(@NotNull Channel channel) {
        Objects.requireNonNull(channel, "Cannot save null channel");
        if (channel.getType() == Type.PROTECTED) {
            strings.info("Unable to save protected channels, they must be modified in channels.yml");
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

    @Contract(value = " -> new", pure = true)
    public @NotNull Map<String, Channel> getChannelSymbols() {
        return new HashMap<>(channelSymbols);
    }

    public void addChannelSymbol(String symbol, Channel channel) {
        channelSymbols.put(symbol, channel);
    }

    @Contract(value = " -> new", pure = true)
    public @NotNull SortedSet<Channel> getSortedChannelSet() {
        return new TreeSet<>(priorityChannels);
    }
}
