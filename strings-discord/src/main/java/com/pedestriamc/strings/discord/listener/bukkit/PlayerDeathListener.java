package com.pedestriamc.strings.discord.listener.bukkit;

import com.pedestriamc.strings.discord.StringsDiscord;
import com.pedestriamc.strings.discord.configuration.Option;
import com.pedestriamc.strings.discord.manager.DiscordManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public class PlayerDeathListener implements Listener {

    private static final Color ORANGE = new Color(255, 111, 0);

    private final DiscordManager manager;

    private final String avatarLink;

    public PlayerDeathListener(@NotNull StringsDiscord strings) {
        manager = strings.getManager();
        avatarLink = strings.getSettings().getString(Option.Text.AVATAR_URL);
    }

    @EventHandler
    void onEvent(@NotNull PlayerDeathEvent event) {
        MessageEmbed embed = new EmbedBuilder()
                .setColor(ORANGE)
                .setAuthor(
                        event.getDeathMessage(),
                        null,
                        avatarLink.replace("{uuid}", event.getEntity().getUniqueId().toString())
                ).build();

        manager.sendDiscordEmbed(embed);
    }
}
