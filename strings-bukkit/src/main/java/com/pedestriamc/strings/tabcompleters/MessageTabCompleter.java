package com.pedestriamc.strings.tabcompleters;

import com.pedestriamc.strings.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class MessageTabCompleter extends AbstractTabCompleter {

    public MessageTabCompleter(@NotNull Strings strings) {
        super(strings);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return switch(args.length) {
            case 0 -> getPlayerNames();
            case 1 -> filter(getPlayerNames(), args[0]);
            default -> EMPTY;
        };
    }

}
