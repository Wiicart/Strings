package com.pedestriamc.common.channel.impl;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.common.channel.base.ProtectedChannel;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@SuppressWarnings("all")
public class SystemChannel extends ProtectedChannel {

    protected SystemChannel(@NotNull StringsPlatform strings, @NotNull String name) {
        super(name);
    }

    public void announce(@NotNull Component component) {

    }

    @Override
    public Set<StringsUser> getRecipients(@NotNull StringsUser player) {
        return Set.of();
    }

}
