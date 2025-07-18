package com.pedestriamc.strings.channel.local;

import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.channel.DefaultChannel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Channel implementation that sends messages based off players in proximity in the sender's {@link World}.
 * Worlds this Channel is effective in must be defined.
 * Expected to be used with the {@link DefaultChannel}
 */
public class ProximityChannel extends AbstractLocalChannel {

    public static final String IDENTIFIER = "proximity";

    @Range(from = -1, to = Integer.MAX_VALUE)
    private double distance;

    // for more efficient calculations.
    private double distanceSquared;

    public ProximityChannel(JavaPlugin plugin, ChannelBuilder builder) {
        this((Strings) plugin, builder);
    }

    public ProximityChannel(@NotNull Strings strings, @NotNull ChannelBuilder builder) {
        super(strings, builder);
        distance = builder.getDistance();
        distanceSquared = distance * distance;
    }

    public @NotNull Set<Player> getRecipients(@NotNull Player sender) {
        Set<Player> members = getMembers();
        if(members.contains(sender)) {
            return universalSet();
        }

        World senderWorld = sender.getWorld();
        HashSet<Player> recipients = new HashSet<>(members);

        Location senderLocation = sender.getLocation();
        for(Player p : senderWorld.getPlayers()) {
            Location pLocation = p.getLocation();
            if(senderLocation.distanceSquared(pLocation) < distanceSquared) {
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

    @Override
    public @NotNull Type getType() {
        return Type.PROXIMITY;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> map = super.getData();
        map.put("distance", String.valueOf(distance));
        return map;
    }

    @Override
    public double getProximity() {
        return distance;
    }

    @Override
    public void setProximity(@Range(from = 0, to = Integer.MAX_VALUE) double proximity) {
        this.distance = proximity;
        distanceSquared = distance * distance;
    }

    @Override
    public @NotNull String getIdentifier() {
        return IDENTIFIER;
    }
}
