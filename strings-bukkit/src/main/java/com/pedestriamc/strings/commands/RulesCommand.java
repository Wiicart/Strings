package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.settings.Option;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RulesCommand implements CommandExecutor {

    private final Strings strings;

    private final Component message;

    public RulesCommand(@NotNull Strings strings) {
        this.strings = strings;

        String raw = strings.getConfiguration().get(Option.Text.RULES_MESSAGE);
        message = MiniMessage.miniMessage().deserialize(raw);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        strings.adventure()
                .sender(sender)
                .sendMessage(message);
        return true;
    }
}
