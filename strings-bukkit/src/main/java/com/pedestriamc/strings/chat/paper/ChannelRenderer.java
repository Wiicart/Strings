package com.pedestriamc.strings.chat.paper;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.chat.MessageProcessor;
import com.pedestriamc.strings.configuration.Option;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChannelRenderer implements ChatRenderer {

    private final Strings strings;
    private final Channel channel;

    private ChannelRenderer(Strings strings, Channel channel) {
        this.strings = strings;
        this.channel = channel;
    }

    public static ChannelRenderer forChannel(Strings strings, Channel channel) {
        return new ChannelRenderer(strings, channel);
    }

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        MessageProcessor processor = new MessageProcessor(strings, channel);
        String template = processor.generateTemplate(source);

        if (strings.getConfiguration().getBoolean(Option.ENABLE_MENTIONS) && Mentioner.hasMentionPermission(source)) {
            //processedMessage = processor.processMentions(source, processedMessage);
        }
        return null;
    }
}
