package com.pedestriamc.strings.discord.listener.bukkit;

import com.pedestriamc.strings.api.discord.Option;
import com.pedestriamc.strings.discord.StringsDiscord;
import com.pedestriamc.strings.discord.manager.DiscordManager;
import com.pedestriamc.strings.discord.misc.AvatarProvider;
import com.pedestriamc.strings.discord.misc.ColorProvider;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public class PlayerDeathListener implements Listener {

    private static final Color ORANGE = new Color(255, 111, 0);

    private final DiscordManager manager;
    private final AvatarProvider avatars;

    private final Color color;

    public PlayerDeathListener(@NotNull StringsDiscord strings) {
        manager = strings.getManager();
        avatars = strings.getAvatarProvider();
        color = ColorProvider.parse(
                strings.getConfiguration().get(Option.Text.DEATH_COLOR),
                ORANGE,
                strings.getLogger()
        );
    }

    @EventHandler
    void onEvent(@NotNull PlayerDeathEvent event) {
        MessageEmbed embed = new EmbedBuilder()
                .setColor(color)
                .setAuthor(
                        ChatColor.stripColor(event.getDeathMessage()),
                        null,
                        avatars.getLink(event.getEntity())
                ).build();

        manager.sendDiscordEmbed(embed);
    }
}
