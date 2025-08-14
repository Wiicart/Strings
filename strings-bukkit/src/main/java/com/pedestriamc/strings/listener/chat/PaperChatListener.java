package com.pedestriamc.strings.listener.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.chat.paper.ChannelChatRenderer;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Chat Listener for Paper servers.
 * Unlike with Spigot servers, the ChatEvent called by the server is not canceled.
 * Does not call {@link Channel#sendMessage(StringsUser, String)}, instead handles the message independently,
 * unless the Channel does not call events.
 */
public class PaperChatListener extends AbstractChatListener {

    private final Map<Channel, ChannelChatRenderer> renderers = new HashMap<>();

    private final Strings strings;
    private final UserUtil userUtil;

    public PaperChatListener(@NotNull Strings strings) {
        super(strings);
        this.strings = strings;
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

        Set<StringsUser> recipients = channel.getRecipients(user);
        event.viewers().clear();
        event.viewers().add(convertToAudience(recipients));

        event.viewers().add((Audience) strings.getServer().getConsoleSender());

        event.renderer(getRenderer(channel));

        callEvent(player, container.message(), convertToPlayers(recipients), channel);
    }

    /**
     * Provides a ChannelChatRenderer associated with a Channel.
     * @param channel The Channel
     * @return A ChannelChatRenderer
     */
    private @NotNull ChannelChatRenderer getRenderer(@NotNull Channel channel) {
        return renderers.computeIfAbsent(channel, k -> new ChannelChatRenderer(strings, k));
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

    /**
     * Converts message recipients to an Audience.
     * @param set The Set of recipients
     * @return A new Audience.
     */
    private @NotNull Audience convertToAudience(@NotNull Set<StringsUser> set) {
        return Audience.audience(set.stream()
                .map(p -> (Audience) User.playerOf(p))
                .toList());
    }

}
