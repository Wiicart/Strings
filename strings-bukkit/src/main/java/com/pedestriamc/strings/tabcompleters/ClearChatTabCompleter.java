package com.pedestriamc.strings.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClearChatTabCompleter implements TabCompleter {

    private static final List<String> LIST = List.of("all");
    private static final List<String> EMPTY = List.of();

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length <= 1) {
            return LIST;
        }
        return EMPTY;
    }
}
