package com.pedestriamc.strings.common.chat.messages;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.util.PermissionChecker;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Adventure chat-message processing utility,
 * with methods to help build to a finalized Component to be sent in chat.
 * <br/>
 * <b>Ordering:</b>
 * <i>generateStringTemplate() -> (Convert to Component) ->
 *     finalizeTemplate() -> processMessage() -> insertMessage() -> Result</i>
 */
public class StandardAdventureRenderer {

    private final StringsPlatform strings;
    private final PlatformAdapter adapter;

    private final boolean applyingMessagePlaceholders;
    private final boolean coloringMessages;
    private final boolean emojisEnabled;

    public StandardAdventureRenderer(@NotNull StringsPlatform strings) {
        this.strings = strings;

        adapter = strings.getAdapter();
        boolean usingPlaceholderAPI = strings.isUsingPlaceholderAPI();

        Settings settings = strings.settings();
        applyingMessagePlaceholders = usingPlaceholderAPI && settings.get(Option.Bool.PROCESS_PLACEHOLDERS);
        coloringMessages = settings.get(Option.Bool.PROCESS_CHATCOLOR);
        emojisEnabled = settings.get(Option.Bool.ENABLE_EMOJI_REPLACEMENT);
    }

    /**
     * The first stage of message construction.
     * Generates a String template with group-formatting, and basic placeholders replaced.
     * <br/>
     * Placeholders replaced:
     * <ul>
     *     <li>{uuid}</li>
     *     <li>{username}</li>
     *     <li>{name}</li>
     *     <li><i>Any PlaceholderAPI string-based placeholders</i></li>
     * </ul>
     * <b>The second stage,
     * {@link StandardAdventureRenderer#finalizeTemplate(StringsUser, Channel, Component)} accepts this,
     * converted into a Component.
     * </b>
     *
     * @param user The sender
     * @param channel The channel the message is being sent in.
     * @return A String template.
     */
    public String generateStringTemplate(@NotNull StringsUser user, @NotNull Channel channel) {
        String template = channel.getGroupFormat(user.getPrimaryGroup());

        template = template.replace("{uuid}", user.getUniqueId().toString());
        template = template.replace("{username}", user.getName());
        template = template.replace("{name}", user.getName());
        template = adapter.setPlaceholders(user, template);

        return template;
    }

    /**
     * Completes the template by applying the prefix, suffix, and displayname placeholders.
     * Accepts the String format produced by
     * {@link #generateStringTemplate(StringsUser, Channel)}, serialized into a Component.
     * @param sender The sender of the message.
     * @param channel The channel the message is being sent in.
     * @param format The Component representation of the template produced in {@link #generateStringTemplate(StringsUser, Channel)}
     * @return A fully formatted template for the message, leaving a placeholder remaining for the message.
     */
    public Component finalizeTemplate(@NotNull StringsUser sender, @NotNull Channel channel, @NotNull Component format) {
        format = format.replaceText(b -> b
                .matchLiteral("{prefix}")
                .replacement(ComponentConverter.toComponent(sender.getPrefix())));
        format = format.replaceText(b -> b
                .matchLiteral("{suffix}")
                .replacement(ComponentConverter.toComponent(sender.getSuffix())));
        format = format.replaceText(b -> b
                .matchLiteral("{displayname}")
                .replacement(ComponentConverter.toComponent(sender.getDisplayName())));

        return format;
    }

    /**
     * Processes a player's raw message.
     * Depending on permissions, this method can:
     * <ul>
     *     <li>Apply PlaceholderAPI placeholders to the message content</li>
     *     <li>Translate any Bukkit color codes present in the message</li>
     *     <li>Apply emojis</li>
     * </ul>
     * @param sender The message sender
     * @param channel The channel the message is being sent in
     * @param message The raw message sent by the player
     * @return A processed message as a Component.
     */
    @SuppressWarnings("deprecation")
    public Component processMessage(@NotNull StringsUser sender, @NotNull Channel channel, @NotNull String message) {
        String raw = sender.getChatColor() + message;

        if (shouldApplyPlaceholders(sender)) {
            raw = adapter.setPlaceholders(sender, raw);
        }

        if (shouldColorMessage(sender)) {
            raw = adapter.colorHex(raw);
            raw = adapter.translateBukkitColor(raw);
        }

        Component result = ComponentConverter.toComponent(raw);

        if (shouldApplyEmojis(sender)) {
            result = strings.emojiManager().applyEmojis(result);
        }

        return result;
    }

    public Component insertMessage(@NotNull Component base, @NotNull Component message) {
        return base.replaceText(b -> b
                .matchLiteral("{message}")
                .replacement(message)
        );
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

}
