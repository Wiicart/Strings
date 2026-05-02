package com.pedestriamc.strings.common.chat;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.managers.Mentioner;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.util.PermissionChecker;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings("PatternValidation")
public class StringsMentioner implements Mentioner {

    private static final Pattern AT_PATTERN = Pattern.compile("@([A-Za-z0-9_]+)");

    /**
     * To be applied solely for the users being mentioned.
     */
    private final Style activeMentionStyle;

    /**
     * To be applied if a mention is valid, but not mentioning the specific player.
     */
    private final Style neutralMentionStyle;

    private final PlatformAdapter adapter;
    private final Component actionBarFormat;
    private final Component atEveryone;
    private final @Nullable Sound sound;

    /**
     * Provides a new MentionProcessor.
     * @param sender The message sender
     * @param message The message
     * @param audience Any potential person who could be mentioned.
     * @return A new MentionProcessor
     */
    @Contract("_, _, _ -> new")
    @Override
    public @NotNull ChatProcessor processor(
            StringsUser sender,
            Component message,
            Set<StringsUser> audience
    ) {
        if (!hasMentionPermission(sender)) {
            return new StaticStringsChatProcessor(message);
        } else {
            return new StringsChatProcessor(sender, message, audience);
        }
    }

    public StringsMentioner(@NotNull StringsPlatform strings) {
        adapter = strings.getAdapter();

        Settings settings = strings.settings();
        actionBarFormat = settings.getComponent(Option.Text.MENTION_TEXT_ACTION_BAR);
        sound = loadSound(settings);

        activeMentionStyle = loadMentionStyle(
                settings, Option.Text.MENTION_COLOR, Option.StringList.MENTION_TEXT_DECORATIONS
        );
        neutralMentionStyle = loadMentionStyle(
                settings, Option.Text.MENTION_COLOR_OTHER, Option.StringList.MENTION_TEXT_DECORATIONS_OTHER
        );

        atEveryone = Component.text("@everyone").style(activeMentionStyle);
    }

    @Nullable
    private Sound loadSound(@NotNull Settings settings) {
        Sound temp = null;
        float volume = settings.getFloat(Option.Double.MENTION_VOLUME);
        float pitch = settings.getFloat(Option.Double.MENTION_PITCH);
        String soundName = settings.get(Option.Text.MENTION_SOUND);
        try {
            temp = Sound.sound(Key.key(soundName), Sound.Source.MASTER, volume, pitch);
        } catch (Exception ignored) {}

        return temp;
    }

    @NotNull
    private Style loadMentionStyle(@NotNull Settings settings, Option.Text color, Option.StringList decorationList) {
        return Style.style(builder -> {
            TextColor mentionColor = adapter.parseColor(settings.get(color));
            if (mentionColor != null) {
                builder.color(mentionColor);
            }

            List<String> decorations = settings.get(decorationList);
            for (String decoration : decorations) {
                try {
                    TextDecoration textDecoration = TextDecoration.valueOf(decoration);
                    builder.decorate(textDecoration);
                } catch (Exception ignored) {}
            }
        });
    }

    @Override
    public boolean hasMentionPermission(@NotNull StringsUser user) {
        return PermissionChecker.anyOrOp(
                user,
                "strings.*",
                "strings.mention",
                "strings.mention.*",
                "strings.mention.all"
        );
    }

    @Override
    public boolean hasMentionEveryonePermission(@NotNull StringsUser user) {
        return PermissionChecker.anyOrOp(
                user,
                "strings.*",
                "strings.mention.*",
                "strings.mention.all"
        );
    }

    @Override
    public boolean hasGroupMentionPermission(@NotNull StringsUser user) {
        return PermissionChecker.anyOrOp(
                user,
                "strings.*",
                "strings.mention.*",
                "strings.mention.group"
        );
    }

    @Override
    public void mention(@NotNull StringsUser recipient, @NotNull StringsUser sender) {
        if (!recipient.hasMentionsEnabled()) {
            return;
        }

        Component message = actionBarFormat.replaceText(config -> config
                        .matchLiteral("%sender%")
                        .replacement(sender.getName())
        );

        sendActionBarAndSound(recipient, message);
    }

