package com.pedestriamc.strings.channel.local;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

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

    public StrictProximityChannel(JavaPlugin plugin, ChannelBuilder builder) {
        this((Strings) plugin, builder);
    }

    public StrictProximityChannel(@NotNull Strings strings, @NotNull ChannelBuilder builder) {
        super(strings, builder);
        defaultChannel = strings.getChannelLoader().getChannel("default");
        messenger = strings.getMessenger();
    }

    @Override
    public void sendMessage(@NotNull Player player, @NotNull String message) {
        if(containsInScope(player)) {
            super.sendMessage(player, message);
        } else {
            defaultChannel.sendMessage(player, message);
            messenger.sendMessage(Message.INELIGIBLE_SENDER, getPlaceholders(), player);
        }
    }

    @Override
    public @NotNull Set<Player> getRecipients(@NotNull Player sender) {
        World senderWorld = sender.getWorld();
        HashSet<Player> recipients = new HashSet<>(getMonitors());

        Location senderLocation = sender.getLocation();
        for(Player p : senderWorld.getPlayers()) {
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

    @Contract(" -> new")
    private @NotNull @Unmodifiable Map<String, String> getPlaceholders() {
        return Map.of("{channel}", getName());
    }

    @Override
    public @NotNull String getIdentifier() {
        return "strict_proximitychannel";
    }
}
