package com.pedestriamc.strings.api.event.moderation;

import com.pedestriamc.strings.api.moderation.StringsModeration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;


import java.util.List;
import java.util.Map;

/**
 * Called when {@link StringsModeration} filters the text on a sign
 */
public final class SignTextFilterEvent extends PlayerEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final SignChangeEvent original;
    private final List<Map.Entry<String, String>> lines;

    @Internal
    public SignTextFilterEvent(@NotNull SignChangeEvent original, List<Map.Entry<String, String>> lines) {
        super(original.getPlayer());
        this.original = original;
        this.lines = lines;
    }

    /**
     * Provides the original {@link SignChangeEvent} that was triggered
     * when the Player started changing the sign's text.
     * @return A pre-existing Event
     */
    @NotNull
    public SignChangeEvent originalEvent() {
        return original;
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

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
