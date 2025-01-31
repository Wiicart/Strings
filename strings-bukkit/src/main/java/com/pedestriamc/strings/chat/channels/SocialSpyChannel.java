package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.chat.channels.base.ProtectedChannel;
import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class SocialSpyChannel extends ProtectedChannel {

    private String format;
    private final PlayerDirectMessenger playerDirectMessenger;
    private final HashSet<Player> spiesList;

    public SocialSpyChannel(PlayerDirectMessenger messenger, String format) {
        super("socialspy");
        this.format = format;
        this.playerDirectMessenger = messenger;
        this.spiesList = new HashSet<>();
    }

    /**
     * Sends msg log to social spies.
     * @param sender The sender of the message
     * @param recipient The recipient of the message
     * @param message The message sent
     */
    public void sendOutMessage(Player sender, Player recipient, String message) {
        String msg = format;
        msg = playerDirectMessenger.processPlaceholders(sender, recipient, msg);
        msg = msg.replace("{message}", message);
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        for(Player spies : spiesList) {
            spies.sendMessage(msg);
        }
    }

    @Override
    public void sendMessage(Player player, String message) {
        for(Player p : spiesList){
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    @Override
    public void broadcastMessage(String message) {
        for(Player p : spiesList){
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String getDefaultColor() {
        return "&f";
    }

    @Override
    public void addPlayer(Player player) {
        spiesList.add(player);
    }

    @Override
    public void removePlayer(Player player) {
        spiesList.remove(player);
    }

    @Override
    public Set<Player> getMembers() {
        return spiesList;
    }

}