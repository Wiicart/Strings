package com.pedestriamc.strings.discord.listener.bukkit

import com.pedestriamc.strings.discord.StringsDiscord
import com.pedestriamc.strings.discord.manager.DiscordManager
import com.pedestriamc.strings.discord.misc.AvatarProvider
import net.dv8tion.jda.api.entities.MessageEmbed
import org.bukkit.event.Listener
import org.jetbrains.annotations.NotNull

abstract class AbstractBukkitListener(@NotNull strings: StringsDiscord): Listener {

    protected val manager: DiscordManager = strings.manager;
    protected val avatars: AvatarProvider = strings.avatarProvider;

    protected fun sendEmbed(embed : MessageEmbed) {
        manager.sendDiscordEmbed(embed);
    }
}