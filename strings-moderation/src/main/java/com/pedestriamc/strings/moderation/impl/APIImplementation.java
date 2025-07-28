package com.pedestriamc.strings.moderation.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class APIImplementation implements com.pedestriamc.strings.api.StringsModeration {

    private final com.pedestriamc.strings.moderation.StringsModeration moderation;

    public APIImplementation(com.pedestriamc.strings.moderation.StringsModeration moderation) {
        this.moderation = moderation;
    }

    @Override
    public boolean isOnCooldown(@NotNull Player player) {
        return moderation.getCooldownManager().isOnCooldown(player);
    }

    @Override
    public void startCooldown(@NotNull Player player) {
        moderation.getCooldownManager().startCooldown(player);
    }

    @Override
    public boolean isRepeating(@NotNull Player player, @NotNull String message) {
        return moderation.getRepetitionManager().isRepeating(player, message);
    }

}
