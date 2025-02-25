package com.pedestriamc.strings.chat.channel.local;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.data.ChannelData;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A WorldChannel that does not send player messages to all members of the Channel.
 * Players must either be in the scope (one of the Worlds of the Channel) or be a monitor.
 */
public class StrictWorldChannel extends WorldChannel {

    private final Channel defaultChannel;
    private final Messenger messenger;
    private final Map<String, String> placeholders = Map.of("{channel}", getName());

    public StrictWorldChannel(Strings strings, ChannelData data) {
        super(strings, data);
        defaultChannel = strings.getChannelLoader().getChannel("default");
        messenger = strings.getMessenger();
    }

    @Override
    public void sendMessage(Player player, String message) {
        if(isWithinScope(player)) {
            super.sendMessage(player, message);
        } else {
            defaultChannel.sendMessage(player, message);
            messenger.sendMessage(Message.INELIGIBLE_SENDER, new HashMap<>(placeholders), player);
        }
    }

    @Override
    public Set<Player> getRecipients(Player sender) {
        HashSet<Player> recipients = new HashSet<>(getMonitors());
        for(World w : getWorlds()) {
            recipients.addAll(w.getPlayers());
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.hasPermission("strings.channels." + this.getName() + ".receive")) {
                recipients.add(p);
            }
        }
        return recipients;
    }

}
