package com.pedestriamc.strings.moderation.listener;

import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import com.pedestriamc.strings.api.event.moderation.PlayerChatFilteredEvent;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.utlity.Permissions;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ChatListener implements Listener {

    private final StringsModeration stringsModeration;
    private final RepetitionManager repetitionManager;
    private final CooldownManager cooldownManager;
    private final ChatFilter chatFilter;
    private final Map<String, String> cooldownPlaceholders;
    private final LinkFilter linkFilter;

    public ChatListener(@NotNull StringsModeration stringsModeration) {
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
                channel.isCooldownEnabled()
                && !player.hasPermission("strings.chat.bypasscooldown") &&
                noPermOrAdmin(player, "strings.chat.bypasscooldown")
                && cooldownManager.isOnCooldown(player)
        ) {
                event.setCancelled(true);
                messenger.sendMessage(Message.COOLDOWN, cooldownPlaceholders, player);
                return;
        }


        if(noPermOrAdmin(player, "strings.chat.bypassrepetition") && repetitionManager.isRepeating(player, message)) {
            event.setCancelled(true);
            messenger.sendMessage(Message.NO_REPETITION, player);
            return;
        }

        if(noPermOrAdmin(player, "strings.chat.filterbypass")) {
            String original = message;
            if(channel.isUrlFiltering()) {
                message = linkFilter.filter(event.getMessage(), player);
            }

            if(channel.isProfanityFiltering()) {
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

    private boolean noPermOrAdmin(@NotNull Player player, @NotNull  String perm) {
        return !Permissions.anyOfOrAdmin(player, "strings.*", "strings.chat.*", perm);
    }
}
