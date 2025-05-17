package com.pedestriamc.strings.channel.base;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.chat.StringsTextColor;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.utlity.Permissions;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.chat.MessageProcessor;
import com.pedestriamc.strings.configuration.Option;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractChannel implements Channel, Monitorable {

    private final Strings strings;

    private final MessageProcessor messageProcessor;

    private String name;
    private StringsTextColor defaultColor;
    private String format;

    private final String broadcastFormat;
    private final Membership membership;

    private boolean doCooldown;
    private boolean doProfanityFilter;
    private boolean doUrlFilter;

    private final boolean callEvent;
    private final boolean mentionsEnabled;

    private final int priority;

    private final Set<Player> monitors;
    private final Set<Player> members;

    protected static final String CHANNEL_PERMISSION = "strings.channels.";
    protected static final String MESSAGE_PLACEHOLDER = "{message}";
    protected static final String DEFAULT_BROADCAST_FORMAT = "&8[&3Broadcast&8] &f{message}";

    protected AbstractChannel(Strings strings, String name, StringsTextColor defaultColor, String format, Membership membership, boolean doCooldown, boolean doProfanityFilter, boolean doUrlFilter, boolean callEvent, int priority, String broadcastFormat) {
        this.strings = strings;
        this.name = name;
        this.defaultColor = defaultColor != null ? defaultColor : StringsTextColor.WHITE;
        this.format = format;
        this.broadcastFormat = broadcastFormat;
        this.membership = membership;
        this.doCooldown = doCooldown;
        this.doProfanityFilter = doProfanityFilter;
        this.doUrlFilter = doUrlFilter;
        this.callEvent = callEvent;
        this.priority = priority;

        messageProcessor = new MessageProcessor(strings, this);
        mentionsEnabled = strings.getConfiguration().getBoolean(Option.ENABLE_MENTIONS);
        updatePermissions();
        members = new HashSet<>();
        monitors = new HashSet<>();
    }

    /**
     * Provides a Set<Player> of all Players in the Channel's scope.
     * Used to determine who to send broadcasts to.
     * @return A populated Set<Player>
     */
    @NotNull
    public abstract Set<Player> getPlayersInScope();

    @Override
    public void sendMessage(@NotNull Player player, @NotNull String message) {
        Set<Player> recipients = getRecipients(player);
        String template = messageProcessor.generateTemplate(player);
        String processedMessage = messageProcessor.processMessage(player, message);

        if (mentionsEnabled && Mentioner.hasMentionPermission(player)) {
            processedMessage = messageProcessor.processMentions(player, processedMessage);
        }

        if (isCallEvent()) {
            sendEventMessage(player, processedMessage, template, recipients);

        } else {
            sendNonEventMessage(player, message, template, recipients);
        }

    }

    private void sendEventMessage(@NotNull Player player, @NotNull String message, @NotNull String template, @NotNull Set<Player> recipients) {
        Bukkit.getScheduler().runTask(strings, () -> {
            AsyncPlayerChatEvent event = new ChannelChatEvent(false, player, message, recipients, this, true);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                String finalForm = template.replace(MESSAGE_PLACEHOLDER, event.getMessage());
                for (Player p : recipients) {
                    p.sendMessage(finalForm);
                }

                Bukkit.getLogger().info(ChatColor.stripColor(finalForm));
                if(!recipients.contains(player)) {
                    player.sendMessage(finalForm);
                }
            }
        });
    }

    private void sendNonEventMessage(@NotNull Player player, @NotNull String message, @NotNull String template, @NotNull Set<Player> recipients) {
        String finalFormNonEvent = template.replace(MESSAGE_PLACEHOLDER, message);
        for (Player p : recipients) {
            p.sendMessage(finalFormNonEvent);
        }

        if(!recipients.contains(player)) {
            player.sendMessage(finalFormNonEvent);
        }

        Bukkit.getLogger().info(ChatColor.stripColor(finalFormNonEvent));
    }

    /**
     * Registers the Channel instance's permissions.
     */
    private void updatePermissions() {
        Permissions util = new Permissions(strings);
        String permission = CHANNEL_PERMISSION + getName();

        util.addPermission(permission);
        util.addPermission(permission + ".broadcast");
    }

    /**
     * Base-line getData() implementation. Most subclasses should override this.
     * @return A populated LinkedHashMap containing channel data
     */
    @Override
    public Map<String, Object> getData() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("format", getFormat());
        map.put("default-color", getDefaultColor().chatColor());
        map.put("call-event", String.valueOf(isCallEvent()));
        map.put("filter-profanity", String.valueOf(isProfanityFiltering()));
        map.put("block-urls", String.valueOf(isUrlFiltering()));
        map.put("cooldown", String.valueOf(isCooldownEnabled()));
        map.put("type", String.valueOf(getType()));
        map.put("membership", String.valueOf(getMembership()));
        map.put("priority", String.valueOf(getPriority()));
        return map;
    }

    @Override
    public boolean allows(@NotNull Permissible permissible) {
        if(permissible instanceof Player player && getMembers().contains(player)) {
            return true;
        }

        return Permissions.anyOfOrAdmin(permissible,
                CHANNEL_PERMISSION + getName(),
                CHANNEL_PERMISSION + "*",
                "strings.*"
        );
    }

    @Override
    public String getBroadcastFormat() {
        return Objects.requireNonNullElse(broadcastFormat, DEFAULT_BROADCAST_FORMAT);
    }

    @Override
    public void broadcast(String message) {
        String finalString = getBroadcastFormat().replace(MESSAGE_PLACEHOLDER, message);
        finalString = ChatColor.translateAlternateColorCodes('&', finalString);
        for (Player p : getPlayersInScope()) {
            p.sendMessage(finalString);
        }
    }

    @Override
    public StringsTextColor getDefaultColor() {
        return defaultColor;
    }

    @Override
    public void setDefaultColor(StringsTextColor defaultColor) {
        this.defaultColor = defaultColor;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name) {
        // Unregister permissions w/ old name
        Permissions util = new Permissions(strings);
        String permission = CHANNEL_PERMISSION + getName();
        util.removePermission(permission);
        util.removePermission(permission + ".broadcast");

        this.name = name;
        updatePermissions();
    }

    public boolean isCallEvent() {
        return callEvent;
    }

    @Override
    public void addMember(@NotNull StringsUser user) {
        addMember(user.getPlayer());
    }

    @Override
    public void removeMember(@NotNull StringsUser user) {
        removeMember(user.getPlayer());
    }

    @Override
    public boolean isUrlFiltering() {
        return doUrlFilter;
    }

    @Override
    public void setUrlFilter(boolean doUrlFilter) {
        this.doUrlFilter = doUrlFilter;
    }

    @Override
    public boolean isProfanityFiltering() {
        return doProfanityFilter;
    }

    @Override
    public void setProfanityFilter(boolean doProfanityFilter) {
        this.doProfanityFilter = doProfanityFilter;
    }

    @Override
    public boolean isCooldownEnabled() {
        return this.doCooldown;
    }

    @Override
    public void setDoCooldown(boolean doCooldown) {
        this.doCooldown = doCooldown;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setFormat(@NotNull String format) {
        Objects.requireNonNull(format);
        this.format = format;
    }

    @Override
    public @NotNull String getFormat() {
        return format;
    }

    @Override
    public void addMonitor(@NotNull Player player) {
        monitors.add(player);
    }

    @Override
    public void removeMonitor(@NotNull Player player) {
        monitors.remove(player);
    }

    @Override
    public @NotNull Set<Player> getMonitors() {
        return new HashSet<>(monitors);
    }

    @Override
    public void addMember(@NotNull Player player) {
        members.add(player);
    }

    @Override
    public void removeMember(@NotNull Player player) {
        members.remove(player);
    }

    @Override
    public Set<Player> getMembers() {
        return new HashSet<>(members);
    }

    @Override
    public Membership getMembership() {
        return membership;
    }

    @Override
    public Channel resolve(@NotNull Player player) {
        return this;
    }

}
