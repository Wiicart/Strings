package com.pedestriamc.strings.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class CommandBase implements CommandExecutor {

    private Map<String, CommandComponent> map;
    private CommandComponent baseCommand;

    @Contract(pure = true)
    protected CommandBase() {}

    /**
     * Initialization of the CommandBase.
     * Must be called before the command is used!
     * @param map The Map containing subcommands and their names.
     * @param baseCommand The BaseCommand that is called when none of the subcommands are eligible to be called.
     */
    @Contract(pure = true)
    protected final void initialize(@NotNull Map<String, CommandComponent> map, @NotNull CommandComponent baseCommand){
        this.map = map;
        this.baseCommand = baseCommand;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if(args.length > 0){
            String arg1 = args[0].toUpperCase();
            if(map.containsKey(arg1)){
                return map.get(arg1).onCommand(sender, command, label, args);
            }
        }
        return baseCommand.onCommand(sender, command, label, args);
    }

    /**
     * Sub command interface.  Passes on all info from the command.
     */
    public interface CommandComponent extends CommandExecutor {}

}
