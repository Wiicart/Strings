package com.pedestriamc.strings.channel.local;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A ProximityChannel that only sends messages to players close enough to the sender.
 * Members are not sent the message by default; to moderate, this Channel can be monitored.
 */
public class StrictProximityChannel extends AbstractLocalChannel {

    public static final String IDENTIFIER = "proximity_strict";

    @Range(from = 0, to = Integer.MAX_VALUE)
    private double distance;

    // for more efficient calculations.
    private double distanceSquared;

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
    public void sendMessage(@NotNull StringsUser user, @NotNull String message) {
        if(containsInScope(user)) {
            super.sendMessage(user, message); // Super class logic references this class's getRecipients impl
        } else {
            defaultChannel.sendMessage(user, message);
            messenger.sendMessage(Message.INELIGIBLE_SENDER, getPlaceholders(), User.playerOf(user));
        }
    }

    @Override
    public @NotNull Set<StringsUser> getRecipients(@NotNull StringsUser sender) {
        Player player = User.playerOf(sender);
        World senderWorld = player.getWorld();
        UserUtil userUtil = getUserUtil();
        HashSet<StringsUser> recipients = new HashSet<>(getMonitors());

        Location senderLocation = player.getLocation();
        for(Player p : senderWorld.getPlayers()) {
            Location pLocation = p.getLocation();
            if(senderLocation.distanceSquared(pLocation) < distanceSquared) {
                recipients.add(userUtil.getUser(p));
            }
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.hasPermission(CHANNEL_PERMISSION + getName() + ".receive")) {
                recipients.add(userUtil.getUser(p));
            }
        }

        return filterMutes(recipients);
    }

    @Override
    public @NotNull Type getType() {
        return Type.PROXIMITY;
    }

    @Contract(" -> new")
    private @NotNull @Unmodifiable Map<String, String> getPlaceholders() {
        return Map.of("{channel}", getName());
    }

    @Override
    public @NotNull String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public @Range(from = -1, to = Integer.MAX_VALUE) double getProximity() throws UnsupportedOperationException {
        return distance;
    }

    @Override
    public void setProximity(@Range(from = -1, to = Integer.MAX_VALUE) double proximity) throws UnsupportedOperationException {
        distance = proximity;
        distanceSquared = proximity * proximity;
    }
}
