package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.User;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public interface Channel {
    
    void sendMessage(Player player, String message);
    void broadcastMessage(String message);
    void closeChannel();
    String getFormat();
    String getDefaultColor();
    String getName();
    void setName(String name);
    void setDefaultColor(String defaultColor);
    void setFormat(String format);
    void addPlayer(Player player);
    void removePlayer(Player player);
    void addPlayer(User user);
    void removePlayer(User user);
    Set<Player> getMembers();
    boolean doURLFilter();
    boolean doProfanityFilter();
    boolean doCooldown();
    Type getType();
    void setEnabled(boolean isEnabled);
    boolean isEnabled();
    Map<String, String> getData();
    
}
