package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.commands.AbstractCommand;
import com.pedestriamc.strings.user.User;
import net.wiicart.commands.command.CartCommandExecutor;
import net.wiicart.commands.command.CommandData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.pedestriamc.strings.api.message.Message.*;

// Provides the sender's current Channel
public class CurrentCommand extends AbstractCommand implements CartCommandExecutor {

    CurrentCommand(@NotNull Strings strings) {
        super(strings);
    }

    @Override
    public void onCommand(@NotNull CommandData data) {
        CommandSender sender = data.sender();
        String[] args = data.args();
        if (checkArgCountAndNotify(sender, args)) {
            return;
        }

        User target = getTargetAndNotify(sender, args);
        if (target == null) {
            return;
        }

        String channelName = target.getActiveChannel().getName();
        Map<String, String> placeholders = Map.of(
                "{channel}", channelName,
                "{player}", target.getName()
        );

        if (target.player().equals(sender)) {
            sendMessage(CURRENT_CHANNEL, placeholders, sender);
        } else {
            sendMessage(CURRENT_CHANNEL_OTHER, placeholders, sender);
        }
    }

    private boolean checkArgCountAndNotify(CommandSender sender, String[] args) {
        return switch (args.length) {
            case 0 -> {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Console must define a player to use this command on.");
                    yield true;
                }
                yield false;
            }
            case 1 -> false;
            default -> {
                sendMessage(Message.TOO_MANY_ARGS, sender);
                yield true;
            }
        };
    }

    private User getTargetAndNotify(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return getUser((Player) sender);
        }

        String username = args[0];
        User target = getUser(username);
        if (target == null) {
            sendMessage(INVALID_PLAYER, Map.of("{player}", username), sender);
            return null;
        }

        return target;
    }

}
