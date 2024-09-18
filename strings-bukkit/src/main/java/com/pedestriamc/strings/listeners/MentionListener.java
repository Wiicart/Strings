package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.chat.Mentioner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MentionListener implements Listener {

    private final Mentioner mentioner;

    public MentionListener(Strings strings){
        mentioner = strings.getMentioner();
    }

    @EventHandler( priority = EventPriority.LOWEST)
    public void onEvent(AsyncPlayerChatEvent event){
        if(!(event instanceof ChannelChatEvent)){
            return;
        }
        Player p = event.getPlayer();
        if(!(p.hasPermission("strings.*") || p.hasPermission("strings.mention") || p.hasPermission("strings.mention.all"))){
            return;
        }
        for(Player subj : Bukkit.getOnlinePlayers()){
            if(event.getMessage().contains("@" + subj.getName())){
                mentioner.mention(subj, p);
            }
        }
        if(p.hasPermission("strings.mention.all")){
            if(event.getMessage().contains("@everyone")){
                for(Player subj : Bukkit.getOnlinePlayers()){
                    mentioner.mention(subj, p);
                }
            }
        }
    }


}
