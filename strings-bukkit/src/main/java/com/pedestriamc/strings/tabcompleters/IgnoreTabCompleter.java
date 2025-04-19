package com.pedestriamc.strings.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class IgnoreTabCompleter extends AbstractTabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        if(args.length == 0) {
            return getPlayerNames();
        }
        if(args.length == 1) {
            return filter(getPlayerNames(), args[0]);
        }
        return EMPTY;
    }
}