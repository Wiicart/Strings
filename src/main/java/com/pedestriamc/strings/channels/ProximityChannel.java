package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.Strings;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ProximityChannel implements Channel{
    
    public ProximityChannel(@NotNull Strings strings, String name, String format, String defaultColor, ChannelManager channelManager, boolean callEvent){
        
    }
    
    @Override
    public void sendMessage(Player player, String message) {
        
    }

    @Override
    public void broadcastMessage(String message) {

    }

    @Override
    public void closeChannel() {

    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public String getDefaultColor() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void setDefaultColor(String defaultColor) {

    }

    @Override
    public void setFormat(String format) {

    }

    @Override
    public void addPlayer(Player player) {

    }

    @Override
    public void removePlayer(Player player) {

    }

    @Override
    public Set<Player> getMembers() {
        return null;
    }
}
