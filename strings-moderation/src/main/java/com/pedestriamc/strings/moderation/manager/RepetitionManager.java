package com.pedestriamc.strings.moderation.manager;

import com.pedestriamc.strings.moderation.StringsModeration;
import com.pedestriamc.strings.moderation.listener.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RepetitionManager {

    private final StringsModeration stringsModeration;
    private final Map<Player, PreviousMessage> map;

    // Is forbidding repetition enabled
    private boolean enabled;

    //Should spaces be removed when checking for repetition?
    private boolean ignoreSpaces;

    //The cooldown length in ticks for a repeating message to be ignored.
    private long cooldownLength;

    public RepetitionManager(@NotNull StringsModeration stringsModeration) {
        this.stringsModeration = stringsModeration;
        map = new HashMap<>();
        configure(stringsModeration.getConfig());
        stringsModeration.registerListener(new PlayerQuitListener(this));
    }

    private void configure(@NotNull FileConfiguration config) {
        enabled = config.getBoolean("forbid-repetition");
        ignoreSpaces = config.getBoolean("ignore-spaces");

        String cooldownString = config.getString("repetition-cooldown");
        if(cooldownString != null) {
            cooldownLength = StringsModeration.calculateTicks(cooldownString);
        } else {
            cooldownLength = -1L;
        }
    }

    public void setPreviousMessage(Player player, String message) {
        map.computeIfAbsent(player, p -> new PreviousMessage(stringsModeration, p));
        map.get(player).setPrevious(message, cooldownLength);
    }

    public boolean isRepeating(Player player, String message) {
        if(!enabled) {
            return false;
        }

        PreviousMessage prev = map.get(player);
        if(prev == null || prev.getPrevious() == null) {
            return false;
        }

        if(ignoreSpaces) {
            message = message.replace(" ", "");
        }

        return prev.getPrevious().equals(message);
    }

    public void logOut(Player player) {
        map.remove(player);
    }

    @SuppressWarnings("unused")
    private static class PreviousMessage {

        private final StringsModeration stringsModeration;
        private final Player player;

        private String previous;

        public PreviousMessage(StringsModeration stringsModeration, Player player) {
            this.stringsModeration = stringsModeration;
            this.player = player;
        }

        public String getPrevious() {
            return previous;
        }

        public void setPrevious(String previous) {
            this.previous = previous;
        }

        /**
         * Sets the previous message, with a timer (in ticks) until the previous message returns to null.
         * @param previous The previous message
         * @param cooldown The tick length of the timer for cooldown.
         */
        public void setPrevious(@NotNull String previous, long cooldown) {
            String previousMessage = previous.strip().trim();
            this.previous = previousMessage;
            if(cooldown > -1) {
                Bukkit.getScheduler().runTaskLater(stringsModeration, () -> removeIfCurrent(previousMessage), cooldown);
            }
        }

        public Player getPlayer() {
            return player;
        }

        /**
         * Sets previous to null if the previous message remains the same.
         * @param message The previous message
         */
        public void removeIfCurrent(String message) {
            if(getPrevious().equals(message)) {
                previous = null;
            }
        }
    }
}
