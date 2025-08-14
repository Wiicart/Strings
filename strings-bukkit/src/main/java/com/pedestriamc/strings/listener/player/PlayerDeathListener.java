package com.pedestriamc.strings.listener.player;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.misc.deathmessages.DeathMessageManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDeathListener implements Listener {

    private final DeathMessageManager manager;

    private final boolean enableDeathMessages;
    private final boolean useCustomDeathMessages;

    public PlayerDeathListener(@NotNull Strings strings) {
        Settings settings = strings.getConfiguration();
        enableDeathMessages = settings.getBoolean(Option.Bool.ENABLE_DEATH_MESSAGES);
        useCustomDeathMessages = settings.getBoolean(Option.Bool.USE_CUSTOM_DEATH_MESSAGES);

        if (useCustomDeathMessages) {
            manager = new DeathMessageManager(strings);
        } else {
            manager = null;
        }
    }

    @EventHandler
    void onEvent(@NotNull PlayerDeathEvent event) {
        if (!enableDeathMessages) {
            event.setDeathMessage(null);
        }

        if (!useCustomDeathMessages) {
            return;
        }

        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
        if (damageEvent == null) {
            return;
        }

        event.setDeathMessage(manager.getDeathMessage(event.getEntity(), damageEvent.getCause(), damageEvent));
    }


}
