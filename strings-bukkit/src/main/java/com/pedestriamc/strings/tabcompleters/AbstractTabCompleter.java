package com.pedestriamc.strings.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Class that provides common methods used in TabCompleters.
 */
abstract class AbstractTabCompleter implements TabCompleter {

    static final @NotNull List<String> EMPTY = List.of();

    /**
     * Provides a List of all Player names.
     *
     * @return A populated List<String>
     */
    final @NotNull List<String> getPlayerNames() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        List<String> list = new ArrayList<>(players.size());
        for(Player p : players) {
            list.add(p.getName());
        }
        return list;
    }

    /**
     * Filters a List down to elements that start with the input, ignoring case.
     *
     * @param list The List to filter
     * @param input The text input
     * @return A filtered List<String>
     */
    final @NotNull List<String> filter(final @NotNull List<String> list, final @NotNull String input) {
        return list.stream()
                .filter(arg -> arg.toLowerCase().startsWith(input.toLowerCase()))
                .toList();
    }

    /**
     * Combines two or more collections into a single List.
     * Ignores duplicate values.
     *
     * @param collections Collections to be added to the final List.
     * @return A populated List<String>
     */
    @SafeVarargs
    @Contract("_ -> new")
    final @NotNull List<String> combine(final Collection<String> @NotNull ... collections) {
        Set<String> set = new LinkedHashSet<>();
        for(Collection<String> collection : collections) {
            set.addAll(collection);
        }
        return new ArrayList<>(set);
    }

    /**
     * Combines a String and one or more collections into a single List.
     * Ignores duplicate values.
     *
     * @param string The individual String
     * @param collections Collections to be added to the final List.
     * @return A populated List<String>
     */
    @SafeVarargs
    @Contract("_, _ -> new")
    final @NotNull List<String> combine(@NotNull final String string, @NotNull final Collection<String>... collections) {
        List<String> list = combine(collections);
        if(!list.contains(string)) {
            list.add(string);
        }
        return list;
    }
}
