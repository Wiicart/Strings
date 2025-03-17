package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.api.message.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.*;

public final class MentionCommand implements CommandExecutor {

    private final Strings strings;
    private final Messenger messenger;

    public MentionCommand(Strings strings) {
        this.strings = strings;
        this.messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player player)) {
            sender.sendMessage("[Strings] This command can only be used by players!");
            return true;
        }

        if(!(player.hasPermission("strings.mention.toggle") || player.hasPermission("strings.mention.*") || player.hasPermission("strings.*"))) {
            messenger.sendMessage(NO_PERMS, sender);
            return true;
        }

        User user = strings.getUser(player);

        if(args.length == 0) {
            boolean isEnabled = user.isMentionsEnabled();
            if(isEnabled){
                disable(player, user);
            }else{
                enable(player, user);
            }
            return true;
        }

        if(args.length == 1) {
            if(args[0].equals("enable") || args[0].equals("on")) {
                enable(player, user);
                return true;
            }
            if(args[0].equals("disable") || args[0].equals("off")) {
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
        messenger.sendMessage(MENTIONS_ENABLED, sender);

    }

    private void disable(CommandSender sender, @NotNull User user) {
        user.setMentionsEnabled(false);
        messenger.sendMessage(MENTIONS_DISABLED, sender);
    }

}
