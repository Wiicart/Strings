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
    private boolean callEvent;
    private final Set<Player> members;
    private final ChatManager chatManager;
    private volatile boolean active;

    public Channel(Strings strings, String name, String format, String defaultColor, ChannelManager channelManager){
        this.strings = strings;
        this.name = name;
        this.members = ConcurrentHashMap.newKeySet();
        this.format = "" + ChatColor.AQUA + ChatColor.AQUA + ChatColor.RESET + format;
        this.defaultColor = defaultColor != null ? defaultColor : "&f";
        this.chatManager = strings.getChatManager();
        this.active = true;
        channelManager.registerChannel(this);
    }

    //Send player messages in the channel.
    //The message will be sent to all players that are a member of the channel,
    //which includes players that don't have this channel as active.
    //Also calls a AsyncPlayerChatEvent so that other plugins can handle this message
    public void sendMessage(Player player, String message){
        if(active){
            String format = chatManager.formatMessage(player, this);
            message = chatManager.processMessage(player, message);
            String finalMessage = message;
            Bukkit.getScheduler().runTask(strings, () ->{
                AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(true, player, finalMessage, members);
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

    //Broadcasts a message to members of this channel. (Auto Broadcasts)
    public void broadcastMessage(String message){
        for(Player p : members){
            p.sendMessage(message);
        }
    }

    // Closes this channel, updates players to reflect this channel closing, and unregisters channel
    // TODO: remove this channel from the config when this method is called
    public void closeChannel(){
        for(Player p : members){
            strings.getUser(p).leaveChannel(this);
        }
        Strings.getInstance().getChannelManager().unregisterChannel(this);
        active = false;
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

    private void setFormat(String format){ this.format = format; }

    public void addPlayer(Player player){
        members.add(player);
    }

    public void removePlayer(Player player){
        members.remove(player);
    }

}

