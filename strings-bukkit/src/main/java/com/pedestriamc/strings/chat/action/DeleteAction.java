package com.pedestriamc.strings.chat.action;

import com.pedestriamc.strings.api.text.action.ClickableAction;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class DeleteAction implements ClickableAction {

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void onClick(Player clicker, Player sender, String message) {

    }

    @Override
    public Type getType() {
        return Type.SENDER;
    }

    public String getMessage() {
        return null;
    }
}
