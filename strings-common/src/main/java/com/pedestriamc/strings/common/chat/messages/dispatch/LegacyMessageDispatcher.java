package com.pedestriamc.strings.common.chat.messages.dispatch;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.util.PermissionChecker;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Message dispatcher based on Bukkit color codes, not supporting any adventure systems.
 */
public class LegacyMessageDispatcher extends AbstractMessageDispatcher {

    private final PlatformAdapter adapter;

    private final boolean usingPlaceholderAPI;
    private final boolean applyingMessagePlaceholders;
    private final boolean coloringMessages;
    private final boolean emojisEnabled;
    private final boolean mentionsEnabled;

    public LegacyMessageDispatcher(@NotNull StringsPlatform strings, @NotNull Channel channel) {
        super(strings, channel);

        adapter = strings.getAdapter();
        usingPlaceholderAPI = strings.isUsingPlaceholderAPI();

        Settings settings = strings.settings();
        applyingMessagePlaceholders = usingPlaceholderAPI && settings.get(Option.Bool.PROCESS_PLACEHOLDERS);
        coloringMessages = settings.get(Option.Bool.PROCESS_CHATCOLOR);
        emojisEnabled = settings.get(Option.Bool.ENABLE_EMOJI_REPLACEMENT);
        mentionsEnabled = settings.get(Option.Bool.ENABLE_MENTIONS);
    }

    @Override
    void dispatchMessageToPlayers(@NotNull StringsUser sender, @NotNull String message, @NotNull Set<StringsUser> recipients) {
        String fullMessage = generateTemplate(sender);
        fullMessage = fullMessage.replace("{message}", processMessage(sender, message));

        for (StringsUser recipient : recipients) {
            recipient.sendMessage(fullMessage);
        }

        if (!recipients.contains(sender)) {
            sender.sendMessage(fullMessage);
        }

        adapter.print(adapter.stripBukkitColor(fullMessage));
    }

    @SuppressWarnings("deprecation")
    public String generateTemplate(@NotNull StringsUser sender) {
        String template = channel().getGroupFormat(sender.getPrimaryGroup());
        String chatColor = sender.getChatColor().isBlank() ? channel().getDefaultColor() : sender.getChatColor();
        template = template
                .replace("{prefix}", sender.getPrefix())
                .replace("{suffix}", sender.getSuffix())
                .replace("{displayname}", sender.getDisplayName())
                .replace("{username}", sender.getName())
                .replace("{message}", chatColor + "{message}");

        if (usingPlaceholderAPI) {
            template = adapter.setPlaceholders(sender, template);
        }

        template = adapter.translateBukkitColor(template);
        template = adapter.colorHex(template);

        return template;
    }

    public String processMessage(@NotNull StringsUser sender, @NotNull String message) {
        if (shouldApplyPlaceholders(sender)) {
            message = adapter.setPlaceholders(sender, message);
        }

        if (shouldColorMessage(sender)) {
            message = adapter.translateBukkitColor(message);
            message = adapter.colorHex(message);
        }

        if (shouldApplyEmojis(sender)) {
            message = strings().emojiManager().applyEmojis(message);
        }

        if (shouldHandleMentions(sender)) {
            message = ComponentConverter.toString(strings().mentioner().processMentions(sender, ComponentConverter.toComponent(message)));
        }

        return message;
    }

    private boolean shouldApplyPlaceholders(@NotNull StringsUser sender) {
        return applyingMessagePlaceholders && PermissionChecker.anyOrOp(sender,
                "strings.*",
                "strings.chat.*",
                "strings.chat.placeholdermsg"
        );
    }

    private boolean shouldColorMessage(@NotNull StringsUser sender) {
        return coloringMessages && PermissionChecker.anyOrOp(sender,
                "strings.*",
                "strings.chat.*",
                "strings.chat.colormsg"
        );
    }

    private boolean shouldApplyEmojis(@NotNull StringsUser sender) {
        return emojisEnabled && PermissionChecker.anyOrOp(sender,
                "strings.*",
                "strings.chat.*",
                "strings.chat.emojis"
        );
    }

    private boolean shouldHandleMentions(@NotNull StringsUser sender) {
        return mentionsEnabled && PermissionChecker.anyOrOp(sender,
                "strings.*",
                "strings.mention",
                "strings.mention.*",
                "strings.mention.all"
        );
    }

}
