package com.pedestriamc.strings.listeners;

import com.pedestriamc.strings.ChatManager;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class ChatListener implements Listener {

    private final ChatManager chatManager = Strings.getInstance().getChatManager();

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Bukkit.getScheduler().runTask(Strings.getInstance(), () -> { //https://www.spigotmc.org/threads/asyncplayerchatevent-may-only-be-triggered-synchronously.611643/
            Player playerSender = event.getPlayer();
            String playerMessage = event.getMessage();

            //this signifies that this event came from a Channel, and has been processed
            if(event.getFormat().contains("" + ChatColor.AQUA + ChatColor.AQUA + ChatColor.RESET)){
                Bukkit.getLogger().info("detected msg already processed");
                return;
            }
            User user = Strings.getInstance().getUser(playerSender);
            if(chatManager.isOnCoolDown(playerSender)){
                event.setCancelled(true);
                Messenger.sendMessage(Message.COOL_DOWN, event.getPlayer());
            }else{
                //cancel event, as it is being replaced by this plugin
                Bukkit.getLogger().info("Attempting to replace msg event");
                user.getActiveChannel().sendMessage(playerSender,playerMessage);
                event.setCancelled(true);
                if(!event.isCancelled()){
                    chatManager.startCoolDown(playerSender);
                }
            }

        });
    }
}
