package com.pedestriamc.strings.channel.base;

import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.text.format.StringsTextColor;
import com.pedestriamc.strings.api.exception.ChannelUnsupportedOperationException;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Type;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Abstract class for ProtectedChannels
 */
public abstract class ProtectedChannel implements Channel {

    private static final String UNIMPLEMENTED = "ProtectedChannel unimplemented operation.";
    private String name;

    protected ProtectedChannel(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Override
    public @NotNull Type getType() {
        return Type.PROTECTED;
    }

    @Override
    public @NotNull Membership getMembership() {
        return Membership.PROTECTED;
    }

    @Override
    public Set<Player> getRecipients(@NotNull Player sender) {
        return Collections.emptySet();
    }

    @Override
    public Map<String, Object> getData() {
        return Collections.emptyMap();
    }

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public boolean allows(@NotNull Permissible permissible) {
        return false;
    }

    @Override
    public Channel resolve(@NotNull Player player) {
        return this;
    }

    // Unimplemented methods from this point on.
    @Override
    public void sendMessage(@NotNull Player player, @NotNull String message) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public void broadcast(String message) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public @NotNull String getFormat() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public String getBroadcastFormat() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public void setFormat(@NotNull String format) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public StringsTextColor getDefaultColor() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public void setDefaultColor(StringsTextColor defaultColor) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public boolean isUrlFiltering() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public void setUrlFilter(boolean doUrlFilter) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public boolean isProfanityFiltering() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public void setProfanityFilter(boolean doProfanityFilter) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public boolean isCooldownEnabled() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public void setDoCooldown(boolean doCooldown) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public void addMember(@NotNull Player player) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public void addMember(@NotNull StringsUser user) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public void removeMember(@NotNull Player player) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public void removeMember(@NotNull StringsUser user) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public Set<Player> getMembers() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

    @Override
    public boolean isCallEvent() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED, this);
    }

}
