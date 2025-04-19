package com.pedestriamc.strings.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatColorTabCompleter extends AbstractTabCompleter {

    private static final List<String> COLORS = List.of(
            "black",
            "darkblue",
            "darkgreen",
            "darkaqua",
            "darkred",
            "darkpurple",
            "gold",
            "gray",
            "darkgray",
            "blue",
            "green",
            "aqua",
            "red",
            "lightpurple",
            "yellow",
            "white",
            "reset"
    );

    private static final List<String> STYLES = List.of(
            "bold",
            "underline",
            "italic",
            "strikethrough",
            "magic"
    );

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        switch (args.length) {
            case 1 -> {
                return filter(COLORS, args[0]);
            }

            case 2 -> {
                List<String> list = new ArrayList<>(STYLES);
                list.addAll(getPlayerNames());
                return filter(list, args[1].toLowerCase());
            }

            case 3 -> {
                return filter(getPlayerNames(), args[2].toLowerCase());
            }

            default -> { return EMPTY; }
        }
    }
}
