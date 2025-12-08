package com.pedestriamc.common.chat;

import com.pedestriamc.common.util.PermissionChecker;
import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

// Designed under bukkit assumptions, likely need to move to a more adventure-based system eventually
public class MessageProcessor {

    private final StringsPlatform strings;
    private final Channel channel;
    private final PlatformAdapter adapter;

    private final boolean usingPlaceholderAPI;
    private final boolean applyingMessagePlaceholders;
    private final boolean coloringMessages;
    private final boolean emojisEnabled;
    private final boolean mentionsEnabled;

    public MessageProcessor(@NotNull StringsPlatform strings, @NotNull Channel channel) {
        this.strings = strings;
        this.channel = channel;
        adapter = strings.getAdapter();
        usingPlaceholderAPI = strings.isUsingPlaceholderAPI();

        Settings settings = strings.getSettings();
        applyingMessagePlaceholders = usingPlaceholderAPI && settings.get(Option.Bool.PROCESS_PLACEHOLDERS);
        coloringMessages = settings.get(Option.Bool.PROCESS_CHATCOLOR);
        emojisEnabled = settings.get(Option.Bool.ENABLE_EMOJI_REPLACEMENT);
        mentionsEnabled = settings.get(Option.Bool.ENABLE_MENTIONS);
    }

    @SuppressWarnings("deprecation")
    public String generateTemplate(@NotNull StringsUser sender) {
        String template = channel.getFormat();
        String chatColor = sender.getChatColor().isBlank() ? channel.getDefaultColor() : sender.getChatColor();
        template = template
                .replace("{prefix}", sender.getPrefix())
                .replace("{suffix}", sender.getSuffix())
                .replace("{displayname}", sender.getDisplayName())
                .replace("{username}", sender.getName())
                .replace("{message}", chatColor + "{message}");

        if (usingPlaceholderAPI) {
            template = adapter.applyPlaceholders(sender, template);
        }

        template = adapter.translateBukkitColor(template);
        template = adapter.colorHex(template);

        return template;
    }

    public String processMessage(@NotNull StringsUser sender, @NotNull String message) {
        if (shouldApplyPlaceholders(sender)) {
            message = adapter.applyPlaceholders(sender, message);
        }

        if (shouldColorMessage(sender)) {
            message = adapter.translateBukkitColor(message);
            message = adapter.colorHex(message);
        }

        if (shouldApplyEmojis(sender)) {
            message = strings.getEmojiManager().applyEmojis(message);
        }

        if (shouldHandleMentions(sender)) {
            message = adapter.processMentions(sender, channel, message);
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
