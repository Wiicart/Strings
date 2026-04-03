package com.pedestriamc.strings.common.channel.impl;

import com.pedestriamc.strings.common.channel.base.AbstractChannel;
import com.pedestriamc.strings.common.util.PermissionChecker;
import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.data.IChannelBuilder;
import com.pedestriamc.strings.api.exception.ChannelUnsupportedOperationException;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.text.format.StringsTextColor;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static com.pedestriamc.strings.api.channel.data.IChannelBuilder.Identifier;

public class HelpOPChannel extends AbstractChannel {

    public static final Identifier IDENTIFIER = Identifier.HELPOP;

    private final Messenger messenger;

    public HelpOPChannel(@NotNull StringsPlatform strings, @NotNull IChannelBuilder<?> builder) {
        super(strings, builder);
        this.messenger = strings.messenger();
    }

    @Override
    public void sendMessage(@NotNull StringsUser user, @NotNull String message) {
        super.sendMessage(user, message);
        messenger.sendMessage(Message.HELPOP_SENT, user);
    }

    @Override
    public Set<StringsUser> getRecipients(@Nullable StringsUser sender) {
        PlatformAdapter adapter = getAdapter();
        Set<StringsUser> recipients = new HashSet<>(adapter.getOperators());

        for (StringsUser user : adapter.getOnlineUsers()) {
            if (PermissionChecker.anyOrOp(user, "strings.*", "strings.helpop.*", "strings.helpop.receive")) {
                recipients.add(user);
            }
        }

        return recipients;
    }

    @Override
    public Set<StringsUser> getPlayersInScope() {
        return getRecipients(null);
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
    public String getDefaultColor() {
        return StringsTextColor.RED.toString();
    }

    @Override
    public boolean allows(@NotNull StringsUser user) {
        return PermissionChecker.anyOrOp(user, "strings.*", "strings.helpop.use");
    }

    @Override
    public void addMonitor(@NotNull StringsUser user) {
        throw new ChannelUnsupportedOperationException("Not supported with HelpOPChannels");
    }

    @Override
    public void removeMember(@NotNull StringsUser user) {
        throw new ChannelUnsupportedOperationException("Not supported with HelpOPChannels");
    }

    @Override
    public void addMember(@NotNull StringsUser user) {
        throw new ChannelUnsupportedOperationException("Not supported with HelpOPChannels");
    }

    @Override
    public void removeMonitor(@NotNull StringsUser user) {
        throw new ChannelUnsupportedOperationException("Not supported with HelpOPChannels");
    }
}
