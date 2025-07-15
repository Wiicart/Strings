package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.text.format.StringsTextColor;
import com.pedestriamc.strings.api.utlity.Permissions;
import com.pedestriamc.strings.commands.base.CommandBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class StringsCommand extends CommandBase {

    private static final String VERSION_MESSAGE = StringsTextColor.DARK_GRAY + "[" +
            StringsTextColor.DARK_AQUA + "Strings" + StringsTextColor.DARK_GRAY + "] " +
            StringsTextColor.WHITE + "Running strings version " + StringsTextColor.GREEN + Strings.VERSION;

    private static final String RELOAD_MESSAGE = StringsTextColor.DARK_GRAY + "[" +
            StringsTextColor.DARK_AQUA + "Strings" + StringsTextColor.DARK_GRAY + "] " +
            StringsTextColor.WHITE + "Strings version " + StringsTextColor.GREEN + Strings.VERSION +
            StringsTextColor.WHITE + " reloaded.";

    public StringsCommand(@NotNull Strings strings) {
        HashMap<String, CommandExecutor> map = new HashMap<>();
        BaseCommand baseCommand = new BaseCommand(strings);
        map.put("VERSION", baseCommand);
        map.put("RELOAD", new ReloadCommand(strings));
        map.put("HELP", new MessengerCommand(strings, Message.STRINGS_HELP));
        initialize(map, baseCommand);
    }


    private record BaseCommand(Strings strings) implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            sender.sendMessage(VERSION_MESSAGE);
            return true;
        }
    }

    private record ReloadCommand(Strings strings) implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if(Permissions.anyOfOrAdmin(sender, "strings.*", "strings.reload") || sender instanceof ConsoleCommandSender) {
                strings.reload();
                sender.sendMessage(RELOAD_MESSAGE);
            } else {
                strings.getMessenger().sendMessage(Message.NO_PERMS, sender);
            }
            return true;
        }
    }

}
