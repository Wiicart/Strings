package com.pedestriamc.strings.api.command;

import com.pedestriamc.strings.api.message.Messageable;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Intended to be used like Bukkit's CommandSender, but for platform-agnostic abstraction
 */
public interface Source extends Messageable {

    /**
     * Casts this Source to a {@link StringsUser} if it is one,
     * otherwise returns null.
     * @return A <code>StringsUser</code> or null
     */
    @Nullable
    default StringsUser user() {
        return this instanceof StringsUser user ? user : null;
    }

    @NotNull
    String getName();

}
