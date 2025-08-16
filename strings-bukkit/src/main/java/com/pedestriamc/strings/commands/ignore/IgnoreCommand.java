package com.pedestriamc.strings.commands.ignore;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.user.User;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class IgnoreCommand extends AbstractIgnoreCommand implements CommandExecutor {

    public IgnoreCommand(@NotNull Strings strings) {
        super(strings);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (doesNotHavePermission(sender)) {
            return true;
        }

        if (checkArgCountAndNotify(sender, args.length)) {
            return true;
        }

        // getTarget only useful for getting the player that will ignore another player
        User ignorer = getTarget(sender, args);
        if (ignorer == null) {
            return true;
        }

        User target = getIgnoreTarget(sender, args);
        if (target == null) {
            return true;
        }

        if (attemptingToIgnoreSelf(sender, ignorer, target)) {
            return true;
        }

        if (alreadyIgnored(sender, ignorer, target)) {
            return true;
        }

        if (cannotIgnore(sender, target)) {
            return true;
        }

        ignorer.ignore(target);
        saveUser(ignorer);

        Map<String, String> placeholders = Map.of(
                "{player}", ignorer.getName(),
                "{target}", target.getName()
        );

        if (!sender.equals(ignorer.player())) {
            sendMessage(Message.PLAYER_IGNORED_OTHER, placeholders, sender);
        }
        messenger().sendMessage(Message.PLAYER_IGNORED, ignorer, placeholders);

        return true;
    }

    private boolean doesNotHavePermission(@NotNull CommandSender sender) {
        if (!Permissions.anyOfOrAdmin(
                sender,
                "strings.*",
                "strings.chat.*",
                "strings.chat.ignore"
        )) {
            sendMessage(Message.NO_PERMS, sender);
            return true;
        } else {
            return false;
        }
    }

    private boolean alreadyIgnored(@NotNull CommandSender sender, @NotNull User ignorer, @NotNull User ignored) {
        if (ignorer.isIgnoring(ignored)) {
            sendMessage(Message.ALREADY_IGNORED, Map.of("{player}", ignored.getName()), sender);
            return true;
        }

        return false;
    }

    private boolean attemptingToIgnoreSelf(@NotNull CommandSender sender, @NotNull User ignorer, @NotNull User target) {
        if (ignorer.equals(target)) {
            if (sender.equals(ignorer.player())) {
                sendMessage(Message.CANT_IGNORE_SELF, sender);
            } else {
                sendMessage(Message.CANT_IGNORE_SELF_OTHER, sender);
            }
            return true;
        }

        return false;
    }

    private boolean cannotIgnore(@NotNull CommandSender sender, @NotNull User target) {
        if (Permissions.anyOfOrAdmin(
                sender,
                "strings.*",
                "strings.chat.*",
                "strings.chat.bypass-unignorable"
        )) {
            return false;
        }

        boolean ignorable = !Permissions.anyOfOrAdmin(
                target.player(),
                "strings.*",
                "strings.chat.*",
                "strings.chat.unignorable"
        );

        if (ignorable) {
            return false;
        } else {
            sendMessage(Message.CANT_IGNORE, Map.of("{player}", target.getName()), sender);
            return true;
        }
    }

}
