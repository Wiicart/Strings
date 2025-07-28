package com.pedestriamc.strings.channel.base;

import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.exception.ChannelUnsupportedOperationException;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Type;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Abstract class for ProtectedChannels
 */
public abstract class ProtectedChannel implements Channel {

    private static final String UNIMPLEMENTED_MESSAGE = "ProtectedChannel unimplemented operation.";
    private String name;

    protected ProtectedChannel(@NotNull String name) {
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
    public Set<StringsUser> getPlayersInScope() {
        return Set.of();
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
    public Set<StringsUser> getRecipients(@NotNull StringsUser sender) {
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
    public @NotNull Channel resolve(@NotNull StringsUser user) {
        return this;
    }

    // Unimplemented methods from this point on.
    @Override
    public void sendMessage(@NotNull StringsUser user, @NotNull String message) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public void broadcast(@NotNull String message) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public void broadcastPlain(@NotNull String message) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public @NotNull String getFormat() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public @NotNull String getBroadcastFormat() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public void setFormat(@NotNull String format) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public String getDefaultColor() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public void setDefaultColor(String defaultColor) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public boolean isUrlFiltering() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public void setUrlFilter(boolean doUrlFilter) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public boolean isProfanityFiltering() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public void setProfanityFilter(boolean doProfanityFilter) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public boolean isCooldownEnabled() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public void setDoCooldown(boolean doCooldown) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public void addMember(@NotNull StringsUser user) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public void removeMember(@NotNull StringsUser user) {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public Set<StringsUser> getMembers() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public boolean callsEvents() {
        throw new ChannelUnsupportedOperationException(UNIMPLEMENTED_MESSAGE, this);
    }

    @Override
    public int compareTo(@NotNull Channel o) {
        return Integer.compare(o.getPriority(), getPriority());
    }
}
