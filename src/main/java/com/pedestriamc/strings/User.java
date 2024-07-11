package com.pedestriamc.strings;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class User {

    private final Strings strings = Strings.getInstance();
    private final UUID uuid;
    private final Player player;
    private String chatColor;
    private String prefix;
    private String suffix;
    private String displayName;

    public User(UUID playerUuid){
        this(playerUuid, null, null, null, null);
    }

    public User(UUID playerUuid, String playerChatColor, String playerPrefix, String playerSuffix, String playerDisplayName){
        this.uuid = playerUuid;
        this.chatColor = playerChatColor;
        this.prefix = playerPrefix;
        this.suffix = playerSuffix;
        this.displayName = playerDisplayName;
        this.player = Bukkit.getPlayer(playerUuid);
        UserUtil.saveUser(this);
        UserUtil.UserMap.addUser(this);
    }


    public HashMap<String, String> getUserInfoMap(){
        HashMap<String, String> infoMap = new HashMap<>();
        infoMap.put("chat-color", this.chatColor != null ? this.chatColor : "");
        infoMap.put("prefix", this.prefix != null ? this.prefix : "");
        infoMap.put("suffix", this.suffix != null ? this.suffix : "");
        infoMap.put("display-name", this.displayName != null ? this.displayName : "");
        return infoMap;
    }

    public UUID getUuid(){
        return uuid;
    }
    public String getChatColor(){
        if(chatColor == null){
            return strings.getDefaultColor();
        }
        return chatColor;
    }
    public String getDisplayName(){
        if(displayName == null){
            return player.getDisplayName();
        }
        return displayName;
    }
    public String getPrefix(){
        if(strings.useVault()){
            return strings.getVaultChat().getPlayerPrefix(player);
        }else{
            if(prefix == null){
                return "";
            }
            return prefix;
        }
    }
    public String getSuffix(){
        if(strings.useVault()){
            return strings.getVaultChat().getPlayerSuffix(player);
        }else{
            if(suffix == null){
                return "";
            }
            return suffix;
        }
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
}
