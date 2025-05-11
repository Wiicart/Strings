package com.pedestriamc.strings.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
abstract class AbstractTabCompleter implements TabCompleter {

    static final List<String> EMPTY = List.of();

    final List<String> getPlayerNames() {
        List<String> list = new ArrayList<>();
        for(Player p : Bukkit.getOnlinePlayers()) {
            list.add(p.getName());
        }
        return list;
    }

    /**
     * Filters a List down to possible args, given input.
     * @param list The List to filter
     * @param input The text input
     * @return A List<String>
     */
    final List<String> filter(@NotNull List<String> list, String input) {
        return list.stream()
                .filter(arg -> arg.toLowerCase().startsWith(input.toLowerCase()))
                .toList();
    }

    /**
     * Combines a number of collections into a single List<String>
     * @param collections Collections to be added to the final List.
     * @return A populated List<String>
     */
    @SafeVarargs
    final @NotNull List<String> combine(@NotNull final Collection<String>... collections) {
        List<String> list = new ArrayList<>();
        for(Collection<String> collection : collections) {
            list.addAll(collection);
        }
        return list;
    }

    /**
     * Combines a String and a number of collections into a single List<String>
     * @param string The individual String
     * @param collections Collections to be added to the final List.
     * @return A populated List<String>
     */
    @SafeVarargs
    final @NotNull List<String> combine(@NotNull final String string, @NotNull final Collection<String>... collections) {
        List<String> list = combine(collections);
        list.add(string);
        return list;
    }
}
