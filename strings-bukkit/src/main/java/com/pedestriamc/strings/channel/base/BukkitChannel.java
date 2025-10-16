package com.pedestriamc.strings.channel.base;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.data.IChannelBuilder;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.chat.MessageProcessor;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BukkitChannel extends AbstractChannel {

    protected static final String CHANNEL_PERMISSION = "strings.channels.";
    protected static final String MESSAGE_PLACEHOLDER = "{message}";
    protected static final String DEFAULT_BROADCAST_FORMAT = "&8[&3Broadcast&8] &f{message}";

    private final Strings strings;

    private final MessageProcessor messageProcessor;
    private final UserUtil userUtil;

    private final boolean mentionsEnabled;

    protected BukkitChannel(@NotNull Strings strings, @NotNull IChannelBuilder<?> data) {
        super(strings, data);
        this.strings = strings;
        this.userUtil = strings.users();

        messageProcessor = new MessageProcessor(strings, this);
        mentionsEnabled = strings.getSettings().get(Option.Bool.ENABLE_MENTIONS);
        updatePermissions();
    }

    @Override
    public void sendMessage(@NotNull StringsUser user, @NotNull String message) {
        Player p = User.playerOf(user);
        Set<Player> recipients = convertToPlayers(getRecipients(user));

        String template = messageProcessor.generateTemplate(p);
        String processedMessage = messageProcessor.processMessage(p, message);

        if (mentionsEnabled && Mentioner.hasMentionPermission(p)) {
            processedMessage = strings.getMentioner().processMentions(p, this, processedMessage);
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

    protected UserUtil getUserUtil() {
        return userUtil;
    }

    @NotNull
    protected Audience getPlayersInScopeAsAudience() {
        BukkitAudiences adventure = strings.adventure();
        Set<Audience> audienceSet = getPlayersInScope().stream()
                .map(p -> adventure.player(p.getUniqueId()))
                .collect(Collectors.toSet());
        audienceSet.add(adventure.console());

        return Audience.audience(audienceSet);
    }

    @Override
    public boolean allows(@NotNull StringsUser user) {
        if (getMembers().contains(user)) {
            return true;
        }

        return Permissions.anyOfOrAdmin(User.playerOf(user),
                CHANNEL_PERMISSION + getName(),
                CHANNEL_PERMISSION + "*",
                "strings.*"
        );
    }

    @Override
    public void broadcast(@NotNull String message) {
        String finalString = getBroadcastFormat().replace(MESSAGE_PLACEHOLDER, message);
        finalString = ChatColor.translateAlternateColorCodes('&', finalString);

        Audience recipients = getPlayersInScopeAsAudience();
        recipients.sendMessage(ComponentConverter.fromString(finalString));
        Sound sound = getBroadcastSound();
        if (sound != null) {
            recipients.playSound(sound);
        }
    }

    @Override
    public void broadcast(@NotNull Component message) {
        Audience recipients = getPlayersInScopeAsAudience();
        recipients.sendMessage(message);
        Sound sound = getBroadcastSound();
        if (sound != null) {
            recipients.playSound(sound);
        }
    }

    @Override
    public void broadcastPlain(@NotNull String message) {
        getPlayersInScopeAsAudience().sendMessage(ComponentConverter.fromString(message));
    }

    @Override
    public void broadcastPlain(@NotNull Component message) {
        getPlayersInScopeAsAudience().sendMessage(message);
    }

    @Override
    public void setName(@NotNull String name) {
        if (strings.getChannelLoader().getChannel(name) != null) {
            throw new IllegalArgumentException("Failed to rename Channel " + getName() + ". Channel " + name + " already exists.");
        }

        // Unregister permissions w/ old name
        Permissions util = new Permissions(strings);
        String permission = CHANNEL_PERMISSION + getName();
        util.removePermission(permission);
        util.removePermission(permission + ".broadcast");

        super.setName(name);
        updatePermissions();
    }

}
