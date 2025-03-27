package com.pedestriamc.strings.user;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class YamlUserUtil implements UserUtil {

    private final Strings strings;
    private final FileConfiguration config;
    private final Map<UUID, User> map;

    public YamlUserUtil(Strings strings)
    {
        this.strings = strings;
        config = strings.getUsersFileConfig();
        map = new HashMap<>();
    }

    /**
     * Saves a User to the users.yml file.
     * @param user the User to be saved.
     */
    @Override
    public void saveUser(@NotNull User user) {
        UUID uuid = user.getUuid();
        Map<String, Object> infoMap = user.getData();
        async(() -> {
            synchronized(config) {
                for(Map.Entry<String, Object> element : infoMap.entrySet()) {
                    if(element.getValue() != null) {
                        config.set("players." + uuid + "." + element.getKey(), element.getValue());
                    }
                }
            }
            strings.saveUsersFile();
        });
    }

    /**
     * Loads a User from the users.yml file.
     * @param uuid The UUID of the User.
     * @return The User, if data is found.
     */
    @Nullable
    @Override
    public User loadUser(UUID uuid) {
        String userPath = "players." + uuid;
        HashSet<Channel> channels = new HashSet<>();
        HashSet<Channel> monitoredChannels = new HashSet<>();
        if(!config.contains(userPath)) {
            return null;
        }
        String suffix = config.getString(userPath + ".suffix");
        String prefix = config.getString(userPath + ".prefix");
        String displayName = config.getString(userPath + ".display-name");
        String chatColor = config.getString(userPath + ".chat-color");
        boolean mentionsEnabled = config.getBoolean(userPath + "mentions-enabled", true);
        Channel activeChannel = strings.getChannel(config.getString(userPath + ".active-channel"));
        List<?> channelNames = config.getList(userPath + ".channels");
        List<?> monitoredChannelNames = config.getList(userPath + ".monitored-channels");

        if(channelNames != null) {
            for(Object item : channelNames) {
                if(item instanceof String string && strings.getChannel(string) != null) {
                    channels.add(strings.getChannel(string));
                }
            }
        }

        if(monitoredChannelNames != null) {
            for(Object item : monitoredChannelNames) {
                if(item instanceof String str && strings.getChannel(str) != null) {
                    monitoredChannels.add(strings.getChannel(str));
                }
            }
        }


        return new User(strings, uuid, chatColor, prefix, suffix, displayName, channels, activeChannel, mentionsEnabled, monitoredChannels);
    }

    private void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(strings, runnable);
    }

    @Override
    public User getUser(UUID uuid) {
        return map.get(uuid);
    }

    @Override
    public User getUser(@NotNull Player player) {
        return getUser(player.getUniqueId());
    }

    @Override
    public void addUser(User user) {
        map.put(user.getUuid(), user);
    }

    @Override
    public void removeUser(UUID uuid) {
        map.remove(uuid);
    }

    @Override
    public Set<User> getUsers() {
        return new HashSet<>(map.values());
    }

}
