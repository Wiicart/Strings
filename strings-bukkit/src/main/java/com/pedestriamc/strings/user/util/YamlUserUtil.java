package com.pedestriamc.strings.user.util;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.api.discord.Snowflake;
import com.pedestriamc.strings.api.event.StringsUserLoadEvent;
import com.pedestriamc.strings.user.User;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * YAML UserUtil implementation
 */
public final class YamlUserUtil implements UserUtil {

    private final Strings strings;
    private final FileConfiguration config;
    private final Map<UUID, User> map;

    public YamlUserUtil(@NotNull Strings strings) {
        this.strings = strings;
        config = strings.files().getUsersFileConfig();
        map = new ConcurrentHashMap<>();
    }

    /**
     * Saves a User to the users.yml file.
     * @param user the User to be saved.
     */
    @Override
    public void saveUser(@NotNull User user) {
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
    public @NotNull CompletableFuture<User> loadUserAsync(@NotNull final UUID uuid) {
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
    @Override
    public @NotNull User loadUser(UUID uuid) {
        synchronized(config) {
            ConfigurationSection section = config.getConfigurationSection("players." + uuid);
            if(section == null) {
                User user = User.builder(strings, uuid, true).build();
                addUser(user);
                return user;
            }

            User user = User.builder(strings, uuid, false)
                    .suffix(section.getString(".suffix"))
                    .prefix(section.getString(".prefix"))
                    .displayName(section.getString(".display-name"))
                    .chatColor(section.getString(".chat-color"))
                    .mentionsEnabled(section.getBoolean(".mentions-enabled"))
                    .msgEnabled(section.getBoolean(".msg-enabled"))
                    .activeChannel(strings.getChannelLoader().getChannel(getStringNonNull(section, ".active-channel")))
                    .channels(getChannels(section.getList(".channels")))
                    .mutedChannels(getChannels(section.getList(".muted-channels")))
                    .monitoredChannels(getMonitorables(section.getList(".monitored-channels")))
                    .ignoredPlayers(getUniqueIds(section.getList(".ignored-players")))
                    .discordId(Snowflake.ofOrEmpty(section.getLong("discord-id")))
                    .build();

            addUser(user);
            return user;
        }
    }

    private Set<Monitorable> getMonitorables(List<?> list) {
        return getChannels(list).stream()
                .filter(Objects::nonNull)
                .filter(Monitorable.class::isInstance)
                .map(Monitorable.class::cast)
                .collect(Collectors.toSet());
    }

    /**
     * Takes a List and provides any Channels that are listed in the List.
     * @param list The List.
     * @return A Set of Channels.
     */
    @Contract("null -> new")
    private @NotNull Set<Channel> getChannels(List<?> list) {
        if(list == null) {
            return new HashSet<>();
        }

        ChannelLoader loader = strings.getChannelLoader();
        Set<Channel> channels = new HashSet<>();
        for(Object item : list) {
            if(item instanceof String name) {
                Channel channel = loader.getChannel(name);
                if(channel != null) {
                    channels.add(channel);
                }
            }
        }

        return channels;
    }

    @Contract("null -> new")
    private @NotNull Set<UUID> getUniqueIds(List<?> list) {
        if(list == null) {
            return new HashSet<>();
        }

        Set<UUID> uuids = new HashSet<>();
        for(Object item : list) {
            if(item instanceof String string) {
                try {
                    UUID uuid = UUID.fromString(string);
                    uuids.add(uuid);
                } catch(IllegalArgumentException ignored) {}
            }
        }
        return uuids;
    }

    @SuppressWarnings("SameParameterValue")
    private String getStringNonNull(@NotNull ConfigurationSection section, @NotNull String key) {
        return Objects.requireNonNullElse(section.getString(key), "");
    }


    @Override
    public @NotNull User getUser(UUID uuid) {
        User user = map.get(uuid);
        if (user == null) {
            return loadUser(uuid);
        }
        return user;
    }

    @Override
    public @NotNull User getUser(@NotNull Player player) {
        return getUser(player.getUniqueId());
    }

    @Override
    public @Nullable User getUser(@NotNull String name) {
        Player player = strings.getServer().getPlayer(name);
        if (player != null) {
            return getUser(player.getUniqueId());
        } else {
            return null;
        }
    }

    @Override
    public void addUser(User user) {
        map.put(user.getUniqueId(), user);
        strings.sync(() -> strings
                .getServer()
                .getPluginManager()
                .callEvent(new StringsUserLoadEvent(user))
        );
    }

    @Override
    public void removeUser(UUID uuid) {
        map.remove(uuid);
    }

    @Contract(" -> new")
    @Override
    public @NotNull Set<User> getUsers() {
        return new HashSet<>(map.values());
    }

}
