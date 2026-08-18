package com.pedestriamc.strings.tabcompleters;

import com.pedestriamc.strings.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class ChatColorTabCompleter extends AbstractTabCompleter {

    public ChatColorTabCompleter(@NotNull Strings strings) {
        super(strings);
    }

    private static final List<String> COLORS = List.of(
            "black", "darkblue", "darkgreen", "darkaqua",
            "darkred", "darkpurple", "gold", "gray", "darkgray",
            "blue", "green", "aqua", "red", "lightpurple", "yellow",
            "white", "reset", "#"
    );

    private static final List<String> STYLES = List.of(
            "bold", "underline", "italic", "strikethrough", "magic"
    );

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        return switch (args.length) {
            case 0 -> COLORS;
            case 1 -> filter(COLORS, args[0]);
            case 2, 3, 4, 5 -> {
                List<String> list = combine(STYLES, getPlayerNames());
                yield filter(list, args[1].toLowerCase());
            }
            case 6 -> filter(getPlayerNames(), args[2].toLowerCase());
            default -> EMPTY;
        };
    }
}
