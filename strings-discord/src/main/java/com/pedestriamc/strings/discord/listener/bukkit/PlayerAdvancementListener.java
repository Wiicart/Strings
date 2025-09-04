package com.pedestriamc.strings.discord.listener.bukkit;

import com.pedestriamc.strings.api.discord.Option;
import com.pedestriamc.strings.discord.StringsDiscord;
import com.pedestriamc.strings.discord.manager.DiscordManager;
import com.pedestriamc.strings.discord.misc.AvatarProvider;
import com.pedestriamc.strings.discord.misc.ColorProvider;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public class PlayerAdvancementListener implements Listener {

    private final DiscordManager manager;
    private final AvatarProvider avatars;

    private final Color color;

    public PlayerAdvancementListener(@NotNull StringsDiscord strings) {
        manager = strings.getManager();
        avatars = strings.getAvatarProvider();
        color = ColorProvider.parse(
                strings.getConfiguration().get(Option.Text.ADVANCEMENT_COLOR),
                Color.MAGENTA,
                strings.getLogger()
        );
    }

    @EventHandler
    void onEvent(@NotNull PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();

        AdvancementDisplay display = event.getAdvancement().getDisplay();
        if(display == null) {
            return;
        }

        MessageEmbed embed = new EmbedBuilder()
                .setColor(color)
                .setAuthor(
                        player.getName() + " has made the advancement " + display.getTitle(),
                        null,
                        avatars.getLink(player)
                ).build();

        manager.sendDiscordEmbed(embed);
    }
}
