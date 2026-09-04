package com.pedestriamc.strings.chat.paper;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.annotation.Platform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.managers.Mentioner;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.chat.MessageUtilities;
import com.pedestriamc.strings.common.chat.messages.StandardAdventureRenderer;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Platform.Paper
public class RendererFactory {

    private final Strings strings;

    private final StandardAdventureRenderer adventureRenderer;
    private final DeletionManager deletionManager;
    private final Mentioner mentioner;

    private final boolean mentionsEnabled;
    private final boolean miniMessageEnabled;

    public RendererFactory(@NotNull Strings strings) {
        this.strings = strings;

        adventureRenderer = new StandardAdventureRenderer(strings);
        deletionManager = new DeletionManager(strings);
        mentioner = strings.mentioner();

        Settings settings = strings.settings();
        mentionsEnabled = settings.get(Option.Bool.ENABLE_MENTIONS);
        miniMessageEnabled = settings.get(Option.Bool.CHANNELS_USE_MINI_MESSAGE_FORMATTING);
    }

    @NotNull
    public ChannelChatRenderer createRenderer(
            @NotNull AsyncChatEvent event,
            @NotNull Channel channel,
            @NotNull SignedMessage signedMessage,
            @NotNull Set<StringsUser> recipients
    ) {
        return new ChannelChatRenderer(event, channel, signedMessage, recipients);
    }

    public final class ChannelChatRenderer implements ChatRenderer {

        private final Component draft;

        private final Channel channel;
        private final SignedMessage signedMessage;
        private final StringsUser sender;

        private Mentioner.ChatProcessor mentionProcessor;

        /**
         * Instantiates the Renderer for the message, and preprocesses the template.
         * @param event The event for this message.
         * @param channel The channel this message is being sent to.
         * @param signedMessage The SignedMessage used for message deletion.
         * @param recipients The recipients of this message.
         */
        ChannelChatRenderer(
                @NotNull AsyncChatEvent event,
                @NotNull Channel channel,
                @NotNull SignedMessage signedMessage,
                @NotNull Set<StringsUser> recipients
        ) {
            this.sender = strings.users().getUser(event.getPlayer());
            this.channel = channel;
            this.signedMessage = signedMessage;

            draft = generateDraft(event.message());

            if (mentionsEnabled) {
                mentionProcessor = mentioner.processor(sender, draft, recipients);
            }
        }

        @Override
        public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
            Component result = draft;

            if (!(viewer instanceof Player player)) {
                return result;
            }

            StringsUser recipient = strings.users().getUser(player);
            if (mentionsEnabled) {
                result = mentionProcessor.processMentions(recipient);
            }
            if (shouldAppendDeleteButton(channel, sender, recipient)) {
                result = result.append(deletionManager.createDeleteButton(signedMessage));
            }

            return result;
        }

        public Set<StringsUser> mentionedPlayers() {
            return mentionsEnabled ? mentionProcessor.mentionedUsers() : Set.of();
        }

        private Component generateDraft(@NotNull Component rawMessage) {
            String stringFormat = adventureRenderer.generateStringTemplate(sender, channel);
            Component base = convertStringFormatToComponent(sender, stringFormat);
            base = adventureRenderer.finalizeTemplate(sender, channel, base);

            Component message = adventureRenderer.processMessage(sender, channel, ComponentConverter.toString(rawMessage));

            return adventureRenderer.insertMessage(base, message);
        }

        private Component convertStringFormatToComponent(@NotNull StringsUser sender, @NotNull String stringFormat) {
            if (miniMessageEnabled) {
                return strings.miniPlaceholders().setPlaceholders(sender, stringFormat);
            } else {
                stringFormat = MessageUtilities.colorHex(stringFormat);
                stringFormat = MessageUtilities.translateColorCodes(stringFormat);

                return ComponentConverter.toComponent(stringFormat);
            }
        }
    }

    private boolean shouldAppendDeleteButton(@NotNull Channel channel, @NotNull StringsUser sender, @NotNull StringsUser viewer) {
        return channel.allowsMessageDeletion() &&
                deletionManager.hasDeletionPermission(sender, viewer);
    }

}
