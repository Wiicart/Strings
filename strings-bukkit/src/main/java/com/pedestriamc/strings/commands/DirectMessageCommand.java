package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import com.pedestriamc.strings.message.Messenger;
import com.pedestriamc.strings.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.message.Message.*;

public final class DirectMessageCommand implements CommandExecutor {

    private final PlayerDirectMessenger playerDirectMessenger;
    private final Messenger messenger;

    public DirectMessageCommand(@NotNull Strings strings){
        this.playerDirectMessenger = strings.getPlayerDirectMessenger();
        this.messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage("[Strings] This command can only be used by players!");
            return true;
        }
        if(!(sender.hasPermission("strings.chat.msg") || sender.hasPermission("strings.chat.*") || sender.hasPermission("strings.*"))){
            messenger.sendMessage(NO_PERMS, sender);
            return true;
        }
        if(args.length < 2){
            messenger.sendMessage(INSUFFICIENT_ARGS, sender);
            return true;
        }
        Player recipient = Bukkit.getPlayer(args[0]);
        if(recipient == null){
            messenger.sendMessage(UNKNOWN_PLAYER, sender);
            return true;
        }
        if(recipient.equals(sender)){
            messenger.sendMessage(SELF_MESSAGE, sender);
            return true;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 1; i < args.length; i++){
            stringBuilder.append(args[i]);
            stringBuilder.append(" ");
        }

        playerDirectMessenger.sendMessage((Player) sender, recipient, stringBuilder.toString());

        return true;
    }
}
