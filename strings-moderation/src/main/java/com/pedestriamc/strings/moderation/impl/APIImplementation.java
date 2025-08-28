package com.pedestriamc.strings.moderation.impl;

import com.pedestriamc.strings.api.moderation.ModerationSettings;
import com.pedestriamc.strings.api.moderation.StringsModeration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class APIImplementation implements StringsModeration {

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

    @Override
    public @NotNull ModerationSettings getSettings() {
        return moderation.getConfiguration();
    }
}
