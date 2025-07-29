package com.pedestriamc.strings.directmessage;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.api.event.moderation.PlayerDirectMessageEvent;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.user.util.UserUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDirectMessenger {

    private final @NotNull Strings strings;
    private final @NotNull UserUtil userUtil;
    private final @NotNull Messenger messenger;

    private final @NotNull Map<Player, Player> replyList = new ConcurrentHashMap<>();

    private final @NotNull String messageFormatSender;
    private final @NotNull String messageFormatRecipient;

    private final boolean usePAPI;

    public PlayerDirectMessenger(@NotNull Strings strings) {
        this.strings = strings;
        this.userUtil = strings.users();
        Configuration config = strings.getConfiguration();
        this.messageFormatSender = config.getString(Option.Text.DIRECT_MESSAGE_FORMAT_OUT);
        this.messageFormatRecipient = config.getString(Option.Text.DIRECT_MESSAGE_FORMAT_IN);
        this.usePAPI = strings.isUsingPlaceholderAPI();
        this.messenger = strings.getMessenger();

    }

    public void reply(@NotNull Player sender, @NotNull String message) {
        Player recipient = replyList.get(sender);
        if(recipient == null) {
            messenger.sendMessage(Message.NO_REPLY, sender);
            return;
        }
        if(!recipient.isOnline()) {
            messenger.sendMessage(Message.PLAYER_OFFLINE, sender);
            return;
        }
        sendMessage(sender, replyList.get(sender), message);
    }

    public void sendMessage(@NotNull Player sender, @NotNull Player recipient, @NotNull String message) {
        String senderString = processPlaceholders(sender, recipient, messageFormatSender)
                .replace("{message}", message);
        String recipientString = processPlaceholders(sender, recipient, messageFormatRecipient)
                .replace("{message}", message);

        if(usePAPI) {
            try {
                senderString = PlaceholderAPI.setPlaceholders(recipient, senderString);
                recipientString = PlaceholderAPI.setPlaceholders(sender, recipientString);
            } catch(Exception ex) {
                strings.warning("An error occurred while handling a /msg command.");
                strings.warning(ex.getMessage());
            }
        }

        senderString = color(senderString);
        recipientString = color(recipientString);

        boolean ignored = isRecipientIgnoring(sender, recipient);

        PlayerDirectMessageEvent event = new PlayerDirectMessageEvent(sender, recipient, message, ignored);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            sender.sendMessage(senderString);
            replyList.put(recipient, sender);

            if(!ignored) {
                recipient.sendMessage(recipientString);
            }
        }
    }

    public String processPlaceholders(@NotNull Player sender, @NotNull Player recipient, @NotNull String message) {
        User senderUser = userUtil.getUser(sender);
        User recipientUser = userUtil.getUser(recipient);
        message = message
                .replace("{sender_username}", sender.getName())
                .replace("{sender_displayname}", sender.getDisplayName())
                .replace("{sender_prefix}", senderUser.getPrefix())
                .replace("{sender_suffix}", senderUser.getSuffix())
                .replace("{recipient_username}", recipient.getName())
                .replace("{recipient_displayname}", recipient.getDisplayName())
                .replace("{recipient_prefix}", recipientUser.getPrefix())
                .replace("{recipient_suffix}", recipientUser.getSuffix());
        return message;
    }

    private @NotNull String color(@NotNull String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    // Checks if the target has ignored the message sender
    private boolean isRecipientIgnoring(@NotNull Player sender, @NotNull Player recipient) {
        return userUtil.getUser(recipient).getIgnoredPlayers().contains(sender.getUniqueId());
    }
}
