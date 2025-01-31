package com.pedestriamc.strings.chat.channels.base;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.api.channels.Channel;
import com.pedestriamc.strings.api.channels.ChannelLoader;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.channels.Membership;
import com.pedestriamc.strings.api.channels.Type;
import com.pedestriamc.strings.chat.ChatManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractChannel implements Channel {

    private final Strings strings;
    private final ChannelLoader channelLoader;
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
    private final boolean mentionsEnabled;

    protected AbstractChannel(Strings strings, ChannelLoader channelLoader, String name, String defaultColor, String format, Membership membership, boolean doCooldown, boolean doProfanityFilter, boolean doUrlFilter, boolean callEvent, int priority) {
        this.strings = strings;
        this.channelLoader = channelLoader;
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

    /**
     * Determines which players messages should be sent to.
     * Must be implemented by extending classes.
     * @param sender The sender of the message.
     * @return A Set<Player> of all players who will see the message.
     */
    public abstract Set<Player> getRecipients(Player sender);

    @Override
    public abstract Set<Player> getMembers();

    @Override
    public abstract void addPlayer(Player player);

    @Override
    public abstract void removePlayer(Player player);

    @Override
    public abstract Type getType();

    @Override
    public void sendMessage(Player player, String message) {
        Set<Player> recipients = getRecipients(player);
        String template = chatManager.formatMessage(player, this);
        String processedMessage = chatManager.processMessage(player, message, this);
        String finalForm = template.replace("{message}", processedMessage);

        if (mentionsEnabled && player.hasPermission("strings.*") || player.hasPermission("strings.mention")) {
            finalForm = chatManager.processMentions(player, finalForm);
        }

        if (isCallEvent()) {
            String finalString = finalForm;
            Bukkit.getScheduler().runTask(strings, () -> {
                AsyncPlayerChatEvent event = new ChannelChatEvent(false, player, message, recipients, this);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    for (Player p : recipients) {
                        p.sendMessage(finalString);
                    }
                    Bukkit.getLogger().info(ChatColor.stripColor(finalString));
                    chatManager.startCoolDown(player);
                    if(!recipients.contains(player)) {
                        player.sendMessage(finalString);
                    }
                }
            });
            return;
        }

        for (Player p : recipients) {
            p.sendMessage(finalForm);
        }
        if(!recipients.contains(player)) {
            player.sendMessage(finalForm);
        }

        Bukkit.getLogger().info(ChatColor.stripColor(finalForm));
        chatManager.startCoolDown(player);

    }

    /**
     * Base-line getData() implementation.  Most subclasses should override this.
     *
     * @return A populated LinkedHashMap containing channel data
     */
    @Override
    public Map<String, Object> getData() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("format", getFormat());
        map.put("default-color", getDefaultColor());
        map.put("call-event", String.valueOf(isCallEvent()));
        map.put("filter-profanity", String.valueOf(doProfanityFilter()));
        map.put("block-urls", String.valueOf(doUrlFilter()));
        map.put("cooldown", String.valueOf(doCooldown()));
        map.put("type", String.valueOf(getType()));
        map.put("membership", String.valueOf(getMembership()));
        map.put("priority", String.valueOf(getPriority()));
        return map;
    }

    @Override
    public boolean allows(Player player) {
        if(getMembers().contains(player)) {
            return true;
        }
        return (
                player.hasPermission("strings.channels." + getName()) ||
                player.hasPermission("strings.channels.*") ||
                player.hasPermission("strings.*") ||
                player.hasPermission("*")
        );
    }

    @Override
    public void broadcastMessage(String message) {
        for (Player p : getRecipients(null)) {
            p.sendMessage(message);
        }
    }

    @Override
    public void saveChannel() {
        channelLoader.saveChannel(this);
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

    public boolean isCallEvent() {
        return callEvent;
    }

    @Override
    public void addPlayer(StringsUser user) {
        addPlayer(user.getPlayer());
    }

    @Override
    public void removePlayer(StringsUser user) {
        removePlayer(user.getPlayer());
    }

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
    public Membership getMembership() {
        return membership;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setFormat(String format){
        this.format = format;
    }

}
