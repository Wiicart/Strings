package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.ChatManager;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProximityChannel implements Channel{

    private final Strings strings;
    private final Set<Player> members;
    private String name;
    private String format;
    private String defaultColor;
    private final ChannelManager channelManager;
    private final ChatManager chatManager;
    private boolean callEvent;
    private volatile boolean active;
    private boolean doURLFilter;
    private boolean doProfanityFilter;
    private boolean doCooldown;
    private int distance;
    private StringsChannel stringsChannel;
    private final Membership membership;
    private final int priority;

    public ProximityChannel(@NotNull Strings strings, String name, String format, String defaultColor, ChannelManager channelManager, ChatManager chatManager, boolean callEvent, boolean doURLFilter, boolean doProfanityFilter, boolean doCooldown, int distance, boolean active, Membership membership, int priority){
        this.strings = strings;
        this.name = name;
        this.format = format;
        this.defaultColor = defaultColor;
        this.channelManager = channelManager;
        this.chatManager = chatManager;
        this.callEvent = callEvent;
        this.doURLFilter = doURLFilter;
        this.doProfanityFilter = doProfanityFilter;
        this.doCooldown = doCooldown;
        this.distance = distance;
        this.members = ConcurrentHashMap.newKeySet();
        this.membership = membership;
        this.priority = priority;
        channelManager.registerChannel(this);
    }

    @Override
    public void sendMessage(@NotNull Player player, String message) {
        if(!active){
            Messenger.sendMessage(Message.CHANNEL_DISABLED, player);
            return;
        }
        HashSet<Player> receivers = new HashSet<>(getRecipients(player));
        receivers.addAll(members);
        String format = chatManager.formatMessage(player, this);
        message = chatManager.processMessage(player, message);
        String finalMessage = message;

        if(callEvent){
            Bukkit.getScheduler().runTask(strings, () ->{
                AsyncPlayerChatEvent event = new ChannelChatEvent(false, player, finalMessage, receivers, this.getStringsChannel());
                event.setFormat(format);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()){
                    String formattedMessage = String.format(event.getFormat(), strings.getUser(player).getDisplayName(), event.getMessage());
                    for(Player p : receivers){
                        p.sendMessage(formattedMessage);
                    }
                    Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
                    chatManager.startCoolDown(player);
                }
            });
            return;
        }

        String formattedMessage = String.format(format, player.getDisplayName(), message);
        for(Player p : receivers){
            p.sendMessage(formattedMessage);
        }
        Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
        chatManager.startCoolDown(player);
    }

    //https://www.spigotmc.org/threads/getting-player-distance-to-another-player.343523/
    private @NotNull HashSet<Player> getRecipients(@NotNull Player sender){
        HashSet<Player> players = new HashSet<>();
        World world = sender.getWorld();
        for(Player p : world.getPlayers()){
            if(sender.getLocation().distance(p.getLocation()) < distance){
                players.add(p);
            }else if(p.hasPermission("strings.channels." + name + ".receive")){
                players.add(p);
            }
        }
        return players;
    }

    @Override
    public void broadcastMessage(String message) {
        for(Player p : members){
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
        return new HashSet<>(members);
    }

    @Override
    public boolean doURLFilter() {
        return doURLFilter;
    }

    @Override
    public void setURLFilter(boolean doURLFilter) {
        this.doURLFilter = doURLFilter;
        channelManager.saveChannel(this);
    }

    @Override
    public boolean doProfanityFilter() {
        return doProfanityFilter;
    }

    @Override
    public void setProfanityFilter(boolean doProfanityFilter) {
        this.doProfanityFilter = doProfanityFilter;
        channelManager.saveChannel(this);
    }

    @Override
    public boolean doCooldown() {
        return doCooldown;
    }

    @Override
    public void setDoCooldown(boolean doCooldown) {
        this.doCooldown = doCooldown;
        channelManager.saveChannel(this);
    }

    @Override
    public Type getType() {
        return Type.PROXIMITY;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.active = isEnabled;
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
        map.put("filter-profanity", String.valueOf(doProfanityFilter));
        map.put("block-urls", String.valueOf(doURLFilter));
        map.put("cooldown", String.valueOf(doCooldown));
        map.put("type", String.valueOf(this.getType()));
        map.put("membership", String.valueOf(membership));
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
}
