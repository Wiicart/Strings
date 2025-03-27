package com.pedestriamc.strings.api.chat.action;

import org.bukkit.entity.Player;

public interface ClickableAction {

    String getName();

    void onClick(Player clicker, Player sender, String message);
}
