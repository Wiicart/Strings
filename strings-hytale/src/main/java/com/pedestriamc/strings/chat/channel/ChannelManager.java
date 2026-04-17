package com.pedestriamc.strings.chat.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.channel.local.ProximityChannel;
import com.pedestriamc.strings.common.channel.AbstractChannelLoader;
import org.jetbrains.annotations.NotNull;

public class ChannelManager extends AbstractChannelLoader {

    private final Strings strings;

    public ChannelManager(@NotNull Strings strings) {
        super(strings);
        this.strings = strings;
        strings.channelStore().loadChannels(strings);
    }

    @Override
    public void save(@NotNull Channel channel) {
        ChannelStore store = strings.channelStore();
        ChannelEntry entry = store.getEntry(channel.getName());
        if (entry == null) {
            strings.warning("Failed to save channel " + channel.getName());
            return;
        }

        entry.name = channel.getName();
        entry.defaultColor = channel.getDefaultColor();
        entry.format = channel.getFormat();
        entry.broadcastFormat = channel.getBroadcastFormat();
        entry.priority = channel.getPriority();
        entry.membership = channel.getMembership();
        entry.doCooldown = channel.isCooldownEnabled();
        entry.doProfanityFilter = channel.isProfanityFiltering();
        entry.doUrlFilter = channel.isUrlFiltering();
        entry.allowMessageDeletion = channel.allowsMessageDeletion();

        if (channel instanceof LocalChannel<?> local) {
            entry.worlds = local.getWorlds().stream()
                    .map(Locality::getName)
                    .toList();

            if (channel instanceof ProximityChannel<?> proximity) {
                entry.proximity = proximity.getProximity();
            }
        }

        strings.saveChannelStore();
    }

}
