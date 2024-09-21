package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.chat.ChatManager;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.api.Type;
import com.pedestriamc.strings.impl.ChannelWrapper;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StringChannel implements Channel{

    private final Strings strings;
    private String name;
    private String format;
    private String defaultColor;
    private final boolean callEvent;
    private final Set<Player> members;
    private final ChatManager chatManager;
    private volatile boolean active;
    private  boolean urlFilter;
    private  boolean profanityFilter;
    private final ChannelManager channelManager;
    private  boolean doCooldown;
    private StringsChannel stringsChannel;
    private final Membership membership;
    private final int priority;
    private final String mentionColor;

    public StringChannel(@NotNull Strings strings, String name, String format, String defaultColor, @NotNull ChannelManager channelManager, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, boolean active, Membership membership, int priority){
        this.strings = strings;
        this.name = name;
        this.members = ConcurrentHashMap.newKeySet();
        this.format = format;
        this.defaultColor = defaultColor != null ? defaultColor : "&f";
        this.chatManager = strings.getChatManager();
        this.active = active;
        this.callEvent = callEvent;
        this.urlFilter = doURLFilter;
        this.profanityFilter = doProfanityFilter;
        this.channelManager = channelManager;
        this.doCooldown = doCooldown;
        this.membership = membership;
        this.priority = priority;
        this.mentionColor = ChatColor.translateAlternateColorCodes('&', strings.getConfig().getString("mention-color", "&e"));
        channelManager.registerChannel(this);
    }

    @Override
    public void sendMessage(Player player, String message){
        if(!active){
            Messenger.sendMessage(Message.CHANNEL_DISABLED, player);
            return;
        }
        String format = chatManager.formatMessage(player, this);
        message = chatManager.processMessage(player, message);
        String fMessage = message;
        String formattedMessage = format.replace("{message}", fMessage);
        if(chatManager.isMentionsEnabled() && player.hasPermission("strings.*") || player.hasPermission("strings.mention")) {
            formattedMessage = chatManager.processMentions(player, formattedMessage);
        }
        if(callEvent){
            String finalFormattedMessage = formattedMessage;
            Bukkit.getScheduler().runTask(strings, () -> {
                AsyncPlayerChatEvent event = new ChannelChatEvent(false, player, fMessage, getRecipients(), this.getStringsChannel());
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()){
                    for(Player p : getRecipients()){
                        p.sendMessage(finalFormattedMessage);
                    }
                    Bukkit.getLogger().info(ChatColor.stripColor(finalFormattedMessage));
                    chatManager.startCoolDown(player);
                }
            });
        }else{
            for(Player p : getRecipients()){
                p.sendMessage(formattedMessage);
            }
            Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
            chatManager.startCoolDown(player);
        }
    }

    public Set<Player> getRecipients(){
        List<Player> list = new ArrayList<>(members);
        if(membership == Membership.DEFAULT){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(list.contains(p)){
                    continue;
                }
                list.add(p);
            }
        }

        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.hasPermission("strings.channels." + name + ".receive")){
                list.add(p);
            }
        }
        return new HashSet<>(list);
    }

    @Override
    public void broadcastMessage(String message){
        for(Player p : getRecipients()){
            p.sendMessage(message);
        }
    }

    @Override
    public void closeChannel(){
        channelManager.unregisterChannel(this);
        channelManager.deleteChannel(this);
        active = false;
    }

    @Override
    public String getFormat(){
        return format;
    }

    @Override
    public String getDefaultColor(){
        return defaultColor;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public void setName(String name){
        this.name = name;
    }

    @Override
    public void setDefaultColor(String defaultColor){
        this.defaultColor = defaultColor;
    }

    @Override
    public void setFormat(String format){ this.format = format; }

    @Override
    public void addPlayer(Player player){
        members.add(player);
    }

    @Override
    public void addPlayer(User user){
        this.addPlayer(user.getPlayer());
    }

    @Override
    public void removePlayer(Player player){
        members.remove(player);
    }

    @Override
    public void removePlayer(User user){
        this.removePlayer(user.getPlayer());
    }

    @Override
    public Set<Player> getMembers(){
        return members;
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
        return this.doCooldown;
    }

    @Override
    public void setDoCooldown(boolean doCooldown) {
        this.doCooldown = doCooldown;
    }

    @Override
    public Type getType() {
        return Type.NORMAL;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        active = isEnabled;
    }

    @Override
    public boolean isEnabled() {
        return active;
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
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("format", format);
        map.put("default-color", defaultColor);
        map.put("call-event", String.valueOf(callEvent));
        map.put("filter-profanity", String.valueOf(profanityFilter));
        map.put("block-urls", String.valueOf(urlFilter));
        map.put("cooldown", String.valueOf(doCooldown));
        map.put("type", String.valueOf(this.getType()));
        map.put("membership", String.valueOf(membership));
        map.put("priority", String.valueOf(priority));
        return map;
    }

    @Override
    public Membership getMembership() {
        return membership;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void saveChannel() {
        channelManager.saveChannel(this);
    }
}
