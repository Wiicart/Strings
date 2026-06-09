package com.pedestriamc.strings.chat.paper;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.annotation.Platform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.managers.Mentioner;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.chat.MessageUtilities;
import com.pedestriamc.strings.manager.Configuration;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Platform.Paper
public class RendererProvider {

    private final Strings strings;

    private final UserUtil userUtil;
    private final DeletionManager deletionManager;
    private final Mentioner mentioner;

    private final boolean processingMessagePlaceholders;
    private final boolean parsingMessageChatColors;
    private final boolean mentionsEnabled;
    private final boolean emojisEnabled;
    private final boolean parseUsingMiniMessage;

    public RendererProvider(@NotNull Strings strings) {
        this.strings = strings;
        userUtil = strings.users();
        deletionManager = new DeletionManager(strings);
        mentioner = strings.mentioner();

        Configuration config = strings.settings();
        mentionsEnabled = config.get(Option.Bool.ENABLE_MENTIONS);
        processingMessagePlaceholders = config.get(Option.Bool.PROCESS_PLACEHOLDERS) && strings.isUsingPlaceholderAPI();
        parsingMessageChatColors = config.get(Option.Bool.PROCESS_CHATCOLOR);
        emojisEnabled = config.get(Option.Bool.ENABLE_EMOJI_REPLACEMENT);
        parseUsingMiniMessage = config.get(Option.Bool.CHANNELS_USE_MINI_MESSAGE_FORMATTING);
    }

    @NotNull
    public ChannelChatRenderer renderer(
            @NotNull AsyncChatEvent event,
            @NotNull Channel channel,
            @NotNull SignedMessage signedMessage,
            @NotNull Set<StringsUser> recipients
    ) {
        return new ChannelChatRenderer(event, channel, signedMessage, recipients);
    }


    public final class ChannelChatRenderer implements ChatRenderer {

        private final Channel channel;
        private final SignedMessage signedMessage;
        private final StringsUser sender;
        private Mentioner.ChatProcessor mentionProcessor;
        private Component base;

        ChannelChatRenderer(
                @NotNull AsyncChatEvent event,
                @NotNull Channel channel,
                @NotNull SignedMessage signedMessage,
                @NotNull Set<StringsUser> recipients
        ) {
            this.channel = channel;
            this.signedMessage = signedMessage;
            sender = userUtil.getUser(event.getPlayer());

            base = generateTemplate(channel, (User) sender);
            base = insertMessage(base, event.message(), channel, (User) sender);

            if (mentionsEnabled) {
                mentionProcessor = mentioner.processor(sender, base, recipients);
            }
        }

        @Override
        @NotNull
        public Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
            Component result = base;
            if (!(viewer instanceof Player player)) {
                return result;
            }

            StringsUser recipient = userUtil.getUser(player);

            if (mentionsEnabled) {
                result = mentionProcessor.processMentions(recipient);
            }

            if (shouldAppendDeleteButton(channel, sender, recipient) && signedMessage.canDelete()) {
                result = result.append(deletionManager.createDeleteButton(signedMessage));
            }

            return result;
        }

        @NotNull
        private Component generateTemplate(@NotNull Channel channel, @NotNull User source) {
            String raw = channel.getFormat();
            raw = raw.replace("{username}", source.getName());
            raw = raw.replace("{uuid}", source.getUniqueId().toString());

            if (strings.isUsingPlaceholderAPI()) {
                raw = setPlaceholderAPIPlaceholders(source.player(), raw);
            }

            Component format;
            if (parseUsingMiniMessage) {
                format = MiniMessage.miniMessage().deserialize(raw);
            } else {
                raw = MessageUtilities.colorHex(raw);
                raw = MessageUtilities.translateColorCodes(raw);
                format = ComponentConverter.fromString(raw);
            }

            format = format.replaceText(b -> b
                    .matchLiteral("{prefix}")
                    .replacement(ComponentConverter.fromString(source.getPrefix())));
            format = format.replaceText(b -> b
                    .matchLiteral("{suffix}")
                    .replacement(ComponentConverter.fromString(source.getSuffix())));
            format = format.replaceText(b -> b
                    .matchLiteral("{displayname}")
                    .replacement(ComponentConverter.fromString(source.getDisplayName())));

            return format;
        }

        @NotNull
        private Component insertMessage(@NotNull Component base, @NotNull Component message, @NotNull Channel channel, @NotNull User source) {
            Component finalComponent = base;
            Player player = source.player();
            String msg = ComponentConverter.toString(message);

            if (shouldReplacePlaceholders(source)) {
                msg = setPlaceholderAPIPlaceholders(player, msg);
            }

            if (shouldColorMessage(source)) {
                msg = MessageUtilities.colorHex(msg);
                msg = MessageUtilities.translateColorCodes(msg);
            }

            String string = source.getChatColor() + msg;
            Component messageComponent = ComponentConverter.fromString(string);

            messageComponent = setEmojisIfAllowed(player, messageComponent);


            finalComponent = MessageUtilities.setPlaceholder(
                    finalComponent,
                    "{message}",
                    messageComponent
            );

            return finalComponent;
        }

        public Set<StringsUser> mentionedPlayers() {
            return mentionProcessor.mentionedUsers();
        }

        @NotNull
        private String setPlaceholderAPIPlaceholders(@NotNull Player sender, @NotNull String str) {
            try {
                return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(sender, str);
            } catch (NoClassDefFoundError e) {
                strings.getLogger().warning("Failed to set placeholders for message from " + sender.getName() + ".");
                return str;
            }
        }

        private Component setEmojisIfAllowed(@NotNull Player sender, @NotNull Component input) {
            if (emojisEnabled && Permissions.anyOfOrAdmin(sender, "strings.*", "strings.chat.*", "strings.chat.emojis")) {
                return strings.emojiManager().applyEmojis(input);
            }
            return input;
        }

        private boolean shouldReplacePlaceholders(User user) {
            return processingMessagePlaceholders &&
                    Permissions.anyOfOrAdmin(user, "strings.*", "strings.chat.*", "strings.chat.placeholdermsg");
        }

        private boolean shouldColorMessage(User user) {
            return parsingMessageChatColors &&
                    Permissions.anyOfOrAdmin(user, "strings.*", "strings.chat.*", "strings.chat.colormsg");
        }

        private boolean shouldAppendDeleteButton(@NotNull Channel channel, @NotNull StringsUser sender, @NotNull StringsUser viewer) {
            return channel.allowsMessageDeletion() &&
                    deletionManager.hasDeletionPermission(sender, viewer);
        }
    }
}
