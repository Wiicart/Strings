package com.pedestriamc.strings.moderation.mute;

import com.pedestriamc.strings.moderation.StringsModeration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class MuteManager {

    private static final Pattern TIME_FORMAT = Pattern.compile("^\\d+(min|[hdmy])$");

    private final StringsModeration plugin;
    private final Set<Mute> mutes;

    public MuteManager(StringsModeration plugin) {
        this.plugin = plugin;
        mutes = new HashSet<>();
        schedule();
    }

    /**
     * Mutes a Player.
     * Overrides previous mutes.
     * If time is null or "", the ban is indefinite.
     * @param player The Player to be muted
     * @param time The duration of the mute
     * @param reason The reason for the mute
     */
    public void mute(Player player, String time, String reason) {
        unmute(player);
        ZonedDateTime unmuteDate;
        if(time == null || time.isEmpty()) {
            unmuteDate = parse("10000y");
        } else {
            unmuteDate = parse(time.trim());
        }

        mutes.add(new Mute(player, unmuteDate, reason));
    }

    public void unmute(Player player) {
        mutes.removeIf(mute -> mute.getPlayer().equals(player));
    }

    public Set<Mute> getMutes() {
        return new HashSet<>(mutes);
    }

    private void write() {
        //
    }

    private void read() {
        //
    }


    private void schedule() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::checkMutes, 0L, 1200L);
    }

    private void checkMutes() {
        ZonedDateTime now = ZonedDateTime.now();
        mutes.removeIf(mute -> now.isAfter(mute.getDate()));
    }

    /**
     * Returns a ZonedDateTime when the amount of time prescribed in arg time will be reached.
     * Format: <number><units>
     * Number will be parsed as a long.
     * Units: min(minutes), h(hours), d(days), m(months), y(years).
     * Example: 1min -> 1 minute
     * @param time The text to parse
     * @return A ZonedDateTime
     */
    @Nullable
    public ZonedDateTime parse(@NotNull String time) {
        if(!TIME_FORMAT.matcher(time).matches()) {
            return null;
        }

        long num = Long.parseLong(time.substring(0, time.length() - 1));
        String end = String.valueOf(time.charAt(time.length() - 1));
        switch(end) {
            case "min" -> {
                return ZonedDateTime.now().plusMinutes(num);
            }
            case "h" -> {
                return ZonedDateTime.now().plusHours(num);
            }
            case "d" -> {
                return ZonedDateTime.now().plusDays(num);
            }
            case "m" -> {
                return ZonedDateTime.now().plusMonths(num);
            }
            case "y" -> {
                return ZonedDateTime.now().plusYears(num);
            }
            default -> {
                return null;
            }
        }
    }
}
