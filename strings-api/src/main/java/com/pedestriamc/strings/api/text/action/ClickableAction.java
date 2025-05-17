package com.pedestriamc.strings.api.text.action;

import org.bukkit.entity.Player;

@SuppressWarnings("unused")
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
