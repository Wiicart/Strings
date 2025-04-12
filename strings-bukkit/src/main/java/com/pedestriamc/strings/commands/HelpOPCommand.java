package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

public final class HelpOPCommand implements CommandExecutor {

    private final Strings strings;
    private final Channel helpOPChannel;
    private final Messenger messenger;

    public HelpOPCommand(@NotNull Strings strings) {
        this.strings = strings;
        this.helpOPChannel = strings.getChannelLoader().getChannel("helpop");
        this.messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!sender.hasPermission("strings.helpop.use")) {
            messenger.sendMessage(NO_PERMS, sender);
            return true;
        }

        //TODO Review if this block necessary.
        if(strings.getChannelLoader().getChannel("helpop") == null) {
            messenger.sendMessage(HELPOP_DISABLED, sender);
            return true;
        }

        if(!(sender instanceof Player)) {
            sender.sendMessage("[Strings] This command can only be used by players.");
            return true;
        }

        if(args.length == 0) {
            messenger.sendMessage(INSUFFICIENT_ARGS, sender);
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for(String arg : args) {
            builder.append(arg);
            builder.append(" ");
        }
        helpOPChannel.sendMessage((Player) sender, builder.toString());
        return true;
    }
}
