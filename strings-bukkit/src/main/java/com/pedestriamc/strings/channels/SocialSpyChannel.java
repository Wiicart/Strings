package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.User;
import com.pedestriamc.strings.api.Type;
import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.impl.ChannelWrapper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SocialSpyChannel implements Channel{

    private String format;
    private final PlayerDirectMessenger playerDirectMessenger;
    private final HashSet<Player> spiesList;
    private StringsChannel stringsChannel;


    public SocialSpyChannel(ChannelManager channelmanager, PlayerDirectMessenger messenger, String format){
        this.format = format;
        this.playerDirectMessenger = messenger;
        this.spiesList = new HashSet<>();
        channelmanager.registerChannel(this);
    }

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
    public void closeChannel() {

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
    public void setName(String name) {

    }

    @Override
    public void setDefaultColor(String defaultColor) {
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public void addPlayer(Player player) {
        spiesList.add(player);
    }

    @Override
    public void addPlayer(User user){
        this.addPlayer(user.getPlayer());
    }

    @Override
    public void removePlayer(Player player) {
        spiesList.remove(player);
    }

    @Override
    public void removePlayer(User user){
        this.removePlayer(user.getPlayer());
    }

    @Override
    public Set<Player> getMembers() {
        return spiesList;
    }

    @Override
    public boolean doURLFilter() {
        return false;
    }

    @Override
    public boolean doProfanityFilter() {
        return false;
    }

    @Override
    public boolean doCooldown() {
        return false;
    }

    @Override
    public Type getType() {
        return Type.PROTECTED;
    }

    @Override
    public void setEnabled(boolean isEnabled) {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, String> getData() {
        return null;
    }

    @Override
    public StringsChannel getStringsChannel(){
        if(stringsChannel == null){
            stringsChannel = new ChannelWrapper(this);
        }
        return stringsChannel;
    }
}
