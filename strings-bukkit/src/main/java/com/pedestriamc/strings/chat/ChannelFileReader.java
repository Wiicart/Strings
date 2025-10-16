package com.pedestriamc.strings.chat;

import com.google.common.base.Preconditions;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.IChannelBuilder;
import com.pedestriamc.strings.api.channel.data.IChannelBuilder.Identifier;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.text.format.StringsTextColor;
import com.pedestriamc.strings.channel.HelpOPChannel;
import com.pedestriamc.strings.channel.SocialSpyChannel;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

final class ChannelFileReader {
    
    private static final Set<Identifier> LOCAL_TYPES = Set.of(
            Identifier.WORLD, Identifier.WORLD_STRICT, Identifier.PROXIMITY, Identifier.PROXIMITY_STRICT
    );

    private final @NotNull Strings strings;
    private final @NotNull ChannelManager manager;

    static void loadChannels(@NotNull Strings strings, @NotNull FileConfiguration config, @NotNull ChannelManager manager) {
        new ChannelFileReader(strings, config, manager);
    }

    private ChannelFileReader(@NotNull Strings strings, @NotNull FileConfiguration config, @NotNull ChannelManager manager) {
        this.strings = strings;
        this.manager = manager;

        ConfigurationSection channels = config.getConfigurationSection("channels");
        if(channels == null) {
            strings.warning("No Channels defined in channels.yml, disabling plugin.");
            strings.getServer().getPluginManager().disablePlugin(strings);
            return;
        }

        read(channels);
        registerDefaults();
    }

    private void registerDefaults() {
        if(manager.getChannel("global") == null) {
            registerGlobal();
        }

        if(manager.getChannel("helpop") == null) {
            registerHelpOp();
        }

        String socialSpyFormat = strings.getSettings().get(Option.Text.SOCIAL_SPY_FORMAT);
        manager.register(new SocialSpyChannel(strings.getPlayerDirectMessenger(), socialSpyFormat));
    }

    private void read(@NotNull ConfigurationSection channels) {
        for(String name : channels.getKeys(false)) {
            ConfigurationSection section = channels.getConfigurationSection(name);
            if(section != null) {
                strings.info("Loading channel '" + name + "'");
                try {
                    loadChannel(name, section);
                } catch(Exception e) {
                    strings.warning("An error occurred while loading channel '" + name + "':");
                    strings.warning(e.getMessage());
                }
            } else {
                strings.warning("An error occurred while loading channel '" + name + "'");
            }
        }
    }

    private void loadChannel(@NotNull String name, @NotNull ConfigurationSection section) {
        String format = section.getString("format");
        Preconditions.checkNotNull(format, "Channel format cannot be null.");

        Identifier identifier = getTypeIdentifier(section); // throws IllegalArgumentException
        Membership membership = getMembership(section); // throws IllegalArgumentException

        IChannelBuilder<?> builder;
        if (isLocal(identifier)) {
            LocalChannelBuilder<World> localBuilder = Channel.localBuilder(
                    name,
                    format,
                    membership,
                    loadWorlds(section)
            );

            if (isProximity(identifier)) {
                localBuilder.setDistance(getDistance(section));
            }

            builder = localBuilder;
        } else {
            builder = Channel.builder(name, format, membership);
        }

        builder
                .setDefaultColor(section.getString("default-color", StringsTextColor.WHITE.toString()))
                .setDoCooldown(section.getBoolean("cooldown", false))
                .setDoProfanityFilter(section.getBoolean("filter-profanity", false))
                .setDoUrlFilter(section.getBoolean("block-urls", false))
                .setCallEvent(section.getBoolean("call-event", true))
                .setAllowMessageDeletion(section.getBoolean("message-deletion", false))
                .setPriority(section.getInt("priority", -1))
                .setBroadcastFormat(section.getString("broadcast-format", "&8[&cBroadcast&8] &f{message}"));
        loadBroadcastSound(builder, section);

        Channel channel = builder.build(identifier);
        manager.register(channel);

        String symbol = section.getString("symbol");
        if(symbol != null && !symbol.isBlank()) {
            manager.registerChannelSymbol(symbol, channel);
        }
    }

