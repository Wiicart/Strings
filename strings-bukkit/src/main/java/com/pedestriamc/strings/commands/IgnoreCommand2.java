package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.commands.channel.AbstractChannelCommand;
import com.pedestriamc.strings.user.User;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

// To be implemented
@SuppressWarnings("all")
public class IgnoreCommand2 extends AbstractChannelCommand implements CommandExecutor {

    public IgnoreCommand2(@NotNull Strings strings) {
        super(strings);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (doesNotHavePermission(sender)) {
            return true;
        }

        if (isArgCountIncorrect(sender, args.length)) {
            return true;
        }

        User ignorer = getTarget(sender, args);
        if (ignorer == null) {
            return true;
        }

        Player target;

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

    private User getUser(@NotNull CommandSender sender, @NotNull String name) {
        return null;
    }

    private void alreadyIgnored(@NotNull CommandSender sender, @NotNull User ignorer, @NotNull Player ignored) {

    }
}
