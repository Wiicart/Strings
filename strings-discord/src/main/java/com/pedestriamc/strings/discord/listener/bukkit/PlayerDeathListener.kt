package com.pedestriamc.strings.discord.listener.bukkit

import com.pedestriamc.strings.api.discord.Option
import com.pedestriamc.strings.discord.StringsDiscord
import com.pedestriamc.strings.discord.misc.ColorProvider
import net.dv8tion.jda.api.EmbedBuilder
import net.md_5.bungee.api.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import java.awt.Color

class PlayerDeathListener(strings: StringsDiscord) : AbstractBukkitListener(strings) {

    companion object {
        val ORANGE: Color = Color(255, 111, 0);
    }

    private val color: Color = ColorProvider.parse(
        strings.configuration[Option.Text.DEATH_COLOR],
        ORANGE,
        strings.logger
    );

    @EventHandler
    internal fun onEvent(event: PlayerDeathEvent) {
        sendEmbed(
            EmbedBuilder()
                .setColor(color)
                .setAuthor(
                    ChatColor.stripColor(event.deathMessage),
                    null,
                    avatars.getLink(event.entity)
                )
            .build()
        );
    }

}