    private @NotNull Membership getMembership(@NotNull ConfigurationSection section) {
        String membershipString = section.getString("membership");
        Preconditions.checkNotNull(membershipString, "Channel membership cannot be null.");
        try {
            return Membership.valueOf(membershipString.toUpperCase(Locale.ROOT));
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid channel membership: " + membershipString);
        }
    }

    private @NotNull Identifier getTypeIdentifier(@NotNull ConfigurationSection section) {
        String type = section.getString("type");
        Preconditions.checkNotNull(type, "Channel type cannot be null.");

        try {
            return Identifier.of(type);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid channel type: '" + type + "'");
        }
    }

    private boolean isProximity(@NotNull Identifier identifier) {
        return identifier == Identifier.PROXIMITY || identifier == Identifier.PROXIMITY_STRICT;
    }

    private double getDistance(@NotNull ConfigurationSection section) {
        double distance = section.getDouble("distance");
        if(distance < 1) {
            throw new IllegalArgumentException("ProximityChannels must have a positive distance value.");
        }
        return distance;
    }

    private @NotNull Set<Locality<World>> loadWorlds(@NotNull ConfigurationSection section) {
        Set<Locality<World>> worlds = new HashSet<>();
        String legacyWorldName = section.getString("world");
        if(legacyWorldName != null) {
            World world = Bukkit.getWorld(legacyWorldName);
            if(world != null) {
                worlds.add(Locality.of(world));
            } else {
                strings.warning("Unknown world '" + legacyWorldName + "' defined, skipping...");
            }
        }

        List<String> list = section.getStringList("worlds");
        for(String str : list) {
            World world = Bukkit.getWorld(str);
            if (world != null) {
                worlds.add(Locality.of(world));
            } else {
                strings.warning("Unknown world '" + str + "' defined, skipping...");
            }
        }

        if(worlds.isEmpty()) {
            throw new IllegalArgumentException("No worlds found, LocalChannels must define one or more worlds.");
        }

        return worlds;
    }

    @SuppressWarnings("PatternValidation")
    private void loadBroadcastSound(@NotNull IChannelBuilder<?> builder, @NotNull ConfigurationSection section) {
        ConfigurationSection soundSection = section.getConfigurationSection("broadcast-sound");
        if (soundSection == null) {
            return;
        }

        if (soundSection.getBoolean("enable")) {
            String name = soundSection.getString("name", "");
            try {
                Key key = Key.key(name);
                Sound.Builder soundBuilder = Sound.sound().type(key);
                if (soundSection.contains("pitch")) {
                    soundBuilder.pitch((float) soundSection.getDouble("pitch"));
                }

                if (soundSection.contains("volume")) {
                    soundBuilder.volume((float) soundSection.getDouble("volume"));
                }

                builder.setBroadcastSound(soundBuilder.build());
            } catch(IllegalArgumentException e) {
                strings.getLogger().warning("Invalid broadcast sound: '" + name + "'");
                strings.getLogger().warning(e.getMessage());
            } catch(InvalidKeyException e) {
                strings.getLogger().warning("Invalid key for broadcast sound: '" + name + "'");
                strings.getLogger().warning(e.getMessage());
            }
        }
    }

    private boolean isLocal(@NotNull Identifier identifier) {
        return LOCAL_TYPES.contains(identifier);
    }

    private void registerGlobal() {
        try {
            manager.register(
                    Channel.builder("global", "{prefix}{displayname}{suffix} &7» {message}", Membership.DEFAULT)
                    .setDefaultColor("&f")
                    .setDoCooldown(false)
                    .setDoProfanityFilter(false)
                    .setDoUrlFilter(false)
                    .setCallEvent(true)
                    .setPriority(-1)
                    .build(Identifier.NORMAL)
            );
        } catch(Exception e) {
            strings.warning("An error occurred while loading global channel fallback");
            strings.warning(e.getMessage());
        }
    }

    private void registerHelpOp() {
        manager.register(new HelpOPChannel(
                strings,
                "&8[&4HelpOP&8] &f{displayname} &7» {message}",
                false,
                false,
                false
        ));

    }
}
