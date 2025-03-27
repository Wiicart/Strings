package com.pedestriamc.strings.moderation.mute;

import org.bukkit.entity.Player;

import java.time.ZonedDateTime;

public class Mute {

    private final Player player;
    private ZonedDateTime date;
    private String reason;

    public Mute(Player player, ZonedDateTime date, String reason) {
        this.player = player;
        this.date = date;
        this.reason = reason;
    }

    public Player getPlayer() {
        return player;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
