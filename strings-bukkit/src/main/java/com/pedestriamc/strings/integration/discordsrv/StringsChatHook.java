package com.pedestriamc.strings.integration.discordsrv;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.event.strings.Listener;
import com.pedestriamc.strings.user.User;
import github.scarsz.discordsrv.Debug;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.hooks.chat.ChatHook;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Channel hook for DiscordSRV.
 */
class StringsChatHook implements ChatHook {

    private final Strings strings;

    StringsChatHook(@NotNull Strings strings) {
        this.strings = strings;
    }

    @Override
    public Strings getPlugin() {
        return strings;
    }

    @Listener
    public void onChat(@NotNull ChannelChatEvent event) {
        DiscordSRV.getPlugin().processChatMessage(
                User.playerOf(event.getPlayer()),
                toDiscordSRV(event.message()),
                event.getChannel().getName(),
                event.isCancelled(),
                null
        );
    }



    @Override
    public void broadcastMessageToChannel(String channelName, github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component message) {
        Channel channel = strings.getChannelLoader().getChannel(channelName);
        if (channel == null) {
            DiscordSRV.debug(
                    Debug.DISCORD_TO_MINECRAFT,
                    "Strings: channel \"" + channelName + "\" not found."
            );
            return;
        }

        channel.broadcastPlain(fromDiscordSRV(message));
    }

    private @NotNull Component fromDiscordSRV(@NotNull github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component component) {
        return GsonComponentSerializer.gson().deserialize(
                github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(component)
        );
    }

    private @NotNull github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component toDiscordSRV(@NotNull Component component) {
        return github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(
                GsonComponentSerializer.gson().serialize(component)
        );
    }


}
