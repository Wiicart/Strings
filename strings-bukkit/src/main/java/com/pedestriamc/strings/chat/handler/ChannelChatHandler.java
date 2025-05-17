package com.pedestriamc.strings.chat.handler;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.chat.StringsTextColor;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChannelChatHandler implements ChatRenderer.ViewerUnaware {

    private final Channel channel;

    private final UserUtil userUtil;

    public ChannelChatHandler(final @NotNull Strings strings, final @NotNull Channel channel) {
        this.channel = channel;
        userUtil = strings.getUserUtil();
    }

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message) {
        message.color(StringsTextColor.RED);
        return null;
    }

    private Component setPlaceholders(Component component, User user) {
        component = setPlaceholder(component, "{prefix}", user.getPrefix());
        component = setPlaceholder(component, "{suffix}", user.getSuffix());
        component = setPlaceholder(component, "{displayname}", user.getDisplayName());

        return component;
    }

    private Component setPlaceholder(Component component, final String original, final String replacement) {
        return component.replaceText(TextReplacementConfig.builder()
                .matchLiteral(original)
                .replacement(replacement)
                .build());
    }
}
