package com.pedestriamc.strings.user.util;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.UserBuilder;
import org.bukkit.configuration.ConfigurationSection;
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

/**
 * YAML UserUtil implementation
 */
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
        UUID uuid = user.getUniqueId();
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
    public @NotNull CompletableFuture<User> loadUserAsync(@NotNull final UUID uuid)
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
    public User loadUser(UUID uuid) {
        synchronized(config) {
            ConfigurationSection section = config.getConfigurationSection("players." + uuid);
            if(section == null) {
                User user = new UserBuilder(strings, uuid, true).build();
                addUser(user);
                return user;
            }

            User user = new UserBuilder(strings, uuid, true)
                    .suffix(section.getString(".suffix"))
                    .prefix(section.getString(".prefix"))
                    .displayName(section.getString(".display-name"))
                    .chatColor(section.getString(".chat-color"))
                    .mentionsEnabled(section.getBoolean(".mentions-enabled"))
                    .activeChannel(strings.getChannelLoader().getChannel(section.getString(".active-channel")))
                    .channels(getChannels(section.getList(".channels")))
                    .monitoredChannels(getChannels(section.getList(".monitored-channels")))
                    .ignoredPlayers(getPlayers(section.getList(".ignored-players")))
                    .build();

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
            if(item instanceof String string && strings.getChannelLoader().getChannel(string) != null) {
                channels.add(strings.getChannelLoader().getChannel(string));
            }
        }

        return channels;
    }

    private Set<Player> getPlayers(List<?> list) {
        if(list == null) {
            return new HashSet<>();
        }

        Set<Player> players = new HashSet<>();
        for(Object item : list) {
            if(item instanceof String string) {
                try {
                    UUID uuid = UUID.fromString(string);
                    Player p = strings.getServer().getPlayer(uuid);
                    players.add(p);
                } catch(IllegalArgumentException ignored) {}
            }
        }
        return players;
    }

    @Override
    public @NotNull User getUser(UUID uuid) {
        User user = map.get(uuid);
        if(user == null) {
            return new UserBuilder(strings, uuid, false).build();
        }
        return user;
    }

    @Override
    public @NotNull User getUser(@NotNull Player player) {
        return getUser(player.getUniqueId());
    }

    @Override
    public void addUser(User user) {
        map.put(user.getUniqueId(), user);
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
