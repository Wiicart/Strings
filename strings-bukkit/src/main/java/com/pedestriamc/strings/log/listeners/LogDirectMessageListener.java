package com.pedestriamc.strings.log.listeners;

import com.pedestriamc.strings.api.event.PlayerDirectMessageEvent;
import com.pedestriamc.strings.log.LogManager;
import com.pedestriamc.strings.log.LogType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Date;

public class LogDirectMessageListener implements Listener {

    private final LogManager logManager;

    public LogDirectMessageListener(LogManager logManager) {
        this.logManager = logManager;
    }

    @EventHandler
    public void onEvent(PlayerDirectMessageEvent event) {
        String log = "["
                + new Date() + "] Player "
                + event.getSender()
                + " sent a message to player "
                + event.getRecipient()
                + ": \""
                + event.getMessage()
                + "\"";

        logManager.log(LogType.DIRECT_MESSAGE, log);
    }
}
