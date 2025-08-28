package com.pedestriamc.strings.api.moderation;

import com.pedestriamc.strings.api.settings.Key;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Hosts enums for {@link ModerationSettings} keys
 */
public final class Option {

    private Option() {}

    @Internal
    public interface ModerationKey<T> extends Key<T> {}

    public enum Text implements ModerationKey<String> {
        COOLDOWN_DURATION("cooldown-time", "1m");

        private final String key;
        private final String defaultValue;

        Text(@NotNull String key, @NotNull String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        @Override
        public @NotNull String key() {
            return key;
        }

        @Override
        public @NotNull String defaultValue() {
            return defaultValue;
        }
    }

    public enum Bool implements ModerationKey<Boolean> {
        FORBID_REPETITION("forbid-repetition", true),
        IGNORE_SPACES_FOR_REPETITION("ignore-spaces", true),
        FILTER_SIGN_TEXT("filter-signs", false);

        private final String key;
        private final boolean defaultValue;

        Bool(@NotNull String key, boolean defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        @Override
        public @NotNull String key() {
            return key;
        }

        @Override
        public @NotNull Boolean defaultValue() {
            return defaultValue;
        }
    }

    public enum Double implements ModerationKey<java.lang.Double> {
        ;

        private final String key;
        private final double defaultValue;

        Double(@NotNull String key, double defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        @Override
        public @NotNull String key() {
            return key;
        }

        @Override
        public java.lang.@NotNull Double defaultValue() {
            return defaultValue;
        }
    }

    public enum Int implements ModerationKey<Integer> {
        REPETITION_COOLDOWN("repetition-cooldown", -1);

        private final String key;
        private final int defaultValue;

        Int(@NotNull String key, int defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        @Override
        public @NotNull String key() {
            return key;
        }

        @Override
        public @NotNull Integer defaultValue() {
            return defaultValue;
        }
    }

    public enum StringList implements ModerationKey<List<String>> {
        URL_WHITELIST("url-whitelist", List.of("minecraft.net", "apple.com", "wiicart.net")),
        BANNED_WORDS("banned-words", List.of("%mzungu@yupo$3sfe"));

        private final String key;
        private final List<String> defaultValue;

        StringList(@NotNull String key, @NotNull List<String> defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        @Override
        public @NotNull String key() {
            return key;
        }

        @Override
        public @NotNull List<String> defaultValue() {
            return defaultValue;
        }
    }


}
