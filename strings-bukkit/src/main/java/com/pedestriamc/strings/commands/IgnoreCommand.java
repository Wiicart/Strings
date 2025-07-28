package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.utlity.Permissions;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class IgnoreCommand implements CommandExecutor {

    private final UserUtil userUtil;
    private final Messenger messenger;

    public IgnoreCommand(@NotNull Strings strings) {
        userUtil = strings.users();
        messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!Permissions.anyOfOrAdmin(
                sender,
                "strings.*",
                "strings.chat.*",
                "strings.chat.ignore"
        )) {
            messenger.sendMessage(Message.NO_PERMS, sender);
            return true;
        }

        switch (args.length) {
            case 0 -> messenger.sendMessage(Message.INSUFFICIENT_ARGS, sender);
            case 1 -> oneArg(sender, args);
            case 2 -> twoArgs(sender, args);
            default -> messenger.sendMessage(Message.TOO_MANY_ARGS, sender);
        }
        return true;
    }

    private void oneArg(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Console cannot use /ignore with one Player.");
            sender.sendMessage("Console format: /ignore <player-to-ignore> <player-ignoring>");
            return;
        }

        String name = args[0];
        Player target = Bukkit.getPlayer(name);
        if(target == null) {
            messenger.sendMessage(Message.UNKNOWN_PLAYER, generatePlaceholders(name), sender);
            return;
        }

        if (player.equals(target)){
            messenger.sendMessage(Message.CANT_IGNORE_SELF, sender);
            return;
        }

        if(isNotIgnorable(target)) {
            messenger.sendMessage(Message.CANT_IGNORE, generatePlaceholders(name), sender);
            return;
        }

        User user = userUtil.getUser(player);
        User targetUser = userUtil.getUser(target);

        user.ignore(targetUser);
        userUtil.saveUser(user);

        messenger.sendMessage(Message.PLAYER_IGNORED, generatePlaceholders(name), sender);
    }

    private void twoArgs(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!Permissions.anyOfOrAdmin(sender, " strings.chat.ignore.other")) {
            messenger.sendMessage(Message.NO_PERMS, sender);
            return;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            messenger.sendMessage(Message.UNKNOWN_PLAYER, generatePlaceholders(targetName), sender);
            return;
        } else if (isNotIgnorable(target)) {
            messenger.sendMessage(Message.CANT_IGNORE, generatePlaceholders(targetName), sender);
            return;
        }

        String ignorerName = args[1];
        Player ignorer = Bukkit.getPlayer(ignorerName);
        if (ignorer == null) {
            messenger.sendMessage(Message.UNKNOWN_PLAYER, generatePlaceholders(ignorerName), sender);
        }

        if (target.equals(ignorer)) {
            messenger.sendMessage(Message.CANT_IGNORE_SELF_OTHER, sender);
            return;
        }

        User user = userUtil.getUser(ignorer);
        User targetUser = userUtil.getUser(target);

        user.ignore(targetUser);
        userUtil.saveUser(user);

        messenger.sendMessage(
                Message.PLAYER_IGNORED_OTHER,
                Map.of("{player}", ignorerName,
                        "{ignored}", targetName),
                sender
        );
    }

    private @NotNull Map<String, String> generatePlaceholders(@NotNull String ignored) {
        return Map.of("{player}", ignored);
    }

    private boolean isNotIgnorable(@NotNull Player player) {
        return !Permissions.anyOfOrAdmin(
                player,
                "strings.*",
                "strings.chat.*",
                "strings.chat.unignorable"
        );
    }

}
