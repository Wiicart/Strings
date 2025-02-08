package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.api.channels.Channel;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
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
    private final Set<Player> coolDownList = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final BukkitScheduler scheduler;
    private final long coolDownLength;
    private final String mentionColor;
    private final boolean mentionsEnabled;
    private final boolean isPaper;

    public ChatManager(@NotNull Strings strings){
        this.strings = strings;
        this.usePAPI = strings.usePlaceholderAPI();
        this.parseChatColors = strings.processMessageColors();
        this.messagePlaceholders = strings.processMessagePlaceholders();
        this.scheduler = Bukkit.getScheduler();
        long cooldown = Strings.calculateTicks(strings.getCoolDownLength());

        if(cooldown != -1) {
            coolDownLength = cooldown;
        } else {
            coolDownLength = 600L;
            Bukkit.getLogger().info("[Strings] Invalid chat cool down in config.  Defaulting to 30s.");
        }

        FileConfiguration config = strings.getConfig();
        this.mentionColor = ChatColor.translateAlternateColorCodes('&', config.getString("mention-color", "&e"));
        this.mentionsEnabled = strings.getConfig().getBoolean("enable-mentions", true);
        isPaper = strings.isPaper();
    }

    public @NotNull String formatMessage(Player sender, Channel channel) {
        String newMessageFormat = channel.getFormat();
        User user = strings.getUser(sender);

        newMessageFormat = newMessageFormat.replace("{prefix}", user.getPrefix());
        newMessageFormat = newMessageFormat.replace("{suffix}", user.getSuffix());
        newMessageFormat = newMessageFormat.replace("{displayname}", user.getDisplayName());
        newMessageFormat = newMessageFormat.replace("{message}", user.getChatColor(channel) + "{message}");

        if(usePAPI) {
            newMessageFormat = PlaceholderAPI.setPlaceholders(sender, newMessageFormat);
        }

        newMessageFormat = ChatColor.translateAlternateColorCodes('&', newMessageFormat);

        return newMessageFormat;
    }

    public String processMessage(Player sender, String message, Channel channel) {

        if(usePAPI && messagePlaceholders && (sender.hasPermission("strings.*") || sender.hasPermission("strings.chat.*") || sender.hasPermission("strings.chat.placeholdermsg"))) {
            message = PlaceholderAPI.setPlaceholders(sender, message);
        }
        if(parseChatColors && (sender.hasPermission("strings.*") || sender.hasPermission("strings.chat.*") || sender.hasPermission("strings.chat.colormsg"))) {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        return message;

    }

    public boolean isOnCoolDown(Player player) {
        return coolDownList.contains(player) && !(player.hasPermission("strings.*") || player.hasPermission("strings.chat.*") || player.hasPermission("strings.chat.bypasscooldown") || player.hasPermission("*"));
    }

    public void startCoolDown(Player player) {
        coolDownList.add(player);
        scheduler.runTaskLater(strings,() -> coolDownList.remove(player), coolDownLength);
    }

    public String processMentions(Player sender, String str)
    {

        if(!str.contains("@")){
            return str;
        }

        String[] splitStr = str.split("((?=@))"); //https://www.baeldung.com/java-split-string-keep-delimiters
        StringBuilder sb = new StringBuilder();
        String color = "";
        for(String segment : splitStr){
            if(!segment.contains("@")){
                color = ChatColor.getLastColors(segment);
                sb.append(segment);
                continue;
            }
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(!strings.getUser(p).isMentionsEnabled() || !segment.contains(p.getName())) {
                    continue;
                }
                segment = segment.replace("@" + p.getName(), mentionColor + "@" + p.getName() + ChatColor.RESET + color);
            }
            if(sender.hasPermission("strings.mention.all") && segment.contains("@everyone")) {
                segment = segment.replace("@everyone", mentionColor + "@everyone" + ChatColor.RESET + color);
            }
            sb.append(segment);
        }
        return sb.toString();
    }

    public boolean isMentionsEnabled(){
        return mentionsEnabled;
    }

}
