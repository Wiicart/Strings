package com.pedestriamc.strings;

import java.util.UUID;

public class User {

    private final Strings strings = Strings.getInstance();
    private final UUID uuid;
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
    }

    public User(UUID playerUuid, String playerChatColor, String playerPrefix, String playerSuffix, String playerDisplayName){
        this.uuid = playerUuid;
        this.chatColor = playerChatColor;
        this.prefix = playerPrefix;
        this.suffix = playerSuffix;
        this.displayName = playerDisplayName;
    }

    public UUID getUuid(){ return uuid; }
    public String getChatColor(){ return chatColor; }
    public String getPrefix(){
        if(strings.useVault()){
            return null;
        }else{
            return prefix;
        }
    }
    public String getSuffix(){ return suffix; }
    public String getDisplayName(){ return displayName; }

    public void setChatColor(String playerChatColor){
        this.chatColor = playerChatColor;
    }
    public void setPrefix(String playerPrefix){
        this.prefix = playerPrefix;
        if(strings.useVault()){

        }
    }
    public void setSuffix(String playerSuffix){
        this.suffix = playerSuffix;
        if(strings.useVault()){

        }
    }
    public void setDisplayName(String playerDisplayName){
        this.displayName = playerDisplayName;
        if(strings.useVault()){

        }
    }
}
