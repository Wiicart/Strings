package com.pedestriamc.strings.chat.paper;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.annotation.Platform;
import com.pedestriamc.strings.api.event.MessageDeletionEvent;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Platform.Paper
public class DeletionManager {

    private final Strings strings;
    private final Component deletionButton;

    public DeletionManager(@NotNull Strings strings) {
        this.strings = strings;

        Settings settings = strings.getConfiguration();
        String buttonFormat = settings.getString(Option.Text.DELETION_BUTTON_FORMAT);
        String buttonHover = settings.getString(Option.Text.DELETION_BUTTON_HOVER);

        Component component = MiniMessage.miniMessage().deserialize(buttonFormat);
        deletionButton = component.hoverEvent(MiniMessage.miniMessage().deserialize(buttonHover));
    }

    public Component getDeletionButton(@NotNull SignedMessage message) {
        return deletionButton.clickEvent(ClickEvent.callback(
                audience -> {
                    ((Audience) strings.getServer()).deleteMessage(message);
                    dispatchDeleteEvent(message);
                })
        );
    }

    public boolean hasDeletionPermission(@NotNull Player source, @NotNull Player subject) {
        if (source.equals(subject)) {
            return Permissions.anyOfOrAdmin(source,
                    "strings.*",
                    "strings.chat.*",
                    "strings.chat.delete-messages"
            );
        } else {
            return Permissions.anyOfOrAdmin(subject,
                    "strings.*",
                    "strings.chat.*",
                    "strings.chat.delete-messages.*",
                    "strings.chat.delete-messages.others"
            );
        }
    }

    private void dispatchDeleteEvent(@NotNull SignedMessage message) {
        strings.sync(() -> strings
                .getServer()
                .getPluginManager()
                .callEvent(new MessageDeletionEvent(message))
        );
    }
}
