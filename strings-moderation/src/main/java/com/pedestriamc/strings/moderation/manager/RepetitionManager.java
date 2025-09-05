package com.pedestriamc.strings.moderation.manager;

import com.pedestriamc.strings.api.moderation.Option;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.moderation.configuration.Configuration;
import com.pedestriamc.strings.moderation.StringsModeration;
import com.pedestriamc.strings.moderation.listener.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RepetitionManager {

    private final StringsModeration stringsModeration;
    private final Map<StringsUser, PreviousMessage> map;

    // Is forbidding repetition enabled
    private boolean enabled;

    //Should spaces be removed when checking for repetition?
    private boolean ignoreSpaces;

    //The cooldown length in ticks for a repeating message to be ignored.
    private long cooldownLength;

    public RepetitionManager(@NotNull StringsModeration strings) {
        this.stringsModeration = strings;
        map = new HashMap<>();
        loadOptions(strings.getConfiguration());
        strings.registerListener(new PlayerQuitListener(this));
    }

    private void loadOptions(@NotNull Configuration config) {
        enabled = config.get(Option.Bool.FORBID_REPETITION);
        ignoreSpaces = config.get(Option.Bool.IGNORE_SPACES_FOR_REPETITION);

        String cooldownString = config.get(Option.Text.COOLDOWN_DURATION);
        cooldownLength = StringsModeration.calculateTicks(cooldownString);
    }

    public void setPreviousMessage(StringsUser user, String message) {
        map.computeIfAbsent(user, p -> new PreviousMessage(stringsModeration, p));
        map.get(user).setPrevious(message, cooldownLength);
    }

    public boolean isRepeating(StringsUser user, String message) {
        if(!enabled) {
            return false;
        }

        PreviousMessage prev = map.get(user);
        if(prev == null || prev.getPrevious() == null) {
            return false;
        }

        if(ignoreSpaces) {
            message = message.replace(" ", "");
        }

        return prev.getPrevious().equals(message);
    }

    public void logOut(StringsUser user) {
        map.remove(user);
    }

    @SuppressWarnings("unused")
    private static class PreviousMessage {

        private final StringsModeration stringsModeration;
        private final StringsUser user;

        private String previous;

        public PreviousMessage(StringsModeration stringsModeration, StringsUser user) {
            this.stringsModeration = stringsModeration;
            this.user = user;
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

        public StringsUser getUser() {
            return user;
        }

        /**
         * Sets previous to null if the previous message remains the same.
         * @param message The previous message
         */
        public void removeIfCurrent(String message) {
            String prev = getPrevious();
            if(prev != null && prev.equals(message)) {
                previous = null;
            }
        }
    }
}
