package com.pedestriamc.strings.moderation.impl;

import com.pedestriamc.strings.api.moderation.ModerationSettings;
import com.pedestriamc.strings.api.moderation.StringsModeration;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

public class StringsModerationImpl implements StringsModeration {

    private final com.pedestriamc.strings.moderation.StringsModeration moderation;

    public StringsModerationImpl(com.pedestriamc.strings.moderation.StringsModeration moderation) {
        this.moderation = moderation;
    }

    @Override
    public boolean isOnCooldown(@NotNull StringsUser user) {
        return moderation.getCooldownManager().isOnCooldown(user);
    }

    @Override
    public void startCooldown(@NotNull StringsUser user) {
        moderation.getCooldownManager().startCooldown(user);
    }

    @Override
    public boolean isRepeating(@NotNull StringsUser user, @NotNull String message) {
        return moderation.getRepetitionManager().isRepeating(user, message);
    }

    @Override
    public @NotNull ModerationSettings getSettings() {
        return moderation.getConfiguration();
    }
}
