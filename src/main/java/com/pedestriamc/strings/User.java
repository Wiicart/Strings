package com.pedestriamc.strings;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
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
        this.uuid = playerUuid;
        this.chatColor = null;
        this.prefix = null;
        this.suffix = null;
        this.displayName = null;
        this.player = Bukkit.getPlayer(playerUuid);
        UserUtil.saveUser(this);
    }

    public User(UUID playerUuid, String playerChatColor, String playerPrefix, String playerSuffix, String playerDisplayName){
        this.uuid = playerUuid;
        this.chatColor = playerChatColor;
        this.prefix = playerPrefix;
        this.suffix = playerSuffix;
        this.displayName = playerDisplayName;
        this.player = Bukkit.getPlayer(playerUuid);
        UserUtil.saveUser(this);
    }

    public HashMap<String, String> getUserInfoMap(){
        return new HashMap<>(Map.of("chatColor", this.chatColor, "prefix", this.prefix, "suffix", this.suffix, "display-name", this.displayName));
    }

    public UUID getUuid(){
        return uuid;
    }
    public String getChatColor(){
        return chatColor;
    }
    public String getDisplayName(){
        return displayName;
    }
    public String getPrefix(){
        if(strings.useVault()){
            return strings.getVaultChat().getPlayerPrefix(player);
        }else{
            return prefix;
        }
    }
    public String getSuffix(){
        if(strings.useVault()){
            return strings.getVaultChat().getPlayerSuffix(player);
        }else{
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
