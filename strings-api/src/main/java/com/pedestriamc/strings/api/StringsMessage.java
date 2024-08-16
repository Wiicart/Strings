package com.pedestriamc.strings.api;

import org.bukkit.entity.Player;

import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public class StringsMessage {

    private final StringsChannel channel;
    private final Player sender;
    private final String message;
    private final Set<Player> recipients;

    public StringsMessage(StringsChannel channel, Player sender, String message, Set<Player> recipients){
        this.channel = channel;
        this.sender = sender;
        this.message = message;
        this.recipients = recipients;
    }

    public StringsChannel getChannel(){
        return this.channel;
    }

    public Player getSender(){
        return this.sender;
    }

    public String getMessage(){
        return this.message;
    }

    public Set<Player> getRecipients(){
        return this.recipients;
    }
}
