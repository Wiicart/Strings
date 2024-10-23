package com.pedestriamc.strings.directmessage;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.api.event.PlayerDirectMessageEvent;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerDirectMessenger {

    private final Strings strings;
    private final ConcurrentHashMap<Player, Player> replyList = new ConcurrentHashMap<>();
    private final String messageFormatSender;
    private final String messageFormatRecipient;
    private final boolean usePAPI;
    private final Messenger messenger;

    public PlayerDirectMessenger(@NotNull Strings strings){
        this.strings = strings;
        this.messageFormatSender = strings.getConfig().getString("msg-format-outgoing");
        this.messageFormatRecipient = strings.getConfig().getString("msg-format-receiving");
        this.usePAPI = strings.usePlaceholderAPI();
        this.messenger = strings.getMessenger();

    }

    public void reply(@NotNull Player sender, @NotNull String message){
        Player recipient = replyList.get(sender);
        if(recipient == null){
            messenger.sendMessage(Message.NO_REPLY, sender);
            return;
        }
        if(!recipient.isOnline()){
            messenger.sendMessage(Message.PLAYER_OFFLINE, sender);
            return;
        }
        sendMessage(sender,replyList.get(sender), message);
    }

    public void sendMessage(@NotNull Player sender, @NotNull Player recipient, @NotNull String message){
        String senderString = messageFormatSender;
        String recipientString = messageFormatRecipient;
        senderString = processPlaceholders(sender,recipient,senderString);
        recipientString = processPlaceholders(sender,recipient, recipientString);
        senderString = senderString.replace("{message}", message);
        recipientString = recipientString.replace("{message}",message);
        if(usePAPI){
            senderString = PlaceholderAPI.setPlaceholders(recipient, senderString);
            recipientString = PlaceholderAPI.setPlaceholders(sender, recipientString);
        }
        senderString = ChatColor.translateAlternateColorCodes('&',senderString);
        recipientString = ChatColor.translateAlternateColorCodes('&', recipientString);
        PlayerDirectMessageEvent event = new PlayerDirectMessageEvent(sender,recipient,message);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()){
            sender.sendMessage(senderString);
            recipient.sendMessage(recipientString);
            replyList.put(recipient, sender);
        }
    }

    public String processPlaceholders(Player sender, Player recipient, String message){
        if(message == null){
            return null;
        }
        User senderUser = strings.getUser(sender);
        User recipientUser = strings.getUser(recipient);
        message = message.replace("{sender_username}", sender.getName());
        message = message.replace("{sender_displayname}", sender.getDisplayName());
        message = message.replace("{sender_prefix}", senderUser.getPrefix());
        message = message.replace("{sender_suffix}", senderUser.getSuffix());
        message = message.replace("{recipient_username}", recipient.getName());
        message = message.replace("{recipient_displayname}", recipient.getDisplayName());
        message = message.replace("{recipient_prefix}", recipientUser.getPrefix());
        message = message.replace("{recipient_suffix}", recipientUser.getSuffix());
        return message;
    }
}
