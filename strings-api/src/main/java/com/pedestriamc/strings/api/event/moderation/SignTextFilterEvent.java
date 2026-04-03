package com.pedestriamc.strings.api.event.moderation;

import com.pedestriamc.strings.api.event.strings.StringsEvent;
import com.pedestriamc.strings.api.moderation.StringsModeration;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;


import java.util.List;
import java.util.Map;

/**
 * Called when {@link StringsModeration} filters the text on a sign
 */
public final class SignTextFilterEvent implements StringsEvent {

    private final StringsUser player;
    private final List<Map.Entry<String, String>> lines;

    @Internal
    public SignTextFilterEvent(@NotNull StringsUser player, List<Map.Entry<String, String>> lines) {
        this.player = player;
        this.lines = lines;
    }

    public StringsUser getPlayer() {
        return player;
    }

    /**
     * Provides a List of {@link Map.Entry}, with the key being the original line,
     * and the value being the filtered line.
     * All lines on the sign are included, whether they are filtered or not.
     * @return A List of Entries with String keys and values.
     */
    @NotNull
    public List<Map.Entry<String, String>> lines() {
        return lines;
    }
}
