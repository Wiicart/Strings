package com.pedestriamc.strings.bukkit;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.text.StringsAudienceProvider;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public class AudienceGetter implements StringsAudienceProvider {

    private final Strings strings;

    private final boolean isPaper;

    public AudienceGetter(@NotNull Strings strings) {
        this.strings = strings;
        isPaper = strings.isPaper();
    }

    @Override
    public @NotNull Audience all() {
        if (isPaper) {
            Audience players = Audience.audience(strings.users()
                    .getUsers()
                    .stream()
                    .map(StringsUser::audience)
                    .toArray(Audience[]::new)
            );

            return Audience.audience(players, console());
        } else {
            return strings.adventure().all();
        }
    }

    @Override
    public @NotNull Audience console() {
        if (isPaper) {
            return (Audience) strings.getServer().getConsoleSender();
        } else {
            return strings.adventure().console();
        }
    }
}
