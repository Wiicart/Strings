package com.pedestriamc.strings.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClearChatTabCompleter extends AbstractTabCompleter {

    private static final List<String> ALL = List.of("all");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return switch(args.length) {
            case 0 -> ALL;
            case 1 -> filter(ALL, args[0]);
            default -> EMPTY;
        };
    }
}
