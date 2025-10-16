package com.pedestriamc.strings.commands.ignore;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.user.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class UnIgnoreCommand extends AbstractIgnoreCommand implements CommandExecutor {

    public UnIgnoreCommand(@NotNull Strings strings) {
        super(strings);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (checkArgCountAndNotify(sender, args.length)) {
            return true;
        }

        // getTarget only useful for getting the player that will ignore another player
        User ignorer = getTarget(sender, args);
        if (ignorer == null) {
            return true;
        }

        User target = getIgnoreTarget(sender, args);
        if (target == null) {
            return true;
        }

        if (checkNotIgnoringAndNotify(sender, ignorer, target)) {
            return true;
        }

        ignorer.stopIgnoring(target);
        saveUser(ignorer);

        Map<String, String> placeholders = Map.of(
                "{player}", ignorer.getName(),
                "{target}", target.getName()
        );

        if (!sender.equals(ignorer.player())) {
            sendMessage(Message.PLAYER_UNIGNORED_OTHER, placeholders, sender);
        }
        messenger().sendMessage(Message.PLAYER_UNIGNORED, ignorer, placeholders);

        return true;
    }

    private boolean checkNotIgnoringAndNotify(@NotNull CommandSender sender, @NotNull User ignorer, @NotNull User target) {
        if (!ignorer.isIgnoring(target)) {
            sendMessage(Message.NOT_IGNORED, Map.of("{player}", target.getName()), sender);
            return true;
        }

        return false;
    }

}
