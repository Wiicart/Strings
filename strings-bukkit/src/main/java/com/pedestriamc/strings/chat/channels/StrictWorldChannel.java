package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channels.data.ChannelData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class StrictWorldChannel extends WorldChannel {

    public StrictWorldChannel(Strings strings, ChannelData data) {
        super(strings, data);
    }

    @Override
    public Set<Player> getRecipients(Player sender) {
        HashSet<Player> recipients = new HashSet<>(getMonitors());

        for(World w : getWorlds()) {
            recipients.addAll(w.getPlayers());
        }

        recipients.addAll(getMembers());
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.hasPermission("strings.channels." + this.getName() + ".receive")) {
                recipients.add(p);
            }
        }
        return recipients;
    }

}
