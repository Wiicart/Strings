package com.pedestriamc.strings;

import org.bukkit.entity.Player;

public class JoinLeaveMessage {

    private final String joinMessageTemplate;
    private final String leaveMessageTemplate;
    private final boolean usePAPI;

    public JoinLeaveMessage(Strings strings){
        this.joinMessageTemplate = strings.getJoinMessageFormat();
        this.leaveMessageTemplate = strings.getLeaveMessageFormat();
        this.usePAPI = strings.usePlaceholderAPI();
    }
    public String joinMessage(Player player){
        String message = joinMessageTemplate;
        return null;
    }
    public String leaveMessage(Player player){
        String message = leaveMessageTemplate;
        return null;
    }
}
