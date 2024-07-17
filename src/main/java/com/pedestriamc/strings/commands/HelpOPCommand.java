package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.channels.Channel;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpOPCommand implements CommandExecutor {

    private final Strings strings = Strings.getInstance();
    private final Channel channel = strings.getChannel("helpop");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(channel == null){
            Messenger.sendMessage(Message.HELPOP_DISABLED, sender);
            return true;
        }
        if(sender instanceof Server){
            sender.sendMessage("[Strings] This command can only be used by players");
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("ON") && (sender.hasPermission("strings.*") || sender.hasPermission("strings.helpop"))){
            strings.getUser((Player) sender).joinChannel(channel);

        }

        return true;
    }
}
