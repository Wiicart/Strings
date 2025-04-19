package com.pedestriamc.strings.user;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class YamlUserUtil implements UserUtil {

    private final Strings strings;
    private final FileConfiguration config;
    private final Map<UUID, User> map;

    public YamlUserUtil(Strings strings)
    {
        this.strings = strings;
        config = strings.files().getUsersFileConfig();
        map = new ConcurrentHashMap<>();
    }

    /**
     * Saves a User to the users.yml file.
     * @param user the User to be saved.
     */
    @Override
    public void saveUser(@NotNull User user)
    {
        UUID uuid = user.getUuid();
        Map<String, Object> infoMap = user.getData();
        strings.async(() -> {
            synchronized(config) {
                for(Map.Entry<String, Object> element : infoMap.entrySet()) {
                    Object value = element.getValue();
                    if(element.getKey() != null && value != null) {
                        config.set("players." + uuid + "." + element.getKey(), value);
                    }
                }
                strings.files().saveUsersFile();
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<User> loadUserAsync(@NotNull UUID uuid)
    {
        CompletableFuture<User> future = new CompletableFuture<>();
        strings.async(() -> {
            try {
                User user = loadUser(uuid);
                future.complete(user);
            } catch(Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    /**
     * Loads a User from the users.yml file.
     * @param uuid The UUID of the User.
     * @return The User, if data is found.
     */
    @NotNull
    @Override
    public User loadUser(UUID uuid)
    {
        synchronized(config) {
            String userPath = "players." + uuid;
            if(!config.contains(userPath)) {
                User user = new User(strings, uuid, true);
                addUser(user);
                return user;
            }

            Set<Channel> channels;
            Set<Channel> monitoredChannels;

            String suffix = config.getString(userPath + ".suffix");
            String prefix = config.getString(userPath + ".prefix");
            String displayName = config.getString(userPath + ".display-name");
            String chatColor = config.getString(userPath + ".chat-color");
            boolean mentionsEnabled = config.getBoolean(userPath + "mentions-enabled", true);

            String activeChannelName = config.getString(userPath + ".active-channel");
            if(activeChannelName == null) {
                activeChannelName = "default";
            }

            Channel activeChannel = strings.getChannelLoader().getChannel(activeChannelName);

            List<?> channelNames = config.getList(userPath + ".channels");
            List<?> monitoredChannelNames = config.getList(userPath + ".monitored-channels");

            channels = getChannels(channelNames);
            monitoredChannels = getChannels(monitoredChannelNames);

            User user = new User(strings, uuid, chatColor, prefix, suffix, displayName, channels, activeChannel, mentionsEnabled, monitoredChannels, true);
            addUser(user);

            return user;
        }
    }

    private Set<Channel> getChannels(List<?> list) {
        if(list == null) {
            return new HashSet<>();
        }

        Set<Channel> channels = new HashSet<>();
        for(Object item : list) {
            if(item instanceof String string && strings.getChannel(string) != null) {
                channels.add(strings.getChannelLoader().getChannel(string));
            }
        }

        return channels;
    }

    @Override
    public @NotNull User getUser(UUID uuid) {
        User user = map.get(uuid);
        if(user == null) {
            return new User(strings, uuid, false);
        }
        return user;
    }

    @Override
    public @NotNull User getUser(@NotNull Player player) {
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
