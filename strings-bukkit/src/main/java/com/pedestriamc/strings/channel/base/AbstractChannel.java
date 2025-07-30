package com.pedestriamc.strings.channel.base;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.chat.MessageProcessor;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import net.md_5.bungee.api.ChatColor;
import net.wiicart.commands.permission.Permissions;
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
import java.util.stream.Collectors;

public abstract class AbstractChannel implements Channel, Monitorable {

    private final Strings strings;

    private final MessageProcessor messageProcessor;
    private final UserUtil userUtil;

    private String name;
    private String defaultColor;
    private String format;

    private final String broadcastFormat;
    private final Membership membership;

    private boolean doCooldown;
    private boolean doProfanityFilter;
    private boolean doUrlFilter;

    private final boolean callEvent;
    private final boolean mentionsEnabled;

    private final int priority;

    private final Set<StringsUser> monitors;
    private final Set<StringsUser> members;

    protected static final String CHANNEL_PERMISSION = "strings.channels.";
    protected static final String MESSAGE_PLACEHOLDER = "{message}";
    protected static final String DEFAULT_BROADCAST_FORMAT = "&8[&3Broadcast&8] &f{message}";

    protected AbstractChannel(@NotNull Strings strings, @NotNull ChannelBuilder data) {
        this.strings = strings;
        this.userUtil = strings.users();
        name = data.getName();
        defaultColor = data.getDefaultColor();
        format = data.getFormat();
        broadcastFormat = data.getBroadcastFormat();
        membership = data.getMembership();
        doCooldown = data.isDoCooldown();
        doProfanityFilter = data.isDoProfanityFilter();
        doUrlFilter = data.isDoUrlFilter();
        callEvent = data.isCallEvent();
        priority = data.getPriority();

        messageProcessor = new MessageProcessor(strings, this);
        mentionsEnabled = strings.getConfiguration().getBoolean(Option.Bool.ENABLE_MENTIONS);
        updatePermissions();
        members = new HashSet<>();
        monitors = new HashSet<>();
    }

    @Override
    public void sendMessage(@NotNull StringsUser user, @NotNull String message) {
        Player p = User.playerOf(user);
        Set<Player> recipients = convertToPlayers(getRecipients(user));

        String template = messageProcessor.generateTemplate(p);
        String processedMessage = messageProcessor.processMessage(p, message);

        if (mentionsEnabled && Mentioner.hasMentionPermission(p)) {
            processedMessage = messageProcessor.processMentions(p, processedMessage);
        }

        if (callsEvents()) {
            sendEventMessage(p, processedMessage, template, recipients);
        } else {
            sendNonEventMessage(p, message, template, recipients);
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

    private @NotNull Set<Player> convertToPlayers(@NotNull Set<StringsUser> users) {
        Set<Player> recipients = new HashSet<>();
        for(StringsUser user : users) {
            try {
                recipients.add(User.playerOf(user));
            } catch(Exception ignored) {}
        }

        return recipients;
    }

    /**
     * Registers the Channel instance's permissions.
     */
    private void updatePermissions() {
        Permissions util = new Permissions(strings);
        String permission = CHANNEL_PERMISSION + getName();

        try {
            util.addPermission(permission);
            util.addPermission(permission + ".broadcast");
        } catch (Exception ignored) {}
    }

    /**
     * Base-line getData() implementation. Most subclasses should override this.
     * @return A populated LinkedHashMap containing channel data
     */
    @Override
    public Map<String, Object> getData() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("format", getFormat());
        map.put("default-color", getDefaultColor());
        map.put("call-event", String.valueOf(callsEvents()));
        map.put("filter-profanity", String.valueOf(isProfanityFiltering()));
        map.put("block-urls", String.valueOf(isUrlFiltering()));
        map.put("cooldown", String.valueOf(isCooldownEnabled()));
        map.put("type", String.valueOf(getType()));
        map.put("membership", String.valueOf(getMembership()));
        map.put("priority", String.valueOf(getPriority()));
        return map;
    }

    @NotNull
    protected Set<StringsUser> filterMutes(@NotNull Set<StringsUser> players) {
        return players.stream()
                .filter(
                p -> {
                    User user = userUtil.getUser(p.getUniqueId());
                    return !user.hasChannelMuted(this);
                })
                .collect(Collectors.toSet());
    }

    protected Set<StringsUser> filterIgnores(@NotNull StringsUser sender, @NotNull Set<StringsUser> players) {
        return players.stream()
                .filter(
                p -> {
                    User user = userUtil.getUser(p.getUniqueId());
                    return !user.isIgnoring(sender);
                })
                .collect(Collectors.toSet());
    }

    protected Set<StringsUser> filterMutesAndIgnores(@NotNull StringsUser sender, @NotNull Set<StringsUser> players) {
        players = filterIgnores(sender, players);
        players = filterMutes(players);

        return players;
    }

    protected UserUtil getUserUtil() {
        return userUtil;
    }

    @Override
    public boolean allows(@NotNull Permissible permissible) {
        if(permissible instanceof Player player && getMembers().contains(strings.users().getUser(player))) {
            return true;
        }

        return Permissions.anyOfOrAdmin(permissible,
                CHANNEL_PERMISSION + getName(),
                CHANNEL_PERMISSION + "*",
                "strings.*"
        );
    }

    @Override
    public @NotNull String getBroadcastFormat() {
        return Objects.requireNonNullElse(broadcastFormat, DEFAULT_BROADCAST_FORMAT);
    }

    @Override
    public void broadcast(@NotNull String message) {
        String finalString = getBroadcastFormat().replace(MESSAGE_PLACEHOLDER, message);
        finalString = ChatColor.translateAlternateColorCodes('&', finalString);
        broadcastPlain(finalString);
    }

    @Override
    public void broadcastPlain(@NotNull String message) {
        for (StringsUser user : getPlayersInScope()) {
            user.sendMessage(message);
        }
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

    public boolean callsEvents() {
        return callEvent;
    }

    @Override
    public void addMember(@NotNull StringsUser user) {
        members.add(user);
    }

    @Override
    public void removeMember(@NotNull StringsUser user) {
        members.remove(user);
    }

    @Override
    public Set<StringsUser> getMembers() {
        return new HashSet<>(members);
    }

    @Override
    public void addMonitor(@NotNull StringsUser user) {
        monitors.add(user);
    }

    public void removeMonitor(@NotNull StringsUser user) {
        monitors.remove(user);
    }

    @Override
    public @NotNull Set<StringsUser> getMonitors() {
        return new HashSet<>(monitors);
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
    public Membership getMembership() {
        return membership;
    }

    @Override
    public @NotNull Channel resolve(@NotNull StringsUser user) {
        return this;
    }

    @Override
    public int compareTo(@NotNull Channel o) {
        return Integer.compare(o.getPriority(), getPriority());
    }

}
