package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.api.text.format.StringsTextColor;
import com.pedestriamc.strings.channel.base.ProtectedChannel;
import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
    public void sendMessage(@NotNull Player player, @NotNull String message) {
        for(Player p : spiesList) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    @Override
    public void broadcast(@NotNull String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        broadcastPlain(message);
    }

    @Override
    public void broadcastPlain(@NotNull String message) {
        for(Player p : spiesList) {
            p.sendMessage(message);
        }
    }



    @Override
    public @NotNull String getFormat() {
        return format;
    }

    @Override
    public void setFormat(@NotNull String format) {
        this.format = format;
    }

    @Override
    public String getDefaultColor() {
        return StringsTextColor.WHITE.toString();
    }

    @Override
    public void addMember(@NotNull Player player) {
        spiesList.add(player);
    }

    @Override
    public void removeMember(@NotNull Player player) {
        spiesList.remove(player);
    }

    @Override
    public Set<Player> getMembers() {
        return spiesList;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "socialspychannel";
    }
}