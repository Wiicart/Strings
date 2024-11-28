package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.api.channels.Channel;
import com.pedestriamc.strings.api.channels.ChannelLoader;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.channels.Type;
import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SocialSpyChannel implements Channel {

    private String format;
    private final PlayerDirectMessenger playerDirectMessenger;
    private final HashSet<Player> spiesList;
    private final ChannelLoader channelLoader;

    public SocialSpyChannel(@NotNull ChannelLoader channelLoader, PlayerDirectMessenger messenger, String format) {
        this.format = format;
        this.playerDirectMessenger = messenger;
        this.spiesList = new HashSet<>();
        this.channelLoader = channelLoader;
    }

    /**
     * Sends msg log to social spies.
     * @param sender The sender of the message
     * @param recipient The recipient of the message
     * @param message The message sent
     */
    public void sendOutMessage(Player sender, Player recipient, String message){
        String msg = format;
        msg = playerDirectMessenger.processPlaceholders(sender, recipient, msg);
        msg = msg.replace("{message}", message);
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        for(CommandSender spies : spiesList){
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
    public String getDefaultColor() {
        return "&f";
    }

    @Override
    public String getName() {
        return "socialspy";
    }

    @Override
    public void setName(String name) {}

    @Override
    public void setDefaultColor(String defaultColor) {}

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public void addPlayer(Player player) {
        spiesList.add(player);
    }

    @Override
    public void addPlayer(StringsUser user){
        this.addPlayer(user.getPlayer());
    }

    @Override
    public void removePlayer(Player player) {
        spiesList.remove(player);
    }

    @Override
    public void removePlayer(StringsUser user){
        this.removePlayer(user.getPlayer());
    }

    @Override
    public Set<Player> getMembers() {
        return spiesList;
    }

    @Override
    public boolean doUrlFilter() {
        return false;
    }

    @Override
    public void setUrlFilter(boolean doUrlFilter) {}

    @Override
    public boolean doProfanityFilter() {
        return false;
    }

    @Override
    public void setProfanityFilter(boolean doProfanityFilter) {}

    @Override
    public boolean doCooldown() {
        return false;
    }

    @Override
    public void setDoCooldown(boolean doCooldown) {}

    @Override
    public Type getType() {
        return Type.PROTECTED;
    }

    @Override
    public Map<String, Object> getData() {
        return null;
    }

    @Override
    public Membership getMembership() {
        return Membership.PROTECTED;
    }

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public boolean isCallEvent() {
        return false;
    }

    @Override
    public void saveChannel() {
        channelLoader.saveChannel(this);
    }

    @Override
    public boolean allows(Player player) {
        return false;
    }

}
