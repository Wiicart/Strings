package com.pedestriamc.strings;

import com.pedestriamc.strings.chat.channels.Channel;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class UserUtil {

    private static final FileConfiguration config = Strings.getInstance().getUsersFileConfig();
    private static final Strings strings = Strings.getInstance();

    /**
     * Saves a User to the users.yml file.
     * @param user the User to be saved.
     */
    public static void saveUser(@NotNull User user){
        UUID uuid = user.getUuid();
        Map<String, Object> infoMap = user.getData();
        for(Map.Entry<String, Object> element : infoMap.entrySet()){
            if(element.getValue() != null){
                config.set("players." + uuid + "." + element.getKey(), element.getValue());
            }
        }
        strings.saveUsersFile();
    }

    /**
     * Loads a User from the users.yml file.
     * @param uuid The UUID of the User.
     * @return The User, if data is found.
     */
    @Nullable
    public static User loadUser(UUID uuid){
        String userPath = "players." + uuid;
        HashSet<Channel> channels = new HashSet<>();
        if(!config.contains(userPath)){
            return null;
        }
        String suffix = config.getString(userPath + ".suffix");
        String prefix = config.getString(userPath + ".prefix");
        String displayName = config.getString(userPath + ".display-name");
        String chatColor = config.getString(userPath + ".chat-color");
        boolean mentionsEnabled = config.getBoolean(userPath + "mentions-enabled", true);
        Channel activeChannel = strings.getChannel(config.getString(userPath + ".active-channel"));
        List<?> channelNames = config.getList(userPath + ".channels");
        if(channelNames != null){
            for(Object item : channelNames){
                if(item instanceof String && strings.getChannel((String) item) != null){
                    channels.add(strings.getChannel((String) item));
                }
            }
        }
        return new User(uuid, chatColor, prefix, suffix, displayName, channels, activeChannel, mentionsEnabled);
    }

    /**
     * A class to hold a HashMap of all online Users.
     * This class is updated as players join and leave the server.
     */
    public static class UserMap{

        private static final HashMap<UUID, User> userHashMap = new HashMap<>();

        /**
         * Gets a User from the UserMap.
         * Returns null if the User is not found.
         * @param uuid The UUID of the User.
         * @return The User, if the UserMap contains it.
         */
        public static User getUser(UUID uuid){
            return userHashMap.get(uuid);
        }

        /**
         * Adds a User to the UserMap.
         * @param user The User to be added.
         */
        public static void addUser(@NotNull User user){
            if(userHashMap.containsKey(user.getUuid())){
                removeUser(user.getUuid());
            }
            userHashMap.put(user.getUuid(), user);
        }

        /**
         * Removes a User from the UserMap.
         * @param uuid The UUID of the User to be removed.
         */
        public static void removeUser(UUID uuid){
            userHashMap.remove(uuid);
        }

        /**
         * Provides a Collection of all Users in the UserMap.
         * @return A populated Collection.
         */
        public static @NotNull Collection<User> getUserSet(){
            return userHashMap.values();
        }

        public static void clear(){ userHashMap.clear(); }
    }
}
