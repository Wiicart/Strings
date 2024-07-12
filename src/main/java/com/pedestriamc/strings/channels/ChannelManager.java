package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.Strings;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;

public class ChannelManager {

    private final HashSet<Channel> channels;
    private final HashMap<Player, Channel> playerChannelHashMap;

    public ChannelManager(Strings strings){
        this.channels = new HashSet<>();
        this.playerChannelHashMap = new HashMap<>();
    }

    public void registerChannel(Channel channel){
        channels.add(channel);
    }

    public void unregisterChannel(Channel channel){
        channels.remove(channel);
    }

    public Channel getPlayerChannel(Player player){
        return playerChannelHashMap.get(player);
    }

    public void setPlayerChannel(Player player, Channel channel){
        playerChannelHashMap.put(player,channel);
    }
    
    public HashSet<Channel> getChannels(){
        return channels;
    }
}
