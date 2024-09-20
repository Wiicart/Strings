package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ClassCanBeRecord")
public class MentionCommand implements CommandExecutor {

    private final Strings strings;

    public MentionCommand(Strings strings){
        this.strings = strings;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player player)){
            sender.sendMessage("[Strings] This command can only be used by players!");
            return true;
        }

        if(!(player.hasPermission("strings.mention.toggle") || player.hasPermission("strings.mention.*") || player.hasPermission("strings.*"))){
            Messenger.sendMessage(Message.NO_PERMS, sender);
            return true;
        }

        User user = strings.getUser(player);

        if(args.length == 0){
            boolean isEnabled = user.isMentionsEnabled();
            if(isEnabled){
                disable(player, user);
            }else{
                enable(player, user);
            }
            return true;
        }

        if(args.length == 1){
            if(args[0].equals("enable") || args[0].equals("on")){
                enable(player, user);
                return true;
            }
            if(args[0].equals("disable") || args[0].equals("off")){
                disable(player, user);
                return true;
            }
            Messenger.sendMessage(Message.INVALID_ARGS, sender);
            return true;
        }

        Messenger.sendMessage(Message.TOO_MANY_ARGS, sender);
        return true;
    }

    private void enable(CommandSender sender, @NotNull User user){
        user.setMentionsEnabled(true);
        Messenger.sendMessage(Message.MENTIONS_ENABLED, sender);

    }

    private void disable(CommandSender sender, @NotNull User user){
        user.setMentionsEnabled(false);
        Messenger.sendMessage(Message.MENTIONS_DISABLED, sender);
    }

}
