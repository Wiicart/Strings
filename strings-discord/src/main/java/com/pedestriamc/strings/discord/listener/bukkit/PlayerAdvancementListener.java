package com.pedestriamc.strings.discord.listener.bukkit;

import com.pedestriamc.strings.discord.StringsDiscord;
import com.pedestriamc.strings.discord.configuration.Option;
import com.pedestriamc.strings.discord.manager.DiscordManager;
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

    private final String avatarLink;

    public PlayerAdvancementListener(@NotNull StringsDiscord strings) {
        manager = strings.getManager();
        avatarLink = strings.getSettings().getString(Option.Text.AVATAR_URL);
    }

    @EventHandler
    void onEvent(@NotNull PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();

        AdvancementDisplay display = event.getAdvancement().getDisplay();
        if(display == null) {
            return;
        }

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.MAGENTA)
                .setAuthor(
                        player.getName() + " has made the advancement " + display.getTitle(),
                        null,
                        avatarLink.replace("{uuid}", player.getUniqueId().toString())
                ).build();

        manager.sendDiscordEmbed(embed);
    }
}
