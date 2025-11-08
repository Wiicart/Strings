package com.pedestriamc.strings.chat.paper;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.annotation.Platform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.chat.MessageUtilities;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import io.papermc.paper.chat.ChatRenderer;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    public RendererProvider(@NotNull Strings strings) {
        this.strings = strings;
        userUtil = strings.users();
        deletionManager = new DeletionManager(strings);
        mentioner = strings.getMentioner();

        Configuration config = strings.getSettings();
        mentionsEnabled = config.get(Option.Bool.ENABLE_MENTIONS);
        processingMessagePlaceholders = config.get(Option.Bool.PROCESS_PLACEHOLDERS) && strings.isUsingPlaceholderAPI();
        parsingMessageChatColors = config.get(Option.Bool.PROCESS_CHATCOLOR);
        emojisEnabled = config.get(Option.Bool.ENABLE_EMOJI_REPLACEMENT);
    }

    @NotNull
    public ChatRenderer createRenderer(@NotNull Channel channel, @NotNull SignedMessage signedMessage) {
        return new ChannelChatRenderer(channel, signedMessage);
    }


    class ChannelChatRenderer implements ChatRenderer {

        private final Channel channel;
        private final SignedMessage signedMessage;

        ChannelChatRenderer(@NotNull Channel channel, @NotNull SignedMessage signedMessage) {
            this.channel = channel;
            this.signedMessage = signedMessage;
        }

        @Override
        @NotNull
        public Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
            User user = userUtil.getUser(source);
            Component component;

            component = generateBase(channel, user);
            component = insertMessage(component, message, channel, user);

            if (shouldAppendDeleteButton(channel, source, viewer) && signedMessage.canDelete()) {
                component = component.append(deletionManager.getDeletionButton(signedMessage));
            }

            return component;
        }

        @NotNull
        private Component generateBase(@NotNull Channel channel, @NotNull User source) {
            String base = channel.getFormat();

            base = base
                    .replace("{prefix}", source.getPrefix())
                    .replace("{suffix}", source.getSuffix())
                    .replace("{displayname}", source.getDisplayName());

            if (strings.isUsingPlaceholderAPI()) {
                base = setPlaceholderAPIPlaceholders(source.player(), base);
            }

            base = MessageUtilities.colorHex(base);
            base = MessageUtilities.translateColorCodes(base);

            return ComponentConverter.fromString(base);
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

            if (mentionsEnabled && Mentioner.hasMentionPermission(player)) {
                msg = mentioner.processMentions(player, channel, msg);
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

        @NotNull
        private String setPlaceholderAPIPlaceholders(@NotNull Player sender, @NotNull String str) {
            try {
                return PlaceholderAPI.setPlaceholders(sender, str);
            } catch (NoClassDefFoundError e) {
                strings.getLogger().warning("Failed to set placeholders for message from " + sender.getName() + ".");
                return str;
            }
        }

        private Component setEmojisIfAllowed(@NotNull Player sender, @NotNull Component input) {
            if (emojisEnabled && Permissions.anyOfOrAdmin(sender, "strings.*", "strings.chat.*", "strings.chat.emojis")) {
                return strings.getEmojiManager().applyEmojis(input);
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

        private boolean shouldAppendDeleteButton(@NotNull Channel channel, @NotNull Player source, @NotNull Audience viewer) {
            return channel.allowsMessageDeletion() &&
                    viewer instanceof Player p &&
                    deletionManager.hasDeletionPermission(source, p);
        }
    }
}
