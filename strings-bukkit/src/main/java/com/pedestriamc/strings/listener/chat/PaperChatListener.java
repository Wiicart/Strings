package com.pedestriamc.strings.listener.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.annotation.Platform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.chat.paper.RendererProvider;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Chat Listener for Paper servers.
 * Unlike with Spigot servers, the ChatEvent called by the server is not canceled.
 * Does not call {@link Channel#sendMessage(StringsUser, String)}, instead handles the message independently,
 * unless the Channel does not call events.
 */
@Platform.Paper
public class PaperChatListener extends AbstractChatListener {

    private final Strings strings;
    private final RendererProvider provider;
    private final UserUtil userUtil;

    public PaperChatListener(@NotNull Strings strings) {
        super(strings);
        this.strings = strings;
        provider = new RendererProvider(strings);
        userUtil = strings.users();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onEvent(@NotNull AsyncChatEvent event) {
        Player player = event.getPlayer();
        User user = userUtil.getUser(player);

        Container container = processSymbol(ComponentConverter.toString(event.message()), user);
        Channel channel = container.channel();
        event.message(ComponentConverter.fromString(container.message()));

        // Resolve Channel if DefaultChannel is returned
        channel = channel.resolve(user);

        // Let the Channel process the message independently if the Channel does not allow Events being called.
        if (!channel.callsEvents()) {
            channel.sendMessage(user, ComponentConverter.toString(event.message()));
            event.setCancelled(true);
            return;
        }

        event.viewers().clear();

        Set<StringsUser> recipients = channel.getRecipients(user);
        for (StringsUser recipient : recipients) {
            event.viewers().add((Audience) User.playerOf(recipient));
        }

        event.viewers().add((Audience) strings.getServer().getConsoleSender());

        event.renderer(provider.createRenderer(channel, event.signedMessage()));

        callEvent(player, container.message(), convertToPlayers(recipients), channel);
    }

    // Calls a non-cancellable ChannelChatEvent
    private void callEvent(Player sender, String message, Set<Player> players, Channel channel) {
        Bukkit.getScheduler().runTask(strings, () -> strings.getServer().getPluginManager()
                .callEvent(new ChannelChatEvent(false, sender, message, players, channel, false)));
    }

    private @NotNull Set<Player> convertToPlayers(@NotNull Set<StringsUser> users) {
        return users.stream()
                .map(User::playerOf)
                .collect(Collectors.toSet());
    }

}
