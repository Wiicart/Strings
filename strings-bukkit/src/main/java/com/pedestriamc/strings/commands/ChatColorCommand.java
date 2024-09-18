package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.AbstractMap;
import java.util.Map;

public class ChatColorCommand implements CommandExecutor {

    private final Map<String, ChatColor> colorMap = Map.ofEntries(
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

    private final Map<String, ChatColor> styleMap = Map.of(
            "BOLD", ChatColor.BOLD,
            "UNDERLINE", ChatColor.UNDERLINE,
            "ITALIC", ChatColor.ITALIC,
            "ITALICS", ChatColor.ITALIC,
            "STRIKETHROUGH", ChatColor.STRIKETHROUGH,
            "STRIKE", ChatColor.STRIKETHROUGH,
            "MAGIC", ChatColor.MAGIC
    );

    private final Strings strings;

    public ChatColorCommand(@NotNull Strings strings){
        this.strings = strings;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0){
            Messenger.sendMessage(Message.INSUFFICIENT_ARGS, sender);
            return true;
        }

        if(args.length > 7){
            Messenger.sendMessage(Message.TOO_MANY_ARGS, sender);
            return true;
        }

        Player player = Bukkit.getPlayer(args[args.length - 1]);

        if(sender instanceof ConsoleCommandSender && player == null){
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
        boolean hasColor = false;
        for (String arg : args) {
            String pattern = "#(?:[0-9a-fA-F]{6}|[0-9a-fA-F]{3})";
            if (colorMap.containsKey(arg.toUpperCase())) {
                if(hasColor){
                    Messenger.sendMessage(Message.ONE_COLOR, sender);
                    return true;
                }
                chatColor.append(colorMap.get(arg.toUpperCase()));
                hasColor = true;
            } else if (styleMap.containsKey(arg.toUpperCase())) {
                chatColor.append(styleMap.get(arg.toUpperCase()));
            } else if (arg.matches(pattern)) {
                ChatColor color = ChatColor.of(new Color(Integer.parseInt(arg.substring(1), 16)));
                chatColor.append(color);
            }
        }
        User user = strings.getUser(player);
        user.setChatColor(chatColor.toString());
        if(!player.equals(sender)){
            Messenger.sendMessage(Message.CHATCOLOR_SET_OTHER, Map.of("{color}", user.getChatColor() + "this", "{player}", player.getName()), sender);
        }
        Messenger.sendMessage(Message.CHATCOLOR_SET, Map.of("{color}", user.getChatColor() + "this", "{player}", player.getName()), player);
        return true;
    }
}
