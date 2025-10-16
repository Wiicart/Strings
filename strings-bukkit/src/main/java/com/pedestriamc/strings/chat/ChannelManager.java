package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.channel.AbstractChannelLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

/**
 * Loads and stores Channels
 */
public final class ChannelManager extends AbstractChannelLoader {

    private final Strings strings;

    private final FileConfiguration config;

    public ChannelManager(@NotNull Strings strings) {
        super(strings);
        this.strings = strings;
        config = strings.files().getChannelsFileConfig();
    }

    public void loadChannels() {
        ChannelFileReader.loadChannels(strings, config, this);
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

}
