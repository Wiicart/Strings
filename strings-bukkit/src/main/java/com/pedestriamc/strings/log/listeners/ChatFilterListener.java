package com.pedestriamc.strings.log.listeners;

import com.pedestriamc.strings.api.event.PlayerChatFilteredEvent;
import com.pedestriamc.strings.log.LogManager;
import com.pedestriamc.strings.log.LogType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Date;

public class ChatFilterListener implements Listener {

    private final LogManager logManager;

    public ChatFilterListener(LogManager logManager) {
        this.logManager = logManager;
    }

    @EventHandler
    public void onEvent(PlayerChatFilteredEvent event) {

        String log = "["
                + new Date() + "] Player "
                + event.getPlayer().getName()
                + " had a message filtered. Original: \""
                + event.getOriginalMessage()
                + "\", Filtered: \" "
                + event.getFilteredMessage()
                + "\"";

        logManager.log(LogType.FILTER, log);

    }
}
