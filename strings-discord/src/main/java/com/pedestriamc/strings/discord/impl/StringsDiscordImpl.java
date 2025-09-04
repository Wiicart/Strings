package com.pedestriamc.strings.discord.impl;

import com.pedestriamc.strings.api.discord.DiscordSettings;
import com.pedestriamc.strings.api.discord.StringsDiscord;
import net.kyori.adventure.chat.SignedMessage;
import org.jetbrains.annotations.NotNull;

public class StringsDiscordImpl implements StringsDiscord {

    private final com.pedestriamc.strings.discord.StringsDiscord strings;

    public StringsDiscordImpl(@NotNull com.pedestriamc.strings.discord.StringsDiscord strings) {
        this.strings = strings;
    }

    @Override
    @NotNull
    public DiscordSettings getSettings() {
        return strings.getConfiguration();
    }

    @Override
    public void broadcastDiscordMessage(@NotNull String message) {
        strings.getManager().broadcastDiscordMessage(message);
    }

    @Override
    public void deleteMessage(@NotNull SignedMessage message) {
        strings.getManager().deleteMessage(message);
    }
}
