package com.pedestriamc.strings.api.managers;

import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Mentioner {

    boolean hasPermission(@NotNull StringsUser user);

    void mention(@NotNull StringsUser recipient, @NotNull StringsUser sender);

    void mention(@NotNull Set<StringsUser> recipients, @NotNull StringsUser sender);

    Component processMentions(@NotNull StringsUser sender, @NotNull Component message);

}
