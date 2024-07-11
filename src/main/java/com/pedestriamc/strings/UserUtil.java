package com.pedestriamc.strings;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class UserUtil {

    private static final FileConfiguration config = Strings.getInstance().getUsersFileConfig();
    private static final Strings strings = Strings.getInstance();

    //Saving and loading users
    public static void saveUser(User user){
        UUID uuid = user.getUuid();
        HashMap<String, String> infoMap = user.getUserInfoMap();
        config.set("players." + uuid, null);
        for(Map.Entry<String, String> element : infoMap.entrySet()){
            config.set("players." + uuid + "." + element.getKey(), element.getValue());
        }
        strings.saveUsersFile();
    }

    public static User loadUser(UUID uuid){
        if(!config.contains("players." + uuid)){ return null; }
        String userPath = "players." + uuid;
        String suffix = config.getString(userPath + "suffix", "");
        String prefix = config.getString(userPath + "prefix", "");
        String displayName = config.getString(userPath + "display-name", null);
        String chatColor = config.getString(userPath + "chat-color", null);
        return new User(uuid,chatColor,prefix,suffix,displayName);
    }

    //User hash map
    public static class UserMap{
        private static final HashMap<UUID, User> userHashMap = new HashMap<>();

        public static User getUser(UUID uuid){
            return userHashMap.get(uuid);
        }
        public static void addUser(User user){
            removeUser(user.getUuid());
            userHashMap.put(user.getUuid(), user);
        }
        public static void removeUser(UUID uuid){
            userHashMap.remove(uuid);
        }
    }
}
