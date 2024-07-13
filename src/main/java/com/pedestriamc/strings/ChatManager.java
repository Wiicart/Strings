package com.pedestriamc.strings;

import com.pedestriamc.strings.channels.Channel;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ChatManager {
    private final Strings strings;
    private final boolean usePAPI;
    private final boolean messagePlaceholders;
    private final boolean parseChatColors;
    private final boolean doCoolDown;
    private final Set<Player> coolDownList = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private BukkitScheduler scheduler;
    private long coolDownLength;

    public ChatManager(@NotNull Strings strings){
        this.strings = strings;
        this.usePAPI = strings.usePlaceholderAPI();
        this.parseChatColors = strings.processMessageColors();
        this.messagePlaceholders = strings.processMessagePlaceholders();
        this.doCoolDown = strings.isDoCoolDown();
        if(doCoolDown){
            this.scheduler = Bukkit.getScheduler();
            this.coolDownLength = calcTicks(strings.getCoolDownLength());

        }
    }

    public @NotNull String formatMessage(Player sender, Channel channel){
        Bukkit.getLogger().info("message format:" + channel.getFormat());
        String newMessageFormat = channel.getFormat();
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
        Bukkit.getLogger().info(message);
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
        if(doCoolDown){
            return coolDownList.contains(player);
        }
        return false;
    }

    public void startCoolDown(Player player){
        if(doCoolDown){
            coolDownList.add(player);
            scheduler.runTaskLater(strings,() -> coolDownList.remove(player), coolDownLength);
        }

    }

    public long calcTicks(String string){
        String regex = "^[0-9]+[sm]$";
        if(string == null || !string.matches(regex)){
            Bukkit.getLogger().info("[Strings] Invalid chat cool down in config.  Defaulting to 30s.");
            return 600L;
        }
        //Get units
        char units = string.charAt(string.length() - 1);
        //Get number as int
        string = string.substring(0, string.length() - 1);
        int delayNum = Integer.parseInt(string);
        //Convert into seconds if in minutes
        if(units == 'm'){
            delayNum *= 60;
        }
        // One tick = 0.05 seconds
        return delayNum * 20L;
    }
}
