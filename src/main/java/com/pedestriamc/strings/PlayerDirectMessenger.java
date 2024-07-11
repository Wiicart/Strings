package com.pedestriamc.strings;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerDirectMessenger {

    private final HashMap<Player, Player> replyList = new HashMap<>();
    private final String messageFormatSender;
    private final String messageFormatRecipient;
    private final boolean usePAPI;

    public PlayerDirectMessenger(Strings strings){
        this.messageFormatSender = strings.getDirectMessageFormatSender();
        this.messageFormatRecipient = strings.getDirectMessageFormatRecipient();
        this.usePAPI = strings.usePlaceholderAPI();

    }

    public void sendMessage(Player sender, Player recipient, String message){
        String senderString = messageFormatSender;
        String recipientString = messageFormatRecipient;
        if(usePAPI){
            senderString = PlaceholderAPI.setPlaceholders(recipient, senderString);
            recipientString = PlaceholderAPI.setPlaceholders(sender, recipientString);
        }

    }
}
