package com.pedestriamc.strings.chat.paper;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.api.text.format.StringsComponent;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.chat.MessageProcessor;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ChannelChatRenderer extends MessageProcessor implements ChatRenderer {

    private final boolean mentionsEnabled;

    public ChannelChatRenderer(final @NotNull Strings strings, final @NotNull Channel channel) {
        super(strings, channel);
        mentionsEnabled = strings.getConfiguration().getBoolean(Option.Bool.ENABLE_MENTIONS);
    }

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        String template = generateTemplateNonChatColor(source);
        Component component = ComponentConverter.fromString(template);
        return setPlaceholder(component, "{message}", processMessage(source, message));
    }

    private @NotNull Component processMessage(@NotNull Player source, @NotNull Component message) {
        String msg = ComponentConverter.toString(message);
        msg = super.processMessage(source, msg);

        if(mentionsEnabled && Mentioner.hasMentionPermission(source)) {
            msg = processMentions(source, msg);
        }

        return StringsComponent.fromString(msg).asComponent();
    }

    private @NotNull Component setPlaceholder(@NotNull Component component, final @NotNull String original, final @NotNull String replacement) {
        return component.replaceText(TextReplacementConfig.builder()
                .matchLiteral(original)
                .replacement(replacement)
                .build());
    }

    @SuppressWarnings("all")
    private @NotNull Component setPlaceholder(@NotNull Component component, final @NotNull String original, final @NotNull Component replacement) {
        return component.replaceText(TextReplacementConfig.builder()
                .matchLiteral(original)
                .replacement(replacement)
                .build());
    }

}
