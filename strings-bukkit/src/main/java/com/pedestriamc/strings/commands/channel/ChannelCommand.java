package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.commands.CartMessengerCommand;
import net.wiicart.commands.command.tree.CommandTree;
import org.jetbrains.annotations.NotNull;

public class ChannelCommand extends CommandTree {

    public ChannelCommand(@NotNull Strings strings) {
        super(builder()
                .run(builder -> {
                    ListCommand command = new ListCommand(strings);
                    builder.getWorkbench().store("list", () -> command);
                    builder.executes(new RootCommand(strings, command));
                })
                .withChild("list", b -> b.executes("list"))
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
                .withChild("current", b -> b.executes(new CurrentCommand(strings)))
                .build()
        );
    }

}
