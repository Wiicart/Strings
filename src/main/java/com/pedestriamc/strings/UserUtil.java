import java.util.UUID;

public final class UserUtil {

    private static final HashMap<UUID, User> userHashMap = new HashMap<>();
    private static final FileConfiguration config = Strings.getInstance().getUsersFileConfig();
    private static final Strings strings = Strings.getInstance();

    public static User getUser(UUID uuid){
        return userHashMap.get(uuid);
    }
    public static void addUser(User user){
        userHashMap.put(user.getUuid(), user);
    }
    public static void removeUser(UUID uuid){
        userHashMap.remove(uuid);
    }

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
}
