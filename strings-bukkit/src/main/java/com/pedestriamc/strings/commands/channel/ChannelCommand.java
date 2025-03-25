package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.commands.CommandBase;

import java.util.HashMap;

public final class ChannelCommand extends CommandBase {

    public static final String CHANNEL_PLACEHOLDER = "{channel}";

    public ChannelCommand(Strings strings) {
        super();
        HashMap<String, CommandComponent> map = new HashMap<>();

        JoinCommand joinCommand = new JoinCommand(strings);
        map.put("JOIN", joinCommand);
        map.put("J", joinCommand);

        LeaveCommand leaveCommand = new LeaveCommand(strings);
        map.put("LEAVE", leaveCommand);
        map.put("L", leaveCommand);

        HelpCommand helpCommand = new HelpCommand(strings);
        map.put("HELP", helpCommand);
        map.put("H", helpCommand);

        MonitorCommand monitorCommand = new MonitorCommand(strings);
        map.put("MONITOR", monitorCommand);

        UnmonitorCommand unmonitorCommand = new UnmonitorCommand(strings);
        map.put("UNMONITOR", unmonitorCommand);

        ChannelBroadcastCommand channelBroadcastCommand = new ChannelBroadcastCommand(strings);
        map.put("BROADCAST", channelBroadcastCommand);
        map.put("ANNOUNCE", channelBroadcastCommand);

        BaseCommand baseCommand = new BaseCommand(strings);
        initialize(map, baseCommand);
    }

}
