package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.utlity.Permissions;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

public final class ClearChatCommand implements CommandExecutor {

    private final Messenger messenger;

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

        if(args.length > 0 && args[0].equalsIgnoreCase("all") && (sender.hasPermission("strings.chat.clear.other") || sender.hasPermission("strings.chat.*"))) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(StringUtils.repeat(" \n",100));
            }
            messenger.sendMessage(CHAT_CLEARED_ALL, sender);
            return true;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage("[Strings] Console cannot clear it's own chat.");
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage(StringUtils.repeat(" \n", 100));
            messenger.sendMessage(CHAT_CLEARED, sender);
            return true;
        }
        messenger.sendMessage(NO_PERMS, sender);
        return true;
    }

}
