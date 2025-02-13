package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.commands.CommandBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.CHANNEL_HELP;

public class HelpCommand implements CommandBase.CommandComponent {

    private final Messenger messenger;

    public HelpCommand(Strings strings){
        this.messenger = strings.getMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        messenger.sendMessage(CHANNEL_HELP, sender);
        return true;
    }
}

