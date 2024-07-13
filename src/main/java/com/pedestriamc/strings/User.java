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
    private Boolean socialSpy;
    private String chatColor;
    private String prefix;
    private String suffix;
    private String displayName;
    private Channel activeChannel;
    private final HashSet<Channel> channels;

    public User(UUID playerUuid){
        this(playerUuid, null, null, null, null, false, null);
    }

    public User(UUID playerUuid, String playerChatColor, String playerPrefix, String playerSuffix, String playerDisplayName, boolean socialSpy, HashSet<Channel> channels){
        this.uuid = playerUuid;
        this.chatColor = playerChatColor;
        this.prefix = playerPrefix;
        this.suffix = playerSuffix;
        this.displayName = playerDisplayName;
        this.socialSpy = socialSpy;
        this.player = Bukkit.getPlayer(playerUuid);
        this.activeChannel = strings.getChannel("global");
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
        infoMap.put("social-spy", this.socialSpy);
        return infoMap;
    }

    public UUID getUuid(){
        return uuid;
    }
    public String getChatColor(){
        if(chatColor == null){
            return strings.getDefaultColor();
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
    public boolean isSocialSpy(){
        return this.socialSpy;
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

    public void setSocialSpy(boolean socialSpy){
        this.socialSpy = socialSpy;
        UserUtil.saveUser(this);
    }

    public Channel getActiveChannel(){
        return activeChannel;
    }

    public void setActiveChannel(Channel activeChannel){
        this.activeChannel = activeChannel;
        channels.add(activeChannel);
        UserUtil.saveUser(this);
    }

    public Set<Channel> getChannels(){
        return channels;
    }

    public void joinChannel(Channel channel){
        channels.add(channel);
        channel.addPlayer(this.getPlayer());
    }

    public void leaveChannel(Channel channel){
        channels.remove(channel);
        channel.removePlayer(this.getPlayer());
    }

    public ArrayList<String> getChannelNames(){
        ArrayList<String> names = new ArrayList<>();
        for(Channel channel : channels){
            names.add(channel.getName());
        }
        return names;
    }
}
