package com.pedestriamc.strings.chat.paper;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.api.text.format.StringsComponent;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.chat.MessageProcessor;
import com.pedestriamc.strings.user.User;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class ChannelChatRenderer extends MessageProcessor implements ChatRenderer {

    private final Channel channel;

    private final DeletionManager deletionManager;
    private final SignedMessage signedMessage;

    private final boolean mentionsEnabled;

    public ChannelChatRenderer(@NotNull Strings strings, @NotNull Channel channel, @NotNull DeletionManager deletionManager, @NotNull SignedMessage message) {
        super(strings, channel);
        this.channel = channel;
        this.signedMessage = message;
        this.deletionManager = deletionManager;
        mentionsEnabled = strings.getConfiguration().getBoolean(Option.Bool.ENABLE_MENTIONS);
    }

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        String template = generateTemplateNonChatColor(source);
        Component component = ComponentConverter.fromString(template);
        component = setPlaceholder(component, "{message}", processMessage(source, message));

        if (
                channel.allowsMessageDeletion() &&
                viewer instanceof Player p &&
                deletionManager.hasDeletionPermission(source, p)
        ) {
            return component.append(deletionManager.getDeletionButton(signedMessage));
        }

        return component;
    }

    private @NotNull Component processMessage(@NotNull Player source, @NotNull Component message) {
        User sourceUser = getUser(source);
        String msg = ComponentConverter.toString(message);
        msg = super.processMessage(source, msg);

        if(mentionsEnabled && Mentioner.hasMentionPermission(source)) {
            msg = processMentions(source, msg);
        }

        return sourceUser.getChatColorComponent().append(StringsComponent.fromString(msg)).asComponent();
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
