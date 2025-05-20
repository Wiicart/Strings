package com.pedestriamc.strings.listener.mention;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.chat.Mentioner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Standard MentionListener used when LuckPerms is unavailable.
 */
public class MentionListener extends AbstractMentionListener {

    public MentionListener(@NotNull Strings strings) {
        super(strings);
    }

    @EventHandler(priority = EventPriority.LOW)
    void onEvent(@NotNull AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        String message = event.getMessage();
        if(!Mentioner.hasMentionPermission(p)) {
            return;
        }

        for(Player subj : Bukkit.getOnlinePlayers()) {
            if(message.contains("@" + subj.getName())) {
                getMentioner().mention(subj, p);
            }
        }

        if(canMentionAll(p) && message.contains("@everyone")) {
            for(Player subj : Bukkit.getOnlinePlayers()) {
                getMentioner().mention(subj, p);
            }
        }
    }
}