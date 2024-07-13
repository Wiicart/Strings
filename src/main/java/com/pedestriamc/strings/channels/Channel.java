package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.ChatManager;
import com.pedestriamc.strings.Strings;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;

public class Channel {

    private String name;
    private String format;
    private String defaultColor;
    private final Set<Player> members;
    private final ChatManager chatManager;
    private boolean active;

    public Channel(Strings strings, String name, String format, String defaultColor, ChannelManager channelManager){
        this.name = name;
        this.members = new HashSet<>();
        this.format = "" + ChatColor.AQUA + ChatColor.AQUA + ChatColor.RESET + format;
        this.defaultColor = defaultColor;
        this.chatManager = strings.getChatManager();
        this.active = true;
        channelManager.registerChannel(this);
    }

    public void sendMessage(Player player, String message){
        Bukkit.getLogger().info("sendMessage triggered");
        message = "hitherehihihihihihi";
        if(active){
            Bukkit.getLogger().info("channel active, proceeding");
            String format = chatManager.formatMessage(player, this);
            message = chatManager.processMessage(player, message);
            AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, player, message, members);
            event.setFormat(format);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    public String getFormat(){
        return format;
    }

    public String getDefaultColor(){
        return defaultColor;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDefaultColor(String defaultColor){
        this.defaultColor = defaultColor;
    }

    public void addPlayer(Player player){
        members.add(player);
    }

    public void removePlayer(Player player){
        members.remove(player);
    }

    public void closeChannel(){
        Strings.getInstance().getChannelManager().unregisterChannel(this);
        active = false;
    }
}
