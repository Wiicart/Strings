package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.text.EmojiManager;
import com.pedestriamc.strings.configuration.Configuration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.wiicart.commands.command.CartCommandExecutor;
import net.wiicart.commands.command.CommandData;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class EmojiCommand extends AbstractCommand implements CartCommandExecutor {

    private static final Component FALLBACK = Component.text("Emojis disabled.", NamedTextColor.RED);

    private final Component message;

    public EmojiCommand(@NotNull Strings strings) {
        super(strings);

        Configuration config = strings.getConfiguration();
        if (config.get(Option.Bool.ENABLE_EMOJI_REPLACEMENT)) {
            message = loadMessage(strings);
        } else {
            message = FALLBACK;
        }
    }

    @Override
    public void onCommand(@NotNull CommandData data) {
        CommandSender sender = data.sender();
        if (!hasPermission(sender)) {
            sendMessage(Message.NO_PERMS, sender);
        } else {
            strings().adventure()
                    .sender(sender)
                    .sendMessage(message);
        }
    }

    @NotNull
    private Component loadMessage(@NotNull Strings strings) {
        Configuration config = strings.getConfiguration();

        Component emojis = config.getAsComponent(Option.Text.EMOJI_COMMAND_HEADER)
                .append(Component.text(" "));

        EmojiManager manager = strings.getEmojiManager();
        Map<String, String> map = manager.mappings();
        List<String> textured = config.get(Option.StringList.TEXTURED_EMOJIS);

        for (String emoji : textured) {
            if (emoji == null || emoji.isEmpty()) {
                continue;
            }

            String val = map.get(emoji);
            if (val != null) {
                emojis = emojis.append(emoji(val, emoji));
            }
        }

        return emojis.append(Component.newline()).append(config.getAsComponent(Option.Text.EMOJI_COMMAND_FOOTER));
    }

    @NotNull
    private static Component emoji(@NotNull String emoji, @NotNull String placeholder) {
        return Component.text(emoji, NamedTextColor.WHITE)
                .hoverEvent(HoverEvent.showText(
                        Component.text(placeholder + "\nClick to copy", NamedTextColor.YELLOW))
                ).clickEvent(ClickEvent.copyToClipboard(placeholder));
    }

    private boolean hasPermission(@NotNull CommandSender sender) {
        return Permissions.anyOfOrAdmin(sender,
                "strings.*", "strings.chat.*", "strings.chat.emojis"
        );
    }

}
