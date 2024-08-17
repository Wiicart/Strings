package com.pedestriamc.strings.api.event;

import com.pedestriamc.strings.api.StringsChannel;
import org.bukkit.entity.Player;

import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public class StringsMessage {

    private final StringsChannel channel;
    private final Player sender;
    private final String message;
    private final Set<Player> recipients;
    private final String finalMessage;

    public StringsMessage(StringsChannel channel, Player sender, String message, Set<Player> recipients, String finalMessage){
        this.channel = channel;
        this.sender = sender;
        this.message = message;
        this.recipients = recipients;
        this.finalMessage = finalMessage;
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

    public String getFinalMessage(){ return this.finalMessage; }
}
