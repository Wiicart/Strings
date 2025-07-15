package com.pedestriamc.strings.discord.listener.discord;

import com.pedestriamc.strings.discord.StringsDiscord;
import com.pedestriamc.strings.discord.manager.DiscordManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public final class MessageListener extends ListenerAdapter {

    private final DiscordManager manager;

    public MessageListener(@NotNull StringsDiscord strings) {
        manager = strings.getManager();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!event.getAuthor().isBot()) {
            manager.sendCraftMessageFromEvent(event);
        }
    }
}
