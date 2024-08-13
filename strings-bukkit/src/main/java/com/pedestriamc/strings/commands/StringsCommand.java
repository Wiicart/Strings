package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import com.pedestriamc.strings.Strings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class StringsCommand implements CommandExecutor {

    private final Strings strings = Strings.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&3Strings&8] &fRunning Strings version &a" + strings.getVersion()));
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("version")){
            strings.reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&3Strings&8] &fRunning Strings version &a" + strings.getVersion()));
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("reload")){
            if(sender.hasPermission("strings.reload") || sender instanceof ConsoleCommandSender){
                strings.reload();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&3Strings&8] &fStrings version &a" + strings.getVersion() + "&f reloaded."));
                return true;
            }
            Messenger.sendMessage(Message.NO_PERMS, sender);
            return true;
        }
        Messenger.sendMessage(Message.TOO_MANY_ARGS, sender);
        return true;
    }
}
