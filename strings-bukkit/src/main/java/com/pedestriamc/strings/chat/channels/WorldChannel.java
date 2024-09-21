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
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WorldChannel implements Channel{

    private final Strings strings;
    private final Set<Player> members;
    private String name;
    private String format;
    private String defaultColor;
    private final ChannelManager channelManager;
    private final ChatManager chatManager;
    private final boolean callEvent;
    private boolean doURLFilter;
    private boolean doProfanityFilter;
    private boolean doCooldown;
    private volatile boolean active;
    private final World world;
    private StringsChannel stringsChannel;
    private final Membership membership;
    private final int priority;


    public WorldChannel(String name, String format, String defaultColor, ChannelManager channelManager, ChatManager chatManager, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, World world, boolean active, Membership membership, int priority){
        strings = Strings.getInstance();
        this.members = ConcurrentHashMap.newKeySet();
        this.name = name;
        this.format = format;
        this.defaultColor = defaultColor;
        this.channelManager = channelManager;
        this.chatManager = chatManager;
        this.callEvent = callEvent;
        this.doURLFilter = doURLFilter;
        this.doProfanityFilter = doProfanityFilter;
        this.doCooldown = doCooldown;
        this.world = world;
        this.active = active;
        this.membership = membership;
        this.priority = priority;
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
        List<Player> list = world.getPlayers();
        list.addAll(members);
        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.hasPermission("strings.channels." + name + ".receive")){
                list.add(p);
            }
        }
        return new HashSet<>(list);
    }

    @Override
    public void broadcastMessage(String message) {
        for(Player p : getRecipients()){
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
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
    public void addPlayer(Player player) {
        members.add(player);
    }

    @Override
    public void addPlayer(User user){
        this.addPlayer(user.getPlayer());
    }

    @Override
    public void removePlayer(Player player) {
        members.remove(player);
    }

    @Override
    public void removePlayer(User user){
        this.removePlayer(user.getPlayer());
    }

    @Override
    public Set<Player> getMembers() {
        return members;
    }

    @Override
    public boolean doURLFilter() {
        return this.doURLFilter;
    }

    @Override
    public void setURLFilter(boolean doURLFilter) {
        this.doURLFilter = doURLFilter;
    }

    @Override
    public boolean doProfanityFilter() {
        return this.doProfanityFilter;
    }

    @Override
    public void setProfanityFilter(boolean doProfanityFilter) {
        this.doProfanityFilter = doProfanityFilter;
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
        return Type.WORLD;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.active = isEnabled;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public World getWorld(){
        return world;
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
        map.put("filter-profanity", String.valueOf(doProfanityFilter));
        map.put("block-urls", String.valueOf(doURLFilter));
        map.put("cooldown", String.valueOf(false));
        map.put("type", String.valueOf(this.getType()));
        map.put("membership", String.valueOf(membership));
        map.put("priority", String.valueOf(priority));
        map.put("world", world.getName());
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
