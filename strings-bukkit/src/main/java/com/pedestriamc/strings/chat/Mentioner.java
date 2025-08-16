package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class Mentioner {

    private final UserUtil userUtil;

    private final float pitch;
    private final float volume;
    private final String format;
    private Sound sound;

    private final String mentionColor;

    public Mentioner(@NotNull Strings strings) {
        this.userUtil = strings.users();

        Configuration config = strings.getConfiguration();
        pitch = config.getFloat(Option.Double.MENTION_PITCH);
        volume = config.getFloat(Option.Double.MENTION_VOLUME);
        format = config.getColored(Option.Text.MENTION_TEXT_ACTION_BAR);
        mentionColor = config.getColored(Option.Text.MENTION_COLOR);

        try {
            sound = Sound.valueOf(config.getString(Option.Text.MENTION_SOUND));
        } catch(IllegalArgumentException e) {
            strings.warning("Invalid sound-type for mentions in config.yml, defaulting to: BLOCK_NOTE_BLOCK_PLING");
            sound = Sound.BLOCK_NOTE_BLOCK_PLING;
        }
    }

    public static boolean hasMentionPermission(@NotNull Player player) {
        return Permissions.anyOfOrAdmin(player, "strings.*", "strings.mention", "strings.mention.*", "strings.mention.all");
    }

    public void mention(@NotNull Player player, @NotNull Player sender) {
        if(!userUtil.getUser(player).isMentionsEnabled()) {
            return;
        }
        String str = format;
        str = str.replace("%sender%", sender.getName());
        sendMention(player, str);
    }

    public void mention(@NotNull User user, @NotNull User sender) {
        mention(user.player(), sender.player());
    }

    public void mention(@NotNull Set<Player> group, @NotNull Player sender) {
        group.removeIf(p -> !userUtil.getUser(p).isMentionsEnabled());

        String str = format;
        str = str.replace("%sender%", sender.getName());

        for(Player p : group) {
            sendMention(p, str);
        }
    }

    public void sendMention(@NotNull Player player, @NotNull String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    @NotNull
    public String processMentions(@NotNull Player sender, @NotNull Channel channel, @NotNull String str) {
        if (!str.contains("@")) {
            return str;
        }

        String chatColor = userUtil.getUser(sender).getChatColor(channel);
        String[] splitStr = str.split("((?=@))"); //https://www.baeldung.com/java-split-string-keep-delimiters
        StringBuilder sb = new StringBuilder();
        for (String segment : splitStr) {
            if (!segment.contains("@")) {
                sb.append(chatColor).append(segment);
                continue;
            }

            if (sender.hasPermission("strings.mention.all") && segment.contains("@everyone")) {
                segment = segment.replace("@everyone", mentionColor + "@everyone" + ChatColor.RESET + chatColor);
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (userUtil.getUser(p).isMentionsEnabled() && segment.contains(p.getName())) {
                    String original = "@" + p.getName();
                    String replacement = mentionColor + "@" + p.getName() + ChatColor.RESET + chatColor;
                    segment = segment.replace(original, replacement);
                }
            }

            sb.append(segment);
        }
        return sb.toString();
    }

}
