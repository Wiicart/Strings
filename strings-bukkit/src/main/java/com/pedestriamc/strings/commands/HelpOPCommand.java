package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.utlity.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

/**
 * Implements the {@code /helpop} command.
 * Syntax: {@code /helpop <message>} (the message may be multiple args).
 */
public final class HelpOPCommand implements CommandExecutor {

    private final Channel helpOPChannel;
    private final Messenger messenger;

    public HelpOPCommand(@NotNull Strings strings) {
        this.helpOPChannel = strings.getChannelLoader().getChannel("helpop");
        this.messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!Permissions.anyOfOrAdmin(sender, "strings.*", "strings.helpop.*", "strings.helpop.use")) {
            messenger.sendMessage(NO_PERMS, sender);
            return true;
        }

        if(!(sender instanceof Player player)) {
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

        helpOPChannel.sendMessage(player, builder.toString());
        return true;
    }
}
