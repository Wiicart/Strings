package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.user.util.UserUtil;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

/**
 * Implements the {@code /mention} command. Allows for toggling mentions.
 * Syntax: {@code /mention <enable | disable>}
 */
public final class MentionCommand implements CommandExecutor {

    private final Messenger messenger;
    private final UserUtil userUtil;

    public MentionCommand(Strings strings) {
        messenger = strings.getMessenger();
        userUtil = strings.users();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("[Strings] This command can only be used by players!");
            return true;
        }

        if(!Permissions.anyOfOrAdmin(player, "strings.*", "strings.mention.*", "strings.mention.toggle")) {
            messenger.sendMessage(NO_PERMS, sender);
            return true;
        }

        User user = userUtil.getUser(player);

        // Handle no args -> toggle on or off based on current status
        if(args.length == 0) {
            if(user.isMentionsEnabled()) {
                disable(player, user);
            } else {
                enable(player, user);
            }
            return true;
        }

        if(args.length == 1) {
            String arg = args[0];
            if(arg.equalsIgnoreCase("enable") || arg.equalsIgnoreCase("on")) {
                enable(player, user);
                return true;
            }
            if(arg.equalsIgnoreCase("disable") || arg.equalsIgnoreCase("off")) {
                disable(player, user);
                return true;
            }
            messenger.sendMessage(INVALID_ARGS, sender);
            return true;
        }

        messenger.sendMessage(TOO_MANY_ARGS, sender);
        return true;
    }

    private void enable(CommandSender sender, @NotNull User user) {
        user.setMentionsEnabled(true);
        userUtil.saveUser(user);
        messenger.sendMessage(MENTIONS_ENABLED, sender);
    }

    private void disable(CommandSender sender, @NotNull User user) {
        user.setMentionsEnabled(false);
        userUtil.saveUser(user);
        messenger.sendMessage(MENTIONS_DISABLED, sender);
    }

}
