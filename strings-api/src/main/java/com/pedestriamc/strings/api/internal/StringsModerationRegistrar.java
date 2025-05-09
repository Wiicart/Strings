package com.pedestriamc.strings.api.internal;

import com.pedestriamc.strings.api.StringsModeration;
import org.jetbrains.annotations.NotNull;

import static org.jetbrains.annotations.ApiStatus.*;

import java.util.UUID;

@Internal
public final class StringsModerationRegistrar {

    private static UUID uuid;
    private static StringsModeration moderation;

    private StringsModerationRegistrar() {}

    @Internal
    public static StringsModeration get() {
        return moderation;
    }

    @Internal
    static void register(StringsModeration moderation, UUID uuid) {
        StringsModerationRegistrar.moderation = moderation;
        StringsModerationRegistrar.uuid = uuid;


    }
    @Internal
    static void unregister(@NotNull UUID uuid) {
        if(!uuid.equals(StringsModerationRegistrar.uuid)) {
            throw new SecurityException("Invalid UUID used to attempt unregistering StringsModeration.");
        }
        StringsModerationRegistrar.moderation = null;
    }

}
