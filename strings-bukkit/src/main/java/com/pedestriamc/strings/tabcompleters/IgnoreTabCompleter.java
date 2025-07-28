package com.pedestriamc.strings.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IgnoreTabCompleter extends AbstractTabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        return switch(args.length) {
            case 0 -> getPlayerNames();
            case 1 -> filter(getPlayerNames(), args[0]);
            case 2 -> {
                List<String> names = getPlayerNames();
                names.remove(args[0]);
                yield filter(names, args[1]);
            }
            default -> EMPTY;
        };
    }
}