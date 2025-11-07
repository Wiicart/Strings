package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.channel.AbstractChannelLoader;
import com.pedestriamc.strings.channel.ResolutionValidator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
        if (strings.getSettings().get(Option.Bool.ENABLE_RESOLUTION_VALIDATION)) {
            Set<Locality<World>> localities = Locality.convertToLocalities(Bukkit.getWorlds());
            ResolutionValidator<Locality<World>> validator = new ResolutionValidator<>(this, localities);
            strings.info(validator.generateReport());
        }
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