    @Override
    public void mention(@NotNull Set<StringsUser> recipients, @NotNull StringsUser sender) {
        for (StringsUser recipient : recipients) {
            mention(recipient, sender);
        }
    }

    private void sendActionBarAndSound(@NotNull StringsUser recipient, @NotNull Component message) {
        Audience audience = recipient.audience();
        audience.sendActionBar(message);

        if (sound != null) {
            audience.playSound(sound);
        }
    }

    @Override
    public @NotNull Component processMentions(@NotNull StringsUser sender, @NotNull Component message) {
        return message.replaceText(config -> {
            config.match(AT_PATTERN);
            config.replacement((result, builder) -> {
                String mentionTarget = result.group(1);
                if (mentionTarget.equals("everyone") && canMentionEveryone(sender)) {
                    return Component.text("@everyone", activeMentionStyle);
                }

                StringsUser recipient = adapter.getUser(mentionTarget);
                if (recipient != null) {
                    return Component.text("@" + recipient.getName(), activeMentionStyle);
                }

                return builder.content(result.group()).build();
            });
        });
    }

    private boolean canMentionEveryone(@NotNull StringsUser user) {
        return PermissionChecker.anyOrOp(user, "strings.*", "strings.mention.*", "strings.mention.all");
    }

    /**
     * More efficient solution for handling stylization of chat mentions.
     */
    public class StringsChatProcessor implements ChatProcessor {

        private final Map<StringsUser, Component> processedMentions;
        private final Set<StringsUser> mentionedUsers;

        private StringsChatProcessor(StringsUser sender, Component message, Set<StringsUser> audience) {
            processedMentions = new HashMap<>();
            mentionedUsers = new HashSet<>();
            Component base = message;

            Map<String, StringsUser> targets = new HashMap<>();
            for (StringsUser user : audience) {
                targets.put(user.getName().toLowerCase(Locale.ROOT), user);
            }

            if (canMentionEveryone(sender)) {
                base = base.replaceText(config -> config
                        .matchLiteral("@everyone")
                        .replacement(b -> {
                            mentionedUsers.addAll(audience);
                            return atEveryone;
                        })
                );
            }

            base = base.replaceText(config -> {
                config.match(AT_PATTERN);
                config.replacement((result, builder) -> {
                    String mentionTarget = result.group(1).toLowerCase(Locale.ROOT);
                    if (targets.containsKey(mentionTarget)) {
                        mentionedUsers.add(targets.get(mentionTarget));
                        return Component.text("@" + mentionTarget, neutralMentionStyle);
                    } else {
                        return Component.text("@" + mentionTarget);
                    }
                });
            });

            for (StringsUser user : audience) {
                Component result = base.replaceText(config -> {
                    String raw = "@" + user.getName();
                    config.matchLiteral(raw);
                    config.replacement(Component.text(raw).style(activeMentionStyle));
                });

                processedMentions.put(user, result);
            }

            mentionedUsers.remove(sender);
        }

        /**
         * Provides the message with mentions handled for specifically one user.<br/>
         * REQUIRES <code>recipient</code> is a member of the Set <code>audience</code> originally passed in.
         * @param recipient The message target.
         * @return A processed Component.
         */
        @Override
        public @NotNull Component processMentions(@NotNull StringsUser recipient) {
            return processedMentions.get(recipient);
        }

        /**
         * Provides a Set of all users that should be mentioned.
         * For simplicity, one can call {@link StringsMentioner#mention(Set, StringsUser)} after processing.
         * @return A Set.
         */
        @Override
        public @NotNull Set<StringsUser> mentionedUsers() {
            return mentionedUsers;
        }

    }

    /**
     * Mention processor that always returns the original message.
     * Appropriate for use when the sender has no mention permissions.
     */
    static class StaticStringsChatProcessor implements ChatProcessor {

        private final Component message;

        StaticStringsChatProcessor(Component message) {
            this.message = message;
        }

        @Override
        public @NotNull Component processMentions(@NotNull StringsUser recipient) {
            return message;
        }

        @Override
        public @NotNull Set<StringsUser> mentionedUsers() {
            return Set.of();
        }

    }
}
