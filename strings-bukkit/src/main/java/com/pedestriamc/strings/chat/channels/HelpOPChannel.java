package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.api.channels.Buildable;
import com.pedestriamc.strings.api.channels.Channel;
import com.pedestriamc.strings.api.channels.ChannelLoader;
import com.pedestriamc.strings.api.channels.data.ChannelData;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.chat.ChatManager;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.channels.Type;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HelpOPChannel implements Channel, Buildable {

    private final Strings strings;
    private String name;
    private final ChatManager chatManager;
    private final boolean callEvent;
    private String format;
    private String defaultColor;
    private final ChannelLoader channelLoader;
    private boolean urlFilter;
    private boolean profanityFilter;
    private final Messenger messenger;

    public HelpOPChannel(@NotNull Strings strings, String name, String format, String defaultColor, ChannelLoader channelLoader, boolean callEvent, boolean urlFilter, boolean profanityFilter)
    {
        this.strings = strings;
        this.chatManager = strings.getChatManager();
        this.callEvent = callEvent;
        this.format = format;
        this.name = name;
        this.defaultColor = defaultColor;
        this.channelLoader = channelLoader;
        this.urlFilter = urlFilter;
        this.profanityFilter = profanityFilter;
        this.messenger = strings.getMessenger();
    }

    public HelpOPChannel(Strings strings, ChannelData data)
    {
        this.strings = strings;
        this.chatManager = strings.getChatManager();
        this.channelLoader = strings.getChannelLoader();
        this.messenger = strings.getMessenger();
        this.name = data.getName();
        this.callEvent = data.isCallEvent();
        this.format = data.getFormat();
        this.defaultColor = data.getDefaultColor();
        this.urlFilter = data.isDoUrlFilter();
        this.profanityFilter = data.isDoProfanityFilter();
    }

    @Override
    public void sendMessage(Player player, String message) {
        Set<Player> members = getRecipients();
        String format = chatManager.formatMessage(player, this);
        message = chatManager.processMessage(player, message);
        String finalMessage = message;
        String formattedMessage = format.replace("{message}", finalMessage);
        if(callEvent){
            Bukkit.getScheduler().runTask(strings, () ->{
                AsyncPlayerChatEvent event = new ChannelChatEvent(false, player, finalMessage, members, this);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()){
                    for(Player p : members){
                        p.sendMessage(formattedMessage);
                    }
                    Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
                }
            });
        }else{
            for(Player p : members){
                p.sendMessage(formattedMessage);
            }
            Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
        }
        messenger.sendMessage(Message.HELPOP_SENT, player);
    }

    private Set<Player> getRecipients(){
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
        return members;
    }

    @Override
    public void broadcastMessage(String message) {}

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
    public void addPlayer(StringsUser user){}

    @Override
    public void removePlayer(Player player) {}

    @Override
    public void removePlayer(StringsUser user){}

    @Override
    public Set<Player> getMembers() {
        return null;
    }

    @Override
    public boolean doUrlFilter() {
        return urlFilter;
    }

    @Override
    public void setUrlFilter(boolean doUrlFilter) {
        this.urlFilter = doUrlFilter;
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
    public void setDoCooldown(boolean doCooldown) {}

    @Override
    public Type getType() {
        return Type.PROTECTED;
    }

    @Override
    public boolean isCallEvent(){
        return callEvent;
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
    public void saveChannel() {
        channelLoader.saveChannel(this);
    }

    @Override
    public boolean allows(Player player) {
        return (player.hasPermission("strings.helpop.use") ||  player.hasPermission("strings.*"));
    }
}
