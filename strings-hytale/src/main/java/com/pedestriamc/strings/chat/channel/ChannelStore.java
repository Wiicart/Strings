package com.pedestriamc.strings.chat.channel;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Only intended for use by {@link ChannelManager}. Refer to methods there to modify Channel files.
 */
public class ChannelStore {

    static final ArrayCodec<ChannelEntry> CHANNEL_ARRAY_CODEC = new ArrayCodec<>(ChannelEntry.CODEC, ChannelEntry[]::new);

    public static final BuilderCodec<ChannelStore> CODEC = BuilderCodec.builder(ChannelStore.class, ChannelStore::new)
            .append(new KeyedCodec<>("Channels", CHANNEL_ARRAY_CODEC),
                    (config, value) -> config.channels = value,
                    config -> config.channels).add()
            .build();

    private ChannelEntry[] channels = new ChannelEntry[]{ChannelEntry.global(), ChannelEntry.staff()};

    void loadChannels(@NotNull Strings strings) {
        ChannelLoader loader = strings.getChannelLoader();
        for (ChannelEntry entry : channels) {
            strings.info("Loading channel '" + entry.name() + "'");
            try {
                loader.register(entry.toChannel(strings));
            } catch (Exception e) {
                strings.warning("An error occurred while loading channel '" + entry.name() + "':");
                strings.warning(e.getMessage());
            }
        }
    }

    @Nullable
    ChannelEntry getEntry(@NotNull String name) {
        for (ChannelEntry entry : channels) {
            if (entry.name().equals(name)) {
                return entry;
            }
        }
        return null;
    }
}
