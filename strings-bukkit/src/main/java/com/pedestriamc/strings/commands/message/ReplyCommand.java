package com.pedestriamc.strings.commands.message;

import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.user.UserManager;
import com.pedestriamc.strings.common.manager.DirectMessageManager;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.bukkit.BukkitMessenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

public final class ReplyCommand implements CommandExecutor {

    private final DirectMessageManager manager;
    private final BukkitMessenger messenger;
    private final UserManager userManager;

    public ReplyCommand(@NotNull Strings strings) {
        manager = strings.getDirectMessageManager();
        messenger = strings.messenger();
        userManager = strings.users();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            messenger.sendMessage(INSUFFICIENT_ARGS, sender);
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("[Strings] This command can only be used by players!");
            return true;
        }

        if (!(sender.hasPermission(
                "strings.chat.msg") ||
                sender.hasPermission("strings.chat.*") ||
                sender.hasPermission("strings.*")
        )) {
            messenger.sendMessage(NO_PERMS, sender);
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg);
            message.append(" ");
        }

        StringsUser user = userManager.getUser(player.getUniqueId());
        manager.reply(user, message.toString().trim());

        return true;

    }


}
