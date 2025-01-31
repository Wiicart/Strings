package com.pedestriamc.strings.log.listeners;

import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.log.LogManager;
import com.pedestriamc.strings.log.LogType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Date;

public class LogChatListener implements Listener {

    private final LogManager logManager;

    public LogChatListener(LogManager logManager) {
        this.logManager = logManager;
    }

    @EventHandler
    public void onEvent(AsyncPlayerChatEvent event) {

        if(event instanceof ChannelChatEvent chatEvent) {
            String log = "["
                    + new Date() + "] Player "
                    + event.getPlayer().getName()
                    + " sent a message in channel \""
                    + chatEvent.getChannel().getName()
                    + "\": \"" + chatEvent.getMessage() + "\"";

            logManager.log(LogType.CHAT, log);
        }

    }

}
