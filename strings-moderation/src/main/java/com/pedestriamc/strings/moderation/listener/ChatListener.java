package com.pedestriamc.strings.moderation.listener;

import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import com.pedestriamc.strings.api.event.moderation.PlayerChatFilteredEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.moderation.api.MessageableSender;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.moderation.Option;
import com.pedestriamc.strings.moderation.StringsModeration;
import com.pedestriamc.strings.moderation.manager.ChatFilter;
import com.pedestriamc.strings.moderation.manager.LinkFilter;
import com.pedestriamc.strings.moderation.manager.CooldownManager;
import com.pedestriamc.strings.moderation.manager.RepetitionManager;
import net.wiicart.commands.permission.Permissions;
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

    // todo work on ChannelChatEvent impl
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        Messenger messenger = StringsProvider.get().getMessenger();
        if(!(asyncPlayerChatEvent instanceof ChannelChatEvent event)) {
            return;
        }

        Player player = event.getPlayer();
        Channel channel = event.getChannel();
        String message = event.getMessage();

        StringsUser user;
        try {
            user = StringsProvider.get().getUser(player.getUniqueId());
        } catch(Exception e) {
            strings.getLogger().warning("Failed to get user for Player: " + player.getName());
            return;
        }


        if(
                channel.isCooldownEnabled()
                && !player.hasPermission("strings.chat.bypasscooldown") &&
                noPermOrAdmin(player, "strings.chat.bypasscooldown")
                && cooldownManager.isOnCooldown(user)
        ) {
                event.setCancelled(true);
                messenger.sendMessage(Message.COOLDOWN, new MessageableSender(player), cooldownPlaceholders);
                return;
        }


        if(noPermOrAdmin(player, "strings.chat.bypassrepetition") && repetitionManager.isRepeating(user, message)) {
            event.setCancelled(true);
            messenger.sendMessage(Message.NO_REPETITION, new MessageableSender(player));
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
                    messenger.sendMessage(Message.BANNED_WORD, new MessageableSender(player));
                    strings.synchronous(() -> {
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

        repetitionManager.setPreviousMessage(user, event.getMessage());
        cooldownManager.startCooldown(user);
        event.setMessage(message);
    }

    private boolean noPermOrAdmin(@NotNull Player player, @NotNull  String perm) {
        return !Permissions.anyOfOrAdmin(player, "strings.*", "strings.chat.*", perm);
    }
}
