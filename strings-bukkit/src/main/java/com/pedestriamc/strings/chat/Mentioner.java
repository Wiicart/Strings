package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.utlity.Permissions;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.configuration.Option;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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

    public Mentioner(@NotNull Strings strings) {
        this.userUtil = strings.users();

        Configuration config = strings.getConfiguration();
        pitch = config.getFloat(Option.MENTION_PITCH);
        volume = config.getFloat(Option.MENTION_VOLUME);
        format = config.getColored(Option.MENTION_FORMAT);

        try {
            sound = Sound.valueOf(config.getString(Option.MENTION_SOUND));
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
        mention(user.getPlayer(), sender.getPlayer());
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

}
