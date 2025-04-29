package com.pedestriamc.strings.tabcompleters;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.chat.ChannelManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * TabCompleter for the /channel command
 */
public class ChannelTabCompleter extends AbstractTabCompleter {

    private final ChannelManager channelLoader;

    public ChannelTabCompleter(@NotNull Strings strings) {
        this.channelLoader = strings.getChannelLoader();
    }

    private static final Set<String> CASES = Set.of(
            "join", "leave", "monitor", "unmonitor", "broadcast", "announce"
    );

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        return switch (args.length) {
            case 1 -> {
                List<String> list = collect(getAllowedChannels(sender), CASES);
                list.add("help");
                yield filter(list, args[0]);
            }
            case 2 -> {
                if(CASES.contains(args[0].toLowerCase())) {
                    yield filter(getAllowedChannels(sender), args[1]);
                }
                yield filter(getPlayerNames(), args[1]);
            }

            case 3 -> filter(getPlayerNames(), args[2]);

            default -> EMPTY;
        };
    }

    /**
     * Provides a List<String> of Channels a Player is allowed in
     * @param sender The Player to check permissions with
     * @return A List<String>
     */
    private List<String> getAllowedChannels(CommandSender sender) {
        return channelLoader.getNonProtectedChannels().stream()
                .filter(channel -> channel.allows(sender))
                .map(Channel::getName)
                .toList();
    }

}
