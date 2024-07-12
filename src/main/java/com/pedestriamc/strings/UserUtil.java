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
        HashMap<String, Object> infoMap = user.getUserInfoMap();
        for(Map.Entry<String, Object> element : infoMap.entrySet()){
            if(element.getValue() != null){
                config.set("players." + uuid + "." + element.getKey(), element.getValue());
            }
        }
        strings.saveUsersFile();
    }

    public static User loadUser(UUID uuid){
        String userPath = "players." + uuid;
        if(!config.contains(userPath)){
            return null;
        }
        String suffix = config.getString(userPath + ".suffix");
        String prefix = config.getString(userPath + ".prefix");
        String displayName = config.getString(userPath + ".display-name");
        String chatColor = config.getString(userPath + ".chat-color");
        boolean socialSpy = config.getBoolean(userPath + ".social-spy");
        User user = new User(uuid, chatColor, prefix, suffix, displayName, socialSpy);
        if(socialSpy){
            strings.getSocialSpy().addSpy(user.getPlayer());
        }
        return user;
    }

    //User hash map
    public static class UserMap{
        private static final HashMap<UUID, User> userHashMap = new HashMap<>();

        public static User getUser(UUID uuid){
            return userHashMap.get(uuid);
        }
        public static void addUser(User user){
            if(userHashMap.containsKey(user.getUuid())){
                removeUser(user.getUuid());
            }
            userHashMap.put(user.getUuid(), user);
        }
        public static void removeUser(UUID uuid){
            userHashMap.remove(uuid);
        }
    }
}
