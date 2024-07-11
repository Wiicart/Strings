package com.pedestriamc.strings;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class ChatManager {
    private final Strings strings;
    private final String messageFormat;
    private final boolean usePAPI;
    private final boolean messagePlaceholders;
    private final boolean parseChatColors;
    private final boolean doCoolDown;
    private final ArrayList<Player> coolDownList = new ArrayList<>();
    private BukkitScheduler scheduler;

    public ChatManager(@NotNull Strings strings){
        this.strings = strings;
        this.messageFormat = ChatColor.translateAlternateColorCodes('&', strings.getMessageFormat());
        this.usePAPI = strings.usePlaceholderAPI();
        this.parseChatColors = strings.processMessageColors();
        this.messagePlaceholders = strings.processMessagePlaceholders();
        this.doCoolDown = strings.isDoCoolDown();
        if(doCoolDown){
            this.scheduler = Bukkit.getScheduler();
        }
    }

    public @NotNull String formatMessage(Player sender){
        String newMessageFormat = messageFormat;
        User user = strings.getUser(sender.getUniqueId());
        if(usePAPI){
            newMessageFormat = PlaceholderAPI.setPlaceholders(sender,newMessageFormat);
        }
        newMessageFormat = newMessageFormat.replace("{prefix}", user.getPrefix());
        newMessageFormat = newMessageFormat.replace("{suffix}", user.getSuffix());
        newMessageFormat = newMessageFormat.replace("{displayname}", "%s");
        newMessageFormat = newMessageFormat.replace("{message}", "%s");

        return newMessageFormat;
    }
    public String processMessage(Player sender, String message){
        User user = strings.getUser(sender.getUniqueId());
        message = user.getChatColor() + message;
        if(parseChatColors && (sender.hasPermission("strings.*") || sender.hasPermission("strings.chat.*") || sender.hasPermission("strings.chat.colormsg"))){
            message = ChatColor.translateAlternateColorCodes('&',message);
        }
        if(usePAPI && messagePlaceholders && (sender.hasPermission("strings.*") || sender.hasPermission("strings.chat.*") || sender.hasPermission("strings.chat.placeholdermsg"))){
            message = PlaceholderAPI.setPlaceholders(sender,message);
        }
        return message;
    }
    public String chatFilter(Player sender, String message){
        return null;
    }
    public boolean isOnCoolDown(Player player){
        return coolDownList.contains(player);
    }

    public void startCoolDown(Player player){
        coolDownList.add(player);
        scheduler.runTaskLater(strings,() -> coolDownList.remove(player), 20L);

    }
}
