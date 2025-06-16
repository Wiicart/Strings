package com.pedestriamc.strings.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MentionCommandTabCompleter extends AbstractTabCompleter {

    private static final List<String> LIST = List.of("enable", "disable", "on", "off");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return switch(args.length) {
            case 0 -> LIST;
            case 1 -> filter(LIST, args[0]);
            default -> EMPTY;
        };
    }
}