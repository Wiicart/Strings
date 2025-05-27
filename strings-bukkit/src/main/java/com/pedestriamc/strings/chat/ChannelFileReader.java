package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.channel.DefaultChannel;
import com.pedestriamc.strings.channel.HelpOPChannel;
import com.pedestriamc.strings.channel.SocialSpyChannel;
import com.pedestriamc.strings.configuration.Option;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ChannelFileReader {

    private final Strings strings;
    private final FileConfiguration config;
    private final ChannelManager channelLoader;
    private boolean globalExists = false;
    private boolean helpOpExists = false;

    public ChannelFileReader(Strings strings, FileConfiguration config, ChannelManager channelLoader) {
        this.strings = strings;
        this.config = config;
        this.channelLoader = channelLoader;
    }

    /**
     * Reads the FileConfiguration and loads/registers all Channels in it
     */
    public void read() {
        if(!config.contains("channels")) {
            log("No Channels defined in channels.yml, disabling plugin.");
            strings.getServer().getPluginManager().disablePlugin(strings);
            return;
        }

        ConfigurationSection channels = config.getConfigurationSection("channels");
        if(channels == null) {
            log("An error occurred while loading channels, disabling plugin.");
            strings.getServer().getPluginManager().disablePlugin(strings);
            return;
        }
        readFile(channels);
        loadDefaults();
    }

    private void readFile(ConfigurationSection channels) {
        for(String channelName : channels.getKeys(false)) {
            ConfigurationSection channel = channels.getConfigurationSection(channelName);
            if(channel == null) {
                continue;
            }

            String typeString = channel.getString("type");
            if(typeString == null) {
                typeString = "stringchannel";
            }

            Type type = getType(typeString, channelName);
            if(type == null) {
                continue;
            }

            boolean local = type == Type.WORLD || type == Type.PROXIMITY;
            ChannelBuilder data = getChannelData(channel, channelName, local);
            String symbol = channel.getString("symbol");

            try {
                Channel c = data.build(typeString);

                channelLoader.registerChannel(c);
                if(symbol != null) {
                    channelLoader.addChannelSymbol(symbol, c);
                }
                if(channelName.equalsIgnoreCase("global")) {
                    globalExists = true;
                }
                if(channelName.equalsIgnoreCase("helpop")) {
                    helpOpExists = true;
                }
            } catch (Exception e) {
                strings.warning("Failed to load channel " + channelName + ", an internal error occurred.");
                strings.warning(e.getMessage());
            }
        }
    }

    private void loadDefaults() {
        if(!globalExists) {
            try {
                Channel channel = Channel.builder("global", "{prefix}{displayname}{suffix} &7» {message}", Membership.DEFAULT)
                        .setDefaultColor("&f")
                        .setDoCooldown(false)
                        .setDoProfanityFilter(false)
                        .setDoUrlFilter(false)
                        .setCallEvent(true)
                        .setWorlds(null)
                        .setPriority(-1)
                        .build("stringchannel");
                channelLoader.registerChannel(channel);
            } catch (Exception ignored) {}
        }

        if(!helpOpExists) {
            Channel c = new HelpOPChannel(
                    strings,
                    "&8[&4HelpOP&8] &f{displayname} &7» {message}",
                    false,
                    false,
                    false
            );

            channelLoader.registerChannel(c);
            channelLoader.addChannelSymbol("?", c);
        }

        String socialSpyFormat = strings.getConfiguration().getString(Option.SOCIAL_SPY_FORMAT);
        log("Loading channel 'socialspy'...");
        channelLoader.registerChannel(new SocialSpyChannel(strings.getPlayerDirectMessenger(), socialSpyFormat));

        channelLoader.registerChannel(new DefaultChannel(strings, channelLoader));
    }

    private ChannelBuilder getChannelData(ConfigurationSection section, String channelName, boolean local) {
        ChannelBuilder data = Channel.builder(channelName, section.getString("format", "{prefix}{displayname}{suffix} &7» &f{message}"), loadMembership(section))
                .setDefaultColor(section.getString("default-color", "&f"))
                .setDoCooldown(section.getBoolean("cooldown", false))
                .setDoProfanityFilter(section.getBoolean("filter-profanity", false))
                .setDoUrlFilter(section.getBoolean("block-urls", false))
                .setCallEvent(section.getBoolean("call-event", true))
                .setPriority(section.getInt("priority", -1))
                .setDistance(section.getDouble("distance"))
                .setBroadcastFormat(section.getString("broadcast-format"));

        if(local) {
            data.setWorlds(loadWorlds(section));
        }

        return data;
    }

    private Set<World> loadWorlds(ConfigurationSection section) {
        Set<World> worlds = new HashSet<>();
        String legacyWorldName = section.getString("world");
        if(legacyWorldName != null) {
            World world = Bukkit.getWorld(legacyWorldName);
            if(world != null) {
                worlds.add(world);
            }
        }

        List<String> list = section.getStringList("worlds");
        for(String str : list) {
            World world = Bukkit.getWorld(str);
            if (world != null) {
                worlds.add(world);
            }
        }

        return worlds;
    }

    private Membership loadMembership(ConfigurationSection section) {
        String membershipString = section.getString("membership");
        if(membershipString == null) {
            return Membership.PROTECTED;
        }

        return switch(membershipString) {
            case "default" -> Membership.DEFAULT;
            case "permission" -> Membership.PERMISSION;
            default -> Membership.PROTECTED;
        };
    }

    private @Nullable Type getType(@NotNull String typeString, @NotNull String channelName) {
        switch (typeString) {
            case "stringchannel" -> {
                log("Loading stringchannel '" + channelName + "'...");
                return Type.NORMAL;
            }
            case "world", "world_strict" -> {
                log("Loading worldchannel '" + channelName + "'...");
                return Type.WORLD;
            }
            case "proximity", "proximity_strict" -> {
                log("Loading proximitychannel '" + channelName + "'...");
                return Type.PROXIMITY;
            }
            case "helpop" -> {
                log("Loading helpopchannel '" + channelName + "'...");
                return Type.PROTECTED;
            }
            default -> {
                strings.warning("Failed to load Channel '" + channelName + "', invalid channel type defined.");
                return null;
            }
        }
    }

    private void log(String message) {
        strings.info(message);
    }

}
