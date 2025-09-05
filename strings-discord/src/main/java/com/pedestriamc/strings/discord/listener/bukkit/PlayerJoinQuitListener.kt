package com.pedestriamc.strings.discord.listener.bukkit

import com.earth2me.essentials.Essentials
import com.pedestriamc.strings.api.StringsProvider
import com.pedestriamc.strings.api.discord.Option
import com.pedestriamc.strings.api.user.StringsUser
import com.pedestriamc.strings.discord.StringsDiscord
import com.pedestriamc.strings.discord.misc.ColorProvider
import net.dv8tion.jda.api.EmbedBuilder
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.annotations.NotNull
import java.awt.Color

class PlayerJoinQuitListener(@NotNull strings: StringsDiscord) : AbstractBukkitListener(strings) {

    private val essentials : Essentials? = strings.server.pluginManager.getPlugin("Essentials") as? Essentials;

    private val joinMessage: String = strings.configuration[Option.Text.JOIN_MESSAGE];
    private val quitMessage: String = strings.configuration[Option.Text.LEAVE_MESSAGE];

    private val joinColor: Color = ColorProvider.parse(
        strings.configuration[Option.Text.JOIN_COLOR],
        Color.decode(Option.Text.JOIN_COLOR.defaultValue()),
        strings.logger
    );

    private val leaveColor: Color = ColorProvider.parse(
        strings.configuration[Option.Text.LEAVE_COLOR],
        Color.decode(Option.Text.LEAVE_COLOR.defaultValue()),
        strings.logger
    );

    @EventHandler
    internal fun onJoin(event: PlayerJoinEvent) {
        val player: Player = event.player.takeUnless { isVanished(it) } ?: return;
        sendEmbed(
            EmbedBuilder()
                .setColor(joinColor)
                .setAuthor(
                    applyPlaceholders(joinMessage, player),
                    null,
                    avatars.getLink(player)
                )
                .build()
        );
    }

    @EventHandler
    internal fun onQuit(event: PlayerQuitEvent) {
        val player: Player = event.player.takeUnless { isVanished(it) } ?: return;
        sendEmbed(
            EmbedBuilder()
                .setColor(leaveColor)
                .setAuthor(
                    applyPlaceholders(quitMessage, player),
                    null,
                    avatars.getLink(player)
                )
                .build()
        );
    }

    private fun applyPlaceholders(original: String, player: Player) : String {
        fun setPrefixAndSuffix(original: String, prefix: String, suffix: String) : String = original
            .replace("{prefix}", prefix)
            .replace("{suffix}", suffix);

        val base: String = ChatColor.stripColor(original)
            .replace("{username}", player.name)
            .replace("{display-name}", player.displayName);

        return runCatching {
            val user: StringsUser = StringsProvider.get().getUser(player.uniqueId) ?: throw Exception();
            return setPrefixAndSuffix(base, user.prefix, user.suffix);
        }.getOrElse {
            setPrefixAndSuffix(base, "", "");
        }
    }



    private fun isVanished(player: Player): Boolean =
        essentials?.getUser(player)?.isVanished ?: player.isInvisible;
}