package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.ChatManager;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.impl.ChannelWrapper;
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
    private final boolean doURLFilter;
    private final boolean doProfanityFilter;
    private final boolean doCooldown;
    private volatile boolean active;
    private final World world;
    private StringsChannel stringsChannel;


    public WorldChannel(String name, String format, String defaultColor, ChannelManager channelManager, ChatManager chatManager, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, World world, boolean active){
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
        channelManager.registerChannel(this);
    }

    @Override
    public void sendMessage(Player player, String message) {
        String format = chatManager.formatMessage(player, this);
        message = chatManager.processMessage(player, message);
        String finalMessage = message;
        if(callEvent){
            Bukkit.getScheduler().runTask(strings, () ->{
                AsyncPlayerChatEvent event = new ChannelChatEvent(false, player, finalMessage, members, this);
                event.setFormat(format);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()){
                    String formattedMessage = String.format(event.getFormat(), strings.getUser(player).getDisplayName(), event.getMessage());
                    for(Player p : getRecipients()){
                        p.sendMessage(formattedMessage);
                    }
                    Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
                }
            });
        }else{
            String formattedMessage = String.format(format, player.getDisplayName(), message);
            for(Player p : getRecipients()){
                p.sendMessage(formattedMessage);
            }
            Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
        }

    }

    public Set<Player> getRecipients(){
        List<Player> list = world.getPlayers();
        list.addAll(members);
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
    public boolean doProfanityFilter() {
        return this.doProfanityFilter;
    }

    @Override
    public boolean doCooldown() {
        return this.doCooldown;
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
        return map;
    }
}
