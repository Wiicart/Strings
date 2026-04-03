package com.pedestriamc.strings.common.manager;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings("PatternValidation")
public class StringsMentioner implements Mentioner {

    private static final Pattern AT_PATTERN = Pattern.compile("@([A-Za-z0-9_]+)");

    private final PlatformAdapter adapter;
    private final Style mentionStyle;
    private final Component format;
    private final @Nullable Sound sound;

    public StringsMentioner(@NotNull StringsPlatform strings) {
        adapter = strings.getAdapter();

        Settings settings = strings.settings();
        format = settings.getComponent(Option.Text.BROADCAST_FORMAT);
        sound = loadSound(settings);
        mentionStyle = loadMentionStyle(settings);
    }

    @Nullable
    private Sound loadSound(@NotNull Settings settings) {
        Sound temp = null;
        float volume = settings.getFloat(Option.Double.BROADCAST_SOUND_VOLUME);
        float pitch = settings.getFloat(Option.Double.BROADCAST_SOUND_PITCH);
        String soundName = settings.get(Option.Text.MENTION_SOUND);
        try {
            temp = Sound.sound(Key.key(soundName), Sound.Source.MASTER, volume, pitch);
        } catch (Exception ignored) {}

        return temp;
    }

    @NotNull
    private Style loadMentionStyle(@NotNull Settings settings) {
        return Style.style(builder -> {
            TextColor mentionColor = adapter.parseColor(settings.get(Option.Text.MENTION_COLOR));
            if (mentionColor != null) {
                builder.color(mentionColor);
            }

            List<String> decorations = settings.get(Option.StringList.MENTION_TEXT_DECORATIONS);
            for (String decoration : decorations) {
                try {
                    TextDecoration textDecoration = TextDecoration.valueOf(decoration);
                    builder.decorate(textDecoration);
                } catch (Exception ignored) {}
            }
        });
    }

    @Override
    public boolean hasPermission(@NotNull StringsUser user) {
        return PermissionChecker.anyOrOp(
                user,
                "strings.*",
                "strings.mention",
                "strings.mention.*",
                "strings.mention.all"
        );
    }

    @Override
    public void mention(@NotNull StringsUser recipient, @NotNull StringsUser sender) {
        if (!recipient.hasMentionsEnabled()) {
            return;
        }

        Component message = format.replaceText(config -> config
                        .matchLiteral("%sender%")
                        .matchLiteral("{sender}")
                        .replacement(sender.getName())
        );

        sendMention(recipient, message);
    }

    @Override
    public void mention(@NotNull Set<StringsUser> recipients, @NotNull StringsUser sender) {
        for (StringsUser recipient : recipients) {
            mention(recipient, sender);
        }
    }

    private void sendMention(@NotNull StringsUser recipient, @NotNull Component message) {
        Audience audience = recipient.audience();
        audience.sendActionBar(message);
        if (sound != null) {
            audience.playSound(sound);
        }
    }

    @Override
    public Component processMentions(@NotNull StringsUser sender, @NotNull Component message) {
        return message.replaceText(config -> {
            config.match(AT_PATTERN);
            config.replacement((result, builder) -> {
                String mentionTarget = result.group(1);
                if (mentionTarget.equals("everyone") && canMentionEveryone(sender)) {
                    return Component.text("@everyone", mentionStyle);
                }

                StringsUser recipient = adapter.getUser(mentionTarget);
                if (recipient != null) {
                    return Component.text("@" + recipient.getName(), mentionStyle);
                }

                return builder.content(result.group()).build();
            });
        });
    }

    private boolean canMentionEveryone(@NotNull StringsUser user) {
        return PermissionChecker.anyOrOp(user, "strings.*", "strings.mention.*", "strings.mention.all");
    }

}
