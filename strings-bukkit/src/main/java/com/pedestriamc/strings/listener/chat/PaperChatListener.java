package com.pedestriamc.strings.listener.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.annotation.Platform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.strings.EventDispatcher;
import com.pedestriamc.strings.api.platform.EventFactory;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.chat.paper.RendererProvider;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

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
    private final EventDispatcher eventDispatcher;
    private final EventFactory factory;

    public PaperChatListener(@NotNull Strings strings) {
        super(strings);
        this.strings = strings;
        provider = new RendererProvider(strings);
        userUtil = strings.users();
        eventDispatcher = strings.getEventDispatcher();
        factory = strings.getEventFactory();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onEvent(@NotNull AsyncChatEvent event) {
        Player player = event.getPlayer();
        User sender = userUtil.getUser(player);

        Container container = processSymbol(ComponentConverter.toString(event.message()), sender);
        Channel channel = container.channel();
        event.message(ComponentConverter.fromString(container.message()));

        // Resolve Channel if DefaultChannel is returned
        channel = channel.resolve(sender);

        // Let the Channel process the message independently if the Channel does not allow Events being called.
        if (!channel.callsEvents()) {
            channel.sendMessage(sender, ComponentConverter.toString(event.message()));
            event.setCancelled(true);
            return;
        }

        event.viewers().clear();

        Set<StringsUser> recipients = channel.getRecipients(sender);
        for (StringsUser recipient : recipients) {
            event.viewers().add((Audience) User.playerOf(recipient));
        }

        event.viewers().add((Audience) strings.getServer().getConsoleSender());

        event.renderer(provider.createRenderer(channel, event.signedMessage()));

        callEvent(sender, container.message(), recipients, channel, event.signedMessage());
    }

    // Calls a non-cancellable ChannelChatEvent
    private void callEvent(StringsUser sender, String message, Set<StringsUser> players, Channel channel, SignedMessage signedMessage) {
        eventDispatcher.dispatch(
                factory.chatEvent(
                        false,
                        false,
                        sender,
                        message,
                        players,
                        channel,
                        signedMessage
                )
        );
    }

}
