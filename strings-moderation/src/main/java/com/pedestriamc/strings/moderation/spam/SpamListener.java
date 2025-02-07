package com.pedestriamc.strings.moderation.spam;

import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.moderation.StringsModeration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

public class SpamListener implements Listener {

    private final SpamManager spamManager;
    private final boolean blockDuplicates;
    private final boolean doCooldown;
    private final Messenger messenger;
    private final Map<String, String> cooldownPlaceholders;

    public SpamListener(StringsModeration stringsModeration, SpamManager spamManager, boolean blockDuplicates, boolean doCooldown) {
        this.spamManager = spamManager;
        this.blockDuplicates = blockDuplicates;
        this.doCooldown = doCooldown;
        messenger = stringsModeration.getStringsAPI().getMessenger();

        cooldownPlaceholders = new HashMap<>();
        cooldownPlaceholders.put("{cooldown_length}", (String) stringsModeration.getConfig().get("cooldown-time"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(AsyncPlayerChatEvent asyncPlayerChatEvent) {

        if(!(asyncPlayerChatEvent instanceof ChannelChatEvent event)) {
            return;
        }

        Player player = event.getPlayer();

        if(doCooldown && event.getChannel().doCooldown()) {
            if(spamManager.isOnCooldown(player)) {
                event.setCancelled(true);
                messenger.sendMessage(Message.COOL_DOWN, cooldownPlaceholders, player);
                return;
            }
        }

        if(blockDuplicates) {
            if(spamManager.isRepeating(player, event.getMessage())) {
                event.setCancelled(true);
                messenger.sendMessage(Message.NO_REPETITION, player);
                return;
            }
        }

        spamManager.setPreviousMessage(player, event.getMessage());
        spamManager.startCooldown(player);


    }
}
