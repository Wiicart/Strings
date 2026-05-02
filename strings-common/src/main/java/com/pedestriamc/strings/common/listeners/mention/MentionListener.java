package com.pedestriamc.strings.common.listeners.mention;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.event.strings.Listener;
import com.pedestriamc.strings.api.managers.Mentioner;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

/**
 * ChatListener now handles this, being kept for reference.
 */
@SuppressWarnings("unused")
public class MentionListener {

    private final Mentioner mentioner;

    public MentionListener(@NotNull StringsPlatform strings) {
        this.mentioner = strings.mentioner();
    }

    protected Mentioner mentioner() {
        return mentioner;
    }

    @Listener
    void onEvent(@NotNull ChannelChatEvent event) {
        String message = event.getMessage();
        StringsUser sender = event.getSender();

        if (!mentioner.hasMentionPermission(sender)) {
            return;
        }

        for (StringsUser recipient : event.getMessageRecipients()) {
            if (message.contains("@" + recipient.getName())) {
                mentioner.mention(recipient, sender);
            }
        }

        if (mentioner.hasMentionEveryonePermission(sender)) {
            mentioner.mention(event.getMessageRecipients(), sender);
        }
    }

}
