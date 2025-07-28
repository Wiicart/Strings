package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.channel.base.ProtectedChannel;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("all")
public class SystemChannel extends ProtectedChannel {

    private final UserUtil userUtil;

    protected SystemChannel(@NotNull Strings strings, @NotNull String name) {
        super(name);
        userUtil = strings.users();
    }

    public void announce(@NotNull Component component) {

    }

    @Override
    public Set<StringsUser> getRecipients(@NotNull StringsUser player) {
        return Set.of();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "system";
    }
}
