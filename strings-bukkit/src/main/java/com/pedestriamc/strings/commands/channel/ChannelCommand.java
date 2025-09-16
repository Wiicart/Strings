package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.commands.CartMessengerCommand;
import net.wiicart.commands.command.tree.CommandTree;
import org.jetbrains.annotations.NotNull;

public class ChannelCommand extends CommandTree {

    public ChannelCommand(@NotNull Strings strings) {
        super(builder()
                .executes(new RootCommand(strings))
                .withChild("join", b -> {
                    b.withAliases("j");
                    b.executes(new JoinCommand(strings));
                })
                .withChild("leave", b -> {
                    b.withAliases("l");
                    b.executes(new LeaveCommand(strings));
                })
                .withChild("help", b -> {
                        b.withAliases("h");
                        b.executes(new CartMessengerCommand(strings, Message.CHANNEL_HELP));
                })
                .withChild("broadcast", b -> {
                    b.withAliases("announce");
                    b.executes(new BroadcastCommand(strings));
                })
                .withChild("mute", b -> b.executes(new MuteCommand(strings)))
                .withChild("unmute", b -> b.executes(new UnmuteCommand(strings)))
                .withChild("monitor", b -> b.executes(new MonitorCommand(strings)))
                .withChild("unmonitor", b -> b.executes(new UnmonitorCommand(strings)))
                .withChild("list", b -> b.executes(new ListCommand(strings)))
                .withChild("current", b -> b.executes(new CurrentCommand(strings)))
                .build()
        );
    }

}
