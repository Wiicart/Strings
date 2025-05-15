package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.commands.MessengerCommand;
import com.pedestriamc.strings.commands.base.CommandBase;
import org.bukkit.command.CommandExecutor;

import java.util.HashMap;

public final class ChannelCommand extends CommandBase {

    public static final String CHANNEL_PLACEHOLDER = "{channel}";

    public ChannelCommand(Strings strings) {
        super();
        HashMap<String, CommandExecutor> map = new HashMap<>();

        CommandExecutor joinCommand = new JoinCommand(strings);
        map.put("JOIN", joinCommand);
        map.put("J", joinCommand);

        CommandExecutor leaveCommand = new LeaveCommand(strings);
        map.put("LEAVE", leaveCommand);
        map.put("L", leaveCommand);

        CommandExecutor helpCommand = new MessengerCommand(strings, Message.CHANNEL_HELP);
        map.put("HELP", helpCommand);
        map.put("H", helpCommand);

        CommandExecutor monitorCommand = new MonitorCommand(strings);
        map.put("MONITOR", monitorCommand);

        CommandExecutor unmonitorCommand = new UnmonitorCommand(strings);
        map.put("UNMONITOR", unmonitorCommand);

        CommandExecutor channelBroadcastCommand = new ChannelBroadcastCommand(strings);
        map.put("BROADCAST", channelBroadcastCommand);
        map.put("ANNOUNCE", channelBroadcastCommand);

        CommandExecutor baseCommand = new ChannelBaseCommand(strings);
        initialize(map, baseCommand);
    }

}
