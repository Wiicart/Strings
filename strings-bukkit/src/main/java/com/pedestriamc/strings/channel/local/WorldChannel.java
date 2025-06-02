package com.pedestriamc.strings.channel.local;

import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.channel.DefaultChannel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Channel implementation that focuses on one or more {@link World}(s) on the server.
 * Expected to be used with the {@link DefaultChannel}
 */
public class WorldChannel extends AbstractLocalChannel {

    public static final String IDENTIFIER = "world";

    public WorldChannel(JavaPlugin plugin, ChannelBuilder builder) {
        this((Strings) plugin, builder);
    }

    public WorldChannel(@NotNull Strings strings, @NotNull ChannelBuilder builder) {
        super(strings, builder);
    }

    @Override
    public @NotNull Set<Player> getRecipients(@NotNull Player sender) {
        HashSet<Player> recipients = new HashSet<>();

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

    @Override
    public @NotNull Type getType() {
        return Type.WORLD;
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

    @Override
    public @NotNull String getIdentifier() {
        return IDENTIFIER;
    }

}

