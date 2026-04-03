package com.pedestriamc.strings.common.channel.impl;

import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.text.format.StringsTextColor;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.CommonStrings;
import com.pedestriamc.strings.common.channel.base.ProtectedChannel;
import com.pedestriamc.strings.common.manager.DirectMessageManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class SocialSpyChannel extends ProtectedChannel {

    private String format;

    private final DirectMessageManager manager;
    private final HashSet<StringsUser> spiesList;
    private final PlatformAdapter adapter;

    public SocialSpyChannel(@NotNull CommonStrings strings) {
        super("socialspy");
        format = strings.settings().get(Option.Text.SOCIAL_SPY_FORMAT);
        manager = strings.getDirectMessageManager();
        adapter = strings.getAdapter();
        this.spiesList = new HashSet<>();
    }

    /**
     * Sends msg log to social spies.
     * @param sender The sender of the message
     * @param recipient The recipient of the message
     * @param message The message sent
     */
    public void sendOutMessage(StringsUser sender, StringsUser recipient, String message) {
        broadcast(manager.applyStringsPlaceholders(sender, recipient, format, message));
    }

    @Override
    public void sendMessage(@NotNull StringsUser user, @NotNull String message) {
        message = adapter.translateBukkitColor(message);
        for (StringsUser p : spiesList) {
            p.sendMessage(message);
        }
    }

    @Override
    public void broadcast(@NotNull String message) {
        message = adapter.translateBukkitColor(message);
        broadcastPlain(message);
    }

    @Override
    public void broadcastPlain(@NotNull String message) {
        for (StringsUser p : spiesList) {
            p.sendMessage(message);
        }
    }

    @Override
    public @NotNull String getFormat() {
        return format;
    }

    @Override
    public void setFormat(@NotNull String format) {
        this.format = format;
    }

    @Override
    public String getDefaultColor() {
        return StringsTextColor.WHITE.toString();
    }

    @Override
    public void addMember(@NotNull StringsUser player) {
        spiesList.add(player);
    }

    @Override
    public void removeMember(@NotNull StringsUser player) {
        spiesList.remove(player);
    }

    @Override
    public Set<StringsUser> getMembers() {
        return spiesList;
    }

}