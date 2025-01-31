package com.pedestriamc.strings.log.listeners;

import com.pedestriamc.strings.log.LogManager;
import com.pedestriamc.strings.log.LogType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Arrays;
import java.util.Date;

public class SignListener implements Listener {

    private final LogManager logManager;

    public SignListener(LogManager logManager) {
        this.logManager = logManager;
    }

    @EventHandler
    public void onEvent(SignChangeEvent event) {
        String log = "["
                + new Date() + "] Player "
                + event.getPlayer().getName()
                + " updated or placed a sign with the following content: "
                + Arrays.toString(event.getLines());

        logManager.log(LogType.SIGN, log);
    }

}
