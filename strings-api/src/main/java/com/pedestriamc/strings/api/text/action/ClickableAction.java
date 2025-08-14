package com.pedestriamc.strings.api.text.action;

import org.bukkit.entity.Player;

import static org.jetbrains.annotations.ApiStatus.Experimental;

@SuppressWarnings("unused")
@Experimental
public interface ClickableAction {

    String getName();

    void onClick(Player clicker, Player sender, String message);

    Type getType();



    enum Type {
        SENDER,
        RECIPIENT,
        BOTH
    }

}
