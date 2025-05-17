package com.pedestriamc.strings.listener.paper;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.chat.paper.ChannelRenderer;
import com.pedestriamc.strings.user.util.UserUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PaperChatListener implements Listener {

    private final Strings strings;
    private final UserUtil userUtil;

    public PaperChatListener(@NotNull Strings strings) {
        this.strings = strings;
        userUtil = strings.getUserUtil();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onEvent(@NotNull AsyncChatEvent event) {
        Player player = event.getPlayer();
        Channel channel = userUtil.getUser(player).getActiveChannel().resolve(player);
        Set<Player> recipients = channel.resolve(player).getRecipients(player);
        event.viewers().clear();
        event.viewers().add(convertToAudience(recipients));
        event.renderer(ChannelRenderer.forChannel(strings, channel));

        callEvent(player, convertToPlainText(event.message()), recipients, channel);
    }

    private void callEvent(Player sender, String message, Set<Player> players, Channel channel) {
        Bukkit.getScheduler().runTask(strings, () -> Bukkit.getPluginManager()
                .callEvent(new ChannelChatEvent(false, sender, message, players, channel, false)));
    }

    /**
     * Converts message recipients to an Audience.
     * @param set The Set of recipients
     * @return A new Audience.
     */
    private @NotNull Audience convertToAudience(Set<Player> set) {
        return Audience.audience(set.stream()
                .map(p -> (Audience) p)
                .toList());
    }

    private String convertToPlainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

}
