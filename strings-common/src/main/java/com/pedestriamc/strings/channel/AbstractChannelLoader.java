package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Implements storage and retrieval of Channels,
 * subclasses must implement Channel loading and saving logic.
 * Subclasses should also call ResolutionValidator when appropriate.
 */
public abstract class AbstractChannelLoader implements ChannelLoader {

    private final StringsPlatform strings;

    private final Map<String, Channel> channels = new HashMap<>();
    private final TreeSet<Channel> prioritySet = new TreeSet<>();
    private final SortedSet<Channel> unmodifiablePrioritySet = Collections.unmodifiableSortedSet(prioritySet);
    private final Map<String, Channel> symbols = new HashMap<>();
    private final Channel defaultChannel;

    protected AbstractChannelLoader(@NotNull StringsPlatform strings) {
        this.strings = strings;
        defaultChannel = loadAndRegisterDefaultChannel();
    }


    @Override
    public void register(@NotNull Channel channel) {
        String name = channel.getName();
        if (channels.containsKey(name)) {
            strings.warning("Failed to register Channel.");
            strings.warning(String.format("A Channel with name '%s' already exists.", name));
            return;
        }

        if (channel.getPriority() > -1) {
            prioritySet.add(channel);
        }

        channels.put(channel.getName(), channel);
        strings.info("Channel '" + channel.getName() + "' registered.");
    }

    @Override
    public void unregister(@NotNull Channel channel) throws NoSuchElementException {
        refresh(); // ensure the channel map is up to date w/ name
        if (!channels.containsKey(channel.getName())) {
            throw new NoSuchElementException("Channel '" + channel.getName() + "' is not registered.");
        }

        for (StringsUser user : channel.getMembers()) {
            if (user.getActiveChannel().equals(channel)) {
                user.setActiveChannel(defaultChannel);
                user.leaveChannel(channel);
                strings.users().saveUser(user);
            }
        }

        for (Map.Entry<String, Channel> entry : symbols.entrySet()) {
            if (entry.getValue().equals(channel)) {
                symbols.remove(entry.getKey());
            }
        }

        prioritySet.remove(channel);
        channels.remove(channel.getName());
    }

    @Override
    public @Nullable Channel getChannel(@NotNull String name) {
        Channel channel = channels.get(name);
        if (channel == null) {
            refresh();
            channel = channels.get(name);
        }

        return channel;
    }

    @Override
    public @NotNull Set<Channel> getChannels() {
        return Set.copyOf(channels.values());
    }

    @Override
    public void refresh() {
        synchronized (channels) {
            Map<String, Channel> refreshed = new HashMap<>();
            for (Channel c : getChannels()) {
                refreshed.put(c.getName(), c);
            }

            channels.clear();
            channels.putAll(refreshed);
        }
    }

    @Override
    @NotNull
    public SortedSet<Channel> getSortedChannelSet() {
        return unmodifiablePrioritySet;
    }

    @Override
    @NotNull
    public Channel getDefaultChannel() {
        return defaultChannel;
    }

    @NotNull
    private Channel loadAndRegisterDefaultChannel() {
        Channel channel = new DefaultChannel(strings);
        register(channel);
        return channel;
    }

    @Override
    @NotNull
    public Map<String, Channel> getChannelSymbols() {
        return Collections.unmodifiableMap(symbols);
    }

    @Override
    public void registerChannelSymbol(@NotNull String symbol, @NotNull Channel channel) {
        symbols.put(symbol, channel);
    }
}
