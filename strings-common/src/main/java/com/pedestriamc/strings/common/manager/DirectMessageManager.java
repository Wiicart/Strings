package com.pedestriamc.strings.common.manager;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.event.DirectMessageEvent;
import com.pedestriamc.strings.api.event.strings.EventManager;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.platform.EventFactory;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.user.UserManager;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class DirectMessageManager {

    private final StringsPlatform strings;
    private final PlatformAdapter adapter;

    private final UserManager userManager;
    private final Messenger messenger;
    private final EventFactory factory;
    private final EventManager dispatcher;

    private final Map<UUID, UUID> replyMap = new ConcurrentHashMap<>();

    private final String outgoingFormat;
    private final String receivingFormat;

    public DirectMessageManager(@NotNull StringsPlatform strings) {
        this.strings = strings;
        adapter = strings.getAdapter();
        userManager = strings.users();
        messenger = strings.messenger();
        factory = strings.eventFactory();
        dispatcher = strings.eventManager();

        Settings settings = strings.settings();
        receivingFormat = settings.get(Option.Text.DM_FORMAT_RECEIVING);
        outgoingFormat = settings.get(Option.Text.DM_FORMAT_OUTGOING);
    }

    public void reply(@NotNull StringsUser sender, @NotNull String message) {
        StringsUser recipient;
        UUID uuid = replyMap.get(sender.getUniqueId());
        if (uuid == null) {
            messenger.sendMessage(Message.NO_REPLY, sender);
        } else if (!adapter.isOnline(uuid)) {
            messenger.sendMessage(Message.PLAYER_OFFLINE, sender);
        } else {
            recipient = userManager.getUser(uuid);
            sendMessage(sender, recipient, message);
        }
    }

    public void sendMessage(@NotNull final StringsUser sender, @NotNull final StringsUser recipient, @NotNull final String message) {
        strings.sync(() -> {
            DirectMessageEvent event = factory.directMessage(sender, recipient, message);
            dispatcher.dispatch(event);
            if (event.isCancelled()) {
                return;
            }

            String finalMessage = event.getMessage();
            // PlaceholderAPI integrations may read Player state. Evaluate each
            // variant on the corresponding entity scheduler, then continue the
            // delivery once both values are ready.
            CompletableFuture<String> outgoing = onUser(sender,
                    () -> generateOutgoingString(sender, recipient, finalMessage));
            CompletableFuture<String> receiving = onUser(recipient,
                    () -> generateReceivingString(sender, recipient, finalMessage));

            outgoing.thenCombine(receiving, (out, in) -> new String[]{out, in})
                    .thenAccept(messages -> {
                        sender.sendMessage(messages[0]);
                        if (!recipient.isIgnoring(sender)) {
                            recipient.sendMessage(messages[1]);
                        }
                        replyMap.put(sender.getUniqueId(), recipient.getUniqueId());
                    });
        });
    }

    private CompletableFuture<String> onUser(@NotNull StringsUser user, @NotNull Supplier<String> work) {
        CompletableFuture<String> future = new CompletableFuture<>();
        strings.sync(user, () -> {
            try {
                future.complete(work.get());
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });
        return future;
    }

    private String generateOutgoingString(StringsUser sender, StringsUser recipient, String message) {
        String outgoing = applyStringsPlaceholders(sender, recipient, outgoingFormat, message);
        outgoing = adapter.applyPlaceholders(recipient, outgoing);
        return adapter.translateBukkitColor(outgoing);
    }

    private String generateReceivingString(StringsUser sender, StringsUser recipient, String message) {
        String receiving = applyStringsPlaceholders(sender, recipient, receivingFormat, message);
        receiving = adapter.applyPlaceholders(sender, receiving);
        return adapter.translateBukkitColor(receiving);
    }

    public String applyStringsPlaceholders(StringsUser sender, StringsUser recipient, String format, String message) {
        return format
                .replace("{sender_username}", sender.getName())
                .replace("{sender_displayname}", sender.getDisplayName())
                .replace("{sender_prefix}", sender.getPrefix())
                .replace("{sender_suffix}", sender.getSuffix())
                .replace("{recipient_username}", recipient.getName())
                .replace("{recipient_displayname}", recipient.getDisplayName())
                .replace("{recipient_prefix}", recipient.getPrefix())
                .replace("{recipient_suffix}", recipient.getSuffix())
                .replace("{message}", message);
    }

}
