package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.ChatManager;
import com.pedestriamc.strings.Strings;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class HelpOPChannel extends Channel{

    private Strings strings;
    private final ChatManager chatManager;
    private final boolean callEvent;

    public HelpOPChannel(@NotNull Strings strings, String name, String format, String defaultColor, ChannelManager channelManager, boolean callEvent) {
        super(strings, name, format, defaultColor, channelManager, callEvent);
        this.strings = strings;
        this.chatManager = strings.getChatManager();
        this.callEvent = callEvent;
    }

    @Override
    public void sendMessage(Player player, String message){
        Set<Player> members = new HashSet<>(super.getMembers());
        for(OfflinePlayer op : Bukkit.getOperators()){
            Player p = Bukkit.getPlayer(op.getName());
            if(p != null){
                members.add(p);
            }
        }
        String format = chatManager.formatMessage(player, this);
        message = chatManager.processMessage(player, message);
        String finalMessage = message;
        if(callEvent){
            Bukkit.getScheduler().runTask(strings, () ->{
                AsyncPlayerChatEvent event = new ChannelChatEvent(false, player, finalMessage, members, this);
                event.setFormat(format);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()){
                    String formattedMessage = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
                    for(Player p : members){
                        p.sendMessage(formattedMessage);
                    }
                    Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
                }
            });
        }else{
            String formattedMessage = String.format(format, player.getDisplayName(), message);
            for(Player p : members){
                p.sendMessage(formattedMessage);
            }
            Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
        }

    }
}
