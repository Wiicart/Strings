package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.api.Type;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.chat.ChatManager;
import com.pedestriamc.strings.impl.ChannelWrapper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.Set;

abstract class AbstractChannel implements Channel{

    private final Strings strings;
    private final ChannelManager channelManager;
    private final ChatManager chatManager;
    private String name;
    private String defaultColor;
    private String format;
    private final Membership membership;
    private boolean doCooldown;
    private boolean doProfanityFilter;
    private boolean doUrlFilter;
    private final boolean callEvent;
    private final int priority;
    private StringsChannel stringsChannel;
    private final boolean mentionsEnabled;

    protected AbstractChannel(Strings strings, ChannelManager channelManager, String name, String defaultColor, String format, Membership membership, boolean doCooldown, boolean doProfanityFilter, boolean doUrlFilter, boolean callEvent, int priority) {
        this.strings = strings;
        this.channelManager = channelManager;
        this.chatManager = strings.getChatManager();
        this.name = name;
        this.defaultColor = defaultColor != null ? defaultColor : "&f";
        this.format = format;
        this.membership = membership;
        this.doCooldown = doCooldown;
        this.doProfanityFilter = doProfanityFilter;
        this.doUrlFilter = doUrlFilter;
        this.callEvent = callEvent;
        this.priority = priority;
        this.mentionsEnabled = chatManager.isMentionsEnabled();
    }

    @Override
    public void sendMessage(Player player, String message) {

        Set<Player> recipients = getRecipients(player);
        String template = chatManager.formatMessage(player, this);
        String processedMessage = chatManager.processMessage(player, message);
        String finalForm = template.replace("{message}", processedMessage);

        if(mentionsEnabled && player.hasPermission("strings.*") || player.hasPermission("strings.mention")) {
            finalForm = chatManager.processMentions(player, finalForm);
        }

        if(callEvent){
            String finalString = finalForm;
            Bukkit.getScheduler().runTask(strings, () -> {
                AsyncPlayerChatEvent event = new ChannelChatEvent(false, player, message, recipients, this.getStringsChannel());
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()){
                    for(Player p : recipients){
                        p.sendMessage(finalString);
                    }
                    Bukkit.getLogger().info(ChatColor.stripColor(finalString));
                    chatManager.startCoolDown(player);
                }
            });
            return;
        }

        for(Player p : recipients){
            p.sendMessage(finalForm);
        }
        Bukkit.getLogger().info(ChatColor.stripColor(finalForm));
        chatManager.startCoolDown(player);

    }

    public abstract Set<Player> getRecipients(Player sender);

    @Override
    public void broadcastMessage(String message) {
        for(Player p : getRecipients(null)){
            p.sendMessage(message);
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
        return defaultColor;
    }

    @Override
    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public boolean isCallEvent(){
        return callEvent;
    }

    @Override
    public abstract void addPlayer(Player player);

    @Override
    public abstract void addPlayer(User user);

    @Override
    public abstract void removePlayer(Player player);

    @Override
    public abstract void removePlayer(User user);

    @Override
    public abstract Set<Player> getMembers();

    @Override
    public boolean doUrlFilter() {
        return doUrlFilter;
    }

    @Override
    public void setUrlFilter(boolean doUrlFilter) {
        this.doUrlFilter = doUrlFilter;
    }

    @Override
    public boolean doProfanityFilter() {
        return doProfanityFilter;
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
    public abstract Type getType();

    @Override
    public abstract Map<String, String> getData();

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

    @Override
    public StringsChannel getStringsChannel() {
        if(stringsChannel == null){
            stringsChannel = new ChannelWrapper(this);
        }
        return stringsChannel;
    }
}
