package com.pedestriamc.strings.tabcompleters;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.chat.StringsChannelLoader;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChannelTabCompleter implements TabCompleter {

    private final StringsChannelLoader channelLoader;

    public ChannelTabCompleter(@NotNull Strings strings) {
        this.channelLoader = (StringsChannelLoader) strings.getChannelLoader();
    }

    private final List<String> empty = new ArrayList<>();

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        if(args.length <= 1) {
            List<String> list = new ArrayList<>(channelLoader.getNonProtectedChannelNames());
            list.add("join");
            list.add("leave");
            return list;
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("leave")) {
                return channelLoader.getNonProtectedChannelNames();
            }
            ArrayList<String> list = new ArrayList<>();
            for(Player p : Bukkit.getOnlinePlayers()) {
                list.add(p.getName());
            }
            return list;
        }
        if(args.length == 3) {
            ArrayList<String> list = new ArrayList<>();
            for(Player p : Bukkit.getOnlinePlayers()) {
                list.add(p.getName());
            }
            return list;
        }
        return empty;
    }
}
