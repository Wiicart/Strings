package com.pedestriamc.strings.discord.listener.bukkit;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.discord.StringsDiscord;
import com.pedestriamc.strings.api.discord.Option;
import com.pedestriamc.strings.discord.configuration.Configuration;
import com.pedestriamc.strings.discord.misc.ColorProvider;
import net.dv8tion.jda.api.EmbedBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;

//todo to kotlin
public class PlayerJoinQuitListener extends AbstractBukkitListener {

    private final String joinMessage;
    private final String leaveMessage;

    private final Color joinColor;
    private final Color leaveColor;

    private final @Nullable  Plugin essentials;

    public PlayerJoinQuitListener(@NotNull StringsDiscord strings) {
        super(strings);

        Configuration config = strings.getConfiguration();
        joinMessage = config.get(Option.Text.JOIN_MESSAGE);
        leaveMessage = config.get(Option.Text.LEAVE_MESSAGE);

        joinColor = ColorProvider.parse(
                config.get(Option.Text.JOIN_COLOR),
                Color.GREEN,
                strings.getLogger()
        );
        leaveColor = ColorProvider.parse(
                config.get(Option.Text.LEAVE_COLOR),
                Color.RED,
                strings.getLogger()
        );

        essentials = strings.getServer().getPluginManager().getPlugin("Essentials");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onEvent(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isPlayerVanished(player)) {
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(joinColor)
                .setAuthor(
                        applyPlaceholders(joinMessage, player),
                        null,
                        avatars().getLink(player)
                );

        manager().sendDiscordEmbed(builder.build());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onEvent(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isPlayerVanished(player)) {
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(leaveColor)
                .setAuthor(
                        applyPlaceholders(leaveMessage, player),
                        null,
                        avatars().getLink(player)
                );

        manager().sendDiscordEmbed(builder.build());
    }

    // Placeholders are the same for join and leave
    private @NotNull String applyPlaceholders(@NotNull String original, @NotNull Player player) {
        try {
            StringsAPI api = StringsProvider.get();

            String prefix = "";
            String suffix = "";
            String displayName = player.getDisplayName();

            StringsUser user = api.getUser(player.getUniqueId());
            if(user != null) {
                prefix = user.getPrefix();
                suffix = user.getSuffix();
                displayName = user.getDisplayName();
            }

            return ChatColor.stripColor(original
                    .replace("{username}", player.getName())
                    .replace("{display-name}", displayName)
                    .replace("{prefix}", prefix)
                    .replace("{suffix}", suffix)
            );
        } catch(Exception e) {
            return ChatColor.stripColor(original
                    .replace("{username}", player.getName())
                    .replace("{display-name}", player.getDisplayName())
                    .replace("{prefix}", "")
                    .replace("{suffix}", "")
            );
        }
    }

    private boolean isPlayerVanished(@NotNull Player player) {
        if (essentials instanceof Essentials ess) {
            User user = ess.getUser(player.getUniqueId());
            if (user != null ) {
                return user.isHidden();
            }
        }

        return player.isInvisible();
    }
}
