package com.pedestriamc.strings.api.event.moderation;

import com.pedestriamc.strings.api.event.strings.StringsEvent;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.chat.SignedMessage.Signature;

public interface MessageDeletionEvent extends StringsEvent {

    SignedMessage getMessage();

    Signature getSignature();
}
