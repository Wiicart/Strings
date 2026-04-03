package com.pedestriamc.strings.chat.paper;

import com.pedestriamc.strings.common.util.PermissionChecker;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.annotation.Platform;
import com.pedestriamc.strings.event.BukkitMessageDeletionEvent;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Class to create Components containing a deletion button as defined in config.yml<br/>
 * Requires a Paper server.
 */
@Platform.Paper
public class DeletionManager {

    private final Strings strings;
    private final Component deletionButton;

    public DeletionManager(@NotNull Strings strings) {
        this.strings = strings;

        Settings settings = strings.settings();
        String buttonFormat = settings.get(Option.Text.DELETION_BUTTON_FORMAT);
        String buttonHover = settings.get(Option.Text.DELETION_BUTTON_HOVER);

        Component component = MiniMessage.miniMessage().deserialize(buttonFormat);
        deletionButton = component.hoverEvent(MiniMessage.miniMessage().deserialize(buttonHover));
    }

    /**
     * Creates a deletion button as defined in configuration files.
     * @param message The SignedMessage to create a deletion button for.
     * @return A {@link Component} containing text that when pressed, deletes the original message.
     */
    public Component createDeleteButton(@NotNull SignedMessage message) {
        return deletionButton.clickEvent(ClickEvent.callback(
                audience -> {
                    ((Audience) strings.getServer()).deleteMessage(message);
                    dispatchDeleteEvent(message);
                })
        );
    }

    public boolean hasDeletionPermission(@NotNull StringsUser source, @NotNull StringsUser subject) {
        if (source.equals(subject)) {
            return PermissionChecker.anyOrOp(source,
                    "strings.*",
                    "strings.chat.*",
                    "strings.chat.delete-messages"
            );
        } else {
            return PermissionChecker.anyOrOp(subject,
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
                .callEvent(new BukkitMessageDeletionEvent(message))
        );
    }
}
