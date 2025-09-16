package com.pedestriamc.strings.api.discord;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Snowflake {

    private static final Snowflake EMPTY = new Snowflake(0);

    private final long id;

    /**
     * Makes a Snowflake.
     * @param id A long with 18 digits, representing a Discord ID
     * @throws IllegalArgumentException If the ID cannot represent a valid Discord ID
     */
    @Contract("_ -> new")
    @NotNull
    public static Snowflake of(long id) {
        if (isInvalid(id)) {
            throw new IllegalArgumentException("Invalid Discord ID: " + id);
        }

        return new Snowflake(id);
    }

    /**
     * Makes a Snowflake.
     * If the id is invalid, returns {@link Snowflake#empty()}
     * @param id The id of the snowflake
     * @return A Snowflake
     */
    @NotNull
    public static Snowflake ofOrEmpty(long id) {
        try {
            return of(id);
        } catch(IllegalArgumentException e) {
            return EMPTY;
        }
    }

    public static Snowflake empty() {
        return EMPTY;
    }

    private static boolean isInvalid(long id) {
        String str = String.valueOf(id);
        int length = str.length();
        return !(length >= 18 && length <= 19);
    }

    private Snowflake(long id) {
        this.id = id;
    }

    public long get() {
        return id;
    }

    public boolean isPresent() {
        return id != 0;
    }

}
