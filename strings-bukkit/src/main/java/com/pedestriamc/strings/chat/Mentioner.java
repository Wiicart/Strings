package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.user.User;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Mentioner {

    private final Strings strings;
    private final float pitch;
    private final float volume;
    private final String format;
    private Sound sound;

    public Mentioner(@NotNull Strings strings)
    {
        this.strings = strings;
        FileConfiguration config = strings.getConfig();
        pitch = (float) config.getDouble("mention-pitch", 0.594604F);
        volume = (float) config.getDouble("mention-vol", 10F);
        format = ChatColor.translateAlternateColorCodes('&',
                config.getString("mention-format", "&e%sender% mentioned you."));
        try{
            sound = Sound.valueOf(config.getString("mention-sound"));
        }catch(IllegalArgumentException e){
            Bukkit.getLogger().info("[Strings] Invalid sound-type for mentions in config.yml, resorting to: BLOCK_NOTE_BLOCK_PLING");
            sound = Sound.BLOCK_NOTE_BLOCK_PLING;
        }
    }

    public void mention(@NotNull Player player, @NotNull Player sender)
    {
        if(!strings.getUser(player).isMentionsEnabled()){
            return;
        }
        String str = format;
        str = str.replace("%sender%", sender.getName());
        sendMention(player, str);
    }

    public void mention(@NotNull User user, @NotNull User sender){
        mention(user.getPlayer(), sender.getPlayer());
    }

    public void mention(Set<Player> group, Player sender)
    {
        group.removeIf(p -> !strings.getUser(p).isMentionsEnabled());

        String str = format;
        str = str.replace("%sender%", sender.getName());

        for(Player p : group){
            sendMention(p, str);
        }
    }

    private void sendMention(Player player, String message)
    {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

}
