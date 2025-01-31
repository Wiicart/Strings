package com.pedestriamc.strings.log.listeners;

import com.pedestriamc.strings.log.LogManager;
import com.pedestriamc.strings.log.LogType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Date;

public class CommandListener implements Listener {

    private final LogManager logManager;

    public CommandListener(LogManager logManager) {
        this.logManager = logManager;
    }

    @EventHandler
    public void onEvent(PlayerCommandPreprocessEvent event) {

        String log = "["
                + new Date() + "] Player "
                + event.getPlayer().getName()
                + " issued the following command: \""
                + event.getMessage()
                + "\"";

        logManager.log(LogType.COMMAND, log);
    }
}
