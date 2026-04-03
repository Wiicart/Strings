package com.pedestriamc.strings.moderation.listener;

import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.event.moderation.PlayerChatFilteredEvent;
import com.pedestriamc.strings.api.event.strings.Listener;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.moderation.Option;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.moderation.StringsModeration;
import com.pedestriamc.strings.moderation.manager.ChatFilter;
import com.pedestriamc.strings.moderation.manager.CooldownManager;
import com.pedestriamc.strings.moderation.manager.LinkFilter;
import com.pedestriamc.strings.moderation.manager.RepetitionManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ChatListener {

    private final StringsModeration strings;
    private final RepetitionManager repetitionManager;
    private final CooldownManager cooldownManager;
    private final ChatFilter chatFilter;
    private final Map<String, String> cooldownPlaceholders;
    private final LinkFilter linkFilter;

    public ChatListener(@NotNull StringsModeration strings) {
        this.strings = strings;
        cooldownPlaceholders = new HashMap<>();
        cooldownPlaceholders.put("{cooldown_length}", strings.getConfiguration().get(Option.Text.COOLDOWN_DURATION));
        linkFilter = strings.getLinkFilter();
        repetitionManager = strings.getRepetitionManager();
        cooldownManager = strings.getCooldownManager();
        chatFilter = strings.getChatFilter();

    }

    @Listener
    public void onEvent(ChannelChatEvent event) {
        Messenger messenger = StringsProvider.get().getMessenger();

        StringsUser player = event.getSender();
        Channel channel = event.getChannel();
        String message = event.getMessage();

        if (
                channel.isCooldownEnabled()
                && !player.hasPermission("strings.chat.bypasscooldown") &&
                noPermOrAdmin(player, "strings.chat.bypasscooldown")
                && cooldownManager.isOnCooldown(player)
        ) {
                event.setCancelled(true);
                messenger.sendMessage(Message.COOLDOWN, player, cooldownPlaceholders);
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
                    strings.synchronous(() -> {
                        PlayerChatFilteredEvent filterEvent = new PlayerChatFilteredEvent(
                                player,
                                original,
                                filtered.message(),
                                filtered.filteredElements()
                        );
                        strings.eventDispatcher().dispatch(filterEvent);
                    });
                    message = filtered.message();
                }
            }
        }

        repetitionManager.setPreviousMessage(player, event.getMessage());
        cooldownManager.startCooldown(player);
        event.setMessage(message);
    }

    private boolean noPermOrAdmin(@NotNull StringsUser player, @NotNull  String perm) {
        return !(player.isOperator() ||
                player.hasPermission("*") ||
                player.hasPermission("strings.*") ||
                player.hasPermission("strings.chat.*") ||
                player.hasPermission(perm)
        );
    }
}
