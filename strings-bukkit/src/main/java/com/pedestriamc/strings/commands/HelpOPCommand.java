package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.chat.channels.Channel;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.message.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpOPCommand implements CommandExecutor {

    private final Strings strings;
    private final Channel helpOPChannel;
    private final Messenger messenger;

    public HelpOPCommand(@NotNull Strings strings){
        this.strings = strings;
        this.helpOPChannel = strings.getChannel("helpop");
        this.messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(strings.getChannel("helpop") == null){
            messenger.sendMessage(Message.HELPOP_DISABLED, sender);
            return true;
        }
        if(!(sender instanceof Player)){
            sender.sendMessage("[Strings] This command can only be used by players.");
            return true;
        }
        if(!sender.hasPermission("strings.helpop.use")){
            messenger.sendMessage(Message.NO_PERMS, sender);
            return true;
        }
        if(args.length == 0){
            messenger.sendMessage(Message.INSUFFICIENT_ARGS, sender);
            return true;
        }
        StringBuilder builder = new StringBuilder();
        for(String arg : args){
            builder.append(arg);
            builder.append(" ");
        }
        helpOPChannel.sendMessage((Player) sender, builder.toString());
        return true;
    }
}
