package com.pedestriamc.strings.listener.player;

import com.pedestriamc.strings.api.event.strings.EventManager;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.manager.Configuration;
import com.pedestriamc.strings.common.external.ModrinthService;
import com.pedestriamc.strings.user.util.UserUtil;
import com.pedestriamc.strings.misc.ServerMessages;
import com.pedestriamc.strings.Strings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener implements Listener {

    private final Strings strings;

    private final UserUtil userUtil;
    private final ServerMessages serverMessages;
    private final ModrinthService modrinth;
    private final EventManager eventManager;

    private final boolean modifyJoinMessage;
    private final boolean doMotd;
    private final boolean doJoinMessage;
    private final boolean doFirstJoinMessage;

    public PlayerJoinListener(@NotNull Strings strings) {
        this.strings = strings;
        userUtil = strings.users();
        serverMessages = strings.getServerMessages();
        modrinth = strings.modrinth();
        eventManager = strings.eventManager();

        Configuration configuration = strings.settings();
        modifyJoinMessage = configuration.get(Option.Bool.USE_CUSTOM_JOIN_LEAVE);
        doMotd = configuration.get(Option.Bool.ENABLE_MOTD);
        doJoinMessage = configuration.get(Option.Bool.ENABLE_JOIN_LEAVE_MESSAGE);
        doFirstJoinMessage = configuration.get(Option.Bool.ENABLE_FIRST_JOIN_MESSAGE);
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onEvent(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        applyPackIfEnabled(player);
        userUtil.loadUserAsync(player.getUniqueId()).thenAccept(user -> {
            eventManager.dispatch(new com.pedestriamc.strings.api.event.server.PlayerJoinEvent(user));

            // Template and PlaceholderAPI evaluation can read Player state, so
            // keep it on this player's entity thread. Only the server-wide
            // broadcast itself is handed to the global scheduler.
            if (doFirstJoinMessage && user.isNew()) {
                String message = serverMessages.firstJoinMessage(user);
                strings.sync(() -> strings.getServer().broadcastMessage(message));
            }

            if (doMotd) {
                serverMessages.sendMOTD(user);
            }

            if (doJoinMessage && modifyJoinMessage) {
                String message = serverMessages.joinMessage(user);
                strings.sync(() -> strings.getServer().broadcastMessage(message));
            }
        });

        if (!doJoinMessage || modifyJoinMessage) {
            event.setJoinMessage(null);
        }
    }

    private void applyPackIfEnabled(@NotNull Player player) {
        if (modrinth == null) {
            return;
        }

        try {
            modrinth.getPack().thenAccept(pack ->
                    strings.forEntity(strings, player, () -> player.setResourcePack(
                            pack.url(),
                            pack.hash(),
                            "Please enable the Emoji resource pack"
                    ))
            );
        } catch(Exception ignored) {}
    }
}
