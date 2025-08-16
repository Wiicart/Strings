package com.pedestriamc.strings.commands.ignore;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.commands.AbstractCommand;
import com.pedestriamc.strings.user.User;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.pedestriamc.strings.api.message.Message.INVALID_PLAYER;
import static com.pedestriamc.strings.api.message.Message.NO_PERMS_MODIFY_OTHER;

abstract class AbstractIgnoreCommand extends AbstractCommand {

    protected AbstractIgnoreCommand(@NotNull Strings strings) {
        super(strings);
    }

    @Nullable
    protected User getIgnoreTarget(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            strings().getLogger().warning("Getting ignore target failed");
            return null;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sendMessage(Message.UNKNOWN_PLAYER, Map.of("{player}", targetName), sender);
            return null;
        }

        return getUser(target);
    }

    protected boolean checkArgCountAndNotify(@NotNull CommandSender sender, int length) {
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
                if (Permissions.anyOfOrAdmin(
                        sender,
                        "strings.*",
                        "strings.chat.*",
                        "strings.chat.ignore.*",
                        "strings.chat.ignore.other"
                )) {
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

    protected @Nullable User getTarget(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player playerSender) {
                return getUser(playerSender);
            } else {
                throw new ClassCastException("If args length is 1, the sender must be a player.");
            }
        } else if (args.length == 2) {
            String playerName = args[0];
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
}
