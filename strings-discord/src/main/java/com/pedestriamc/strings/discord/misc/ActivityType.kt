package com.pedestriamc.strings.discord.misc

import net.dv8tion.jda.api.entities.Activity

enum class ActivityType(private val activity: java.util.function.Function<String, Activity>) {
    PLAYING(Activity::playing),
    LISTENING(Activity::listening),
    WATCHING(Activity::watching),
    COMPETING(Activity::competing),
    CUSTOM(Activity::customStatus);

    fun apply(string: String) : Activity {
        return activity.apply(string)
    }

    companion object {
        @JvmStatic
        fun of(string: String) : ActivityType {
            return when (string.lowercase()) {
                "playing" -> PLAYING
                "listening" -> LISTENING
                "watching" -> WATCHING
                "competing" -> COMPETING
                else -> CUSTOM
            }
        }
    }
}