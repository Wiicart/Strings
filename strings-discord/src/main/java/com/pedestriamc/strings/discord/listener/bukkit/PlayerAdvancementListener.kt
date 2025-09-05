package com.pedestriamc.strings.discord.listener.bukkit

import com.pedestriamc.strings.api.discord.Option
import com.pedestriamc.strings.discord.StringsDiscord
import com.pedestriamc.strings.discord.misc.ColorProvider
import net.dv8tion.jda.api.EmbedBuilder
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import java.awt.Color

class PlayerAdvancementListener(strings: StringsDiscord) : AbstractBukkitListener(strings) {

    private val color: Color = ColorProvider.parse(
        strings.configuration[Option.Text.ADVANCEMENT_COLOR],
        Color.MAGENTA,
        strings.logger
    );

    @EventHandler
    internal fun onEvent(event: PlayerAdvancementDoneEvent) {
        val display = event.advancement.display ?: return;
        val player = event.player;
        sendEmbed(
            EmbedBuilder()
                .setColor(color)
                .setAuthor(
                    "${player.name} has made the advancement ${display.title}",
                    null,
                    avatars.getLink(player)
                )
                .build()
        )
    }

}