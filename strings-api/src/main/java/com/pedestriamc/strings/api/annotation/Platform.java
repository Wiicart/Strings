package com.pedestriamc.strings.api.annotation;

/**
 * Signifies a specific platform a class is designed for
 */
public @interface Platform {

    /**
     * Signifies a class is only intended for Spigot
     */
    @interface Spigot {

    }

    /**
     * Signifies a class is only intended for Paper
     */
    @interface Paper {

    }

    /**
     * Signifies a class is fully platform-agnostic
     */
    @interface Agnostic {

    }

}
