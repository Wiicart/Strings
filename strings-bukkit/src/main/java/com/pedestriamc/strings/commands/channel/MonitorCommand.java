package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.user.User;
import net.wiicart.commands.command.CartCommandExecutor;
import net.wiicart.commands.command.CommandData;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.pedestriamc.strings.api.message.Message.*;

class MonitorCommand extends AbstractChannelCommand implements CartCommandExecutor {

    MonitorCommand(@NotNull Strings strings) {
        super(strings);
    }

    @Override
    public void onCommand(@NotNull CommandData data) {
        CommandSender sender = data.sender();
        int length = data.args().length;
        String[] args = data.args();

        if (isArgCountIncorrect(sender, length)) {
            return;
        }

        Channel channel = getChannel(sender, args[0]);
        if (channel == null) {
            return;
        }

        Monitorable monitorable = getMonitorable(sender, channel);
        if (monitorable == null) {
            return;
        }

        if (doesNotHaveMonitorPermission(sender, monitorable)) {
            return;
        }

        User target = getTarget(sender, args);
        if (target == null) {
            return;
        }

        if (isAlreadyMonitoring(sender, target, monitorable)) {
            return;
        }

        target.monitor(monitorable);
        saveUser(target);

        sendFinalMessages(sender, target, channel);
    }

    private boolean isAlreadyMonitoring(@NotNull CommandSender sender, @NotNull StringsUser target, @NotNull Monitorable monitorable) {
        return checkAlreadySet(
                target.isMonitoring(monitorable),
                sender,
                target,
                monitorable,
                ALREADY_MONITORING,
                ALREADY_MONITORING
        );
    }

    private void sendFinalMessages(@NotNull CommandSender sender, @NotNull User target, @NotNull Channel channel) {
        sendFinalMessages(sender, target, channel, MONITOR_SUCCESS, MONITOR_SUCCESS_OTHER);
    }

    private boolean doesNotHaveMonitorPermission(@NotNull CommandSender sender, @NotNull Monitorable monitorable) {
        if (Permissions.anyOfOrAdmin(sender,
                "strings.*",
                "strings.channels.*",
                "strings.channels." + monitorable.getName() + ".*",
                "strings.channels." + monitorable.getName() + ".monitor"
        )) {
            return false;
        } else {
            sendMessage(NO_PERM_MONITOR, Map.of("{channel}", monitorable.getName()), sender);
            return true;
        }
    }
}
