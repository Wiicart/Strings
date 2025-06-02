package com.pedestriamc.strings.channel.local;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import org.bukkit.Bukkit;
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
 * A WorldChannel that does not send player messages to all members of the Channel.
 * Players must either be in the scope (one of the Worlds of the Channel) or be a monitor.
 */
public class StrictWorldChannel extends AbstractLocalChannel {

    public static final String IDENTIFIER = "world_strict";

    private final Channel defaultChannel;
    private final Messenger messenger;

    public StrictWorldChannel(JavaPlugin plugin, ChannelBuilder builder) {
        this((Strings) plugin, builder);
    }

    public StrictWorldChannel(@NotNull Strings strings, @NotNull ChannelBuilder builder) {
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

    @Override
    public @NotNull Type getType() {
        return Type.WORLD;
    }

    @Contract(" -> new")
    private @NotNull @Unmodifiable Map<String, String> getPlaceholders() {
        return Map.of("{channel}", getName());
    }

    @Override
    public @NotNull String getIdentifier() {
        return IDENTIFIER;
    }

    // N/A to this implementation, UnsupportedOperationException always thrown.
    @Override
    public double getProximity() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("getProximity() called on WorldChannel instance, which is unsupported.");
    }

    // N/A to this implementation, UnsupportedOperationException always thrown.
    @Override
    public void setProximity(double proximity) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("setProximity() called on WorldChannel instance, which is unsupported.");
    }
}
