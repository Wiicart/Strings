package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.ChatManager;
import com.pedestriamc.strings.Strings;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StringChannel implements Channel{

    private final Strings strings;
    private String name;
    private String format;
    private String defaultColor;
    private final boolean callEvent;
    private final Set<Player> members;
    private final ChatManager chatManager;
    private volatile boolean active;
    private final boolean urlFilter;
    private final boolean profanityFilter;
    private final boolean doCooldown;

    public StringChannel(@NotNull Strings strings, String name, String format, String defaultColor, ChannelManager channelManager, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown){
        this.strings = strings;
        this.name = name;
        this.members = ConcurrentHashMap.newKeySet();
        this.format = format;
        this.defaultColor = defaultColor != null ? defaultColor : "&f";
        this.chatManager = strings.getChatManager();
        this.active = true;
        this.callEvent = callEvent;
        this.urlFilter = doURLFilter;
        this.profanityFilter = doProfanityFilter;
        this.doCooldown = doCooldown;
        channelManager.registerChannel(this);
    }

    //Send player messages in the channel.
    //The message will be sent to all players that are a member of the channel,
    //which includes players that don't have this channel as active.
    //Also calls a AsyncPlayerChatEvent so that other plugins can handle this message
    @Override
    public void sendMessage(Player player, String message){
        if(active){
            String format = chatManager.formatMessage(player, this);
            message = chatManager.processMessage(player, message);
            String finalMessage = message;
            if(callEvent){
                Bukkit.getScheduler().runTask(strings, () ->{
                    AsyncPlayerChatEvent event = new ChannelChatEvent(false, player, finalMessage, members, this);
                    event.setFormat(format);
                    Bukkit.getPluginManager().callEvent(event);
                    if(!event.isCancelled()){
                        String formattedMessage = String.format(event.getFormat(), strings.getUser(player).getDisplayName(), event.getMessage());
                        for(Player p : members){
                            p.sendMessage(formattedMessage);
                        }
                        Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
                    }
                });
            }else{
                String formattedMessage = String.format(format, player.getDisplayName(), message);
                for(Player p : members){
                    p.sendMessage(formattedMessage);
                }
                Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
            }
        }
    }

    //Broadcasts a message to members of this channel. (Auto Broadcasts)
    @Override
    public void broadcastMessage(String message){
        for(Player p : members){
            p.sendMessage(message);
        }
    }

    // Closes this channel, updates players to reflect this channel closing, and unregisters channel
    @Override
    public void closeChannel(){
        Strings.getInstance().getChannelManager().unregisterChannel(this);
        Strings.getInstance().getChannelManager().deleteChannel(this);
        active = false;
    }

    @Override
    public String getFormat(){
        return format;
    }

    @Override
    public String getDefaultColor(){
        return defaultColor;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public void setName(String name){
        this.name = name;
    }

    @Override
    public void setDefaultColor(String defaultColor){
        this.defaultColor = defaultColor;
    }

    @Override
    public void setFormat(String format){ this.format = format; }

    @Override
    public void addPlayer(Player player){
        members.add(player);
    }

    @Override
    public void removePlayer(Player player){
        members.remove(player);
    }

    @Override
    public Set<Player> getMembers(){
        return members;
    }

    @Override
    public boolean doURLFilter() {
        return urlFilter;
    }

    @Override
    public boolean doProfanityFilter() {
        return profanityFilter;
    }

    @Override
    public boolean doCooldown() {
        return this.doCooldown;
    }
    @Override
    public Type getType() {
        return Type.NORMAL;
    }

    @Override
    public Map<String, String> getData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("format", format);
        map.put("default-color", defaultColor);
        map.put("call-event", String.valueOf(callEvent));
        map.put("filter-profanity", String.valueOf(profanityFilter));
        map.put("block-urls", String.valueOf(urlFilter));
        map.put("cooldown", String.valueOf(doCooldown));
        map.put("type", String.valueOf(this.getType()));
        return map;
    }
}
