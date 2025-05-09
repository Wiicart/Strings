package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.data.ChannelData;
import com.pedestriamc.strings.channel.DefaultChannel;
import com.pedestriamc.strings.channel.HelpOPChannel;
import com.pedestriamc.strings.channel.SocialSpyChannel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

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
            ChannelData data = getChannelData(channel, channelName, local);
            String symbol = channel.getString("symbol");

            try {
                Channel c = channelLoader.build(data, typeString);

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
                log("Failed to load channel " + channelName + ", an internal error occurred.");
            }
        }
    }

    private void loadDefaults() {
        if(!globalExists) {
            ChannelData data = new ChannelData("global");
            data.setMembership(Membership.DEFAULT);
            data.setFormat("{prefix}{displayname}{suffix} &7» {message}");
            data.setDefaultColor("&f");
            data.setDoCooldown(false);
            data.setDoProfanityFilter(false);
            data.setDoUrlFilter(false);
            data.setCallEvent(true);
            data.setPriority(-1);

            try {
                Channel channel = channelLoader.build(data, "normal");
                channelLoader.registerChannel(channel);
            } catch (Exception ignored) {}
        }

        if(!helpOpExists) {
            Channel c = new HelpOPChannel(
                    strings,
                    "&8[&4HelpOP&8] &f{displayname} &7» {message}",
                    "&7",
                    false,
                    false,
                    false
            );

            channelLoader.registerChannel(c);
            channelLoader.addChannelSymbol("?", c);
        }

        String socialSpyFormat = strings.getConfig().getString("social-spy-format");
        log("Loading channel 'socialspy'...");
        channelLoader.registerChannel(
                new SocialSpyChannel(
                        strings.getPlayerDirectMessenger(),
                        socialSpyFormat
                )
        );

        channelLoader.registerChannel(new DefaultChannel(strings, channelLoader));
    }

    private ChannelData getChannelData(ConfigurationSection section, String channelName, boolean local) {
        ChannelData data = new ChannelData();
        data.setName(channelName);
        data.setFormat(section.getString("format", "{prefix}{displayname}{suffix} &7» {message}"));
        data.setDefaultColor(section.getString("default-color", "&f"));
        data.setDoCooldown(section.getBoolean("cooldown", false));
        data.setDoProfanityFilter(section.getBoolean("filter-profanity", false));
        data.setDoUrlFilter(section.getBoolean("block-urls", false));
        data.setCallEvent(section.getBoolean("call-event", true));
        data.setPriority(section.getInt("priority", -1));
        data.setDistance(section.getDouble("distance"));
        data.setBroadcastFormat(section.getString("broadcast-format"));
        loadMembership(data, section);

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

    private void loadMembership(ChannelData data, ConfigurationSection section) {
        String membershipString = section.getString("membership");
        if(membershipString == null) {
            data.setMembership(Membership.PROTECTED);
            return;
        }

        switch(membershipString) {
            case "default" -> data.setMembership(Membership.DEFAULT);
            case "permission" -> data.setMembership(Membership.PERMISSION);
            default -> data.setMembership(Membership.PROTECTED);
        }
    }

    private Type getType(String typeString, String channelName) {
        switch (typeString) {
            case "stringchannel" -> {
                log("Loading stringchannel '" + channelName + "'...");
                return Type.NORMAL;
            }
            case "world", "world_strict" -> {
                log("Loading world channel '" + channelName + "'...");
                return Type.WORLD;
            }
            case "proximity", "proximity_strict" -> {
                log("Loading proximity channel '" + channelName + "'...");
                return Type.PROXIMITY;
            }
            case "helpop" -> {
                log("Loading helpop channel '" + channelName + "'...");
                return Type.PROTECTED;
            }
            default -> {
                log("Failed to load Channel '" + channelName + "', invalid channel type defined.");
                return null;
            }
        }
    }

    private void log(String message) {
        strings.info(message);
    }

}
