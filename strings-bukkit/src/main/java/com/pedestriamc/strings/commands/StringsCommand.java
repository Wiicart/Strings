package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.text.format.StringsTextColor;
import net.wiicart.commands.command.CartCommandExecutor;
import net.wiicart.commands.command.CommandData;
import net.wiicart.commands.command.tree.CommandTree;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class StringsCommand extends CommandTree {

    private static final String VERSION_MESSAGE = StringsTextColor.DARK_GRAY + "[" +
            StringsTextColor.DARK_AQUA + "Strings" + StringsTextColor.DARK_GRAY + "] " +
            StringsTextColor.WHITE + "Running strings version " + StringsTextColor.GREEN + Strings.VERSION;

    private static final String RELOAD_MESSAGE = StringsTextColor.DARK_GRAY + "[" +
            StringsTextColor.DARK_AQUA + "Strings" + StringsTextColor.DARK_GRAY + "] " +
            StringsTextColor.WHITE + "Strings version " + StringsTextColor.GREEN + Strings.VERSION +
            StringsTextColor.WHITE + " reloaded.";

    public StringsCommand(@NotNull Strings strings) {
        super(CommandTree.builder()
                .executes(data -> data.sender().sendMessage(VERSION_MESSAGE))
                .withChild(
                        "reload",
                        b -> b.executes(new ReloadCommand(strings))
                )
                .withChild("help",
                        b -> b
                                .executes(new CartMessengerCommand(strings, Message.STRINGS_HELP))
                                .withAliases("h")
                )
                .build()
        );
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class ReloadCommand implements CartCommandExecutor {

        private final Strings strings;

        ReloadCommand(@NotNull Strings strings) {
            this.strings = strings;
        }

        @Override
        public void onCommand(@NotNull CommandData data) {
            if(Permissions.anyOfOrAdmin(data.sender(), "strings.*", "strings.reload")
                    || data.sender() instanceof ConsoleCommandSender
            ) {
                strings.reload();
                data.sender().sendMessage(RELOAD_MESSAGE);
            } else {
                strings.getMessenger().sendMessage(Message.NO_PERMS, data.sender());
            }
        }
    }

}
