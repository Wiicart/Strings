package com.pedestriamc.strings.listener.player;

import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.external.ModrinthService;
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

    private final boolean modifyJoinMessage;
    private final boolean doMotd;
    private final boolean doJoinMessage;

    public PlayerJoinListener(@NotNull Strings strings) {
        this.strings = strings;
        userUtil = strings.users();
        serverMessages = strings.getServerMessages();
        modrinth = strings.modrinth();

        Configuration configuration = strings.getConfiguration();
        modifyJoinMessage = configuration.get(Option.Bool.USE_CUSTOM_JOIN_LEAVE);
        doMotd = configuration.get(Option.Bool.ENABLE_MOTD);
        doJoinMessage = configuration.get(Option.Bool.ENABLE_JOIN_LEAVE_MESSAGE);
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onEvent(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        userUtil.loadUserAsync(player.getUniqueId());

        if (!doJoinMessage) {
            event.setJoinMessage(null);
        } else if(modifyJoinMessage) {
            event.setJoinMessage(serverMessages.joinMessage(player));
        }

        applyPackIfEnabled(player);

        if (doMotd) {
            serverMessages.sendMOTD(player);
        }
    }

    private void applyPackIfEnabled(@NotNull Player player) {
        if (modrinth == null) {
            return;
        }

        modrinth.getPack().thenAccept(pack ->
                strings.sync(() -> player.setResourcePack(
                        pack.url(),
                        pack.hash(),
                        "Please enable the Emoji resource pack"
                ))
        );
    }
}
