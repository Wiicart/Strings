package com.pedestriamc.strings;

import com.pedestriamc.strings.channels.Channel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class User {

    private final Strings strings = Strings.getInstance();
    private final UUID uuid;
    private final Player player;
    private String chatColor;
    private String prefix;
    private String suffix;
    private String displayName;
    private Channel activeChannel;
    private final HashSet<Channel> channels;

    public User(UUID playerUuid){
        this(playerUuid, null, null, null, null, null, null);
    }

    public User(UUID playerUuid, String playerChatColor, String playerPrefix, String playerSuffix, String playerDisplayName, HashSet<Channel> channels, Channel activeChannel){
        this.uuid = playerUuid;
        this.chatColor = playerChatColor;
        this.prefix = playerPrefix;
        this.suffix = playerSuffix;
        this.displayName = playerDisplayName;
        this.player = Bukkit.getPlayer(playerUuid);
        this.activeChannel = activeChannel != null ? activeChannel : strings.getChannel("global");
        this.channels = Objects.requireNonNullElseGet(channels, HashSet::new);
        if(channels != null){
            for(Channel channel : channels){
                channel.addPlayer(this.getPlayer());
            }
        }else{
            this.joinChannel(strings.getChannel("global"));
        }
        UserUtil.saveUser(this);
        UserUtil.UserMap.addUser(this);
    }


    public HashMap<String, Object> getUserInfoMap(){
        HashMap<String, Object> infoMap = new HashMap<>();
        infoMap.put("chat-color", this.chatColor);
        infoMap.put("prefix", this.prefix);
        infoMap.put("suffix", this.suffix);
        infoMap.put("display-name", this.displayName);
        infoMap.put("active-channel", this.activeChannel.getName());
        return infoMap;
    }

    public UUID getUuid(){
        return uuid;
    }
    public String getChatColor(){
        if(chatColor == null){
            return activeChannel.getDefaultColor();
        }
        return ChatColor.translateAlternateColorCodes('&',chatColor);
    }
    public String getDisplayName(){
        if(displayName == null){
            return player.getDisplayName();
        }
        return ChatColor.translateAlternateColorCodes('&',displayName);
    }
    public String getPrefix(){
        if(strings.useVault()){
            return ChatColor.translateAlternateColorCodes('&', strings.getVaultChat().getPlayerPrefix(player));
        }else{
            if(prefix == null){
                return "";
            }
            return ChatColor.translateAlternateColorCodes('&', prefix);
        }
    }
    public String getSuffix(){
        if(strings.useVault()){
            return ChatColor.translateAlternateColorCodes('&', strings.getVaultChat().getPlayerSuffix(player));
        }else{
            if(suffix == null){
                return "";
            }
            return ChatColor.translateAlternateColorCodes('&', suffix);
        }
    }

    public Player getPlayer(){
        return player;
    }

    public void setChatColor(String playerChatColor){
        this.chatColor = playerChatColor;
        UserUtil.saveUser(this);
    }
    public void setPrefix(String playerPrefix){
        this.prefix = playerPrefix;
        if(strings.useVault()){
            strings.getVaultChat().setPlayerPrefix(player,playerPrefix);
        }
        UserUtil.saveUser(this);
    }
    public void setSuffix(String playerSuffix){
        this.suffix = playerSuffix;
        if(strings.useVault()){
            strings.getVaultChat().setPlayerSuffix(player,playerSuffix);
        }
        UserUtil.saveUser(this);
    }
    public void setDisplayName(String playerDisplayName){
        this.displayName = playerDisplayName;
        UserUtil.saveUser(this);
    }

    public Channel getActiveChannel(){
        return activeChannel;
    }

    public void setActiveChannel(Channel activeChannel){
        if(activeChannel.getName().equals("helpop")){
            return;
        }
        this.activeChannel = activeChannel;
        channels.add(activeChannel);
        activeChannel.addPlayer(this.getPlayer());
        UserUtil.saveUser(this);
    }

    public Set<Channel> getChannels(){
        return channels;
    }

    public void joinChannel(Channel channel){
        channels.add(channel);
        channel.addPlayer(this.getPlayer());
        UserUtil.saveUser(this);

    }

    public boolean memberOf(Channel channel){
        return channels.contains(channel);
    }

    public void leaveChannel(Channel channel){
        if(channel.equals(strings.getChannel("global"))){
            Bukkit.getLogger().info("[Strings] Player " + player.getName() + " just tried to leave channel global!  Cancelled leaving channel.");
            return;
        }
        channels.remove(channel);
        channel.removePlayer(this.getPlayer());
        if(activeChannel.equals(channel)){
            activeChannel = strings.getChannel("global");
        }
        UserUtil.saveUser(this);
    }

    public ArrayList<String> getChannelNames(){
        ArrayList<String> names = new ArrayList<>();
        for(Channel channel : channels){
            names.add(channel.getName());
        }
        return names;
    }

    public void leaveChannelsLogOff(){
        for(Channel channel : channels){
            channel.removePlayer(this.getPlayer());
        }
    }
}

