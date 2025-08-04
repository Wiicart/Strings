package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.impl.BukkitMessenger;
import net.wiicart.commands.permission.Permissions;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

public final class ClearChatCommand implements CommandExecutor {

    private final @NotNull BukkitMessenger messenger;

    public ClearChatCommand(@NotNull Strings strings) {
        messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(!Permissions.anyOfOrAdmin(sender, "strings.*", "strings.chat.*", "strings.chat.clear")) {
            messenger.sendMessage(NO_PERMS, sender);
            return true;
        }

        if(args.length > 1) {
            messenger.sendMessage(TOO_MANY_ARGS, sender);
            return true;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("all")) {
                if(!Permissions.anyOfOrAdmin(sender, "strings.*", "strings.chat.*", "strings.chat.clear")) {
                    messenger.sendMessage(NO_PERMS, sender);
                    return true;
                }
                clearChatAll();
                messenger.sendMessage(CHAT_CLEARED_ALL, sender);
            } else {
                messenger.sendMessage(INVALID_ARGS, sender);
            }
            return true;
        }

        if(!(sender instanceof Player)) {
            sender.sendMessage("[Strings] Console cannot clear it's own chat.");
            return true;
        }

        // One arg
        sender.sendMessage(StringUtils.repeat(" \n", 100));
        messenger.sendMessage(CHAT_CLEARED, sender);
        return true;
    }

    private void clearChatAll() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(StringUtils.repeat(" \n",100));
        }
    }

}
