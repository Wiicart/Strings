package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.text.format.Element;
import com.pedestriamc.strings.api.text.format.StringsComponent;
import com.pedestriamc.strings.api.text.format.StringsTextColor;
import com.pedestriamc.strings.api.text.format.StringsTextDecoration;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.pedestriamc.strings.api.message.Message.*;

public final class ChatColorCommand implements CommandExecutor {

    private static final Map<String, StringsTextColor> colorMap = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("BLACK", StringsTextColor.BLACK),
            new AbstractMap.SimpleEntry<>("DARKBLUE", StringsTextColor.DARK_BLUE),
            new AbstractMap.SimpleEntry<>("DARKGREEN", StringsTextColor.DARK_GREEN),
            new AbstractMap.SimpleEntry<>("DARKAQUA", StringsTextColor.DARK_AQUA),
            new AbstractMap.SimpleEntry<>("DARKRED", StringsTextColor.DARK_RED),
            new AbstractMap.SimpleEntry<>("DARKPURPLE", StringsTextColor.DARK_PURPLE),
            new AbstractMap.SimpleEntry<>("GOLD", StringsTextColor.GOLD),
            new AbstractMap.SimpleEntry<>("GRAY", StringsTextColor.GRAY),
            new AbstractMap.SimpleEntry<>("DARKGRAY", StringsTextColor.DARK_GRAY),
            new AbstractMap.SimpleEntry<>("BLUE", StringsTextColor.BLUE),
            new AbstractMap.SimpleEntry<>("GREEN", StringsTextColor.GREEN),
            new AbstractMap.SimpleEntry<>("AQUA", StringsTextColor.AQUA),
            new AbstractMap.SimpleEntry<>("RED", StringsTextColor.RED),
            new AbstractMap.SimpleEntry<>("LIGHTPURPLE", StringsTextColor.PINK),
            new AbstractMap.SimpleEntry<>("YELLOW", StringsTextColor.YELLOW),
            new AbstractMap.SimpleEntry<>("WHITE", StringsTextColor.WHITE)
    );

    private static final Map<String, StringsTextDecoration> styleMap = Map.of(
            "BOLD", StringsTextDecoration.BOLD,
            "UNDERLINE", StringsTextDecoration.UNDERLINE,
            "ITALIC", StringsTextDecoration.ITALIC,
            "ITALICS", StringsTextDecoration.ITALIC,
            "STRIKETHROUGH", StringsTextDecoration.STRIKETHROUGH,
            "STRIKE", StringsTextDecoration.STRIKETHROUGH,
            "MAGIC", StringsTextDecoration.MAGIC
    );

    private final Strings strings;
    private final UserUtil userUtil;
    private final Messenger messenger;

    public ChatColorCommand(@NotNull Strings strings) {
        this.strings = strings;
        userUtil = strings.users();
        messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender.hasPermission("strings.chat.chatcolor") || sender.hasPermission("strings.chat.*") || sender.hasPermission("strings.*"))) {
            messenger.sendMessage(NO_PERMS, sender);
            return true;
        }

        if(args.length == 0) {
            messenger.sendMessage(INSUFFICIENT_ARGS, sender);
            return true;
        }

        if(args.length > 7) {
            messenger.sendMessage(TOO_MANY_ARGS, sender);
            return true;
        }

        Player player = Bukkit.getPlayer(args[args.length - 1]);

        if(sender instanceof ConsoleCommandSender && player == null) {
            messenger.sendMessage(SERVER_MUST_SPECIFY_PLAYER, sender);
            return true;
        }

        if(player != null) {
            if (!sender.hasPermission("strings.chat.chatcolor.other")) {
                messenger.sendMessage(NO_PERMS, sender);
                return true;
            }
        } else {
            player = (Player) sender;
        }

        User user = strings.users().getUser(player);

        if(args.length == 1 && args[0].equalsIgnoreCase("reset")) {
            user.setChatColorComponent(StringsComponent.of());
            userUtil.saveUser(user);
            messenger.sendMessage(CHATCOLOR_SET, Map.of("{color}", user.getChatColor() + "this", "{player}", player.getName()), player);
            return true;
        }

        List<Element<?>> elements = new ArrayList<>();
        boolean hasColor = false;
        for (String arg : args) {
            String pattern = "#(?:[0-9a-fA-F]{6}|[0-9a-fA-F]{3})";
            if(colorMap.containsKey(arg.toUpperCase())) {
                if (hasColor) {
                    messenger.sendMessage(ONE_COLOR, sender);
                    return true;
                }
                elements.add(colorMap.get(arg.toUpperCase()));
                hasColor = true;
            } else if (styleMap.containsKey(arg.toUpperCase())) {
                elements.add(styleMap.get(arg.toUpperCase()));
            } else if (arg.matches(pattern)) {
                StringsTextColor color = StringsTextColor.of(new Color(Integer.parseInt(arg.substring(1), 16)));
                elements.add(color);
                hasColor = true;
            }
        }

        if(!hasColor) {
            messenger.sendMessage(UNKNOWN_COLOR, sender);
            return true;
        }

        user.setChatColorComponent(StringsComponent.of(elements));
        userUtil.saveUser(user);
        if(!player.equals(sender)) {
            messenger.sendMessage(CHATCOLOR_SET_OTHER, Map.of("{color}", user.getChatColorComponent() + "this", "{player}", player.getName()), sender);
        }
        messenger.sendMessage(CHATCOLOR_SET, Map.of("{color}", user.getChatColorComponent() + "this", "{player}", player.getName()), player);
        return true;
    }
}
