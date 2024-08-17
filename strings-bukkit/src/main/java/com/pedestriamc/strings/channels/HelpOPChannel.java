package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.ChatManager;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.api.Type;
import com.pedestriamc.strings.impl.ChannelWrapper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HelpOPChannel implements Channel {

    private final Strings strings;
    private String name;
    private final ChatManager chatManager;
    private final boolean callEvent;
    private String format;
    private String defaultColor;
    private final ChannelManager channelManager;
    private boolean urlFilter;
    private boolean profanityFilter;
    private StringsChannel stringsChannel;

    public HelpOPChannel(@NotNull Strings strings, String name, String format, String defaultColor, ChannelManager channelManager, boolean callEvent, boolean urlFilter, boolean profanityFilter) {
        this.strings = strings;
        this.chatManager = strings.getChatManager();
        this.callEvent = callEvent;
        this.format = format;
        this.name = name;
        this.defaultColor = defaultColor;
        this.channelManager = channelManager;
        this.urlFilter = urlFilter;
        this.profanityFilter = profanityFilter;
        channelManager.registerChannel(this);
    }

    @Override
    public void sendMessage(Player player, String message){
        HashSet<Player> members = new HashSet<>();
        for(OfflinePlayer op : Bukkit.getOperators()){
            if(op.getName() != null){
                Player p = Bukkit.getPlayer(op.getName());
                if(p != null){
                    members.add(p);
                }
            }
        }
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
            if(onlinePlayer.hasPermission("strings.helpop.receive") || onlinePlayer.hasPermission("strings.helpop.*") || onlinePlayer.hasPermission("strings.*")){
                members.add(onlinePlayer);
            }
        }
        String format = chatManager.formatMessage(player, this);
        message = chatManager.processMessage(player, message);
        String finalMessage = message;
        if(callEvent){
            Bukkit.getScheduler().runTask(strings, () ->{
                AsyncPlayerChatEvent event = new ChannelChatEvent(false, player, finalMessage, members, this.getStringsChannel());
                event.setFormat(format);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()){
                    String formattedMessage = String.format(event.getFormat(), strings.getUser(player).getDisplayName(), event.getMessage());
                    for(Player p : members){
                        p.sendMessage(formattedMessage);
                    }
                    Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
                }
            });
        }else{
            String formattedMessage = String.format(format, player.getDisplayName(), message);
            for(Player p : members){
                p.sendMessage(formattedMessage);
            }
            Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
        }

    }

    @Override
    public void broadcastMessage(String message) {

    }

    @Override
    public void closeChannel() {
        channelManager.unregisterChannel(this);
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public String getDefaultColor() {
        return defaultColor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public void addPlayer(Player player) {}

    @Override
    public void addPlayer(User user){}

    @Override
    public void removePlayer(Player player) {}

    @Override
    public void removePlayer(User user){}

    @Override
    public Set<Player> getMembers() {
        return null;
    }

    @Override
    public boolean doURLFilter() {
        return urlFilter;
    }

    @Override
    public void setURLFilter(boolean doURLFilter) {
        this.urlFilter = doURLFilter;
    }

    @Override
    public boolean doProfanityFilter() {
        return profanityFilter;
    }

    @Override
    public void setProfanityFilter(boolean doProfanityFilter) {
        this.profanityFilter = doProfanityFilter;
    }

    @Override
    public boolean doCooldown() {
        return false;
    }

    @Override
    public void setDoCooldown(boolean doCooldown) {
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
    public StringsChannel getStringsChannel(){
        if(stringsChannel == null){
            stringsChannel = new ChannelWrapper(this);
        }
        return stringsChannel;
    }

    @Override
    public Map<String, String> getData() {
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
    public void saveChannel() {
        channelManager.saveChannel(this);
    }
}
