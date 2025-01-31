package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channels.Membership;
import com.pedestriamc.strings.api.channels.Channel;
import com.pedestriamc.strings.api.channels.Type;
import com.pedestriamc.strings.api.channels.data.ChannelData;
import com.pedestriamc.strings.chat.channels.DefaultChannel;
import com.pedestriamc.strings.chat.channels.HelpOPChannel;
import com.pedestriamc.strings.chat.channels.SocialSpyChannel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChannelFileReader {

    private final Strings strings;
    private final FileConfiguration config;
    private final StringsChannelLoader channelLoader;

    public ChannelFileReader(Strings strings, FileConfiguration config, StringsChannelLoader channelLoader) {
        this.strings = strings;
        this.config = config;
        this.channelLoader = channelLoader;
    }

    /**
     * Reads the FileConfiguration and loads/registers all Channels in it
     */
    public void read() {

        boolean globalExists = false;
        boolean helpOpExists = false;

        if(!config.contains("channels")) {
            log("No Channels defined in channels.yml, disabling plugin.");
            strings.getServer().getPluginManager().disablePlugin(strings);
            return;
        }

        ConfigurationSection channels = config.getConfigurationSection("channels");
        if(channels == null){
            log("An error occurred while loading channels, disabling plugin.");
            strings.getServer().getPluginManager().disablePlugin(strings);
            return;
        }

        for(String channelName : channels.getKeys(false)) {

            ConfigurationSection channel = channels.getConfigurationSection(channelName);
            if(channel == null){
                continue;
            }

            String typeString = channel.getString("type");
            if(typeString == null){
                typeString = "stringchannel";
            }

            Type type = getType(typeString, channelName);
            if(type == null){
                continue;
            }

            String symbol = channel.getString("symbol");

            ChannelData data = getChannelData(channel, channelName, type);

            try {
                Channel c = channelLoader.build(data, type);
                if(c == null){
                    continue;
                }

                channelLoader.registerChannel(c);

                if(symbol != null){
                    channelLoader.addChannelSymbol(symbol, c);
                }

                if(channelName.equalsIgnoreCase("global")){
                    globalExists = true;
                }
                if(channelName.equalsIgnoreCase("helpop")){
                    helpOpExists = true;
                }

            } catch (Exception e) {
                log("Failed to load channel " + channelName + ", an internal error occurred.");
            }

        }

        loadDefaults(globalExists, helpOpExists);

    }

    private void loadDefaults(boolean globalExists, boolean helpOpExists) {

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
                Channel channel = channelLoader.build(data, Type.NORMAL);
                if(channel != null) {
                    channelLoader.registerChannel(channel);
                }
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

    private ChannelData getChannelData(ConfigurationSection section, String channelName, Type type) {

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

        loadMembership(data, section);

        if(type == Type.WORLD || type == Type.PROXIMITY) {
            loadWorlds(data, section);
        }

        return data;

    }

    private void loadWorlds(ChannelData data, ConfigurationSection section) {
        Set<World> worlds = new HashSet<>();
        String legacyWorldName = section.getString("world");
        if(legacyWorldName != null){
            World world = Bukkit.getWorld(legacyWorldName);
            if(world != null) {
                worlds.add(world);
            }
        } else {
            List<?> list = section.getList("worlds");
            if(list != null){
                for(Object obj : list) {
                    if (obj instanceof String str) {
                        World world = Bukkit.getWorld(str);
                        if (world != null) {
                            worlds.add(world);
                        }
                    }
                }
            }
        }
        data.setWorlds(worlds);
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

            case "world" -> {
                log("Loading world channel '" + channelName + "'...");
                return Type.WORLD;
            }

            case "proximity" -> {
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
        strings.getLogger().info(message);
    }

}
