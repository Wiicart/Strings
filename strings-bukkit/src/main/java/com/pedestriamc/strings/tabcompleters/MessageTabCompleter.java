package com.pedestriamc.strings.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MessageTabCompleter implements TabCompleter {

    private final List<String> empty = new ArrayList<>();

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length <= 1){
            ArrayList<String> list = new ArrayList<>();
            for(Player p : Bukkit.getOnlinePlayers()){
                list.add(p.getName());
            }
            return list;
        }
        return empty;
    }
}
