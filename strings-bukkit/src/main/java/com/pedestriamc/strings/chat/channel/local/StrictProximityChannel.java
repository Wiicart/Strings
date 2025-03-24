package com.pedestriamc.strings.chat.channel.local;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.data.ChannelData;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A ProximityChannel that only sends messages to players close enough to the sender.
 * Members are not sent the message by default; to moderate, this Channel can be monitored.
 */
public class StrictProximityChannel extends ProximityChannel {

    private final Channel defaultChannel;
    private final Messenger messenger;

    public StrictProximityChannel(Strings strings, ChannelData data) {
        super(strings, data);
        defaultChannel = strings.getChannelLoader().getChannel("default");
        messenger = strings.getMessenger();
    }

    @Override
    public void sendMessage(Player player, String message) {
        if(containsInScope(player)) {
            super.sendMessage(player, message);
        } else {
            defaultChannel.sendMessage(player, message);
            messenger.sendMessage(Message.INELIGIBLE_SENDER, getPlaceholders(), player);
        }
    }

    @Override
    public Set<Player> getRecipients(@NotNull Player sender) {
        World senderWorld = sender.getWorld();
        HashSet<Player> recipients = new HashSet<>(getMonitors());

        Location senderLocation = sender.getLocation();
        for(Player p : senderWorld.getPlayers()){
            Location pLocation = p.getLocation();
            if(senderLocation.distanceSquared(pLocation) < getDistanceSquared()) {
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

    private Map<String, String> getPlaceholders() {
        return Map.of("{channel}", getName());
    }


}
