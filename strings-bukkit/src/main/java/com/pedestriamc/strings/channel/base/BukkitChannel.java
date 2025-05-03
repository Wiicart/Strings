package com.pedestriamc.strings.channel.base;

import com.pedestriamc.strings.api.channel.Channel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface BukkitChannel extends Channel {
    void sendMessage(@NotNull Player player, @NotNull String message);
}
