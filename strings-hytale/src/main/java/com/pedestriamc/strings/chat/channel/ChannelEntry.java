package com.pedestriamc.strings.chat.channel;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.api.channel.data.IChannelBuilder;
import com.pedestriamc.strings.api.channel.data.IChannelBuilder.Identifier;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.Locality;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChannelEntry {

    static final BuilderCodec<ChannelEntry> CODEC = BuilderCodec.builder(ChannelEntry.class, ChannelEntry::new)
            .append(new KeyedCodec<>("Name", Codec.STRING),
                    (config, value) -> config.name = value,
                    config -> config.name).add()
            .append(new KeyedCodec<>("DefaultColor", Codec.STRING),
                    (config, value) -> config.defaultColor = value,
                    config -> config.defaultColor).add()
            .append(new KeyedCodec<>("Format", Codec.STRING),
                    (config, value) -> config.format = value,
                    config -> config.format).add()
            .append(new KeyedCodec<>("Type", Codec.STRING),
                    (config, value) -> config.type = Identifier.of(value),
                    config -> config.type.toString()).add()
            .append(new KeyedCodec<>("Membership", Codec.STRING),
                    (config, value) -> config.membership = Membership.valueOf(value.toUpperCase()),
                    config -> config.membership.toString().toLowerCase()).add()
            .append(new KeyedCodec<>("Priority", Codec.INTEGER),
                    (config, value) -> config.priority = value,
                    config -> config.priority).add()
            .append(new KeyedCodec<>("BroadcastFormat", Codec.STRING),
                    (config, value) -> config.broadcastFormat = value,
                    config -> config.broadcastFormat).add()
            .append(new KeyedCodec<>("EnableCooldown", Codec.BOOLEAN),
                    (config, value) -> config.doCooldown = value,
                    config -> config.doCooldown).add()
            .append(new KeyedCodec<>("EnableProfanityFilter", Codec.BOOLEAN),
                    (config, value) -> config.doProfanityFilter = value,
                    config -> config.doProfanityFilter).add()
            .append(new KeyedCodec<>("EnableUrlFilter", Codec.BOOLEAN),
                    (config, value) -> config.doUrlFilter = value,
                    config -> config.doUrlFilter).add()
            .append(new KeyedCodec<>("EnableMessageDeletion", Codec.BOOLEAN),
                    (config, value) -> config.allowMessageDeletion = value,
                    config -> config.allowMessageDeletion).add()
            .append(new KeyedCodec<>("Proximity", Codec.DOUBLE),
                    (config, value) -> config.proximity = value,
                    config -> config.proximity).add()
            .append(new KeyedCodec<>("Worlds", Codec.STRING_ARRAY),
                    (config, value) -> config.worlds = List.of(value),
                    config -> config.worlds.toArray(new String[0])).add()
            .build();

    public static @NotNull ChannelEntry global() {
        ChannelEntry entry = new ChannelEntry();
        entry.name = "global";
        entry.defaultColor = "white";
        entry.format = "{prefix} {displayname} {suffix} &7» &f{message}";
        entry.type = Identifier.NORMAL;
        entry.membership = Membership.DEFAULT;
        entry.priority = 1;

        return entry;
    }

    public static @NotNull ChannelEntry staff() {
        ChannelEntry entry = new ChannelEntry();
        entry.name = "staff";
        entry.defaultColor = "white";
        entry.format = "[Staff] {displayname} {suffix} &7» &f{message}";
        entry.type = Identifier.NORMAL;
        entry.membership = Membership.PERMISSION;
        entry.priority = 1;

        return entry;
    }

    String name = "UNKNOWN_CHANNEL";
    String defaultColor = "white";
    String format = "{displayname} {message}";

    String broadcastFormat = "{broadcast}";

    int priority = 100;

    /**
     * Specific channel implementation, more detail than Type.
     */
    Identifier type = Identifier.NORMAL;
    Membership membership = Membership.PERMISSION;

    boolean doCooldown = false;
    boolean doProfanityFilter = false;
    boolean doUrlFilter = false;
    boolean allowMessageDeletion = false;

    /**
     * The following two ONLY necessary if type.isLocal()
     */
    double proximity = -1.0;
    List<String> worlds = List.of();

    @SuppressWarnings("unchecked")
    Channel toChannel(@NotNull Strings strings) {
        boolean isLocal = type.isLocal();
        IChannelBuilder<?> builder = isLocal ?
                new LocalChannelBuilder<>(name, format, membership, loadWorlds(strings)) :
                new ChannelBuilder(name, format, membership);

        if (type == Identifier.PROXIMITY || type == Identifier.PROXIMITY_STRICT) {
            ((LocalChannelBuilder<World>) builder).setDistance(proximity);
        }

        builder.setDoCooldown(doCooldown)
                .setDoProfanityFilter(doProfanityFilter)
                .setDoUrlFilter(doUrlFilter)
                .setAllowMessageDeletion(allowMessageDeletion);

        return builder.build(type);
    }

    public String name() {
        return name;
    }

    private Set<Locality<World>> loadWorlds(@NotNull Strings strings) {
        Universe universe = strings.universe();
        Set<World> hytaleWorlds = new HashSet<>();
        for (String worldName : worlds) {
            World world = universe.getWorld(worldName);
            if (world != null) {
                hytaleWorlds.add(world);
            } else {
                strings.warning("Failed to find world: " + worldName + " when loading channel " + name);
            }
        }

        return strings.localityManager().convertToLocalities(hytaleWorlds);
    }

}
