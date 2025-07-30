package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.channel.HelpOPChannel;
import com.pedestriamc.strings.commands.AbstractCommand;
import com.pedestriamc.strings.user.User;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.pedestriamc.strings.api.message.Message.*;

abstract class AbstractChannelCommand extends AbstractCommand {

    protected AbstractChannelCommand(@NotNull Strings strings) {
        super(strings);
    }

    // Returns a Channel if it exists, it allows the sender and is not protected.
    // Sends the appropriate message to the sender if not valid.
    protected @Nullable Channel getChannel(@NotNull CommandSender sender, @NotNull String channelName) {
        Channel channel = getChannel(channelName);

        if (channel == null) {
            sendMessage(UNKNOWN_CHANNEL, Map.of("{channel}", channelName), sender);
            return null;
        }

        if(channel.getType() == Type.PROTECTED && !(channel instanceof HelpOPChannel)) {
            sendMessage(PROTECTED_CHANNEL_UNSUPPORTED_OPERATION, sender);
            return null;
        }

        return channel;
    }

    protected @Nullable Channel getChannelAndCheckAllows(@NotNull CommandSender sender, @NotNull String channelName) {
        Channel channel = getChannel(sender, channelName);
        if (channel != null && !channel.allows(sender)) {
                sendMessage(NO_PERMS_CHANNEL, Map.of("{channel}", channel.getName()), sender);
                return null;
        }

        return channel;
    }

    protected @Nullable Monitorable getMonitorable(@NotNull CommandSender sender, @NotNull Channel channel) {
        if (channel instanceof Monitorable monitorable) {
            return monitorable;
        } else {
            sendMessage(NOT_MONITORABLE, Map.of("{channel}", channel.getName()), sender);
            return null;
        }
    }

    /*
    / Takes a String array, which should have a length of 1 or 2.
    / If the length is 2, returns the Player defined in args[1], if they exist, otherwise the senders User.
    / * THE COMMANDSENDER MUST BE A PLAYER *
    */
    protected @Nullable User getTarget(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player playerSender) {
                return getUser(playerSender);
            } else {
                throw new ClassCastException("If args length is 1, the sender must be a player.");
            }
        } else if (args.length == 2) {
            String playerName = args[1];
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                sendMessage(INVALID_PLAYER, Map.of("{player}", playerName), sender);
                return null;
            } else {
                return getUser(player);
            }
        }

        return null;
    }

    /*
    / Checks arg counts for commands like /channel join and leave which accept a Channel and optionally a Player.
    / Checks for correct arg counts, factoring in if the sender is a Player or not
    / Also checks for permissions to modify others if args length is 2
    */
    protected boolean isArgCountIncorrect(@NotNull CommandSender sender, int length) {
        return switch (length) {
            case 0 -> {
                sendMessage(Message.INSUFFICIENT_ARGS, sender);
                yield true;
            }

            case 1 -> {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("[Strings] You must define a player to use this command on.");
                    yield true;
                } else {
                    yield false;
                }
            }

            case 2 -> {
                if(Permissions.anyOfOrAdmin(
                        sender,
                        "strings.*",
                        "strings.channels.*",
                        "strings.channels.modifyplayers"
                )
                ) {
                    yield false;
                } else {
                    sendMessage(NO_PERMS_MODIFY_OTHER, sender);
                    yield true;
                }
            }
            default -> {
                sendMessage(Message.TOO_MANY_ARGS, sender);
                yield true;
            }
        };
    }

    protected Map<String, String> getPlaceholders(@NotNull StringsUser user, @NotNull Channel channel) {
        return Map.of(
                "{channel}", channel.getName(),
                "{player}", user.getName()
        );
    }

    // Checks if the param set is true. If so, the appropriate message is sent to the sender.
    protected boolean checkAlreadySet(
            boolean set,
            @NotNull CommandSender sender,
            @NotNull StringsUser target,
            @NotNull Channel channel,
            @NotNull Message single, // Sent when the command is used on the Player that sent it
            @NotNull Message other // Sent when the command is used on a Player other than the CommandSender
    ) {
        if (set) {
            Map<String, String> placeholders = getPlaceholders(target, channel);
            if (sender.equals(User.playerOf(target))) {
                sendMessage(single, placeholders, sender);
            } else {
                sendMessage(other, sender);
            }

            return true;
        } else {
            return false;
        }
    }

    // Sends an update message to the target, and one to the sender, if different from the target.
    protected void sendFinalMessages(
            @NotNull CommandSender sender,
            @NotNull User target,
            @NotNull Channel channel,
            @NotNull Message targetMsg,
            @NotNull Message otherMsg
    ) {
        Map<String, String> placeholders = getPlaceholders(target, channel);
        Player targetPlayer = target.player();
        if (!sender.equals(targetPlayer)) {
            sendMessage(otherMsg, placeholders, sender);
        }
        sendMessage(targetMsg, placeholders, targetPlayer);
    }
}
