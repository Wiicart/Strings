package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Map;

public class ChatColorCommand implements CommandExecutor {

    Map<String, ChatColor> colorMap = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("BLACK", ChatColor.BLACK),
            new AbstractMap.SimpleEntry<>("DARKBLUE", ChatColor.DARK_BLUE),
            new AbstractMap.SimpleEntry<>("DARKGREEN", ChatColor.DARK_GREEN),
            new AbstractMap.SimpleEntry<>("DARKAQUA", ChatColor.DARK_AQUA),
            new AbstractMap.SimpleEntry<>("DARKRED", ChatColor.DARK_RED),
            new AbstractMap.SimpleEntry<>("DARKPURPLE", ChatColor.DARK_PURPLE),
            new AbstractMap.SimpleEntry<>("GOLD", ChatColor.GOLD),
            new AbstractMap.SimpleEntry<>("GRAY", ChatColor.GRAY),
            new AbstractMap.SimpleEntry<>("DARKGRAY", ChatColor.DARK_GRAY),
            new AbstractMap.SimpleEntry<>("BLUE", ChatColor.BLUE),
            new AbstractMap.SimpleEntry<>("GREEN", ChatColor.GREEN),
            new AbstractMap.SimpleEntry<>("AQUA", ChatColor.AQUA),
            new AbstractMap.SimpleEntry<>("RED", ChatColor.RED),
            new AbstractMap.SimpleEntry<>("LIGHT_PURPLE", ChatColor.LIGHT_PURPLE),
            new AbstractMap.SimpleEntry<>("YELLOW", ChatColor.YELLOW),
            new AbstractMap.SimpleEntry<>("WHITE", ChatColor.WHITE)
    );

    Map<String, ChatColor> styleMap = Map.of(
            "BOLD", ChatColor.BOLD,
            "UNDERLINE", ChatColor.UNDERLINE,
            "ITALIC", ChatColor.ITALIC,
            "ITALICS", ChatColor.ITALIC,
            "STRIKETHROUGH", ChatColor.STRIKETHROUGH,
            "STRIKE", ChatColor.STRIKETHROUGH,
            "MAGIC", ChatColor.MAGIC
    );

    Strings strings = Strings.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0){
            Messenger.sendMessage(Message.INSUFFICIENT_ARGS, sender);
            return true;
        }

        Player player = Bukkit.getPlayer(args[args.length - 1]);

        if(sender instanceof Server && player == null){
            Messenger.sendMessage(Message.SERVER_MUST_SPECIFY_PLAYER, sender);
            return true;
        }
        if(!(sender.hasPermission("strings.chat.chatcolor") || sender.hasPermission("strings.chat.*") || sender.hasPermission("strings.*"))){
            Messenger.sendMessage(Message.NO_PERMS, sender);
            return true;
        }

        if(player != null){
            if(!sender.hasPermission("strings.chat.chatcolor.other")){
                Messenger.sendMessage(Message.NO_PERMS, sender);
                return true;
            }
        }else{
            player = (Player) sender;
        }

        StringBuilder chatColor = new StringBuilder();
        for(int i=0; i<args.length - 1; i++){
            if(colorMap.containsKey(args[i].toUpperCase())){
                chatColor.append(colorMap.get(args[i].toUpperCase()).toString());
            }else if(styleMap.containsKey(args[i].toUpperCase())){
                chatColor.append(styleMap.get(args[i].toUpperCase()).toString());
            }else{
                Messenger.sendMessage(Message.UNKNOWN_STYLE_COLOR, sender);
                return true;
            }
        }

        User user = strings.getUser((Player) sender);
        user.setChatColor(chatColor.toString());


        return true;
    }
}
