package com.pedestriamc.strings.hytale;

import com.hypixel.hytale.server.core.universe.Universe;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class HytalePlatformAdapter implements PlatformAdapter {

    private static final Universe UNIVERSE = Universe.get();

    private final Strings strings;

    public HytalePlatformAdapter(@NotNull Strings strings) {
        this.strings = strings;
    }

    @Override
    public @NotNull @UnmodifiableView Collection<StringsUser> getOnlineUsers() {
        return strings.users().getUsers();
    }

    @Override
    public @NotNull @UnmodifiableView Collection<StringsUser> getOperators() {
        return List.of();
    }

    @Override
    public String colorHex(@NotNull String input) {
        return "";
    }

    @Override
    public String translateBukkitColor(@NotNull String input) {
        return "";
    }

    @Override
    public String stripBukkitColor(@NotNull String input) {
        return "";
    }

    @Override
    public String applyPlaceholders(@NotNull StringsUser source, @NotNull String input) {
        return "";
    }

    @Override
    public String processMentions(@NotNull StringsUser sender, @NotNull Channel channel, @NotNull String str) {
        return "";
    }

    @Override
    public String setPlaceholders(@NotNull StringsUser user, @NotNull String input) {
        return "";
    }

    @Override
    public @Nullable TextColor parseColor(@NotNull String input) {
        return null;
    }

    @Override
    public void removePermission(@NotNull String... permission) {

    }

    @Override
    public void addPermission(@NotNull String... permission) {

    }

    @Override
    public void print(@NotNull String message) {

    }

    @Override
    public boolean isOnline(@NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean isOnline(@NotNull StringsUser user) {
        return false;
    }
}
