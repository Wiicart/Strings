package com.pedestriamc.strings.moderation.chat;

import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.event.PlayerChatFilteredEvent;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.moderation.StringsModeration;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatListener implements Listener {

    private final StringsModeration stringsModeration;
    private final ChatModerationManager chatModerationManager;
    private final boolean blockDuplicates;
    private final Map<String, String> cooldownPlaceholders;
    private final Set<String> bannedWords;
    private final LinkFilter linkFilter;

    public ChatListener(StringsModeration stringsModeration, ChatModerationManager chatModerationManager, boolean blockDuplicates, Set<String> bannedWords) {
        this.stringsModeration = stringsModeration;
        this.chatModerationManager = chatModerationManager;
        this.blockDuplicates = blockDuplicates;
        this.bannedWords = bannedWords;
        cooldownPlaceholders = new HashMap<>();
        cooldownPlaceholders.put("{cooldown_length}", (String) stringsModeration.getConfig().get("cooldown-time"));
        linkFilter = new LinkFilter(stringsModeration);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(AsyncPlayerChatEvent asyncPlayerChatEvent) {

        Messenger messenger = StringsProvider.get().getMessenger();

        if(!(asyncPlayerChatEvent instanceof ChannelChatEvent event)) {
            return;
        }

        Player player = event.getPlayer();
        Channel channel = event.getChannel();

        if(
                channel.doCooldown()
                && !player.hasPermission("*")
                && !player.hasPermission("strings.chat.bypasscooldown")
                && chatModerationManager.isOnCooldown(player)
                && !player.isOp()
        ) {
                event.setCancelled(true);
                messenger.sendMessage(Message.COOL_DOWN, cooldownPlaceholders, player);
                return;
        }


        if(blockDuplicates && chatModerationManager.isRepeating(player, event.getMessage())) {
                event.setCancelled(true);
                messenger.sendMessage(Message.NO_REPETITION, player);
                return;
        }


        if(!(player.hasPermission("strings.*") || player.hasPermission("strings.chat.*") || player.hasPermission("*") || player.hasPermission("strings.chat.filterbypass"))) {
            if(channel.doUrlFilter()) {
                event.setMessage(linkFilter.filter(event.getMessage(), player));
            }
            if(channel.doProfanityFilter()) {
                wordFilter(stringsModeration, event, messenger);
            }
        }

        chatModerationManager.setPreviousMessage(player, event.getMessage());
        chatModerationManager.startCooldown(player);

    }

    private void wordFilter(StringsModeration stringsModeration, ChannelChatEvent event, Messenger messenger) {
        Player player = event.getPlayer();
        String original = event.getMessage();
        boolean wordReplaced = false;
        List<String> filteredElements = new ArrayList<>();

        String modifiedMessage = original;
        for(String str : bannedWords) {
            if(StringUtils.containsIgnoreCase(modifiedMessage, str)) {
                modifiedMessage = modifiedMessage.replaceAll("(?i)" + str, "");
                wordReplaced = true;
                filteredElements.add(str);
            }
        }

        event.setMessage(modifiedMessage);

        if(wordReplaced) {
            messenger.sendMessage(Message.BANNED_WORD, player);
            String finalMessage = modifiedMessage;
            Bukkit.getScheduler().runTask(stringsModeration, () -> {
                PlayerChatFilteredEvent filterEvent = new PlayerChatFilteredEvent(
                        player,
                        original,
                        finalMessage,
                        filteredElements
                );
                Bukkit.getPluginManager().callEvent(filterEvent);
            });
        }
    }
}
