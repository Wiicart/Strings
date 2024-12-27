package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.channels.LocalChannel;
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
        log("Channel '" + channel.getName() + "' registered.");
    }

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
    }

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

    public List<Channel> getNonProtectedChannels() {
        return getChannelList().stream()
                .filter(channel -> channel.getType() != Type.PROTECTED)
                .toList();
    }

    public List<String> getNonProtectedChannelNames() {
        return getNonProtectedChannels().stream()
                .map(Channel::getName)
                .toList();
    }

    public List<Channel> getProtectedChannels() {
        return getChannelList().stream()
                .filter(channel -> channel.getType() == Type.PROTECTED)
                .toList();
    }

    public List<Channel> getChannelsByPriority() {
        return new ArrayList<>(prioritySorted);
    }

    public List<Channel> getWorldPriorityChannels(World world) {
        return getChannels(world).stream()
                .filter(channel -> channel.getMembership() == Membership.DEFAULT)
                .sorted(Comparator.comparingInt(Channel::getPriority).reversed())
                .toList();
    }

    public Set<Channel> getChannels(World world) {
        return worldChannels.stream()
                .filter(channel -> {
                    if(channel instanceof WorldChannel w) {
                        return w.getWorlds().contains(world);
                    }

                    if(channel instanceof ProximityChannel p) {
                        return p.getWorlds().contains(world);
                    }

                    return false;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public @Nullable LocalChannel asLocalChannel(Channel channel) {

        if(channel instanceof LocalChannel) {
            return (LocalChannel) channel;
        }

        return null;

    }

}
