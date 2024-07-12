package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.ChatManager;
import com.pedestriamc.strings.Strings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class Channel {

    private String name;
    private final Set<Player> members;
    private final ChatManager chatManager;

    public Channel(Strings strings, String name, Set<Player> members){
        this.name = name;
        this.members = members;
        this.chatManager = strings.getChatManager();
    }

    public void sendMessage(Player player, String message){
        String format = chatManager.formatMessage(player);
        message = chatManager.processMessage(player, message);
        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, player, message, members);
        event.setFormat(format);
        Bukkit.getPluginManager().callEvent(event);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void addPlayer(Player player){
        members.add(player);
    }

    public void removePlayer(Player player){
        members.remove(player);
    }
}
