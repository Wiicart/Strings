package com.pedestriamc.strings.moderation.listener;

import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.event.PlayerChatFilteredEvent;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.moderation.StringsModeration;
import com.pedestriamc.strings.moderation.manager.ChatFilter;
import com.pedestriamc.strings.moderation.manager.LinkFilter;
import com.pedestriamc.strings.moderation.manager.CooldownManager;
import com.pedestriamc.strings.moderation.manager.RepetitionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

public class ChatListener implements Listener {

    private final StringsModeration stringsModeration;
    private final RepetitionManager repetitionManager;
    private final CooldownManager cooldownManager;
    private final ChatFilter chatFilter;
    private final Map<String, String> cooldownPlaceholders;
    private final LinkFilter linkFilter;

    public ChatListener(StringsModeration stringsModeration) {
        this.stringsModeration = stringsModeration;
        cooldownPlaceholders = new HashMap<>();
        cooldownPlaceholders.put("{cooldown_length}", (String) stringsModeration.getConfig().get("cooldown-time"));
        linkFilter = stringsModeration.getLinkFilter();
        repetitionManager = stringsModeration.getRepetitionManager();
        cooldownManager = stringsModeration.getCooldownManager();
        chatFilter = stringsModeration.getChatFilter();

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        Messenger messenger = StringsProvider.get().getMessenger();
        if(!(asyncPlayerChatEvent instanceof ChannelChatEvent event)) {
            return;
        }

        Player player = event.getPlayer();
        Channel channel = event.getChannel();
        String message = event.getMessage();
        if(
                channel.doCooldown()
                && !player.hasPermission("*")
                && !player.hasPermission("strings.chat.bypasscooldown")
                && cooldownManager.isOnCooldown(player)
                && !player.isOp()
        ) {
                event.setCancelled(true);
                messenger.sendMessage(Message.COOL_DOWN, cooldownPlaceholders, player);
                return;
        }


        if(repetitionManager.isRepeating(player, message)) {
                event.setCancelled(true);
                messenger.sendMessage(Message.NO_REPETITION, player);
                return;
        }


        if(!(player.hasPermission("strings.*") || player.hasPermission("strings.chat.*") || player.hasPermission("*") || player.hasPermission("strings.chat.filterbypass"))) {
            String original = message;
            if(channel.doUrlFilter()) {
                message = linkFilter.filter(event.getMessage(), player);
            }

            if(channel.doProfanityFilter()) {
                ChatFilter.FilteredChat filtered = chatFilter.filter(message);
                if(!filtered.message().equals(message)) {
                    messenger.sendMessage(Message.BANNED_WORD, player);
                    Bukkit.getScheduler().runTask(stringsModeration, () -> {
                        PlayerChatFilteredEvent filterEvent = new PlayerChatFilteredEvent(
                                player,
                                original,
                                filtered.message(),
                                filtered.filteredElements()
                        );
                        Bukkit.getPluginManager().callEvent(filterEvent);
                    });
                    message = filtered.message();
                }
            }
        }

        repetitionManager.setPreviousMessage(player, event.getMessage());
        cooldownManager.startCooldown(player);
        event.setMessage(message);
    }
}
