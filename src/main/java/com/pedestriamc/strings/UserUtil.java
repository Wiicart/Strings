package com.pedestriamc.strings;

import com.pedestriamc.strings.channels.Channel;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public final class UserUtil {

    private static final FileConfiguration config = Strings.getInstance().getUsersFileConfig();
    private static final Strings strings = Strings.getInstance();

    //Saving and loading users
    public static void saveUser(User user){
        UUID uuid = user.getUuid();
        ArrayList<String> channelNames = user.getChannelNames();
        HashMap<String, Object> infoMap = user.getUserInfoMap();
        for(Map.Entry<String, Object> element : infoMap.entrySet()){
            if(element.getValue() != null){
                config.set("players." + uuid + "." + element.getKey(), element.getValue());
            }
        }
        if(!channelNames.isEmpty()){
            config.set("players." + uuid + ".channels", channelNames);
        }
        strings.saveUsersFile();
    }

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
        Channel activeChannel = strings.getChannel(config.getString(userPath + ".active-channel"));
        List<?> channelNames = config.getList(userPath + ".channels");
        if(channelNames != null){
            for(Object item : channelNames){
                if(item instanceof String && strings.getChannel((String) item) != null){
                    channels.add(strings.getChannel((String) item));
                }
            }
        }
        return new User(uuid, chatColor, prefix, suffix, displayName, channels, activeChannel);
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
        public static Collection<User> getUserSet(){
            return userHashMap.values();
        }
    }
}
