package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.commands.base.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class StringsCommand extends CommandBase {

    public StringsCommand(@NotNull Strings strings) {
        HashMap<String, CommandComponent> map = new HashMap<>();
        BaseCommand baseCommand = new BaseCommand(strings);
        map.put("VERSION", baseCommand);
        map.put("RELOAD", new ReloadCommand(strings));
        map.put("HELP", new HelpCommand(strings));
        initialize(map, baseCommand);
    }


    private record BaseCommand(Strings strings) implements CommandComponent {

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&3Strings&8] &fRunning Strings version &a" + strings.getVersion()));
            return true;
        }

    }

    private record ReloadCommand(Strings strings) implements CommandComponent {

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if(sender.hasPermission("strings.reload") || sender instanceof ConsoleCommandSender || sender.isOp()) {
                strings.reload();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&3Strings&8] &fStrings version &a" + strings.getVersion() + "&f reloaded."));
            } else {
                strings.getMessenger().sendMessage(Message.NO_PERMS, sender);
            }
            return true;
        }

    }

    private record HelpCommand(Strings strings) implements CommandComponent {

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            strings.getMessenger().sendMessage(Message.STRINGS_HELP, sender);
            return true;
        }

    }

}
