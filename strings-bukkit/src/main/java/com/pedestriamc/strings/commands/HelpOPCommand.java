package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.message.Messenger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpOPCommand implements CommandExecutor {

    private final Strings strings = Strings.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(strings.getChannel("helpop") == null){
            Messenger.sendMessage(Message.HELPOP_DISABLED, sender);
            return true;
        }
        if(sender instanceof Server){
            sender.sendMessage("[Strings] This command can only be used by players.");
            return true;
        }
        if(!sender.hasPermission("strings.helpop.use")){
            Messenger.sendMessage(Message.NO_PERMS, sender);
            return true;
        }
        if(args.length == 0){
            Messenger.sendMessage(Message.INSUFFICIENT_ARGS, sender);
            return true;
        }
        StringBuilder builder = new StringBuilder();
        for(String arg : args){
            builder.append(arg);
            builder.append(" ");
        }
        strings.getChannel("helpop").sendMessage((Player) sender,builder.toString());
        Messenger.sendMessage(Message.HELPOP_SENT, sender);
        return true;
    }
}
