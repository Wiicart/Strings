package com.pedestriamc.strings.discord.misc

import com.pedestriamc.strings.api.discord.Option
import com.pedestriamc.strings.discord.StringsDiscord
import org.bukkit.entity.Player

class AvatarProvider(strings: StringsDiscord) {

    private val avatarLink: String = strings.configuration[Option.Text.AVATAR_URL];

    fun getLink(player: Player) : String {
        return avatarLink.replace("{uuid}", player.uniqueId.toString());
    }

}