package com.pedestriamc.strings.discord.command;

import com.pedestriamc.strings.discord.StringsDiscord;
import com.pedestriamc.strings.api.discord.Option;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

// Does not provide reloading - use /strings reload to reload.
public class DiscordCommand implements CommandExecutor {

    private final String message; // config value "discord-cmd-message"

    public DiscordCommand(@NotNull StringsDiscord strings) {
        message = strings.getConfiguration().getColored(Option.Text.DISCORD_COMMAND_MESSAGE);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(message);
        return true;
    }

}
