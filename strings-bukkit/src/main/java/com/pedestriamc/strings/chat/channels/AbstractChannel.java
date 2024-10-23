package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.event.StringsChatEvent;
import com.pedestriamc.strings.chat.ChannelManager;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.api.Type;
import com.pedestriamc.strings.chat.ChatManager;
import com.pedestriamc.strings.impl.ChannelWrapper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

abstract class AbstractChannel implements Channel {

    private final Strings strings;
    private final ChannelManager channelManager;
    private final ChatManager chatManager;
    private String name;
    private String defaultColor;
    private String format;
    private String eventFormat;
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
        this.eventFormat = format.replace("{displayname}", "%s").replace("{message}", "%s");
    }

    /**
     * Determines which players messages should be sent to.
     * Must be implemented by extending classes.
     *
     * @param sender The sender of the message.
     * @return A Set<Player> of all players who will see the message.
     */
    public abstract Set<Player> getRecipients(Player sender);

    @Override
    public void sendMessage(Player player, String message) {
        sendMessage(player, message, null);

    }

    @Override
    public void sendMessage(Player player, String message, AsyncPlayerChatEvent event){

        boolean eventIsNull = event == null;

        Set<Player> recipients = getRecipients(player);
        if(!eventIsNull){
            event.getRecipients().clear();
            event.getRecipients().addAll(recipients);
        }

        String template = chatManager.formatMessage(player, this);
        String processedMessage = chatManager.processMessage(player, message);
        String finalForm = template.replace("{message}", processedMessage);

        if (mentionsEnabled && player.hasPermission("strings.*") || player.hasPermission("strings.mention")) {
            finalForm = chatManager.processMentions(player, finalForm);
        }

        if (callEvent && !eventIsNull) {
            String finalString = finalForm;
            Bukkit.getScheduler().runTask(strings, () -> {
                event.setMessage(processedMessage);
                event.setFormat(eventFormat);
                StringsChatEvent stringsChatEvent = new StringsChatEvent(event, this.getStringsChannel());
                Bukkit.getPluginManager().callEvent(stringsChatEvent);
                if (!event.isCancelled()) {
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

    @Override
    public void broadcastMessage(String message) {
        for (Player p : getRecipients(null)) {
            p.sendMessage(message);
        }
    }

    @Override
    public void saveChannel() {
        channelManager.saveChannel(this);
    }

    /**
     * Base-line getData() implementation.  Most subclasses should override this.
     *
     * @return A populated LinkedHashMap containing channel data
     */
    @Override
    public Map<String, String> getData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
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
    public StringsChannel getStringsChannel() {
        if (stringsChannel == null) {
            stringsChannel = new ChannelWrapper(this);
        }
        return stringsChannel;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
        this.eventFormat = format.replace("{displayname}", "%s").replace("{message}", "%s");
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
    public abstract void addPlayer(Player player);

    @Override
    public void addPlayer(User user) {
        addPlayer(user.getPlayer());
    }

    @Override
    public abstract void removePlayer(Player player);

    @Override
    public void removePlayer(User user) {
        removePlayer(user.getPlayer());
    }

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
    public Membership getMembership() {
        return membership;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean allows(Player player) {
        if(getMembers().contains(player)){
            return true;
        }
        return (
                player.hasPermission("strings.channels." + getName()) ||
                player.hasPermission("strings.channels.*") ||
                player.hasPermission("strings.*"
                )
        );
    }
}
