package com.pedestriamc.strings.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MentionCommandTabCompleter implements TabCompleter {

    private final List<String> list = List.of("enable", "disable", "on", "off");

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length <= 1){
            return list;
        }
        return new ArrayList<>();
    }
}
