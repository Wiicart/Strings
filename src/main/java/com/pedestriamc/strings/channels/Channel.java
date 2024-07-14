package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.ChatManager;
import com.pedestriamc.strings.Strings;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Channel {

    private final Strings strings;
    private String name;
    private String format;
    private String defaultColor;
    private final Set<Player> members;
    private final ChatManager chatManager;
    private volatile boolean active;

    public Channel(Strings strings, String name, String format, String defaultColor, ChannelManager channelManager){
        this.strings = strings;
        this.name = name;
        this.members = ConcurrentHashMap.newKeySet();
        this.format = "" + ChatColor.AQUA + ChatColor.AQUA + ChatColor.RESET + format;
        this.defaultColor = defaultColor;
        this.chatManager = strings.getChatManager();
        this.active = true;
        channelManager.registerChannel(this);
    }

    public void sendMessage(Player player, String message){
        if(active){
            String format = chatManager.formatMessage(player, this);
            message = chatManager.processMessage(player, message);
            String finalMessage = message;
            Bukkit.getScheduler().runTask(strings, () ->{
                AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, player, finalMessage, members);
                event.setFormat(format);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()){
                    String formattedMessage = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
                    for(Player p : members){
                        p.sendMessage(formattedMessage);
                    }
                    Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
                }
            });
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

