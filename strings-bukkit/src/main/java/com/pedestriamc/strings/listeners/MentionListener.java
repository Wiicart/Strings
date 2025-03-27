package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.chat.Mentioner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class MentionListener implements Listener {

    private final Mentioner mentioner;

    public MentionListener(@NotNull Strings strings) {
        mentioner = strings.getMentioner();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEvent(@NotNull AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        if(!(p.hasPermission("strings.*") || p.hasPermission("strings.mention") || p.hasPermission("strings.mention.all"))) {
            return;
        }

        for(Player subj : Bukkit.getOnlinePlayers()) {
            if(event.getMessage().contains("@" + subj.getName())) {
                mentioner.mention(subj, p);
            }
        }

        if(p.hasPermission("strings.mention.all") && event.getMessage().contains("@everyone")) {
            for(Player subj : Bukkit.getOnlinePlayers()) {
                mentioner.mention(subj, p);
            }
        }
    }
}