package com.pedestriamc.strings.api.user;

import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.api.discord.Snowflake;
import com.pedestriamc.strings.api.text.format.StringsComponent;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@SuppressWarnings("deprecation")
final class AutoSavingUser implements StringsUser {

    private final StringsUser user;

    AutoSavingUser(@NotNull StringsUser user) {
        this.user = user;
    }

    private void save() {
        try {
            StringsProvider.get().saveUser(user);
        } catch(Exception ignored) {}
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return user.getUniqueId();
    }

    @Override
    public @NotNull String getName() {
        return user.getName();
    }

    @Override
    @Nullable
    public String getChatColor() {
        return user.getChatColor();
    }

    @Override
    public void setChatColor(String chatColor) {
        user.setChatColor(chatColor);
        save();
    }

    @Override
    public StringsComponent getChatColorComponent() {
        return user.getChatColorComponent();
    }

    @Override
    public void setChatColorComponent(StringsComponent chatColor) {
        user.setChatColorComponent(chatColor);
        save();
    }

    @Override
    public @NotNull String getPrefix() {
        return user.getPrefix();
    }

    @Override
    public void setPrefix(@NotNull String prefix) {
        user.setPrefix(prefix);
        save();
    }

    @Override
    @NotNull
    public String getSuffix() {
        return user.getSuffix();
    }

    @Override
    public void setSuffix(@NotNull String suffix) {
        user.setSuffix(suffix);
        save();
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return user.getDisplayName();
    }

    @Override
    public void setDisplayName(@NotNull String displayName) {
        user.setDisplayName(displayName);
        save();
    }

    @Override
    @NotNull
    public Channel getActiveChannel() {
        return user.getActiveChannel();
    }

    @Override
    public void setActiveChannel(@NotNull Channel channel) {
        user.setActiveChannel(channel);
        save();
    }

    @Override
    @NotNull
    public Set<Channel> getChannels() {
        return user.getChannels();
    }

    @Override
    public void joinChannel(@NotNull Channel channel) {
        user.joinChannel(channel);
        save();
    }

    @Override
    public void leaveChannel(@NotNull Channel channel) {
        user.leaveChannel(channel);
        save();
    }

    @Override
    public boolean memberOf(@NotNull Channel channel) {
        return user.memberOf(channel);
    }

    @Override
    public boolean isMentionsEnabled() {
        return user.isMentionsEnabled();
    }

    @Override
    public void setMentionsEnabled(boolean mentionsEnabled) {
        user.setMentionsEnabled(mentionsEnabled);
        save();
    }

    @Override
    public boolean isIgnoring(@NotNull StringsUser other) {
        return user.isIgnoring(other);
    }

    @Override
    public void ignore(@NotNull StringsUser user) {
        user.ignore(user);
        save();
    }

    @Override
    public void stopIgnoring(@NotNull StringsUser user) {
        user.stopIgnoring(user);
        save();
    }

    @Override
    public Set<UUID> getIgnoredPlayers() {
        return user.getIgnoredPlayers();
    }

    @Override
    public boolean isMonitoring(@NotNull Monitorable monitorable) {
        return user.isMonitoring(monitorable);
    }

    @Override
    public void monitor(@NotNull Monitorable monitorable) {
        user.monitor(monitorable);
        save();
    }

    @Override
    public void unmonitor(@NotNull Monitorable monitorable) {
        user.unmonitor(monitorable);
        save();
    }

    @Override
    @NotNull
    public Set<Channel> getMonitoredChannels() {
        return user.getMonitoredChannels();
    }

    @Override
    public void muteChannel(@NotNull Channel channel) {
        user.muteChannel(channel);
        save();
    }

    @Override
    public void unmuteChannel(@NotNull Channel channel) {
        user.unmuteChannel(channel);
        save();
    }

    @Override
    @NotNull
    public Set<Channel> getMutedChannels() {
        return user.getMutedChannels();
    }

    @Override
    public boolean hasChannelMuted(@NotNull Channel channel) {
        return user.hasChannelMuted(channel);
    }

    @Override
    public boolean hasDirectMessagesEnabled() {
        return user.hasDirectMessagesEnabled();
    }

    @Override
    public void setDirectMessagesEnabled(boolean msgEnabled) {
        user.setDirectMessagesEnabled(msgEnabled);
        save();
    }

    @Override
    public boolean isDiscordLinked() {
        return user.isDiscordLinked();
    }

    @Override
    @NotNull
    public Snowflake getDiscordId() {
        return user.getDiscordId();
    }

    @Override
    public void setDiscordId(@NotNull Snowflake snowflake) {
        user.setDiscordId(snowflake);
        save();
    }

    @Override
    public boolean isNew() {
        return user.isNew();
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return user.hasPermission(permission);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        user.sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        user.sendMessage(message);
    }

}
