package com.pedestriamc.strings.chat.channel.local;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channels.data.ChannelData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * A ProximityChannel that only sends messages to players close enough to the sender.
 * Members are not sent the message by default; to moderate, this Channel can be monitored.
 */
public class StrictProximityChannel extends ProximityChannel {

    public StrictProximityChannel(Strings strings, ChannelData data) {
        super(strings, data);
    }

    @Override
    public Set<Player> getRecipients(Player sender) {
        if(sender == null) {
            return defaultSet();
        }

        World senderWorld = sender.getWorld();
        HashSet<Player> recipients = new HashSet<>(getMonitors());

        Location senderLocation = sender.getLocation();
        for(Player p : senderWorld.getPlayers()){
            Location pLocation = p.getLocation();
            if(senderLocation.distance(pLocation) < getProximity()) {
                recipients.add(p);
            }
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.hasPermission(CHANNEL_PERMISSION + getName() + ".receive")) {
                recipients.add(p);
            }
        }
        return recipients;
    }


}
